package com.citas.service;

import com.citas.entity.DetalleCita;
import com.citas.repository.DetalleCitaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DetalleCitaServiceImpl implements DetalleCitaService {

    private final DetalleCitaRepository detalleCitaRepository;

    public DetalleCitaServiceImpl(
            DetalleCitaRepository detalleCitaRepository) {
        this.detalleCitaRepository = detalleCitaRepository;
    }

    @Override
    public List<DetalleCita> listarTodos() {
        return detalleCitaRepository.findAll();
    }

    @Override
    public Optional<DetalleCita> buscarPorId(Long id) {
        return detalleCitaRepository.findById(id);
    }

    @Override
    public List<DetalleCita> listarPorUsuario(Long usuarioId) {
        return detalleCitaRepository.findByCitaUsuarioId(usuarioId);
    }

    @Override
    public List<DetalleCita> listarPorMedico(Long medicoId) {
        return detalleCitaRepository.findByCitaMedicoId(medicoId);
    }

    @Override
    public DetalleCita guardar(DetalleCita detalleCita) {
        return detalleCitaRepository.save(detalleCita);
    }

    @Override
    public DetalleCita actualizar(
            Long id,
            DetalleCita detalleCita) {

        Optional<DetalleCita> existente =
                detalleCitaRepository.findById(id);

        if (existente.isEmpty()) {
            throw new RuntimeException(
                    "No se encontró el detalle de cita con ID: " + id
            );
        }

        DetalleCita actual = existente.get();

        actual.setCita(detalleCita.getCita());
        actual.setMotivoConsulta(detalleCita.getMotivoConsulta());
        actual.setObservaciones(detalleCita.getObservaciones());
        actual.setDiagnostico(detalleCita.getDiagnostico());
        actual.setTratamiento(detalleCita.getTratamiento());
        actual.setEstadoAtencion(detalleCita.getEstadoAtencion());

        return detalleCitaRepository.save(actual);
    }

    @Override
    public void eliminar(Long id) {
        if (!detalleCitaRepository.existsById(id)) {
            throw new RuntimeException(
                    "No se encontró el detalle de cita con ID: " + id
            );
        }

        detalleCitaRepository.deleteById(id);
    }
}