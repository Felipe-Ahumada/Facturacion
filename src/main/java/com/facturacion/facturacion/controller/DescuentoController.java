package com.facturacion.facturacion.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.facturacion.facturacion.model.Descuento;
import com.facturacion.facturacion.service.DescuentoService;

@RestController
@RequestMapping("/api/descuentos")
@CrossOrigin(origins = "*")
public class DescuentoController {

    private final DescuentoService service;

    public DescuentoController(DescuentoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Descuento d) {
        try {
            Descuento nuevo = service.crearDescuento(d);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "mensaje", "Error al crear el descuento",
                "error", e.getMessage()
            ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody Descuento nuevo) {
        try {
            Descuento actualizado = service.editarDescuento(id, nuevo);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "mensaje", "Descuento no encontrado",
                "id", id
            ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (service.eliminarDescuento(id)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "mensaje", "No existe descuento con ID",
                "id", id
            ));
        }
    }

    @PostMapping("/aplicar")
    public ResponseEntity<?> aplicarDescuento(@RequestBody Map<String, Object> data) {
        try {
            Long idDescuento = Long.parseLong(data.get("idDescuento").toString());
            double montoBase = Double.parseDouble(data.get("montoBase").toString());
            double resultado = service.aplicarDescuento(idDescuento, montoBase);
            return ResponseEntity.ok(Map.of(
                "montoFinal", resultado
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "mensaje", "Descuento no encontrado",
                "error", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "mensaje", "Error en los datos enviados",
                "error", e.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<List<Descuento>> listar() {
        return ResponseEntity.ok(service.obtenerTodos());
    }
}
