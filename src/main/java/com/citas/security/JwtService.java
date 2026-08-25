package com.citas.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET_KEY =
            "SaludriaSecretKey2026ProyectoCitasMedicasSeguridad";

    private final SecretKey secretKey =
            Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    private final long expiration = 1000 * 60 * 60; // 1 hora

    /**
     * Genera un token JWT con correo y rol.
     */
    public String generarToken(String correo, String rol) {

        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expiration);

        return Jwts.builder()
                .subject(correo)
                .claim("rol", rol == null ? "PACIENTE" : rol.toUpperCase().replace("ROLE_", ""))
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Obtiene el correo almacenado dentro del JWT.
     */
    public String obtenerCorreo(String token) {

        Claims claims = obtenerClaims(token);

        return claims.getSubject();
    }

    /**
     * Obtiene el rol almacenado dentro del JWT.
     */
    public String obtenerRol(String token) {

        Claims claims = obtenerClaims(token);

        return claims.get("rol", String.class);
    }

    /**
     * Valida el token y obtiene sus datos.
     */
    private Claims obtenerClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}