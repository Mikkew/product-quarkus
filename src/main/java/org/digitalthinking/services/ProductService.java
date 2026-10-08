package org.digitalthinking.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.digitalthinking.entites.Product;
import org.digitalthinking.repositories.ProductRepository;

import java.util.List;

@ApplicationScoped
public class ProductService {

    @Inject
    private ProductRepository productRepository;

    public List<Product> listProduct() {
        return productRepository.listAll();
    }

    public Product findProduct(Long id) {
        return productRepository.findByIdOptional(id)
                .orElseThrow(() -> new WebApplicationException("Producto no encontrado con el ID: " + id, Response.Status.NOT_FOUND));
    }

    @Transactional
    public Product createdProduct(Product product) {
        if (product.getId() != null) {
            throw new WebApplicationException("El ID debe ser nulo al crear un producto nuevo", Response.Status.BAD_REQUEST);
        }
        productRepository.persist(product);
        return product;
    }

    @Transactional
    public Product updateProduct(Long id, Product detalles) {
        Product entity = findProduct(id);

        entity.setCode(detalles.getCode());
        entity.setName(detalles.getName());
        entity.setDescription(detalles.getDescription());

        return entity;
    }

    @Transactional
    public void deleteProduct(Long id) {
        boolean deleted = productRepository.deleteById(id);
        if (!deleted) {
            throw new WebApplicationException("No se pudo eliminar, producto no encontrado con ID: " + id, Response.Status.NOT_FOUND);
        }
    }
}