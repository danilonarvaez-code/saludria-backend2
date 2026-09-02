package com.citas.controller;

import com.citas.entity.Cita;
import com.citas.entity.Medico;
import com.citas.entity.Usuario;
import com.citas.repository.MedicoRepository;
import com.citas.repository.UsuarioRepository;
import com.citas.service.CitaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
@CrossOrigin(origins = "*")
public class CitaController {

    private final CitaService citaService;
    private final MedicoRepository medicoRepository;
    private final UsuarioRepository usuarioRepository;

    public CitaController(
            CitaService citaService,
            MedicoRepository medicoRepository,
            UsuarioRepository usuarioRepository) {

        this.citaService = citaService;
        this.medicoRepository = medicoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // ============================================================
    // GET TODAS LAS CITAS SEGÚN EL ROL
    // ============================================================

    @GetMapping
    public ResponseEntity<?> obtenerCitas(Authentication authentication) {

        if (!autenticado(authentication)) {
            return forbidden("No autenticado.");
        }

        String rol = obtenerRol(authentication);

        // ADMIN: puede ver todas
        if ("ROLE_ADMIN".equals(rol)) {
            return ResponseEntity.ok(citaService.obtenerTodas());
        }

        // PACIENTE: solamente sus propias citas
        if ("ROLE_PACIENTE".equals(rol)) {

            Usuario usuario = buscarUsuario(authentication);

            if (usuario == null) {
                return forbidden("No se encontró el usuario autenticado.");
            }

            return ResponseEntity.ok(
                    citaService.obtenerCitasPorUsuario(usuario.getId())
            );
        }

        // MÉDICO: solamente sus citas asignadas
        if ("ROLE_MEDICO".equals(rol)) {

            Medico medico = buscarMedico(authentication);

            if (medico == null) {
                return forbidden("No se encontró médico asociado al usuario.");
            }

            System.out.println(
                    "Médico autenticado: ID=" + medico.getId()
                            + " | Nombre=" + medico.getNombre()
            );

            return ResponseEntity.ok(
                    citaService.obtenerCitasPorMedico(medico.getId())
            );
        }

        return forbidden("Rol no autorizado.");
    }

    // ============================================================
    // CITAS DE UN USUARIO
    // ============================================================

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> obtenerPorUsuario(
            @PathVariable Long usuarioId,
            Authentication authentication) {

        if (!autenticado(authentication)) {
            return forbidden("No autenticado.");
        }

        String rol = obtenerRol(authentication);

        // ADMIN puede consultar cualquier paciente
        if ("ROLE_ADMIN".equals(rol)) {
            return ResponseEntity.ok(
                    citaService.obtenerCitasPorUsuario(usuarioId)
            );
        }

        // PACIENTE solamente puede consultar sus propias citas
        if ("ROLE_PACIENTE".equals(rol)) {

            Usuario usuario = buscarUsuario(authentication);

            if (usuario != null &&
                    usuario.getId().equals(usuarioId)) {

                return ResponseEntity.ok(
                        citaService.obtenerCitasPorUsuario(usuarioId)
                );
            }

            return forbidden(
                    "No puede consultar citas de otro paciente."
            );
        }

        return forbidden("No autorizado.");
    }

    // ============================================================
    // CITAS DE UN MÉDICO
    // ============================================================

    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<?> obtenerPorMedico(
            @PathVariable Long medicoId,
            Authentication authentication) {

        if (!autenticado(authentication)) {
            return forbidden("No autenticado.");
        }

        String rol = obtenerRol(authentication);

        // ADMIN puede consultar cualquier médico
        if ("ROLE_ADMIN".equals(rol)) {

            return ResponseEntity.ok(
                    citaService.obtenerCitasPorMedico(medicoId)
            );
        }

        // MÉDICO solamente puede consultar sus propias citas
        if ("ROLE_MEDICO".equals(rol)) {

            Medico medico = buscarMedico(authentication);

            if (medico == null) {
                return forbidden(
                        "No se encontró médico asociado al usuario."
                );
            }

            if (!medico.getId().equals(medicoId)) {
                return forbidden(
                        "No puede consultar citas de otro médico."
                );
            }

            return ResponseEntity.ok(
                    citaService.obtenerCitasPorMedico(medicoId)
            );
        }

        return forbidden("No autorizado.");
    }

    // ============================================================
    // CREAR CITA
    // ============================================================

    @PostMapping
    public ResponseEntity<?> crear(
            @RequestBody Cita cita,
            Authentication authentication) {

        if (!autenticado(authentication)) {
            return forbidden("No autenticado.");
        }

        String rol = obtenerRol(authentication);

        // --------------------------------------------------------
        // ADMIN
        // --------------------------------------------------------

        if ("ROLE_ADMIN".equals(rol)) {

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(citaService.guardar(cita));
        }

        // --------------------------------------------------------
        // PACIENTE
        // --------------------------------------------------------

        if ("ROLE_PACIENTE".equals(rol)) {

            Usuario usuario = buscarUsuario(authentication);

            System.out.println(
                    "=== CREANDO CITA COMO PACIENTE ==="
            );

            System.out.println(
                    "Authentication name: "
                            + authentication.getName()
            );

            System.out.println(
                    "Rol: " + rol
            );

            if (usuario != null) {

                System.out.println(
                        "Usuario autenticado encontrado: ID="
                                + usuario.getId()
                                + " | Correo="
                                + usuario.getCorreo()
                );

            } else {

                System.out.println(
                        "Usuario autenticado encontrado: NULL"
                );
            }

            // Validar usuario autenticado
            if (usuario == null) {

                return ResponseEntity
                        .badRequest()
                        .body("Usuario autenticado no válido.");
            }

            // Validar usuario enviado
            if (cita == null ||
                    cita.getUsuario() == null ||
                    cita.getUsuario().getId() == null) {

                return ResponseEntity
                        .badRequest()
                        .body("Debe indicar el usuario de la cita.");
            }

            // El paciente solamente puede crear citas para sí mismo
            if (!usuario.getId().equals(
                    cita.getUsuario().getId())) {

                return forbidden(
                        "No puede crear una cita para otro paciente."
                );
            }

            // Validar médico
            if (cita.getMedico() == null ||
                    cita.getMedico().getId() == null) {

                return ResponseEntity
                        .badRequest()
                        .body("Debe indicar un médico.");
            }

            Long medicoId = cita.getMedico().getId();

            if (!medicoRepository.existsById(medicoId)) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "El médico con ID "
                                        + medicoId
                                        + " no existe."
                        );
            }

            System.out.println(
                    "Médico seleccionado: ID=" + medicoId
            );

            // Crear cita
            Cita citaGuardada = citaService.guardar(cita);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(citaGuardada);
        }

        // --------------------------------------------------------
        // MÉDICO
        // --------------------------------------------------------

        if ("ROLE_MEDICO".equals(rol)) {

            return forbidden(
                    "El médico no puede registrar ni agendar citas."
            );
        }

        return forbidden("Rol no autorizado.");
    }

    // ============================================================
    // ACTUALIZAR CITA
    // SOLO ADMIN
    // ============================================================

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestBody Cita cita,
            Authentication authentication) {

        if (!autenticado(authentication)) {
            return forbidden("No autenticado.");
        }

        if (!"ROLE_ADMIN".equals(obtenerRol(authentication))) {

            return forbidden(
                    "Solo el administrador puede modificar citas."
            );
        }

        return ResponseEntity.ok(
                citaService.actualizar(id, cita)
        );
    }

    // ============================================================
    // ELIMINAR CITA
    // SOLO ADMIN
    // ============================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @PathVariable Long id,
            Authentication authentication) {

        if (!autenticado(authentication)) {
            return forbidden("No autenticado.");
        }

        if (!"ROLE_ADMIN".equals(obtenerRol(authentication))) {

            return forbidden(
                    "Solo el administrador puede eliminar citas."
            );
        }

        citaService.eliminar(id);

        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // BUSCAR USUARIO AUTENTICADO
    // ============================================================

    private Usuario buscarUsuario(Authentication authentication) {

        if (authentication == null) {
            return null;
        }

        String correo = authentication.getName();

        if (correo == null || correo.trim().isEmpty()) {
            return null;
        }

        correo = correo.trim();

        System.out.println(
                "Buscando usuario por correo: " + correo
        );

        Usuario usuario = usuarioRepository
                .findByCorreo(correo)
                .orElse(null);

        if (usuario != null) {

            System.out.println(
                    "Usuario encontrado: ID="
                            + usuario.getId()
                            + " | Nombre="
                            + usuario.getNombre()
                            + " | Email="
                            + usuario.getCorreo()
            );

        } else {

            System.out.println(
                    "Usuario NO encontrado para correo: "
                            + correo
            );
        }

        return usuario;
    }

    // ============================================================
    // BUSCAR MÉDICO DEL USUARIO AUTENTICADO
    // ============================================================

    private Medico buscarMedico(Authentication authentication) {

        if (authentication == null) {
            return null;
        }

        String correo = authentication.getName();

        if (correo == null || correo.trim().isEmpty()) {
            return null;
        }

        correo = correo.trim();

        System.out.println(
                "Buscando médico por correo: " + correo
        );

        Medico medico = medicoRepository
                .findByEmailIgnoreCase(correo)
                .orElse(null);

        if (medico != null) {

            System.out.println(
                    "Médico encontrado: ID="
                            + medico.getId()
                            + " | Nombre="
                            + medico.getNombre()
                            + " | Email="
                            + medico.getEmail()
            );
        }

        return medico;
    }

    // ============================================================
    // OBTENER ROL
    // ============================================================

    private String obtenerRol(Authentication authentication) {

        if (authentication == null) {
            return "";
        }

        for (GrantedAuthority authority :
                authentication.getAuthorities()) {

            return authority
                    .getAuthority()
                    .trim()
                    .toUpperCase();
        }

        return "";
    }

    // ============================================================
    // VALIDAR AUTENTICACIÓN
    // ============================================================

    private boolean autenticado(Authentication authentication) {

        return authentication != null
                && authentication.isAuthenticated();
    }

    // ============================================================
    // RESPUESTA 403
    // ============================================================

    private ResponseEntity<String> forbidden(String mensaje) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(mensaje);
    }
}