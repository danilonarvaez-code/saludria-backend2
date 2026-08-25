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
                // Registro público: solo pacientes; el servicio fuerza el rol PACIENTE.
                .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                // Consultar médicos es necesario para agendar una cita.
                .requestMatchers(HttpMethod.GET, "/api/medicos", "/api/medicos/**")
                    .hasAnyRole("ADMIN", "MEDICO", "PACIENTE")
                // Administración de médicos.
                .requestMatchers("/api/medicos/**")
                    .hasRole("ADMIN")
                // Administración de usuarios.
                .requestMatchers("/api/usuarios/**")
                    .hasRole("ADMIN")
                // Citas: el controlador valida además que el paciente solo opere sus propias citas.
                .requestMatchers(HttpMethod.GET, "/api/citas/usuario/**")
                    .hasAnyRole("ADMIN", "MEDICO", "PACIENTE")
                .requestMatchers(HttpMethod.GET, "/api/citas/**")
                    .hasAnyRole("ADMIN", "MEDICO")
                .requestMatchers(HttpMethod.POST, "/api/citas/**")
                    .hasAnyRole("ADMIN", "MEDICO", "PACIENTE")
                .requestMatchers(HttpMethod.PUT, "/api/citas/**")
                    .hasAnyRole("ADMIN", "MEDICO", "PACIENTE")
                .requestMatchers(HttpMethod.DELETE, "/api/citas/**")
                    .hasAnyRole("ADMIN", "MEDICO", "PACIENTE")
                // Detalle y reportes.
                .requestMatchers(HttpMethod.GET, "/api/detalle-citas/**")
                    .hasAnyRole("ADMIN", "MEDICO")
                .requestMatchers("/api/detalle-citas/**")
                    .hasAnyRole("ADMIN", "MEDICO")
                // API pública de Colombia puede ser consultada por usuarios autenticados.
                .requestMatchers("/api/colombia/**").authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
