package com.citas.service;

import com.citas.entity.DetalleCita;
import com.citas.repository.DetalleCitaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DetalleCitaServiceImpl implements DetalleCitaService {

    private final DetalleCitaRepository detalleCitaRepository;

    public DetalleCitaServiceImpl(DetalleCitaRepository detalleCitaRepository) {
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
    public DetalleCita guardar(DetalleCita detalleCita) {
        return detalleCitaRepository.save(detalleCita);
    }

    @Override
    public DetalleCita actualizar(Long id, DetalleCita detalleCita) {

        Optional<DetalleCita> existente =
                detalleCitaRepository.findById(id);

        if (existente.isPresent()) {

            DetalleCita detalleActual = existente.get();

            detalleActual.setCita(detalleCita.getCita());
            detalleActual.setMotivoConsulta(detalleCita.getMotivoConsulta());
            detalleActual.setObservaciones(detalleCita.getObservaciones());
            detalleActual.setDiagnostico(detalleCita.getDiagnostico());
            detalleActual.setTratamiento(detalleCita.getTratamiento());
            detalleActual.setEstadoAtencion(detalleCita.getEstadoAtencion());

            return detalleCitaRepository.save(detalleActual);
        }

        return null;
    }

    @Override
    public void eliminar(Long id) {
        detalleCitaRepository.deleteById(id);
    }
}