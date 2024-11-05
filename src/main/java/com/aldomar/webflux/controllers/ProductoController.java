package com.aldomar.webflux.controllers;

import com.aldomar.webflux.models.documents.Producto;
import com.aldomar.webflux.services.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    @Autowired
    private ProductoService productoService;

    @Value("${config.uploads.path}")
    private String uploadPath;

    /**
     * Listado de productos [v1]
     *
     * @return
     */
    @GetMapping("/v1")
    public Flux<Producto> findAll() {
        return productoService.findAll();
    }

    /**
     * Listado de productos [v2]
     *
     * @return
     */
    @GetMapping
    public Mono<ResponseEntity<Flux<Producto>>> findAll2() {
        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productoService.findAll())
        );
    }

    /**
     * Obtener un producto por su identificador
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Producto>> findById(@PathVariable String id) {
        return productoService.findById(id).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Guardar un nuevo producto
     *
     * @param producto
     * @return
     */
    @PostMapping
    public Mono<ResponseEntity<Producto>> save(@RequestBody Producto producto) {
        if (Objects.isNull(producto.getCreateAt()))
            producto.setCreateAt(new Date());
        return productoService.save(producto).map(o -> ResponseEntity
                .created(URI.create("api/productos/".concat(o.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(o));
    }

    /**
     * Actualizar un producto por su identificador
     *
     * @param id
     * @param producto
     * @return
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Producto>> update(@PathVariable String id, @RequestBody Producto producto) {
        return productoService.findById(id).flatMap(p -> {
                    p.setNombre(producto.getNombre());
                    p.setPrecio(producto.getPrecio());
                    p.setCategoria(producto.getCategoria());
                    return productoService.save(p);
                }).map(o -> ResponseEntity
                        .created(URI.create("api/productos/".concat(o.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(o))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Eliminar un producto por su identificador
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return productoService.findById(id)
                .flatMap(p -> productoService.delete(p).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Cargar el foto del producto de forma independiente
     *
     * @param id
     * @param file
     * @return
     */
    @PostMapping("/upload/{id}")
    public Mono<ResponseEntity<Producto>> upload(@PathVariable String id, @RequestPart("file") FilePart file) {
        return productoService.findById(id).flatMap(p -> {
                    p.setFoto(UUID.randomUUID().toString().concat("-").concat(file.filename()));
                    return file.transferTo(new File(uploadPath.concat(p.getFoto()))).then(productoService.save(p));
                }).map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Crear un nuevo producto incluido su imagen (foto)
     *
     * @param producto
     * @param file
     * @return
     */
    @PostMapping("/v2")
    public Mono<ResponseEntity<Producto>> saveWithPhoto(@RequestBody Producto producto, @RequestPart("file") FilePart file) {
        if (Objects.isNull(producto.getCreateAt()))
            producto.setCreateAt(new Date());
        producto.setFoto(UUID.randomUUID().toString().concat("-").concat(file.filename()));
        return file.transferTo(new File(uploadPath.concat(producto.getFoto()))).then(productoService.save(producto)).map(o -> ResponseEntity
                .created(URI.create("api/productos/".concat(o.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(o));
    }

    /**
     * Crear un nuevo producto, pero antes se realiza la validación de los campos
     *
     * @param monoProducto
     * @return
     */
    @PostMapping("/v3")
    public Mono<ResponseEntity<Map<String, Object>>> saveValidate(@Valid @RequestBody Mono<Producto> monoProducto) {
        Map<String, Object> response = new HashMap<>();
        return monoProducto.flatMap(producto -> {
            if (Objects.isNull(producto.getCreateAt()))
                producto.setCreateAt(new Date());

            return productoService.save(producto).map(o -> {
                        response.put("producto", o);
                        response.put("message", "Producto creado con éxito");
                        response.put("timestamp", new Date());
                        return ResponseEntity
                                .created(URI.create("api/productos/".concat(o.getId())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(response);
                    }
            );
        }).onErrorResume(t -> Mono.just(t).cast(WebExchangeBindException.class)
                .flatMap(e -> Mono.just(e.getFieldErrors()))
                .flatMapMany(Flux::fromIterable)
                .map(fieldError -> "El campo ".concat(fieldError.getField()).concat(" ").concat(Objects.requireNonNull(fieldError.getDefaultMessage())))
                .collectList()
                .flatMap(list -> {
                    response.put("errors", list);
                    response.put("timestamp", new Date());
                    response.put("status", HttpStatus.BAD_REQUEST.value());
                    return Mono.just(ResponseEntity.badRequest().body(response));
                })
        );

    }

}
