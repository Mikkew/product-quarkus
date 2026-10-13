package org.digitalthinking.services;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.digitalthinking.entites.Product;
import org.digitalthinking.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductService productService;

    private Product product(Long id) {
        return new Product(id, "C-1", "Name", "Description");
    }

    @Test
    void listProductReturnsAll() {
        when(productRepository.listAll()).thenReturn(List.of(product(1L), product(2L)));

        assertEquals(2, productService.listProduct().size());
    }

    @Test
    void findProductReturnsExisting() {
        Product p = product(1L);
        when(productRepository.findByIdOptional(1L)).thenReturn(Optional.of(p));

        assertSame(p, productService.findProduct(1L));
    }

    @Test
    void findProductThrowsNotFound() {
        when(productRepository.findByIdOptional(99L)).thenReturn(Optional.empty());

        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> productService.findProduct(99L));
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), ex.getResponse().getStatus());
    }

    @Test
    void createdProductPersists() {
        Product p = product(null);

        assertSame(p, productService.createdProduct(p));
        verify(productRepository).persist(p);
    }

    @Test
    void createdProductRejectsId() {
        Product p = product(5L);

        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> productService.createdProduct(p));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), ex.getResponse().getStatus());
        verify(productRepository, never()).persist(any(Product.class));
    }

    @Test
    void updateProductCopiesFields() {
        Product existing = product(1L);
        when(productRepository.findByIdOptional(1L)).thenReturn(Optional.of(existing));

        Product result = productService.updateProduct(1L, new Product(null, "C-2", "New", "Other"));

        assertEquals("C-2", result.getCode());
        assertEquals("New", result.getName());
        assertEquals("Other", result.getDescription());
        assertEquals(1L, result.getId());
    }

    @Test
    void updateProductThrowsNotFound() {
        when(productRepository.findByIdOptional(99L)).thenReturn(Optional.empty());

        assertThrows(WebApplicationException.class, () -> productService.updateProduct(99L, product(null)));
    }

    @Test
    void deleteProductReturnsDeleted() {
        Product p = product(1L);
        when(productRepository.findByIdOptional(1L)).thenReturn(Optional.of(p));
        when(productRepository.deleteById(1L)).thenReturn(true);

        assertSame(p, productService.deleteProduct(1L));
    }

    @Test
    void deleteProductThrowsWhenNotFound() {
        when(productRepository.findByIdOptional(99L)).thenReturn(Optional.empty());

        assertThrows(WebApplicationException.class, () -> productService.deleteProduct(99L));
        verify(productRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteProductThrowsWhenNotDeleted() {
        when(productRepository.findByIdOptional(1L)).thenReturn(Optional.of(product(1L)));
        when(productRepository.deleteById(1L)).thenReturn(false);

        WebApplicationException ex = assertThrows(WebApplicationException.class, () -> productService.deleteProduct(1L));
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), ex.getResponse().getStatus());
    }
}
