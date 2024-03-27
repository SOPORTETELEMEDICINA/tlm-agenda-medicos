package net.amentum.niomedic.medicos.DbAgendaMedicos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;

@Repository
public interface TurnosLaboralesRepository extends JpaRepository<TurnosLaborales, Long>, JpaSpecificationExecutor<TurnosLaborales> {

    //   TurnosLaborales findByMedicoId(@NotNull UUID medicoId);
    TurnosLaborales findByIdUsuario(@NotNull Long idUsuario);
}
