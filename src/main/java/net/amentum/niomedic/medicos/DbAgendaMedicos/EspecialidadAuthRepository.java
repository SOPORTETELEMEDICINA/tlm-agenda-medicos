package net.amentum.niomedic.medicos.DbAgendaMedicos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EspecialidadAuthRepository extends JpaRepository<EspecialidadAuth, String>, JpaSpecificationExecutor<EspecialidadAuth> {
}
