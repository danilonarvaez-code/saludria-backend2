package com.citas.service;

import com.citas.entity.Cita;
import com.citas.entity.Medico;
import com.citas.entity.Usuario;

import com.citas.repository.CitaRepository;
import com.citas.repository.MedicoRepository;
import com.citas.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CitaServiceImpl implements CitaService {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Override
    public List<Cita> obtenerTodas() {
        return citaRepository.findAll();
    }

    @Override
    public Optional<Cita> obtenerPorId(Long id) {
        return citaRepository.findById(id);
    }

    @Override
    public List<Cita> obtenerCitasPorPaciente(
            Long usuarioId) {

        if (!usuarioRepository.existsById(usuarioId)) {
            throw new IllegalArgumentException(
                    "El usuario con ID "
                            + usuarioId
                            + " no existe."
            );
        }

        return citaRepository.findByUsuarioId(
                usuarioId
        );
    }
@Override
public List<Cita> obtenerCitasPorMedico(Long medicoId) {

    if (!medicoRepository.existsById(medicoId)) {
        throw new IllegalArgumentException(
                "El médico con ID "
                        + medicoId
                        + " no existe."
        );
    }

    return citaRepository.findByMedicoId(medicoId);
}
    @Override
    public Cita agendarCita(Cita cita) {

        if (cita.getUsuario() == null
                || cita.getUsuario().getId() == null) {

            throw new IllegalArgumentException(
                    "La cita debe estar asociada a un usuario con ID válido."
            );
        }

        if (cita.getMedico() == null
                || cita.getMedico().getId() == null) {

            throw new IllegalArgumentException(
                    "La cita debe estar asociada a un médico con ID válido."
            );
        }

        Usuario usuarioExistente =
                usuarioRepository.findById(
                        cita.getUsuario().getId()
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "El usuario con ID "
                                        + cita.getUsuario().getId()
                                        + " no existe."
                        )
                );

        Medico medicoExistente =
                medicoRepository.findById(
                        cita.getMedico().getId()
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "El médico con ID "
                                        + cita.getMedico().getId()
                                        + " no existe."
                        )
                );

        cita.setUsuario(usuarioExistente);

        cita.setMedico(medicoExistente);

        if (cita.getEstado() == null
                || cita.getEstado().isBlank()) {

            cita.setEstado("ASIGNADA");
        }

        return citaRepository.save(cita);
    }

    @Override
    public Cita actualizarCita(
            Long id,
            Cita citaActualizada) {

        Cita cita =
                citaRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "No se encontró la cita con ID: "
                                                + id
                                )
                        );

        if (citaActualizada.getFechaHora() != null) {
            cita.setFechaHora(
                    citaActualizada.getFechaHora()
            );
        }

        if (citaActualizada.getEspecialidad() != null
                && !citaActualizada
                .getEspecialidad()
                .isBlank()) {

            cita.setEspecialidad(
                    citaActualizada.getEspecialidad()
            );
        }

        if (citaActualizada.getEstado() != null
                && !citaActualizada
                .getEstado()
                .isBlank()) {

            cita.setEstado(
                    citaActualizada.getEstado()
            );
        }

        if (citaActualizada.getUsuario() != null
                && citaActualizada.getUsuario().getId() != null) {

            Usuario usuario =
                    usuarioRepository.findById(
                            citaActualizada
                                    .getUsuario()
                                    .getId()
                    ).orElseThrow(() ->
                            new IllegalArgumentException(
                                    "El usuario indicado no existe."
                            )
                    );

            cita.setUsuario(usuario);
        }

        if (citaActualizada.getMedico() != null
                && citaActualizada.getMedico().getId() != null) {

            Medico medico =
                    medicoRepository.findById(
                            citaActualizada
                                    .getMedico()
                                    .getId()
                    ).orElseThrow(() ->
                            new IllegalArgumentException(
                                    "El médico indicado no existe."
                            )
                    );

            cita.setMedico(medico);
        }

        return citaRepository.save(cita);
    }

    @Override
    public void eliminar(Long id) {

        if (!citaRepository.existsById(id)) {

            throw new IllegalArgumentException(
                    "No se encontró la cita con ID: " + id
            );
        }

        citaRepository.deleteById(id);
    }
}