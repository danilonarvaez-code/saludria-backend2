package com.citas.repository;

import com.citas.entity.DetalleCita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleCitaRepository
        extends JpaRepository<DetalleCita, Long> {

    List<DetalleCita> findByCitaUsuarioId(Long usuarioId);

    List<DetalleCita> findByCitaMedicoId(Long medicoId);
}