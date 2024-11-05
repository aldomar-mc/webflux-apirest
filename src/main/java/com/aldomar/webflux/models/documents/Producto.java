package com.aldomar.webflux.models.documents;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Document(collection = "productos")
public class Producto {
    @Id
    private String id;

    @NotEmpty
    private String nombre;

    @NotNull
    private Double precio;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createAt;

    @Valid
    @NotNull
    private Categoria categoria;

    private String foto;

    public Producto() {
    }

    public Producto(String nombre, Double precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    public Producto(String nombre, Double precio, Categoria categoria) {
        this(nombre, precio);
        this.categoria = categoria;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public @NotEmpty String getNombre() {
        return nombre;
    }

    public void setNombre(@NotEmpty String nombre) {
        this.nombre = nombre;
    }

    public @NotNull Double getPrecio() {
        return precio;
    }

    public void setPrecio(@NotNull Double precio) {
        this.precio = precio;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public @Valid Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(@Valid Categoria categoria) {
        this.categoria = categoria;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
