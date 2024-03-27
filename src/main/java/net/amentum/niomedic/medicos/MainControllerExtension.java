package net.amentum.niomedic.medicos;

import lombok.extern.slf4j.Slf4j;
import net.amentum.common.TimeUtils;
import net.amentum.niomedic.catalogos.views.CatEspecialidadesView;
import net.amentum.niomedic.expediente.views.EventosView;
import net.amentum.niomedic.medicos.DbAgendaMedicos.*;
import net.amentum.niomedic.medicos.DbAgendaMedicos.CatalogoTipoEvento;
import net.amentum.niomedic.medicos.DbEventos.*;
import net.amentum.niomedic.medicos.configuration.ApiConfiguration;
import net.amentum.niomedic.medicos.views.AgendaMedicosView;
import net.amentum.niomedic.medicos.views.EspecialidadView;
import net.amentum.niomedic.medicos.views.MedicoAgendaPageView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.MimetypesFileTypeMap;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.File;
import java.io.FileInputStream;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.*;

import static net.amentum.common.TimeUtils.parseDate;

@Slf4j
@Component
public class MainControllerExtension {

    @Autowired
    ApiConfiguration apiConfiguration;

    @Autowired
    AgendaMedicosAuthRepository agendaMedicosRepository;

    @Autowired
    EspecialidadAuthRepository especialidadRepository;

    @Autowired
    MedicoAuthRepository medicoRepository;

    @Autowired
    CatEspecialidadesRepository catEspecialidadesRepository;

    @Autowired
    EventosRepository eventosRepository;

    @Autowired
    EventosAuthRepository eventosAuthRepository;

    @Autowired
    TurnosLaboralesRepository turnosLaboralesRepository;

    @Autowired
    VacacionesRepository vacacionesRepository;

    @Autowired
    AsuetosRepository asuetosRepository;

    @Autowired
    CatalogoTipoEventoRepository catalogoTipoEventoRepository;

    @Transactional(rollbackFor = {Exception.class})
    EventosView createEventos(EventosView eventosView) throws Exception {
        try {
            //         que la fecha final no sea menor que la fecha inicial
            long inicioView = eventosView.getInicio().getTime();
            long finView = eventosView.getFin().getTime();

            if (inicioView >= finView) {
                String textoError = "El tiempo final NO puede ser MENOR que el tiempo inicial-> fechaInicio: " + eventosView.getInicio() + " - fechaFin: " + eventosView.getFin();
                log.error("===>>>" + textoError);
                throw new Exception("Existe un Error : " + textoError);
            }

            //         EVENTO es 1  IMPOSIBLE
            if (eventosView.getTipoEventoId() == 1) {
                String textoError = "El tipo de evento 1 no es elegible para utilizarlo ";
                log.error("===>>>" + textoError);
                throw new Exception("Existe un Error : " + textoError);
            }

            //         EL EVENTO ES UNA CITA
            if (eventosView.getTipoEventoId() == 2) {

                if (eventosView.getIdConsulta() == null) {
                    String textoError = "Una Cita DEBE estar relacionada a una Consulta";
                    log.error("===>>>" + textoError);
                    throw new Exception("Existe un Error : " + textoError);
                }

                //         DONE: que la hora inicio no este duplicado en otro registro
                List<EventosAuth> duplicado = eventosAuthRepository.buscarPorIdUsuarioCreaAndInicio(eventosView.getIdUsuario(), eventosView.getInicio(), 2);
                if (duplicado != null && !duplicado.isEmpty()) {
                    String textoError = "Eventos DUPLICADO en otro registro: idEventos: " + duplicado.get(0).getIdEventos() +
                            " - fechaInicio: " + duplicado.get(0).getInicio();
                    log.error("===>>>" + textoError);
                    throw new Exception("Existe un Error : " + textoError);
                }

                //         DONE: que no se cruce con otro registro
                List<EventosAuth> cruce = eventosAuthRepository.buscarPorIdUsuarioCreaAndNuevaFechaEntreInicioFin(eventosView.getIdUsuario(), eventosView.getInicio(), 2);
                if (cruce != null && !cruce.isEmpty()) {
                    if (eventosView.getIdEventos() != cruce.get(0).getIdEventos()) {
                        String textoError = "Eventos CRUCE entre horas con otro registro: idEventos: " + cruce.get(0).getIdEventos() +
                                " - fechaInicio: " + cruce.get(0).getInicio() +
                                " - fechaFin: " + cruce.get(0).getFin();
                        log.error("===>>>" + textoError);
                        throw new Exception("Existe un Error : " + textoError);
                    }
                }

                List<EventosAuth> cruceFinal = eventosAuthRepository.buscarPorIdUsuarioCreaAndNuevaFechaEntreInicioFin(eventosView.getIdUsuario(), eventosView.getFin(), 2);
                if (cruceFinal != null && !cruceFinal.isEmpty()) {
                    if (eventosView.getIdEventos() != cruceFinal.get(0).getIdEventos()) {
                        String textoError = "Eventos CRUCE entre horas con otro registro: idEventos: " + cruceFinal.get(0).getIdEventos() +
                                " - fechaInicio: " + cruceFinal.get(0).getInicio() +
                                " - fechaFin: " + cruceFinal.get(0).getFin();
                        log.error("===>>>" + textoError);
                        throw new Exception("Existe un Error : " + textoError);
                    }
                }

                TurnosLaborales turnosLaborales = turnosLaboralesRepository.findByIdUsuario(eventosView.getIdUsuario());
                long valIni = 1L;
                long valFin = 1L;
                boolean flag = false;

                if (turnosLaborales != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(eventosView.getInicio());
                    int dia = calendar.get(Calendar.DAY_OF_WEEK);
                    valIni = quitarFecha(turnosLaborales.getInicio()).getTime();
                    valFin = quitarFecha(turnosLaborales.getFin()).getTime();
                    inicioView = quitarFecha(eventosView.getInicio()).getTime();
                    finView = quitarFecha(eventosView.getFin()).getTime();
                    flag = false;
                    switch (dia) {
                        case 1:
                            if (turnosLaborales.getDomingo()) {
                                flag = dentroRango(inicioView, finView, valIni, valFin);
                            } else {
                                flag = true;
                            }
                            break;
                        case 2:
                            if (turnosLaborales.getLunes()) {
                                flag = dentroRango(inicioView, finView, valIni, valFin);
                            } else {
                                flag = true;
                            }
                            break;
                        case 3:
                            if (turnosLaborales.getMartes()) {
                                flag = dentroRango(inicioView, finView, valIni, valFin);
                            } else {
                                flag = true;
                            }
                            break;
                        case 4:
                            if (turnosLaborales.getMiercoles()) {
                                flag = dentroRango(inicioView, finView, valIni, valFin);
                            } else {
                                flag = true;
                            }
                            break;
                        case 5:
                            if (turnosLaborales.getJueves()) {
                                flag = dentroRango(inicioView, finView, valIni, valFin);
                            } else {
                                flag = true;
                            }
                            break;
                        case 6:
                            if (turnosLaborales.getViernes()) {
                                flag = dentroRango(inicioView, finView, valIni, valFin);
                            } else {
                                flag = true;
                            }
                            break;
                        case 7:
                            if (turnosLaborales.getSabado()) {
                                flag = dentroRango(inicioView, finView, valIni, valFin);
                            } else {
                                flag = true;
                            }
                            break;
                    }

                    if (flag) {
                        String textoError = "Eventos no esta en horario laboral-> idUsuario: " + eventosView.getIdUsuario() + " - fechaInicio: " + eventosView.getInicio() + " - fechaFin: " + eventosView.getFin();
                        log.error("===>>>" + textoError);
                        throw new Exception("Existe un Error : " + textoError);
                    }
                }
                //         DONE: checar vacaciones
                List<Vacaciones> vacacionesList = vacacionesRepository.findByIdUsuario(eventosView.getIdUsuario());

                inicioView = eventosView.getInicio().getTime();
                finView = eventosView.getFin().getTime();
                flag = false;

                if (vacacionesList != null && !vacacionesList.isEmpty()) { // revision de vacacionesList NO vacio
                    for (Vacaciones vaca : vacacionesList) {
                        valIni = quitarTiempo(vaca.getInicio(), true).getTime();
                        valFin = quitarTiempo(vaca.getFin(), false).getTime();
                        flag = !dentroRango(inicioView, finView, valIni, valFin);
                        break;
                    }
                }

                if (flag) {
                    String textoError = "Eventos esta en horario vacacional-> idUsuario: " + eventosView.getIdUsuario() + " - fechaInicio: " + eventosView.getInicio() + " - fechaFin: " + eventosView.getFin();
                    log.error("===>>>" + textoError);
                    throw new Exception("Existe un Error : " + textoError);
                }

                //         DONE: checar asuetos
                List<Asuetos> asuetosList = asuetosRepository.findAll();

                inicioView = quitarTiempo(eventosView.getInicio(), true).getTime();
                flag = false;

                if (asuetosList != null && !asuetosList.isEmpty()) { // revision de asuetosList NO vacio
                    for (Asuetos asue : asuetosList) {
                        valIni = quitarTiempo(asue.getFecha(), true).getTime();
                        if (inicioView == valIni) {
                            flag = true;
                            break;
                        }
                    }
                }

                if (flag) {
                    String textoError = "Eventos esta en dia de asueto-> idUsuario: " + eventosView.getIdUsuario() + " - fechaInicio: " + eventosView.getInicio() + " - fechaFin: " + eventosView.getFin();
                    log.error("===>>>" + textoError);
                    throw new Exception("Existe un Error : " + textoError);
                }
            }

            //         EL EVENTO ES UN RECORDATORIO
            if (eventosView.getTipoEventoId() == 3) {
                eventosView.setInicio(eventosView.getAlerta());
                eventosView.setFin(eventosView.getAlerta());
                eventosView.setInvitadosViewList(new ArrayList<>());
                eventosView.setDescripcion(null);
                eventosView.setIdConsulta(null);
            }

            //         EL EVENTO ES UNA TAREA
            if (eventosView.getTipoEventoId() == 4) {
                eventosView.setInicio(eventosView.getAlerta());
                eventosView.setFin(eventosView.getAlerta());
                eventosView.setInvitadosViewList(new ArrayList<>());
                eventosView.setIdConsulta(null);
            }
            EventosAuth eventos = toEntity(eventosView, new EventosAuth());
            eventos.setIdEventos(apiConfiguration.getEventoId());
            log.info("Insertar nuevo Eventos: {}", eventos);
            eventosAuthRepository.save(eventos);
            return toView(eventos);
        } catch (ConstraintViolationException cve) {
            StringBuilder builder = new StringBuilder();
            final Set<ConstraintViolation<?>> violaciones = cve.getConstraintViolations();
            for (Iterator<ConstraintViolation<?>> iterator = violaciones.iterator(); iterator.hasNext(); ) {
                ConstraintViolation<?> siguiente = iterator.next();
                builder.append(siguiente.getPropertyPath() + " " + siguiente.getMessage() + "\n");
            }
            log.error("===>>>Error en la validacion: " + builder);
            Exception eveE = new Exception("Error en la validacion: " + builder);
            throw eveE;
        } catch (DataIntegrityViolationException dive) {
            log.error("===>>>Error al insertar nuevo Eventos - CODE: {} - {}", eventosView, dive);
            throw new Exception("No fue posible agregar  Eventos: ", dive);
        } catch (Exception ex) {
            log.error("===>>>Error al insertar nuevo Eventos - CODE: {} - {}", eventosView, ex);
            throw new Exception("Error al agregar un Evento: ", ex);
        }
    }

    public Page<EventosView> getEventosSearch(List<Long> idUsuario, List<Integer> idTipoEvento, String titulo, Long startDate, Long endDate,
                                              List<Long> idUsuarioRecibe, List<String> idPaciente, List<String> especialidad, List<Long> regionSanitaria,
                                              List<String> unidadMedica, List<Integer> status, Integer page, Integer size, String orderColumn, String orderType) throws Exception {
        try {
            final Map<String, Object> colOrderNames = new HashMap<>();
            {
                colOrderNames.put("idEventos", "idEventos");
                colOrderNames.put("idUsuario", "idUsuario");
                colOrderNames.put("inicio", "inicio");
                colOrderNames.put("fin", "fin");
                colOrderNames.put("ubicacion", "ubicacion");
                colOrderNames.put("conferencia", "conferencia");
                colOrderNames.put("descripcion", "descripcion");
                colOrderNames.put("alerta", "alerta");
                colOrderNames.put("visible", "visible");
                colOrderNames.put("idConsulta", "idConsulta");
                colOrderNames.put("titulo", "titulo");
            }
            if(!colOrderNames.containsKey(orderColumn)) {
                log.info("getEventosSearch() - No existen ordenamiento por la columna: {}, asignando una por defecto", orderColumn);
                orderColumn = "titulo";
            }
            log.info("getEventosSearch(): - idUsuario: {} - idTipoEvento: {} - startDate: {} - endDate: {} - page: {} - size: {} - orderColumn: {} - orderType: {}",
                    idUsuario, idTipoEvento, startDate, endDate, page, size, orderColumn, orderType);
            List<EventosView> eventosViewList = new ArrayList<>();
            Page<Eventos> eventosPage = null;
            Sort sort;

            if (orderType.equalsIgnoreCase("asc"))
                sort = new Sort(Sort.Direction.ASC, (String) colOrderNames.get(orderColumn));
            else
                sort = new Sort(Sort.Direction.DESC, (String) colOrderNames.get(orderColumn));
            PageRequest request = new PageRequest(page, size, sort);
            Specifications<Eventos> spec = Specifications.where(
                    (root, query, cb) -> {
                        Predicate tc = null;
                        Expression<Long> usuario = root.get("idUsuarioCrea");
                        Expression<Long> tipoEvento = root.get("catalogoTipoEvento");
                        if(especialidad != null)
                            if(!especialidad.isEmpty()) {
                                for(String element : especialidad) {
                                    String especialidadPattern = "%" + sinAcentos(element.toLowerCase()) + "%";
                                    tc = (tc != null ? cb.or(tc, cb.like(cb.function("unaccent", String.class, cb.lower(root.get("especialidad"))), especialidadPattern)) :
                                            cb.like(cb.function("unaccent", String.class, cb.lower(root.get("especialidad"))), especialidadPattern));
                                }
                            }
                        if(idUsuario != null)
                            if(!idUsuario.isEmpty())
                                tc = (tc != null ? cb.and(tc, usuario.in(idUsuario)) : usuario.in(idUsuario));
                        if(idTipoEvento != null)
                            if(!idTipoEvento.isEmpty())
                                tc = (tc != null ? cb.and(tc, tipoEvento.in(idTipoEvento)) : tipoEvento.in(idTipoEvento));
                        if(titulo != null)
                            if(!titulo.isEmpty()) {
                                String pattern = "%" + sinAcentos(titulo.toLowerCase()) + "%";
                                tc = (tc != null ? cb.and(tc, cb.like(cb.function("unaccent", String.class, cb.lower(root.get("titulo"))), pattern)) :
                                        cb.like(cb.function("unaccent", String.class, cb.lower(root.get("titulo"))), pattern));
                            }
                        if (startDate != null && endDate != null) {
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                Date inicialDate = parseDate(sdf.format(startDate) + " 00:00:00", TimeUtils.LONG_DATE);
                                Date finalDate = parseDate(sdf.format(endDate) + " 23:59:59", TimeUtils.LONG_DATE);
                                tc = (tc != null) ?
                                        cb.and(tc, cb.greaterThanOrEqualTo(root.get("inicio"), inicialDate), cb.lessThanOrEqualTo(root.get("fin"), finalDate)) :
                                        cb.and(cb.greaterThanOrEqualTo(root.get("inicio"), inicialDate), cb.lessThanOrEqualTo(root.get("fin"), finalDate));
                            } catch (Exception ex) {
                                log.warn("Error al convertir fechas", ex);
                            }
                        }
                        if(idUsuarioRecibe != null)
                            if(!idUsuarioRecibe.isEmpty())
                                tc = (tc != null ? cb.and(tc, root.get("idUsuarioRecibe").in(idUsuarioRecibe)) :  root.get("idUsuarioRecibe").in(idUsuarioRecibe));
                        if(idPaciente != null)
                            if(!idPaciente.isEmpty())
                                tc = (tc != null ? cb.and(tc, root.get("idPaciente").in(idPaciente)) : root.get("idPaciente").in(idPaciente));
                        if(regionSanitaria != null)
                            if(!regionSanitaria.isEmpty())
                                tc = (tc != null ? cb.and(tc, root.get("regionSanitaria").in(regionSanitaria)) : root.get("regionSanitaria").in(regionSanitaria));
                        if(unidadMedica != null)
                            if(!unidadMedica.isEmpty())
                                tc = (tc != null ? cb.and(tc, root.get("unidadMedica").in(unidadMedica)) : root.get("unidadMedica").in(unidadMedica));
                        if(status != null)
                            if(!status.isEmpty())
                                tc = (tc != null ? cb.and(tc, root.get("status").in(status)) : root.get("status").in(status));
                        return tc;
                    }
            );
            eventosPage = eventosRepository.findAll(spec, request);
            eventosPage.getContent().forEach(eventos -> eventosViewList.add(toView(eventos)));
            return new PageImpl<>(eventosViewList, request, eventosPage.getTotalElements());
        } catch (Exception ex) {
            log.error("===>>>Error al tratar de seleccionar lista Eventos paginable - error: ", ex);
            throw new Exception("Ocurrió un error al seleccionar lista Eventos paginable", ex);
        }
    }

    List<CatEspecialidadesView> especialidadesFindAll() throws Exception {
        try {
            Sort sort = new Sort(Sort.Direction.ASC, "especialidadDescripcion");
            List<CatEspecialidades> catEspecialidadesList = catEspecialidadesRepository.findAll(sort);
            List<CatEspecialidadesView> catEspecialidadesViewList = new ArrayList<>();
            for (CatEspecialidades cpl : catEspecialidadesList) {
                catEspecialidadesViewList.add(toView(cpl));
            }
            return catEspecialidadesViewList;
        } catch (Exception ex) {
            log.error("Error al obtener todos los registros - error: ", ex);
            throw new Exception("No fue posible obtener todos los registros: ", ex);
        }
    }

    Page<MedicoAgendaPageView> getMedicDetailsByEspecialidad(String nombreEspecialidad, Integer page, Integer size, String orderColumn, String orderType) throws Exception {
        try {
            final Map<String, Object> colOrderNames = new HashMap<>();
            {
                colOrderNames.put("nombre", "nombre");
                colOrderNames.put("email", "email");
                colOrderNames.put("cedula", "cedula");
                colOrderNames.put("especialidad", "especialidad");
            }
            log.info("nombreEspecialidad {} - page {} - size {} - orderColumn {} - orderType {} -",nombreEspecialidad, page, size, orderColumn, orderType);
            Sort sort = new Sort(Sort.Direction.ASC, (String) colOrderNames.get(orderColumn));
            if (orderColumn != null && orderType != null)
                if (orderType.equalsIgnoreCase("asc"))
                    sort = new Sort(Sort.Direction.ASC, (String) colOrderNames.get(orderColumn));
                else
                    sort = new Sort(Sort.Direction.DESC, (String) colOrderNames.get(orderColumn));
            PageRequest request = new PageRequest(page, size, sort);
            final String patternSearch = "%" + nombreEspecialidad.toLowerCase() + "%";
            Specifications<EspecialidadAuth> spec = Specifications.where(
                    (root, query, cb) -> {
                        Predicate tc = null;
                        if (!nombreEspecialidad.isEmpty())
                            tc = cb.like(cb.function("unaccent", String.class, cb.lower(root.get("especialidad"))), sinAcentos(patternSearch));
                        return tc;
                    }
            );
            List<MedicoAgendaPageView> medicoView = new ArrayList<>();
            Page<EspecialidadAuth> especialidadPage = null;
            especialidadPage = especialidadRepository.findAll(spec, request);
            especialidadPage.getContent().forEach(especialidad -> {
                try {
                    MedicoAuth medico = medicoRepository.findByIdMedico(especialidad.getMedico().getIdMedico());
                    MedicoAgendaPageView medicoAgendaView = toMedicoAgendaViewPage(medico);
                    AgendaMedicosAuth agenda = agendaMedicosRepository.findByIdmedico(especialidad.getMedico().getIdMedico());
                    if(agenda != null)
                        medicoAgendaView.setAgenda(toView(agenda));
                    medicoView.add(medicoAgendaView);
                } catch (Exception e) {
                    log.error(e.toString());
                }
            });
            return new PageImpl<>(medicoView, request, especialidadPage.getTotalElements());
        } catch (Exception ex) {
            log.error("===>>>Error al tratar de seleccionar lista de médicos por especialidad - ", ex);
            throw new Exception("Ocurrió un error al seleccionar lista de médicos por especialidad: ", ex);
        }
    }

    MedicoAgendaPageView toMedicoAgendaViewPage(MedicoAuth medico) throws Exception {
        MedicoAgendaPageView medicoPageView = new MedicoAgendaPageView();
        medicoPageView.setIdMedico(medico.getIdMedico());
        medicoPageView.setNombre(medico.getNombre() + " " + medico.getApellidoPaterno() + " " + medico.getApellidoMaterno());
        medicoPageView.setEmail(medico.getEmail());
        medicoPageView.setCurp(medico.getCurp());
        medicoPageView.setUserName(medico.getUserName());
        medicoPageView.setIdUsuario(medico.getIdUsuario());
        medicoPageView.setEspecialidades("");
        if (medico.getEspecialidadList() != null && !medico.getEspecialidadList().isEmpty()) {
            medicoPageView.setEspecialidadViewList(toViewAuth(medico.getEspecialidadList()));
            ArrayList<String> especialidad = new ArrayList<>();
            for(EspecialidadAuth esp: medico.getEspecialidadList()){
                especialidad.add(esp.getEspecialidad());
            }
            Collections.sort(especialidad);
            medicoPageView.setEspecialidades(especialidad.toString());
        }
        medicoPageView.setIdUnidadMedica(medico.getIdUnidadMedica());
        return medicoPageView;
    }

    EventosAuth toEntity(EventosView eventosView, EventosAuth eventos) {
        eventos.setIdUsuarioCrea(eventosView.getIdUsuario());
        eventos.setTitulo(eventosView.getTitulo());
        eventos.setInicio(eventosView.getInicio());
        eventos.setFin(eventosView.getFin());
        eventos.setUbicacion(eventosView.getUbicacion());
        eventos.setConferencia(eventosView.getConferencia());
        eventos.setDescripcion(eventosView.getDescripcion());
        eventos.setAlerta(eventosView.getAlerta());
        eventos.setVisible(eventosView.getVisible());
        eventos.setIdConsulta(eventosView.getIdConsulta());
        eventos.setRegionSanitaria(eventosView.getRegionSanitaria());
        eventos.setUnidadMedica(eventosView.getUnidadMedica());
        eventos.setStatus(eventosView.getStatus());
        eventos.setIdUsuarioRecibe(eventosView.getIdUsuarioRecibe());
        eventos.setIdPaciente(eventosView.getIdPaciente());
        eventos.setEspecialidad(eventosView.getEspecialidad());
        CatalogoTipoEvento catalogoTipoEvento = catalogoTipoEventoRepository.findOne(eventosView.getTipoEventoId());
        if (catalogoTipoEvento != null)
            eventos.setCatalogoTipoEvento(catalogoTipoEvento);
        else
            eventos.setCatalogoTipoEvento(null);
        return eventos;
    }

    EventosView toView(EventosAuth eventos) {
        EventosView eventosView = new EventosView();
        eventosView.setIdEventos(eventos.getIdEventos());
        eventosView.setIdUsuario(eventos.getIdUsuarioCrea());
        eventosView.setInicio(eventos.getInicio());
        eventosView.setFin(eventos.getFin());
        eventosView.setIdConsulta(eventos.getIdConsulta());
        eventosView.setRegionSanitaria(eventos.getRegionSanitaria());
        eventosView.setUnidadMedica(eventos.getUnidadMedica());
        eventosView.setStatus(eventos.getStatus());
        eventosView.setIdUsuarioRecibe(eventos.getIdUsuarioRecibe());
        eventosView.setIdPaciente(eventos.getIdPaciente());
        eventosView.setEspecialidad(eventos.getEspecialidad());
        return eventosView;
    }

    EventosView toView(Eventos eventos) {
        EventosView eventosView = new EventosView();
        eventosView.setIdEventos(eventos.getIdEventos());
        eventosView.setIdUsuario(eventos.getIdUsuarioCrea());
        eventosView.setInicio(eventos.getInicio());
        eventosView.setFin(eventos.getFin());
        eventosView.setIdConsulta(eventos.getIdConsulta());
        eventosView.setRegionSanitaria(eventos.getRegionSanitaria());
        eventosView.setUnidadMedica(eventos.getUnidadMedica());
        eventosView.setStatus(eventos.getStatus());
        eventosView.setIdUsuarioRecibe(eventos.getIdUsuarioRecibe());
        eventosView.setIdPaciente(eventos.getIdPaciente());
        eventosView.setEspecialidad(eventos.getEspecialidad());
        return eventosView;
    }

    Collection<EspecialidadView> toViewAuth(Collection<EspecialidadAuth> especialidadArrayList) throws Exception {
        Collection<EspecialidadView> especialidadViews = new ArrayList<>();
        for (EspecialidadAuth esp : especialidadArrayList) {
            EspecialidadView espV = new EspecialidadView();
            espV.setIdEspecialidad(esp.getIdEspecialidad());
            espV.setEspecialidad(esp.getEspecialidad());
            espV.setSubespecialidad(esp.getSubespecialidad());
            espV.setUniversidad(esp.getUniversidad());
            espV.setCedula(esp.getCedula());
            if (esp.getImgCedula64() != null)
                espV.setImgCedula64(obtenerImagenBase64(esp.getImgCedula64()));
            if (esp.getImgTitulo64() != null)
                espV.setImgTitulo64(obtenerImagenBase64(esp.getImgTitulo64()));
            espV.setNombreImagenCedula(esp.getNombreImagenCedula());
            espV.setNombreImagenDiploma(esp.getNombreImagenDiploma());
            espV.setValidado(esp.getValidado());
            espV.setFechaCreacion(esp.getFechaCreacion());
            espV.setFechaValidacion(esp.getFechaValidacion());
            especialidadViews.add(espV);
        }
        return especialidadViews;
    }

    String obtenerImagenBase64(String imagenBase64) throws Exception {
        String encodedBase64;
        //se obtiene la imagen
        File inputFile= new File(imagenBase64);
        if(inputFile.exists()) {
            try {
                log.info("obtenerImagenBase64() - direccion de la imagen: "+ imagenBase64);
                FileInputStream fileInputStreamReader = new FileInputStream(inputFile);
                log.info("obtenerImagenBase64() - se obtine el archivo : "+ imagenBase64);
                //se conbierte a bytes
                byte[] bytes = new byte[(int)inputFile.length()];
                log.info("obtenerImagenBase64() - convercion a bytes del archivo: "+ bytes);
                fileInputStreamReader.read(bytes);
                //el conjunto de bytes se le da formato base64
                encodedBase64 = Base64.getEncoder().encodeToString(bytes);
                log.info("obtenerImagenBase64() - convercion de bytes del archivo a base64");
                //obtencion del mime
                MimetypesFileTypeMap mapper=new MimetypesFileTypeMap();
                String typeFile=mapper.getContentType(inputFile);
                String name=inputFile.getName();
                log.info("obtenerImagenBase64() - obtencin del mime base64: "+ "data:"+typeFile+";base64," );
                String extension = typeFile.substring(0, typeFile.lastIndexOf("/")+1)+""+name.substring(name.lastIndexOf(".")+1);
                encodedBase64 = "data:"+extension+";base64,"+encodedBase64.trim();
                return encodedBase64;
            }catch(Exception e){
                log.error("obtenerImagenBase64() - Ocurrio un error al obtener la imagen del File System - "+e);
                throw e;
            }
        } else
            return null;
    }

    CatEspecialidadesView toView(CatEspecialidades catEspecialidades) {
        CatEspecialidadesView catEspecialidadesView = new CatEspecialidadesView();
        catEspecialidadesView.setIdCatEspecialidades(catEspecialidades.getIdCatEspecialidades());
        catEspecialidadesView.setEspecialidadCodigo(catEspecialidades.getEspecialidadCodigo());
        catEspecialidadesView.setEspecialidadDescripcion(catEspecialidades.getEspecialidadDescripcion());
        catEspecialidadesView.setActivo(catEspecialidades.getActivo());
        catEspecialidadesView.setFechaUltimaModificacion(catEspecialidades.getFechaUltimaModificacion());
        return catEspecialidadesView;
    }

    AgendaMedicosView toView(AgendaMedicosAuth Agendamedicos) {
        AgendaMedicosView AgendamedicosView = new AgendaMedicosView();

        AgendamedicosView.setIdagenda(Agendamedicos.getIdagenda());
        AgendamedicosView.setFechaingresoinst(Agendamedicos.getFechaingresoinst());
        AgendamedicosView.setIdmedico(Agendamedicos.getIdmedico());
        AgendamedicosView.setMeddom(Agendamedicos.getMeddom());
        AgendamedicosView.setMedlun(Agendamedicos.getMedlun());
        AgendamedicosView.setMedmar(Agendamedicos.getMedmar());
        AgendamedicosView.setMedmie(Agendamedicos.getMedmie());
        AgendamedicosView.setMedjue(Agendamedicos.getMedjue());
        AgendamedicosView.setMedvie(Agendamedicos.getMedvie());
        AgendamedicosView.setMedsab(Agendamedicos.getMedsab());

        AgendamedicosView.setLunEntMat(Agendamedicos.getLunEntMat());
        AgendamedicosView.setLunSalMat(Agendamedicos.getLunSalMat());
        AgendamedicosView.setLunEntVesp(Agendamedicos.getLunEntVesp());
        AgendamedicosView.setLunSalVesp(Agendamedicos.getLunSalVesp());
        AgendamedicosView.setLunEntNoct(Agendamedicos.getLunEntNoct());
        AgendamedicosView.setLunSalNoct(Agendamedicos.getLunSalNoct());

        AgendamedicosView.setMarEntMat(Agendamedicos.getMarEntMat());
        AgendamedicosView.setMarSalMat(Agendamedicos.getMarSalMat());
        AgendamedicosView.setMarEntVesp(Agendamedicos.getMarEntVesp());
        AgendamedicosView.setMarSalVesp(Agendamedicos.getMarSalVesp());
        AgendamedicosView.setMarEntNoct(Agendamedicos.getMarEntNoct());
        AgendamedicosView.setMarSalNoct(Agendamedicos.getMarSalNoct());

        AgendamedicosView.setMieEntMat(Agendamedicos.getMieEntMat());
        AgendamedicosView.setMieSalMat(Agendamedicos.getMieSalMat());
        AgendamedicosView.setMieEntVesp(Agendamedicos.getMieEntVesp());
        AgendamedicosView.setMieSalVesp(Agendamedicos.getMieSalVesp());
        AgendamedicosView.setMieEntNoct(Agendamedicos.getMieEntNoct());
        AgendamedicosView.setMieSalNoct(Agendamedicos.getMieSalNoct());

        AgendamedicosView.setJueEntMat(Agendamedicos.getJueEntMat());
        AgendamedicosView.setJueSalMat(Agendamedicos.getJueSalMat());
        AgendamedicosView.setJueEntVesp(Agendamedicos.getJueEntVesp());
        AgendamedicosView.setJueSalVesp(Agendamedicos.getJueSalVesp());
        AgendamedicosView.setJueEntNoct(Agendamedicos.getJueEntNoct());
        AgendamedicosView.setJueSalNoct(Agendamedicos.getJueSalNoct());

        AgendamedicosView.setVieEntMat(Agendamedicos.getVieEntMat());
        AgendamedicosView.setVieSalMat(Agendamedicos.getVieSalMat());
        AgendamedicosView.setVieEntVesp(Agendamedicos.getVieEntVesp());
        AgendamedicosView.setVieSalVesp(Agendamedicos.getVieSalVesp());
        AgendamedicosView.setVieEntNoct(Agendamedicos.getVieEntNoct());
        AgendamedicosView.setVieSalNoct(Agendamedicos.getVieSalNoct());

        AgendamedicosView.setSabEntMat(Agendamedicos.getSabEntMat());
        AgendamedicosView.setSabSalMat(Agendamedicos.getSabSalMat());
        AgendamedicosView.setSabEntVesp(Agendamedicos.getSabEntVesp());
        AgendamedicosView.setSabSalVesp(Agendamedicos.getSabSalVesp());
        AgendamedicosView.setSabEntNoct(Agendamedicos.getSabEntNoct());
        AgendamedicosView.setSabSalNoct(Agendamedicos.getSabSalNoct());

        AgendamedicosView.setDomEntMat(Agendamedicos.getDomEntMat());
        AgendamedicosView.setDomSalMat(Agendamedicos.getDomSalMat());
        AgendamedicosView.setDomEntVesp(Agendamedicos.getDomEntVesp());
        AgendamedicosView.setDomSalVesp(Agendamedicos.getDomSalVesp());
        AgendamedicosView.setDomEntNoct(Agendamedicos.getDomEntNoct());
        AgendamedicosView.setDomSalNoct(Agendamedicos.getDomSalNoct());
        log.debug("convertir Agendamedicos to View: {}", AgendamedicosView);
        return AgendamedicosView;
    }

    String sinAcentos(String cadena) {
        return Normalizer.normalize(cadena, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    private static Date quitarTiempo(Date date, boolean inicial) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (inicial) {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        } else {
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 59);
        }
        return cal.getTime();
    }

    private static Date quitarFecha(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(0, 0, 0);
        return cal.getTime();
    }

    private static boolean dentroRango(long inicioView, long finView, long valIni, long valFin) {
        if ((inicioView >= valIni) && (finView <= valFin)) {
            return false;
        } else {
            return true;
        }
    }
}
