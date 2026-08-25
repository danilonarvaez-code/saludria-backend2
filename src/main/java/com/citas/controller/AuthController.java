package com.citas.controller;

import com.citas.entity.Usuario;
import com.citas.security.JwtService;
import com.citas.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(
            AuthService authService,
            JwtService jwtService) {

        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        Usuario usuario = authService.autenticar(
                request.getCorreo(),
                request.getPassword()
        );

        if (usuario == null) {

            Map<String, String> respuesta = new HashMap<>();

            respuesta.put(
                    "mensaje",
                    "Correo o contraseña incorrectos"
            );

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(respuesta);
        }

        String rol = usuario.getRol() == null
                ? "PACIENTE"
                : usuario.getRol().trim().toUpperCase().replace("ROLE_", "");

        // Generar JWT con rol normalizado.
        String token = jwtService.generarToken(
                usuario.getCorreo(),
                rol
        );

        Map<String, Object> respuesta = new HashMap<>();

        respuesta.put(
                "mensaje",
                "Inicio de sesión exitoso"
        );

        respuesta.put(
                "token",
                token
        );

        respuesta.put(
                "id",
                usuario.getId()
        );

        respuesta.put(
                "nombre",
                usuario.getNombre()
        );

        respuesta.put(
                "correo",
                usuario.getCorreo()
        );

        respuesta.put(
                "rol",
                rol
        );

        return ResponseEntity.ok(respuesta);
    }

    public static class LoginRequest {

        private String correo;
        private String password;

        public LoginRequest() {
        }

        public String getCorreo() {
            return correo;
        }

        public void setCorreo(String correo) {
            this.correo = correo;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}