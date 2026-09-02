package com.citas.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/error").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()

                // Médicos: todos pueden consultar; solamente ADMIN administra médicos.
                .requestMatchers(HttpMethod.GET, "/api/medicos", "/api/medicos/**")
                    .hasAnyRole("ADMIN", "MEDICO", "PACIENTE")
                .requestMatchers(HttpMethod.POST, "/api/medicos", "/api/medicos/**")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/medicos/**")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/medicos/**")
                    .hasRole("ADMIN")

                // Usuarios: administración exclusiva del ADMIN.
                .requestMatchers(HttpMethod.GET, "/api/usuarios", "/api/usuarios/**")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/usuarios/admin", "/api/usuarios/admin/**")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/usuarios/**")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**")
                    .hasRole("ADMIN")

                // Citas por propietario.
                .requestMatchers(HttpMethod.GET, "/api/citas/usuario/**")
                    .hasAnyRole("PACIENTE", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/citas/medico/**")
                    .hasAnyRole("MEDICO", "ADMIN")

                // GET /api/citas se filtra por rol en CitaController.
                .requestMatchers(HttpMethod.GET, "/api/citas", "/api/citas/**")
                    .hasAnyRole("ADMIN", "MEDICO", "PACIENTE")

                // Solamente PACIENTE y ADMIN pueden agendar.
                .requestMatchers(HttpMethod.POST, "/api/citas", "/api/citas/**")
                    .hasAnyRole("ADMIN", "PACIENTE")

                // Solamente ADMIN puede modificar/eliminar citas.
                .requestMatchers(HttpMethod.PUT, "/api/citas/**")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/citas/**")
                    .hasRole("ADMIN")

                // Detalles: GET para consultar según propietario; POST para médico/ADMIN;
                // PUT/DELETE exclusivamente ADMIN.
                .requestMatchers(HttpMethod.GET, "/api/detalle-citas", "/api/detalle-citas/**")
                    .hasAnyRole("ADMIN", "MEDICO", "PACIENTE")
                .requestMatchers(HttpMethod.POST, "/api/detalle-citas", "/api/detalle-citas/**")
                    .hasAnyRole("ADMIN", "MEDICO")
                .requestMatchers(HttpMethod.PUT, "/api/detalle-citas/**")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/detalle-citas/**")
                    .hasRole("ADMIN")

                .requestMatchers("/api/colombia/**").authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
