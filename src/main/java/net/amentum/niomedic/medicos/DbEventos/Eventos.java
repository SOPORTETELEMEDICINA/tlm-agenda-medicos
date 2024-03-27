package net.amentum.niomedic.medicos.DbEventos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Data // @Getter, @Setter, @ToString, @EqualsAndHashCode and @RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "eventos")
public class Eventos implements Serializable {
    @Id
    @Column(name = "id_eventos")
    private Long idEventos;
    private Long idUsuarioCrea;
    @Size(max = 60)
    private String titulo;
    @Temporal(TemporalType.TIMESTAMP)
    private Date inicio;
    @Temporal(TemporalType.TIMESTAMP)
    private Date fin;
    @Size(max = 255)
    private String ubicacion;
    @Size(max = 255)
    private String conferencia;
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    @Temporal(TemporalType.TIMESTAMP)
    private Date alerta;
    private Boolean visible;
    private Long regionSanitaria;
    private String unidadMedica;
    private Integer status;
    private Long idUsuarioRecibe;
    private String idPaciente;
    private String especialidad;

    //   cuando el evento es una consulta debe existir una relacion
    private Long idConsulta;

    //   relaciones
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "tipo_evento_id", referencedColumnName = "id_tipo_evento")
    private CatalogoTipoEvento2 catalogoTipoEvento;

    /*@OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, mappedBy = "eventos")
    private Collection<Invitados> invitadosList = new ArrayList<>();*/

    @Override
    public String toString() {
        return "Eventos{" +
                "idEventos=" + idEventos +
                ", idUsuarioCrea=" + idUsuarioCrea +
                ", titulo='" + titulo + '\'' +
                ", inicio=" + inicio +
                ", fin=" + fin +
                ", ubicacion='" + ubicacion + '\'' +
                ", conferencia='" + conferencia + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", alerta=" + alerta +
                ", visible=" + visible +
                ", regionSanitaria=" + regionSanitaria +
                ", unidadMedica='" + unidadMedica + '\'' +
                ", status=" + status +
                ", idUsuarioRecibe=" + idUsuarioRecibe +
                ", idPaciente='" + idPaciente + '\'' +
                ", especialidad='" + especialidad + '\'' +
                ", idConsulta=" + idConsulta +
                '}';
    }
}