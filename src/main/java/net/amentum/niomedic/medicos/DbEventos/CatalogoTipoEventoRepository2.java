package net.amentum.niomedic.medicos.DbEventos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;

@Repository
public interface CatalogoTipoEventoRepository2 extends JpaRepository<CatalogoTipoEvento2, Integer>, JpaSpecificationExecutor<CatalogoTipoEvento2> {

    CatalogoTipoEvento2 findByDescripcion(@NotNull String descripcion);

}