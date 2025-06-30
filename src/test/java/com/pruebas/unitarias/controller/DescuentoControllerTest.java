package com.pruebas.unitarias.controller;

import static org.mockito.ArgumentMatchers.any;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pruebas.unitarias.model.Descuento;
import com.pruebas.unitarias.service.DescuentoService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;


@WebMvcTest(DescuentoController.class)
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


    @Test
    public void testObtenerTodosLosDescuentos() throws Exception {
        Mockito.when(descuentoService.obtenerTodos()).thenReturn(Arrays.asList(descuento));

        mockMvc.perform(get("/api/descuentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre").value(descuento.getNombre()));
    }

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

    @Test
    public void testEliminarDescuento() throws Exception {
        Mockito.when(descuentoService.eliminarDescuento(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/descuentos/1"))
                .andExpect(status().isNoContent()); // ← corregido
    }

}
