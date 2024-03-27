package net.amentum.niomedic.medicos.DbAgendaMedicos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;

@Repository
public interface CatalogoTipoEventoRepository extends JpaRepository<CatalogoTipoEvento, Integer>, JpaSpecificationExecutor<CatalogoTipoEvento> {

    CatalogoTipoEvento findByDescripcion(@NotNull String descripcion);

}