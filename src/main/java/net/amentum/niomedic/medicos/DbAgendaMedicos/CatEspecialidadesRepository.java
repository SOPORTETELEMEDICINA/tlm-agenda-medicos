package net.amentum.niomedic.medicos.DbAgendaMedicos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CatEspecialidadesRepository extends JpaRepository<CatEspecialidades, Integer>, JpaSpecificationExecutor<CatEspecialidades> {
}
