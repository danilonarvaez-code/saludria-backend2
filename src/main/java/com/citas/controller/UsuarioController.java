package com.citas.controller;

import com.citas.entity.Usuario;
import com.citas.service.IUsuarioService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:3000"
})
public class UsuarioController {

    @Autowired
    private IUsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {

        return ResponseEntity.ok(
                usuarioService.listarTodos()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerPorId(
            @PathVariable Long id) {

        return usuarioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }

    @PostMapping
    public ResponseEntity<Usuario> crear(
            @Valid @RequestBody Usuario usuario) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        usuarioService.guardar(usuario)
                );
    }

    @PostMapping("/admin")
    public ResponseEntity<Usuario> crearComoAdmin(
            @Valid @RequestBody Usuario usuario) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioService.guardarComoAdmin(usuario));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Usuario usuario) {

        return ResponseEntity.ok(
                usuarioService.actualizar(
                        id,
                        usuario
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        usuarioService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}