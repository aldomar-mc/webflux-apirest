package com.aldomar.webflux.services;

import com.aldomar.webflux.models.documents.Categoria;
import com.aldomar.webflux.models.documents.Producto;
import com.aldomar.webflux.repositories.CategoriaRepository;
import com.aldomar.webflux.repositories.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductoServiceImpl implements ProductoService {
    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;


    /**
     * @return
     */
    @Override
    public Flux<Producto> findAll() {
        return productoRepository.findAll();
    }

    /**
     * @return
     */
    @Override
    public Flux<Producto> findAllConNombreUpperCase() {
        return productoRepository.findAll().map(producto -> {
            producto.setNombre(producto.getNombre().toUpperCase());
            return producto;
        });
    }

    /**
     * @return
     */
    @Override
    public Flux<Producto> findAllConNombreUpperCaseRepeat() {
        return findAllConNombreUpperCase().repeat(5000);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Mono<Producto> findById(String id) {
        return productoRepository.findById(id);
    }

    /**
     * @param producto
     * @return
     */
    @Override
    public Mono<Producto> save(Producto producto) {
        return productoRepository.save(producto);
    }

    /**
     * @param producto
     * @return
     */
    @Override
    public Mono<Void> delete(Producto producto) {
        return productoRepository.delete(producto);
    }

    /**
     * @return
     */
    @Override
    public Flux<Categoria> findAllCategoria() {
        return categoriaRepository.findAll();
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Mono<Categoria> findCategoriaById(String id) {
        return categoriaRepository.findById(id);
    }

    /**
     * @param categoria
     * @return
     */
    @Override
    public Mono<Categoria> saveCategoria(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }
}
