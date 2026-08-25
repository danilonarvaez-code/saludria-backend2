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
import java.util.List;

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

        // No hay token
        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        // Extraer token
        String token = authorizationHeader.substring(7);

        try {

            // Validar JWT y obtener correo
            String correo = jwtService.obtenerCorreo(token);

            if (correo != null &&
                    SecurityContextHolder.getContext()
                            .getAuthentication() == null) {

                // Buscar usuario en la base de datos
                Usuario usuario = usuarioRepository
                        .findByCorreo(correo)
                        .orElse(null);

                if (usuario != null) {

                    /*
                     * El rol se toma directamente de la base de datos.
                     * Así evitamos inconsistencias entre el JWT y MySQL.
                     */
                    String rol = usuario.getRol();

                    if (rol != null) {

                        String rolNormalizado = rol.toUpperCase().replace("ROLE_", "");
                        String autoridad = "ROLE_" + rolNormalizado;

                        List<SimpleGrantedAuthority> authorities =
                                List.of(
                                        new SimpleGrantedAuthority(
                                                autoridad
                                        )
                                );

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        usuario,
                                        null,
                                        authorities
                                );

                        SecurityContextHolder
                                .getContext()
                                .setAuthentication(authentication);
                    }
                }
            }

        } catch (Exception e) {

            // JWT inválido, expirado o manipulado
            SecurityContextHolder
                    .clearContext();
        }

        filterChain.doFilter(request, response);
    }
}