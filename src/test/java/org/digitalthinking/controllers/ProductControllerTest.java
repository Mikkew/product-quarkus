package org.digitalthinking.controllers;

import jakarta.ws.rs.core.Response;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    ProductService productService;

    @InjectMocks
    ProductController controller;

    private final Product product = new Product(1L, "C-1", "Name", "Description");

    @Test
    void listProduct() {
        when(productService.listProduct()).thenReturn(List.of(product));

        assertEquals(List.of(product), controller.listProduct());
    }

    @Test
    void findProduct() {
        when(productService.findProduct(1L)).thenReturn(product);

        assertSame(product, controller.findProduct(1L));
    }

    @Test
    void createdProductReturns201() {
        when(productService.createdProduct(product)).thenReturn(product);

        Response response = controller.createdProduct(product);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertSame(product, response.getEntity());
    }

    @Test
    void updateProduct() {
        when(productService.updateProduct(1L, product)).thenReturn(product);

        assertSame(product, controller.updateProduct(1L, product));
    }

    @Test
    void deleteProductReturns204() {
        Response response = controller.deleteProduct(1L);

        verify(productService).deleteProduct(1L);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }
}
