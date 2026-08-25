package com.citas.service;

import com.citas.entity.Cita;

import java.util.List;
import java.util.Optional;

public interface CitaService {

    List<Cita> obtenerTodas();

    Optional<Cita> obtenerPorId(Long id);

    List<Cita> obtenerCitasPorPaciente(Long usuarioId);

    List<Cita> obtenerCitasPorMedico(Long medicoId);

    Cita agendarCita(Cita cita);

    Cita actualizarCita(Long id, Cita citaActualizada);

    void eliminar(Long id);
}