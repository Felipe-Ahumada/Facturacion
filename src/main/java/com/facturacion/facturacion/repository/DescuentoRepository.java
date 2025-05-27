package com.facturacion.facturacion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.facturacion.facturacion.model.Descuento;

@Repository
public interface DescuentoRepository extends JpaRepository<Descuento, Long> {
}
