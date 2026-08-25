package com.citas.controller;

import com.citas.entity.Cita;
import com.citas.entity.Medico;
import com.citas.entity.Usuario;
import com.citas.repository.MedicoRepository;
import com.citas.service.CitaService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:3000"
})
public class CitaController {

    @Autowired
    private CitaService citaService;

    @Autowired
    private MedicoRepository medicoRepository;

    // =========================================================
    // ADMIN: consultar todas las citas
    // =========================================================
    @GetMapping
    public ResponseEntity<List<Cita>> listarTodas(
            Authentication authentication) {

        if (!esAdmin(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(citaService.obtenerTodas());
    }

    // =========================================================
    // Buscar una cita por ID
    // ADMIN puede consultar cualquier cita.
    // PACIENTE y MEDICO solo su propia cita.
    // =========================================================
    @GetMapping("/{id}")
    public ResponseEntity<Cita> buscarPorId(
            @PathVariable Long id,
            Authentication authentication) {

        Cita cita = citaService.obtenerPorId(id).orElse(null);

        if (cita == null) {
            return ResponseEntity.notFound().build();
        }

        if (esAdmin(authentication)) {
            return ResponseEntity.ok(cita);
        }

        if (esPaciente(authentication)) {

            if (cita.getUsuario() == null ||
                    !esPropioUsuario(authentication,
                            cita.getUsuario().getId())) {

                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            return ResponseEntity.ok(cita);
        }

        if (esMedico(authentication)) {

            if (cita.getMedico() == null ||
                    !esPropioMedico(authentication,
                            cita.getMedico().getId())) {

                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            return ResponseEntity.ok(cita);
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    // =========================================================
    // PACIENTE: consultar sus propias citas
    // =========================================================
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Cita>> obtenerPorPaciente(
            @PathVariable Long usuarioId,
            Authentication authentication) {

        if (!esPaciente(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (!esPropioUsuario(authentication, usuarioId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(
                citaService.obtenerCitasPorPaciente(usuarioId)
        );
    }

    // =========================================================
    // MEDICO: consultar únicamente sus citas
    // =========================================================
    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<List<Cita>> obtenerPorMedico(
            @PathVariable Long medicoId,
            Authentication authentication) {

        if (!esMedico(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (!esPropioMedico(authentication, medicoId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(
                citaService.obtenerCitasPorMedico(medicoId)
        );
    }

    // =========================================================
    // AGENDAR CITA
    // PACIENTE solo puede crear citas para sí mismo.
    // ADMIN puede crear citas.
    // =========================================================
    @PostMapping
    public ResponseEntity<Cita> agendarCita(
            @Valid @RequestBody Cita cita,
            Authentication authentication) {

        if (esPaciente(authentication)) {

            if (cita.getUsuario() == null ||
                    cita.getUsuario().getId() == null ||
                    !esPropioUsuario(
                            authentication,
                            cita.getUsuario().getId())) {

                return ResponseEntity.status(
                        HttpStatus.FORBIDDEN
                ).build();
            }
        } else if (!esAdmin(authentication)) {

            return ResponseEntity.status(
                    HttpStatus.FORBIDDEN
            ).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(citaService.agendarCita(cita));
    }

    // =========================================================
    // ACTUALIZAR CITA
    // =========================================================
    @PutMapping("/{id}")
    public ResponseEntity<Cita> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Cita citaDetalles,
            Authentication authentication) {

        Cita existente = citaService.obtenerPorId(id).orElse(null);

        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        // ADMIN puede modificar cualquier cita
        if (esAdmin(authentication)) {
            return ResponseEntity.ok(
                    citaService.actualizarCita(id, citaDetalles)
            );
        }

        // PACIENTE solo puede modificar sus propias citas
        if (esPaciente(authentication)) {

            if (existente.getUsuario() == null ||
                    !esPropioUsuario(
                            authentication,
                            existente.getUsuario().getId())) {

                return ResponseEntity.status(
                        HttpStatus.FORBIDDEN
                ).build();
            }

            if (citaDetalles.getUsuario() != null &&
                    citaDetalles.getUsuario().getId() != null &&
                    !esPropioUsuario(
                            authentication,
                            citaDetalles.getUsuario().getId())) {

                return ResponseEntity.status(
                        HttpStatus.FORBIDDEN
                ).build();
            }

            return ResponseEntity.ok(
                    citaService.actualizarCita(id, citaDetalles)
            );
        }

        // MEDICO solo puede modificar sus propias citas
        if (esMedico(authentication)) {

            if (existente.getMedico() == null ||
                    !esPropioMedico(
                            authentication,
                            existente.getMedico().getId())) {

                return ResponseEntity.status(
                        HttpStatus.FORBIDDEN
                ).build();
            }

            if (citaDetalles.getMedico() != null &&
                    citaDetalles.getMedico().getId() != null &&
                    !esPropioMedico(
                            authentication,
                            citaDetalles.getMedico().getId())) {

                return ResponseEntity.status(
                        HttpStatus.FORBIDDEN
                ).build();
            }

            return ResponseEntity.ok(
                    citaService.actualizarCita(id, citaDetalles)
            );
        }

        return ResponseEntity.status(
                HttpStatus.FORBIDDEN
        ).build();
    }

    // =========================================================
    // ELIMINAR CITA
    // =========================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            Authentication authentication) {

        Cita existente = citaService.obtenerPorId(id).orElse(null);

        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        // ADMIN puede eliminar cualquier cita
        if (esAdmin(authentication)) {
            citaService.eliminar(id);
            return ResponseEntity.noContent().build();
        }

        // PACIENTE solo puede eliminar sus propias citas
        if (esPaciente(authentication)) {

            if (existente.getUsuario() == null ||
                    !esPropioUsuario(
                            authentication,
                            existente.getUsuario().getId())) {

                return ResponseEntity.status(
                        HttpStatus.FORBIDDEN
                ).build();
            }

            citaService.eliminar(id);
            return ResponseEntity.noContent().build();
        }

        // MEDICO solo puede eliminar sus propias citas
        if (esMedico(authentication)) {

            if (existente.getMedico() == null ||
                    !esPropioMedico(
                            authentication,
                            existente.getMedico().getId())) {

                return ResponseEntity.status(
                        HttpStatus.FORBIDDEN
                ).build();
            }

            citaService.eliminar(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(
                HttpStatus.FORBIDDEN
        ).build();
    }

    // =========================================================
    // MÉTODOS DE SEGURIDAD
    // =========================================================

    private boolean esAdmin(Authentication authentication) {

        return authentication != null &&
                authentication.getAuthorities().stream()
                        .anyMatch(a ->
                                a.getAuthority()
                                        .equals("ROLE_ADMIN"));
    }

    private boolean esPaciente(Authentication authentication) {

        return authentication != null &&
                authentication.getAuthorities().stream()
                        .anyMatch(a ->
                                a.getAuthority()
                                        .equals("ROLE_PACIENTE"));
    }

    private boolean esMedico(Authentication authentication) {

        return authentication != null &&
                authentication.getAuthorities().stream()
                        .anyMatch(a ->
                                a.getAuthority()
                                        .equals("ROLE_MEDICO"));
    }

    private boolean esPropioUsuario(
            Authentication authentication,
            Long usuarioId) {

        if (authentication == null ||
                !(authentication.getPrincipal()
                        instanceof Usuario usuario)) {

            return false;
        }

        return usuario.getId() != null &&
                usuario.getId().equals(usuarioId);
    }

    private boolean esPropioMedico(
            Authentication authentication,
            Long medicoId) {

        if (authentication == null ||
                authentication.getName() == null) {

            return false;
        }

        Medico medico = medicoRepository
                .findByEmailIgnoreCase(
                        authentication.getName()
                )
                .orElse(null);

        return medico != null &&
                medico.getId().equals(medicoId);
    }
}