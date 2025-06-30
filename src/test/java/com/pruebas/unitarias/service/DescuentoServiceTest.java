package com.pruebas.unitarias.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.facturacion.facturacion.model.Descuento;
import com.facturacion.facturacion.repository.DescuentoRepository;
import com.facturacion.facturacion.service.DescuentoService;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class DescuentoServiceTest {

    @Mock
    private DescuentoRepository descuentoRepository;

    @InjectMocks
    private DescuentoService descuentoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /* Test para guardar descuento en la capa servicio */
    @Test
    void testCrearDescuento() {
        Descuento nuevo = new Descuento(null, "Estudiante", 10.0);
        Descuento guardado = new Descuento(1L, "Estudiante", 10.0);

        when(descuentoRepository.save(nuevo)).thenReturn(guardado);

        Descuento resultado = descuentoService.crearDescuento(nuevo);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Estudiante");
        assertThat(resultado.getPorcentaje()).isEqualTo(10.0);
        verify(descuentoRepository).save(nuevo);
    }

    @Test
    void testEditarDescuento() {
        Descuento actual = new Descuento(1L, "Antiguo", 5.0);
        Descuento nuevo = new Descuento(null, "Nuevo", 15.0);
        Descuento esperado = new Descuento(1L, "Nuevo", 15.0);

        when(descuentoRepository.findById(1L)).thenReturn(Optional.of(actual));
        when(descuentoRepository.save(actual)).thenReturn(esperado);

        Descuento resultado = descuentoService.editarDescuento(1L, nuevo);

        assertThat(resultado.getNombre()).isEqualTo("Nuevo");
        assertThat(resultado.getPorcentaje()).isEqualTo(15.0);
        verify(descuentoRepository).findById(1L);
        verify(descuentoRepository).save(actual);
    }

    @Test
    void testEliminarDescuento_Existente() {
        when(descuentoRepository.existsById(1L)).thenReturn(true);

        boolean eliminado = descuentoService.eliminarDescuento(1L);

        assertThat(eliminado).isTrue();
        verify(descuentoRepository).deleteById(1L);
    }

    @Test
    void testAplicarDescuento() {
        Descuento descuento = new Descuento(1L, "Navidad", 20.0);

        when(descuentoRepository.findById(1L)).thenReturn(Optional.of(descuento));

        double resultado = descuentoService.aplicarDescuento(1L, 100.0);

        assertThat(resultado).isEqualTo(80.0);
    }

    @Test
    void testObtenerTodos() {
        Descuento d1 = new Descuento(1L, "Promo 1", 5.0);
        Descuento d2 = new Descuento(2L, "Promo 2", 10.0);

        when(descuentoRepository.findAll()).thenReturn(Arrays.asList(d1, d2));

        List<Descuento> resultado = descuentoService.obtenerTodos();

        assertThat(resultado).hasSize(2).contains(d1, d2);
        verify(descuentoRepository).findAll();
    }
}

//Que pasa si no encuentro un descuento?
