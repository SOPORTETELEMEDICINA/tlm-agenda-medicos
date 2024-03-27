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
@Table(name = "especialidad")
public class EspecialidadAuth implements Serializable {
   @Id
   @GeneratedValue(generator = "UUID")
   @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
   private String idEspecialidad;
   private String especialidad;
   private String subespecialidad;
   private String universidad;
   private String cedula;
   private String nombreImagenCedula;
   private String nombreImagenDiploma;
   private String imgCedula64;
   private String imgTitulo64;
   private Boolean validado;
   @Temporal(TemporalType.TIMESTAMP)
   private Date fechaCreacion;
   private Date fechaValidacion;
//   relaciones
   @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
   @JoinColumn(name = "medico_id", referencedColumnName = "id_medico")
   private MedicoAuth medico;

   @Override
   public String toString() {
      return "Especialidad{" +
         "idEspecialidad='" + idEspecialidad + '\'' +
         ", especialidad='" + especialidad + '\'' +
         ", subespecialidad='" + subespecialidad + '\'' +
         ", universidad='" + universidad + '\'' +
         ", cedula='" + cedula + '\'' +
         ", imagenCedula='" + nombreImagenCedula + '\'' +
         ", imagenDiploma='" + nombreImagenDiploma + '\'' +
         ", validado=" + validado +
         ", fechaCreacion=" + fechaCreacion +
         ", fechaValidacion=" + fechaValidacion +
         '}';
   }
}




