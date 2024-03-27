package net.amentum.niomedic.medicos.DbAgendaMedicos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "domicilio")
public class DomicilioAuth implements Serializable {
   @Id
   @GeneratedValue(generator = "UUID")
   @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
   private String idDomicilio;
   private String domicilio;
   private String localidad;
   private String municipio;
   private String estado;
   private String pais;
   private String cp;
   private String telefonoFijo;
   private String registroSanitario;
   private Date fechaCreacion;
   private Boolean activo;
   private String tipo; // particular, hospital(es)
   private String horarioAtencion; // GGR20200731 Agrego campo horario de atención
   //relaciones
//    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinColumn(name = "paciente_id", referencedColumnName = "id_paciente")
//    private Paciente paciente;

   @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
   @JoinColumn(name = "medico_id", referencedColumnName = "id_medico")
   private MedicoAuth medico;


   @Override
   public String toString() {
      return "Domicilio{" +
         "idDomicilio='" + idDomicilio + '\'' +
         ", domicilio='" + domicilio + '\'' +
         ", localidad='" + localidad + '\'' +
         ", municipio='" + municipio + '\'' +
         ", estado='" + estado + '\'' +
         ", pais='" + pais + '\'' +
         ", cp='" + cp + '\'' +
         ", telefonoFijo='" + telefonoFijo + '\'' +
         ", registroSanitario='" + registroSanitario + '\'' +
         ", fechaCreacion=" + fechaCreacion +
         ", activo=" + activo +
         ", tipo='" + tipo + '\'' +
         '}';
   }
}

