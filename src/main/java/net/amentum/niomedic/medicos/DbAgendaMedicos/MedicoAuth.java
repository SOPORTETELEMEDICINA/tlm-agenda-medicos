package net.amentum.niomedic.medicos.DbAgendaMedicos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "medico")
public class MedicoAuth {

    private static final long serialVersionUID = 7504220664222030541L;

    @Id
    @Column(name = "id_medico")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String idMedico;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private Date fechaNacimiento;
    private String lugarNacimiento;
    private String estadoCivil;
    private String sexo;
    private String curp;
    private String rfc;

    private String email;
    private String telefonoFijo;
    private String telefonoMovil;
    private String id_cat_nacionalidades;
    private String id_cat_entidades;
    private String id_institucion;
    private String per_id;
    private String act_id;
    private String atr_id;
    private String id_cat_clues;
    private String jor_id;
    private String id_cat_especialidades;
    private String con_id;
    private String pla_id;

    @Column(unique = true, nullable = false)
    private Long idUsuario;
    private Boolean activo;
    private String userName;
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;
    private String datosBusqueda;
    private String idUsuarioZoom;
    private Integer idUnidadMedica;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, mappedBy = "medico")
    private Collection<DomicilioAuth> domicilioList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, mappedBy = "medico")
    private Collection<EspecialidadAuth> especialidadList = new ArrayList<>();

}
