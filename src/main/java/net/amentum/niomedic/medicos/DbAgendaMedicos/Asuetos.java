package net.amentum.niomedic.medicos.DbAgendaMedicos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "asuetos")
public class Asuetos implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asuetos")
    private Long idAsuetos;
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    private Boolean obligatorio;
}

