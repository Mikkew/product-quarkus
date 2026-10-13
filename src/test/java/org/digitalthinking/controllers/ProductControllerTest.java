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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    void listProductReturns200() {
        when(productService.listProduct()).thenReturn(List.of(product));

        Response response = controller.listProduct();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(List.of(product), response.getEntity());
    }

    @Test
    void findProduct() {
        when(productService.findProduct(1L)).thenReturn(product);

        assertSame(product, controller.findProduct(1L));
    }

    @Test
    void createdProductReturns201WithLocation() {
        when(productService.createdProduct(product)).thenReturn(product);

        Response response = controller.createdProduct(product);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertNotNull(response.getLocation());
        assertTrue(response.getLocation().toString().endsWith("/1"));
    }

    @Test
    void updateProductReturns202() {
        when(productService.updateProduct(1L, product)).thenReturn(product);

        Response response = controller.updateProduct(1L, product);

        assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());
        assertSame(product, response.getEntity());
    }

    @Test
    void deleteProductReturns202() {
        when(productService.deleteProduct(1L)).thenReturn(product);

        Response response = controller.deleteProduct(1L);

        verify(productService).deleteProduct(1L);
        assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());
        assertSame(product, response.getEntity());
    }
}
