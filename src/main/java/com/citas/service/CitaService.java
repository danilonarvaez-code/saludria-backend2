package com.citas.service;

import com.citas.entity.Cita;

import java.util.List;

public interface CitaService {

    // =========================================================
    // OBTENER TODAS LAS CITAS
    // =========================================================

    List<Cita> obtenerTodas();

    // =========================================================
    // OBTENER CITAS POR USUARIO
    // =========================================================

    List<Cita> obtenerCitasPorUsuario(Long usuarioId);

    // =========================================================
    // OBTENER CITAS POR MÉDICO
    // =========================================================

    List<Cita> obtenerCitasPorMedico(Long medicoId);

    // =========================================================
    // GUARDAR
    // =========================================================

    Cita guardar(Cita cita);

    // =========================================================
    // ACTUALIZAR
    // =========================================================

    Cita actualizar(Long id, Cita cita);

    // =========================================================
    // ELIMINAR
    // =========================================================

    void eliminar(Long id);
}