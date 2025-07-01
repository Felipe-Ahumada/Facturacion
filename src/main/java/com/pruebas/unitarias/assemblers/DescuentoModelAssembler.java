package com.pruebas.unitarias.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.pruebas.unitarias.controller.DescuentoController;
import com.pruebas.unitarias.model.Descuento;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@Component
public class DescuentoModelAssembler implements RepresentationModelAssembler<Descuento, EntityModel<Descuento>> {

    @SuppressWarnings("null")
    @Override
    public EntityModel<Descuento> toModel(@org.springframework.lang.NonNull Descuento descuento) {
        return EntityModel.of(descuento,
            linkTo(methodOn(DescuentoController.class).listar()).withRel("all-descuentos"),
            linkTo(methodOn(DescuentoController.class).eliminar(descuento.getId())).withRel("delete")
        );
    }
}

