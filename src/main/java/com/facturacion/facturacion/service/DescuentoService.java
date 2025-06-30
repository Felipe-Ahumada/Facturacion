package com.facturacion.facturacion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.facturacion.facturacion.model.Descuento;
import com.facturacion.facturacion.repository.DescuentoRepository;

@Service
public class DescuentoService {

    @Autowired
    private DescuentoRepository descuentoRepository;

    public Descuento crearDescuento(Descuento d) {
        return descuentoRepository.save(d);
    }

    public Descuento editarDescuento(Long id, Descuento nuevo) {
        Descuento actual = descuentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Descuento no encontrado"));
        actual.setNombre(nuevo.getNombre());
        actual.setPorcentaje(nuevo.getPorcentaje());
        return descuentoRepository.save(actual);
    }

    public boolean eliminarDescuento(Long id) {
        if (descuentoRepository.existsById(id)) {
            descuentoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public double aplicarDescuento(Long idDescuento, double montoBase) {
        Descuento descuento = descuentoRepository.findById(idDescuento)
                .orElseThrow(() -> new RuntimeException("Descuento no encontrado"));
        return montoBase * (1 - descuento.getPorcentaje() / 100);
    }

    public List<Descuento> obtenerTodos() {
        return descuentoRepository.findAll();
    }

    
}
