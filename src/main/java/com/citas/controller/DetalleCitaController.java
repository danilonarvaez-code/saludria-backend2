package com.citas.controller;

import com.citas.entity.DetalleCita;
import com.citas.service.DetalleCitaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detalle-citas")
@CrossOrigin(origins = "*")
public class DetalleCitaController {

    private final DetalleCitaService detalleCitaService;

    public DetalleCitaController(DetalleCitaService detalleCitaService) {
        this.detalleCitaService = detalleCitaService;
    }

    // GET - listar todos los detalles
    @GetMapping
    public ResponseEntity<List<DetalleCita>> listarTodos() {
        return ResponseEntity.ok(detalleCitaService.listarTodos());
    }

    // GET - buscar detalle por ID
    @GetMapping("/{id}")
    public ResponseEntity<DetalleCita> buscarPorId(@PathVariable Long id) {

        return detalleCitaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
@GetMapping("/reporte/{id}")
public ResponseEntity<DetalleCita> generarReporte(@PathVariable Long id) {

    return detalleCitaService.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}
    // POST - crear detalle
    @PostMapping
    public ResponseEntity<DetalleCita> guardar(
            @RequestBody DetalleCita detalleCita) {

        DetalleCita nuevoDetalle =
                detalleCitaService.guardar(detalleCita);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(nuevoDetalle);
    }

    // PUT - actualizar detalle
    @PutMapping("/{id}")
    public ResponseEntity<DetalleCita> actualizar(
            @PathVariable Long id,
            @RequestBody DetalleCita detalleCita) {

        DetalleCita actualizado =
                detalleCitaService.actualizar(id, detalleCita);

        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(actualizado);
    }

    // DELETE - eliminar detalle
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        if (detalleCitaService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        detalleCitaService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}