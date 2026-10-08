package org.digitalthinking.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.digitalthinking.entites.Product;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {

    // Panache maneja automáticamente las operaciones CRUD básicas:
    // listAll(), findById(), findByIdOptional(), persist(), delete(), etc.

    // Método personalizado utilizando la sintaxis concisa de Panache
    public Optional<Product> findByNombre(String nombre) {
        return find("nombre", nombre).firstResultOptional();
    }

    public List<Product> findByPrecioMayorA(Double precio) {
        return list("precio > ?1", precio);
    }
}