package com.citas.security;

import com.citas.entity.Usuario;
import com.citas.repository.UsuarioRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UsuarioRepository usuarioRepository) {

        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader("Authorization");

        // ==========================================
        // NO HAY TOKEN
        // ==========================================

        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token =
                authorizationHeader.substring(7).trim();

        try {

            // ==========================================
            // OBTENER CORREO DEL TOKEN
            // ==========================================

            String correo =
                    jwtService.obtenerCorreo(token);

            if (correo == null ||
                    correo.isBlank()) {

                System.out.println(
                        "JWT ERROR -> No se pudo obtener el correo"
                );

                filterChain.doFilter(request, response);
                return;
            }

            correo = correo.trim();

            // ==========================================
            // BUSCAR USUARIO EN LA BASE DE DATOS
            // ==========================================

            Usuario usuario =
                    usuarioRepository
                            .findByCorreo(correo)
                            .orElse(null);

            if (usuario == null) {

                System.out.println(
                        "JWT ERROR -> Usuario no encontrado: "
                                + correo
                );

                filterChain.doFilter(request, response);
                return;
            }

            // ==========================================
            // NORMALIZAR ROL
            // ==========================================

            String rol = usuario.getRol();

            if (rol == null ||
                    rol.isBlank()) {

                rol = "PACIENTE";
            }

            rol = rol
                    .trim()
                    .toUpperCase()
                    .replace("ROLE_", "");

            // ==========================================
            // CREAR AUTORIDAD
            // ==========================================

            SimpleGrantedAuthority autoridad =
                    new SimpleGrantedAuthority(
                            "ROLE_" + rol
                    );

            // ==========================================
            // AUTENTICACIÓN
            //
            // IMPORTANTE:
            // El principal será el CORREO.
            // Así authentication.getName()
            // devuelve juan@sanber.com
            // ==========================================

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            correo,
                            null,
                            Collections.singletonList(
                                    autoridad
                            )
                    );

            // ==========================================
            // GUARDAR AUTENTICACIÓN
            // ==========================================

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(
                            authentication
                    );

            System.out.println(
                    "JWT OK -> Usuario: "
                            + correo
                            + " | Rol: ROLE_"
                            + rol
            );

        } catch (Exception e) {

            System.out.println(
                    "JWT inválido: "
                            + e.getMessage()
            );

            SecurityContextHolder
                    .clearContext();
        }

        filterChain.doFilter(
                request,
                response
        );
    }
}