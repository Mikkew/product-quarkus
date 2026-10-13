package org.digitalthinking.graphql;

import org.digitalthinking.entites.Product;
import org.digitalthinking.services.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductResourceTest {

    @Mock
    ProductService productService;

    @InjectMocks
    ProductResource resource;

    private final Product product = new Product(1L, "C-1", "Name", "Description");

    @Test
    void getProducts() {
        when(productService.listProduct()).thenReturn(List.of(product));

        assertEquals(List.of(product), resource.getProducts());
    }

    @Test
    void getProductById() {
        when(productService.findProduct(1L)).thenReturn(product);

        assertSame(product, resource.getProductById(1L));
    }

    @Test
    void createProduct() {
        when(productService.createdProduct(product)).thenReturn(product);

        assertSame(product, resource.createProduct(product));
    }

    @Test
    void updateProductUsesProductId() {
        when(productService.updateProduct(1L, product)).thenReturn(product);

        assertSame(product, resource.updateProduct(product));
    }

    @Test
    void deletProduct() {
        when(productService.deleteProduct(1L)).thenReturn(product);

        assertSame(product, resource.deletProduct(1L));
    }
}
