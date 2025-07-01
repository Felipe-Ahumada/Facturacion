package com.pruebas.unitarias.controller;

import static org.mockito.ArgumentMatchers.any;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pruebas.unitarias.assemblers.DescuentoModelAssembler;
import com.pruebas.unitarias.model.Descuento;
import com.pruebas.unitarias.service.DescuentoService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;


@WebMvcTest(DescuentoController.class)
@Import(DescuentoModelAssembler.class)
public class DescuentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DescuentoService descuentoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Descuento descuento;

    @BeforeEach
    public void setup() {
        descuento = new Descuento();
        descuento.setId(1L);
        descuento.setNombre("Descuento de prueba");
        descuento.setPorcentaje(10.0);
    }

    // Test para crear un descuento
    @Test
    public void testCrearDescuento() throws Exception {
        Mockito.when(descuentoService.crearDescuento(any(Descuento.class))).thenReturn(descuento);

        mockMvc.perform(post("/api/descuentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(descuento)))
                .andExpect(status().isCreated()) // <-- cambiado
                .andExpect(jsonPath("$.id").value(descuento.getId()))
                .andExpect(jsonPath("$.nombre").value(descuento.getNombre()));
    }

    // Test para obtener un descuento
    @Test
    public void testObtenerTodos() throws Exception {
        Mockito.when(descuentoService.obtenerTodos()).thenReturn(Arrays.asList(descuento, descuento));

        mockMvc.perform(get("/api/descuentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.descuentoList", hasSize(2)))
                .andExpect(jsonPath("_embedded.descuentoList[0].nombre").value(descuento.getNombre()));
    }
    // Test para obtener un descuento por ID
    @Test
    public void testEditarDescuento() throws Exception {
        Descuento actualizado = new Descuento();
        actualizado.setId(1L);
        actualizado.setNombre("Descuento actualizado");
        actualizado.setPorcentaje(15.0);

        Mockito.when(descuentoService.editarDescuento(eq(1L), any(Descuento.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/descuentos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Descuento actualizado"));
    }

    // Test para eliminar un descuento
    @Test
    public void testEliminarDescuento() throws Exception {
        Mockito.when(descuentoService.eliminarDescuento(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/descuentos/1"))
                .andExpect(status().isNoContent()); // ← corregido
    }

    // Test para eliminar un descuento que no existe
    @Test
    public void testCrearDescuento_error() throws Exception {
        Mockito.when(descuentoService.crearDescuento(any(Descuento.class)))
            .thenThrow(new RuntimeException("Error inesperado"));

        mockMvc.perform(post("/api/descuentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(descuento)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Error al crear el descuento"))
                .andExpect(jsonPath("$.error").exists());
    }

    // Test para editar un descuento que no existe
    @Test
    public void testEliminarDescuento_NoExiste() throws Exception {
        Mockito.when(descuentoService.eliminarDescuento(1L))
            .thenThrow(new RuntimeException("No existe descuento"));

        mockMvc.perform(delete("/api/descuentos/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Descuento no encontrado"))
                .andExpect(jsonPath("$.id").value(1));
    }

    // Test para aplicar un descuento
    @Test
    public void testAplicarDescuento() throws Exception {
        var data = new java.util.HashMap<String, Object>();
        data.put("idDescuento", 1L);
        data.put("montoBase", 100.0);

        Mockito.when(descuentoService.aplicarDescuento(1L, 100.0)).thenReturn(90.0);

        mockMvc.perform(post("/api/descuentos/aplicar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.montoFinal").value(90.0));
    }

    //Este es para el descuento no encontrado
    @Test
    public void testAplicarDescuento_NoEncontrado() throws Exception {
        var data = new java.util.HashMap<String, Object>();
        data.put("idDescuento", 1L);
        data.put("montoBase", 100.0);

        Mockito.when(descuentoService.aplicarDescuento(1L, 100.0))
            .thenThrow(new RuntimeException("No se encontró el descuento"));

        mockMvc.perform(post("/api/descuentos/aplicar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Descuento no encontrado"))
                .andExpect(jsonPath("$.error").exists());
    }

    //Este test es para datos inválidos
    @Test
    public void testAplicarDescuento_NoValido() throws Exception {
        var data = new java.util.HashMap<String, Object>();
        data.put("idDescuento", 1L); // falta montoBase

        mockMvc.perform(post("/api/descuentos/aplicar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Error en los datos enviados"))
                .andExpect(jsonPath("$.error").exists());
    }

    //Es para cuando el descuento no existe al editar
    @Test
    public void testEditarDescuento_NoExiste() throws Exception {
        Descuento nuevo = new Descuento();
        nuevo.setNombre("Nuevo descuento");
        nuevo.setPorcentaje(20.0);

        Mockito.when(descuentoService.editarDescuento(eq(99L), any(Descuento.class)))
            .thenThrow(new RuntimeException("Descuento no encontrado"));

        mockMvc.perform(put("/api/descuentos/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Descuento no encontrado"))
                .andExpect(jsonPath("$.id").value(99));
    }

    //Es para cuando no se elimina un descuento
    @Test
    public void testEliminarDescuento_NoSeElimino() throws Exception {
        Long id = 2L;

        Mockito.when(descuentoService.eliminarDescuento(id)).thenReturn(false);

        mockMvc.perform(delete("/api/descuentos/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("No existe descuento con ID"))
                .andExpect(jsonPath("$.id").value(id));
    }
}

