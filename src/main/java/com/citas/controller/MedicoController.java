package com.citas.controller;

import com.citas.entity.Medico;
import com.citas.service.MedicoService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicos")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:3000"
})
public class MedicoController {

    @Autowired
    private MedicoService medicoService;

    @GetMapping
    public ResponseEntity<List<Medico>> obtenerTodos() {
        return ResponseEntity.ok(
                medicoService.obtenerTodos()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Medico> obtenerPorId(
            @PathVariable Long id) {

        return medicoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }

    @PostMapping
    public ResponseEntity<Medico> guardarMedico(
            @Valid @RequestBody Medico medico) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(medicoService.guardar(medico));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Medico> actualizarMedico(
            @PathVariable Long id,
            @Valid @RequestBody Medico detallesMedico) {

        return ResponseEntity.ok(
                medicoService.actualizar(
                        id,
                        detallesMedico
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMedico(
            @PathVariable Long id) {

        medicoService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}