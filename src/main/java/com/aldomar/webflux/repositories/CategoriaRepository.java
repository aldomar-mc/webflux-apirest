package com.aldomar.webflux.repositories;

import com.aldomar.webflux.models.documents.Categoria;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CategoriaRepository extends ReactiveMongoRepository<Categoria, String> {
}
