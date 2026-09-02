package com.citas.controller;

import com.citas.entity.Cita;
import com.citas.entity.DetalleCita;
import com.citas.entity.Medico;
import com.citas.entity.Usuario;
import com.citas.repository.CitaRepository;
import com.citas.repository.MedicoRepository;
import com.citas.repository.UsuarioRepository;
import com.citas.service.DetalleCitaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detalle-citas")
@CrossOrigin(origins = "*")
public class DetalleCitaController {

    private final DetalleCitaService detalleService;
    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MedicoRepository medicoRepository;

    public DetalleCitaController(DetalleCitaService detalleService,
                                 CitaRepository citaRepository,
                                 UsuarioRepository usuarioRepository,
                                 MedicoRepository medicoRepository) {
        this.detalleService = detalleService;
        this.citaRepository = citaRepository;
        this.usuarioRepository = usuarioRepository;
        this.medicoRepository = medicoRepository;
    }

    @GetMapping
    public ResponseEntity<?> listar(Authentication authentication) {
        if (!autenticado(authentication)) return forbidden("No autenticado.");
        String rol = obtenerRol(authentication);

        if ("ROLE_ADMIN".equals(rol)) {
            return ResponseEntity.ok(detalleService.listarTodos());
        }

        if ("ROLE_PACIENTE".equals(rol)) {
            Usuario usuario = usuarioRepository.findByCorreo(authentication.getName()).orElse(null);
            if (usuario == null) return forbidden("Usuario no encontrado.");
            return ResponseEntity.ok(detalleService.listarPorUsuario(usuario.getId()));
        }

        if ("ROLE_MEDICO".equals(rol)) {
            Medico medico = medicoRepository.findByEmailIgnoreCase(authentication.getName()).orElse(null);
            if (medico == null) return forbidden("No se encontró médico asociado al usuario.");
            return ResponseEntity.ok(detalleService.listarPorMedico(medico.getId()));
        }

        return forbidden("Rol no autorizado.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id, Authentication authentication) {
        if (!autenticado(authentication)) return forbidden("No autenticado.");
        DetalleCita detalle = detalleService.buscarPorId(id).orElse(null);
        if (detalle == null) return ResponseEntity.notFound().build();

        if (puedeVerDetalle(detalle, authentication)) return ResponseEntity.ok(detalle);
        return forbidden("No tiene permiso para consultar este registro.");
    }

    @GetMapping("/reporte/{id}")
    public ResponseEntity<?> reporte(@PathVariable Long id, Authentication authentication) {
        return buscarPorId(id, authentication);
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody DetalleCita detalle, Authentication authentication) {
        if (!autenticado(authentication)) return forbidden("No autenticado.");
        String rol = obtenerRol(authentication);

        if (!("ROLE_ADMIN".equals(rol) || "ROLE_MEDICO".equals(rol))) {
            return forbidden("El paciente no puede registrar atención médica.");
        }

        Cita cita = obtenerCita(detalle);
        if (cita == null) return ResponseEntity.badRequest().body("Debe indicar una cita válida.");

        if ("ROLE_MEDICO".equals(rol)) {
            Medico medico = medicoRepository.findByEmailIgnoreCase(authentication.getName()).orElse(null);
            if (medico == null || cita.getMedico() == null || !medico.getId().equals(cita.getMedico().getId())) {
                return forbidden("El médico solo puede registrar atención de sus propias citas.");
            }
        }

        detalle.setCita(cita);
        return ResponseEntity.status(HttpStatus.CREATED).body(detalleService.guardar(detalle));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @RequestBody DetalleCita detalle,
                                        Authentication authentication) {
        if (!autenticado(authentication)) return forbidden("No autenticado.");
        if (!"ROLE_ADMIN".equals(obtenerRol(authentication))) {
            return forbidden("Solo el administrador puede modificar un registro de atención.");
        }

        DetalleCita actualizado = detalleService.actualizar(id, detalle);
        if (actualizado == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, Authentication authentication) {
        if (!autenticado(authentication)) return forbidden("No autenticado.");
        if (!"ROLE_ADMIN".equals(obtenerRol(authentication))) {
            return forbidden("Solo el administrador puede eliminar un registro de atención.");
        }
        if (detalleService.buscarPorId(id).isEmpty()) return ResponseEntity.notFound().build();
        detalleService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private Cita obtenerCita(DetalleCita detalle) {
        if (detalle == null || detalle.getCita() == null || detalle.getCita().getId() == null) return null;
        return citaRepository.findById(detalle.getCita().getId()).orElse(null);
    }

    private boolean puedeVerDetalle(DetalleCita detalle, Authentication authentication) {
        String rol = obtenerRol(authentication);
        if ("ROLE_ADMIN".equals(rol)) return true;
        if (detalle.getCita() == null) return false;

        if ("ROLE_PACIENTE".equals(rol)) {
            Usuario usuario = usuarioRepository.findByCorreo(authentication.getName()).orElse(null);
            return usuario != null && detalle.getCita().getUsuario() != null
                    && usuario.getId().equals(detalle.getCita().getUsuario().getId());
        }

        if ("ROLE_MEDICO".equals(rol)) {
            Medico medico = medicoRepository.findByEmailIgnoreCase(authentication.getName()).orElse(null);
            return medico != null && detalle.getCita().getMedico() != null
                    && medico.getId().equals(detalle.getCita().getMedico().getId());
        }
        return false;
    }

    private String obtenerRol(Authentication authentication) {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            return authority.getAuthority().toUpperCase();
        }
        return "";
    }

    private boolean autenticado(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated();
    }

    private ResponseEntity<String> forbidden(String mensaje) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensaje);
    }
}
