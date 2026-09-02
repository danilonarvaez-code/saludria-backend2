package com.citas.service;

import com.citas.entity.DetalleCita;

import java.util.List;
import java.util.Optional;

public interface DetalleCitaService {

    List<DetalleCita> listarTodos();

    Optional<DetalleCita> buscarPorId(Long id);

    List<DetalleCita> listarPorUsuario(Long usuarioId);

    List<DetalleCita> listarPorMedico(Long medicoId);

    DetalleCita guardar(DetalleCita detalleCita);

    DetalleCita actualizar(
            Long id,
            DetalleCita detalleCita);

    void eliminar(Long id);
}