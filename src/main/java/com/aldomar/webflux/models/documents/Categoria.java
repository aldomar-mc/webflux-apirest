package com.aldomar.webflux.models.documents;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categorias")
public class Categoria {
    @Id
    @NotEmpty
    private String id;

    private String nombre;

    public Categoria() {
    }


    public Categoria(String nombre) {
        this.nombre = nombre;
    }

    public @NotEmpty String getId() {
        return id;
    }

    public void setId(@NotEmpty String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
