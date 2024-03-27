package net.amentum.niomedic.medicos.DbEventos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "catalogo_tipo_evento")
public class CatalogoTipoEvento2 implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_evento")
    private Integer idTipoEvento;
    @Size(max = 60)
    private String descripcion;
    @Size(max = 7)
    private String color;
}