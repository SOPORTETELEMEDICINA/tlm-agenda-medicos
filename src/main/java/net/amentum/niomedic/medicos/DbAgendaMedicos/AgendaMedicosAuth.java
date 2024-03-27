package net.amentum.niomedic.medicos.DbAgendaMedicos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "agenda_medicos")
public class AgendaMedicosAuth implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idagenda")
    private Integer idagenda;

    @Column(name = "fechaingresoinst")
    private Date fechaingresoinst;

    @Column(name = "idmedico")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String idmedico;

    @Column(name = "meddom")
    private Boolean meddom;

    @Column(name = "medlun")
    private Boolean medlun;

    @Column(name = "medmar")
    private Boolean medmar;

    @Column(name = "medmie")
    private Boolean medmie;

    @Column(name = "medjue")
    private Boolean medjue;

    @Column(name = "medvie")
    private Boolean medvie;

    @Column(name = "medsab")
    private Boolean medsab;

    private Time lunEntMat;
    private Time lunSalMat;
    private Time lunEntVesp;
    private Time lunSalVesp;
    private Time lunEntNoct;
    private Time lunSalNoct;
    private Time marEntMat;
    private Time marSalMat;
    private Time marEntVesp;
    private Time marSalVesp;
    private Time marEntNoct;
    private Time marSalNoct;
    private Time mieEntMat;
    private Time mieSalMat;
    private Time mieEntVesp;
    private Time mieSalVesp;
    private Time mieEntNoct;
    private Time mieSalNoct;
    private Time jueEntMat;
    private Time jueSalMat;
    private Time jueEntVesp;
    private Time jueSalVesp;
    private Time jueEntNoct;
    private Time jueSalNoct;
    private Time vieEntMat;
    private Time vieSalMat;
    private Time vieEntVesp;
    private Time vieSalVesp;
    private Time vieEntNoct;
    private Time vieSalNoct;
    private Time sabEntMat;
    private Time sabSalMat;
    private Time sabEntVesp;
    private Time sabSalVesp;
    private Time sabEntNoct;
    private Time sabSalNoct;
    private Time domEntMat;
    private Time domSalMat;
    private Time domEntVesp;
    private Time domSalVesp;
    private Time domEntNoct;
    private Time domSalNoct;

}

