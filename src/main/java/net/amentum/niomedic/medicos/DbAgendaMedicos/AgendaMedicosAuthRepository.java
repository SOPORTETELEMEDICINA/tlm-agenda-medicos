package net.amentum.niomedic.medicos.DbAgendaMedicos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface AgendaMedicosAuthRepository extends JpaRepository<AgendaMedicosAuth, Long>, JpaSpecificationExecutor<AgendaMedicosAuth> {
    AgendaMedicosAuth findByIdmedico(String idMedico) throws Exception;
}
