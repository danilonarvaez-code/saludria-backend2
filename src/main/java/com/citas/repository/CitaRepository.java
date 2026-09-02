package com.citas.repository;

import com.citas.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    // =========================================================
    // CITAS DE UN USUARIO/PACIENTE
    // =========================================================

    List<Cita> findByUsuarioId(Long usuarioId);

    // =========================================================
    // CITAS DE UN MÉDICO
    // =========================================================

    List<Cita> findByMedicoId(Long medicoId);
}