package com.citas.service;

import com.citas.entity.Usuario;
import com.citas.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Usuario guardar(Usuario usuario) {

        if (usuario.getPassword() == null
                || usuario.getPassword().isBlank()) {

            throw new IllegalArgumentException(
                    "La contraseña es obligatoria al registrar un usuario."
            );
        }

        usuarioRepository.findByCorreo(
                usuario.getCorreo()
        ).ifPresent(existente -> {
            throw new IllegalArgumentException(
                    "Ya existe un usuario registrado con ese correo."
            );
        });

        // El endpoint público nunca permite registrar ADMIN o MEDICO.
        usuario.setRol("PACIENTE");

        usuario.setPassword(
                passwordEncoder.encode(
                        usuario.getPassword()
                )
        );

        return usuarioRepository.save(usuario);
    }

    /** Crea un usuario desde el módulo administrativo. */
    public Usuario guardarComoAdmin(Usuario usuario) {
        if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria al registrar un usuario.");
        }

        usuarioRepository.findByCorreo(usuario.getCorreo()).ifPresent(existente -> {
            throw new IllegalArgumentException("Ya existe un usuario registrado con ese correo.");
        });

        String rol = usuario.getRol() == null ? "PACIENTE" : usuario.getRol().trim().toUpperCase();
        if (!rol.equals("ADMIN") && !rol.equals("MEDICO") && !rol.equals("PACIENTE")) {
            throw new IllegalArgumentException("Rol no válido. Use ADMIN, MEDICO o PACIENTE.");
        }
        usuario.setRol(rol);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario actualizar(
            Long id,
            Usuario usuarioActualizado) {

        return usuarioRepository.findById(id)
                .map(usuario -> {

                    usuarioRepository
                            .findByCorreo(
                                    usuarioActualizado
                                            .getCorreo()
                            )
                            .ifPresent(otro -> {

                                if (!otro.getId().equals(id)) {

                                    throw new IllegalArgumentException(
                                            "El correo ya está siendo utilizado por otro usuario."
                                    );
                                }
                            });

                    usuario.setNombre(
                            usuarioActualizado.getNombre()
                    );

                    usuario.setCorreo(
                            usuarioActualizado.getCorreo()
                    );

                    String rol = usuarioActualizado.getRol() == null
                            ? usuario.getRol()
                            : usuarioActualizado.getRol().trim().toUpperCase().replace("ROLE_", "");

                    if (!rol.equals("ADMIN") && !rol.equals("MEDICO") && !rol.equals("PACIENTE")) {
                        throw new IllegalArgumentException("Rol no válido. Use ADMIN, MEDICO o PACIENTE.");
                    }

                    usuario.setRol(rol);

                    if (usuarioActualizado.getPassword() != null
                            && !usuarioActualizado
                            .getPassword()
                            .isBlank()) {

                        usuario.setPassword(
                                passwordEncoder.encode(
                                        usuarioActualizado
                                                .getPassword()
                                )
                        );
                    }

                    return usuarioRepository.save(
                            usuario
                    );
                })
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Usuario no encontrado con ID: "
                                        + id
                        )
                );
    }

    @Override
    public void eliminar(Long id) {

        if (!usuarioRepository.existsById(id)) {

            throw new IllegalArgumentException(
                    "Usuario no encontrado con ID: " + id
            );
        }

        usuarioRepository.deleteById(id);
    }
}