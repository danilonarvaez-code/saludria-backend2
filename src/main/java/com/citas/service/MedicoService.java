package com.citas.service;

import com.citas.entity.Medico;
import com.citas.repository.MedicoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicoService {

    @Autowired
    private MedicoRepository medicoRepository;

    public List<Medico> obtenerTodos() {
        return medicoRepository.findAll();
    }

    public Optional<Medico> obtenerPorId(Long id) {
        return medicoRepository.findById(id);
    }

    public Medico guardar(Medico medico) {
        return medicoRepository.save(medico);
    }

    public Medico actualizar(
            Long id,
            Medico detallesMedico) {

        Medico medicoExistente =
                medicoRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Médico no encontrado con ID: " + id
                                )
                        );

        medicoExistente.setNombre(
                detallesMedico.getNombre()
        );

        medicoExistente.setEspecialidad(
                detallesMedico.getEspecialidad()
        );

        medicoExistente.setTarjetaProfesional(
                detallesMedico.getTarjetaProfesional()
        );

        medicoExistente.setEmail(
                detallesMedico.getEmail()
        );

        return medicoRepository.save(
                medicoExistente
        );
    }

    public void eliminar(Long id) {

        if (!medicoRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Médico no encontrado con ID: " + id
            );
        }

        medicoRepository.deleteById(id);
    }
}