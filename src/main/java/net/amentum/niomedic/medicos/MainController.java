package net.amentum.niomedic.medicos;

import lombok.extern.slf4j.Slf4j;
import net.amentum.niomedic.catalogos.views.CatEspecialidadesView;
import net.amentum.niomedic.expediente.views.EventosView;
import net.amentum.niomedic.medicos.views.MedicoAgendaPageView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Slf4j
public class MainController {

    @Autowired
    MainControllerExtension extension;

    @RequestMapping(value = "/medicos-auth/obtenerPorEspecialidad", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    Page<MedicoAgendaPageView> getDetailsByEspecialidad(@RequestParam() String nombreEspecialidad,
                                                        @RequestParam(required = false) Integer page,
                                                        @RequestParam(required = false) Integer size,
                                                        @RequestParam(required = false) String orderColumn,
                                                        @RequestParam(required = false) String orderType) throws Exception {
        try {
            log.info("Obtener médicos por especialidad: {}", nombreEspecialidad);
            if (page == null)
                page = 0;
            if (size == null)
                size = 10;
            if (orderType == null || orderType.isEmpty())
                orderType = "asc";
            if (orderColumn == null || orderColumn.isEmpty())
                orderColumn = "especialidad";
            return extension.getMedicDetailsByEspecialidad(nombreEspecialidad, page, size, orderColumn, orderType);
        } catch (Exception me) {
            throw me;
        }
    }

    @RequestMapping(value = "/catalogo-especialidades-auth/findAll", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<CatEspecialidadesView> getCatEspecialidades() throws Exception {
        return extension.especialidadesFindAll();
    }

    @RequestMapping(value = "/eventos-auth", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public EventosView createEventos(@RequestBody @Validated EventosView eventosView) throws Exception {
        try {
            log.info("===>>>Guardar nuevo Eventos: {}", eventosView);
            return extension.createEventos(eventosView);
        } catch (Exception ex) {
            log.error("===>>>Error al insertar  Eventos - ", ex);
            throw new Exception("No fue posible insertar  Eventos: ",ex);
        }
    }

    @RequestMapping(value = "/eventos-auth/search", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Page<EventosView> getEventosSearch(@RequestParam(required = false) List<Long> idUsuario,
                                              @RequestParam(required = false) List<Integer> idTipoEvento,
                                              @RequestParam(required = false) String titulo,
                                              @RequestParam(required = false) Long startDate,
                                              @RequestParam(required = false) Long endDate,
                                              @RequestParam(required = false) List<Long> idUsuarioRecibe,
                                              @RequestParam(required = false) List<String> idPaciente,
                                              @RequestParam(required = false) List<Long> regionSanitaria,
                                              @RequestParam(required = false) List<String> unidadMedica,
                                              @RequestParam(required = false) List<String> especialidad,
                                              @RequestParam(required = false) List<Integer> status,
                                              @RequestParam(required = false, defaultValue = "0") Integer page,
                                              @RequestParam(required = false, defaultValue = "10") Integer size,
                                              @RequestParam(required = false, defaultValue = "titulo") String orderColumn,
                                              @RequestParam(required = false, defaultValue = "asc") String orderType) throws Exception {

        log.info("===>>>getEventosSearch(): - idUsuarioCrea: {} - idTipoEvento: {} - titulo: {} - startDate: {} - endDate: {} - page: {} - size: {} - orderColumn: {} - orderType: {}",
                idUsuario, idTipoEvento, titulo, startDate, endDate, page, size, orderColumn, orderType);
        log.info("idUsuarioRecibe: {} - idPaciente: {} - regionSanitaria: {} - unidadMedica: {} - status: {}",
                idUsuarioRecibe, idPaciente, regionSanitaria, unidadMedica, status);
        if(page < 0)
            page = 0;
        if(size < 0)
            size = 10;
        if(!orderType.equalsIgnoreCase("asc") && !orderType.equalsIgnoreCase("desc"))
            orderType = "asc";
        return extension.getEventosSearch(idUsuario, idTipoEvento, titulo, startDate, endDate,
                idUsuarioRecibe, idPaciente, especialidad, regionSanitaria, unidadMedica, status,page, size, orderColumn, orderType.toLowerCase());
    }
}
