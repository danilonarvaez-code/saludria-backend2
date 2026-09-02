package com.citas.service;

import com.citas.entity.Cita;
import com.citas.entity.Medico;
import com.citas.repository.CitaRepository;
import com.citas.repository.MedicoRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;
    private final MedicoRepository medicoRepository;

    public CitaServiceImpl(
            CitaRepository citaRepository,
            MedicoRepository medicoRepository) {

        this.citaRepository = citaRepository;
        this.medicoRepository = medicoRepository;
    }

    // =========================================================
    // OBTENER TODAS
    // =========================================================

    @Override
    public List<Cita> obtenerTodas() {

        return citaRepository.findAll();
    }

    // =========================================================
    // OBTENER CITAS POR USUARIO
    // =========================================================

    @Override
    public List<Cita> obtenerCitasPorUsuario(Long usuarioId) {

        return citaRepository.findByUsuarioId(usuarioId);
    }

    // =========================================================
    // OBTENER CITAS POR MÉDICO
    // =========================================================

    @Override
    public List<Cita> obtenerCitasPorMedico(Long medicoId) {

        if (!medicoRepository.existsById(medicoId)) {

            throw new RuntimeException(
                    "No existe el médico con ID: " + medicoId
            );
        }

        return citaRepository.findByMedicoId(medicoId);
    }

    // =========================================================
    // GUARDAR CITA
    // =========================================================

    @Override
    public Cita guardar(Cita cita) {

        // -----------------------------------------------------
        // VALIDAR MÉDICO
        // -----------------------------------------------------

        if (cita.getMedico() != null &&
                cita.getMedico().getId() != null) {

            Medico medico =
                    medicoRepository.findById(
                            cita.getMedico().getId()
                    ).orElseThrow(() ->
                            new RuntimeException(
                                    "No existe el médico con ID: "
                                            + cita.getMedico().getId()
                            )
                    );

            cita.setMedico(medico);
        }

        return citaRepository.save(cita);
    }

    // =========================================================
    // ACTUALIZAR CITA
    // =========================================================

    @Override
    public Cita actualizar(
            Long id,
            Cita citaActualizada) {

        Cita cita =
                citaRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "No existe la cita con ID: "
                                                + id
                                )
                        );

        // -----------------------------------------------------
        // FECHA
        // -----------------------------------------------------

        if (citaActualizada.getFechaHora() != null) {

            cita.setFechaHora(
                    citaActualizada.getFechaHora()
            );
        }

        // -----------------------------------------------------
        // ESPECIALIDAD
        // -----------------------------------------------------

        if (citaActualizada.getEspecialidad() != null) {

            cita.setEspecialidad(
                    citaActualizada.getEspecialidad()
            );
        }

        // -----------------------------------------------------
        // ESTADO
        // -----------------------------------------------------

        if (citaActualizada.getEstado() != null) {

            cita.setEstado(
                    citaActualizada.getEstado()
            );
        }

        // -----------------------------------------------------
        // MÉDICO
        // -----------------------------------------------------

        if (citaActualizada.getMedico() != null &&
                citaActualizada.getMedico().getId() != null) {

            Medico medico =
                    medicoRepository.findById(
                            citaActualizada
                                    .getMedico()
                                    .getId()
                    ).orElseThrow(() ->
                            new RuntimeException(
                                    "No existe el médico con ID: "
                                            + citaActualizada
                                                    .getMedico()
                                                    .getId()
                            )
                    );

            cita.setMedico(medico);
        }

        // -----------------------------------------------------
        // GUARDAR
        // -----------------------------------------------------

        return citaRepository.save(cita);
    }

    // =========================================================
    // ELIMINAR
    // =========================================================

    @Override
    public void eliminar(Long id) {

        if (!citaRepository.existsById(id)) {

            throw new RuntimeException(
                    "No existe la cita con ID: " + id
            );
        }

        citaRepository.deleteById(id);
    }
}