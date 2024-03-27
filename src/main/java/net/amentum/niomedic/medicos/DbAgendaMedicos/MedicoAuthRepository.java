package net.amentum.niomedic.medicos.DbAgendaMedicos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;

@Repository
public interface MedicoAuthRepository extends JpaSpecificationExecutor<MedicoAuth>, JpaRepository<MedicoAuth, String> {
   MedicoAuth findByIdMedico(@NotNull String idMedico)  throws Exception;
}
