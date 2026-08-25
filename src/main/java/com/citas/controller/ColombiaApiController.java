package com.citas.controller;

import com.citas.service.ColombiaApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/colombia")
@CrossOrigin(origins = "*")
public class ColombiaApiController {

    @Autowired
    private ColombiaApiService colombiaApiService;

    @GetMapping("/departamentos")
    public ResponseEntity<Object> listarDepartamentos() {
        Object departamentos = colombiaApiService.obtenerDepartamentosColombia();
        return ResponseEntity.ok(departamentos);
    }
}