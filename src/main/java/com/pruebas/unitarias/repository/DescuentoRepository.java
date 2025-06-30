package com.pruebas.unitarias.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pruebas.unitarias.model.Descuento;

@Repository
public interface DescuentoRepository extends JpaRepository<Descuento, Long> {
}
