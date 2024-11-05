package com.aldomar.webflux.repositories;

import com.aldomar.webflux.models.documents.Producto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;


public interface ProductoRepository extends ReactiveMongoRepository<Producto, String> {
}
