package net.amentum.niomedic.medicos.DbAgendaMedicos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;

@Repository
public interface VacacionesRepository extends JpaRepository<Vacaciones, Long>, JpaSpecificationExecutor<Vacaciones> {

    List<Vacaciones> findByIdUsuario(@NotNull Long idUsuario);
}

