package org.digitalthinking.controllers;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriBuilder;
import org.digitalthinking.entites.Product;
import org.digitalthinking.services.ProductService;

import java.net.URI;
import java.util.List;

@Path("/product")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductController {

    private ProductService productService;

    @Inject
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GET
    public Response listProduct() {
        return Response.ok(productService.listProduct()).build();
    }

    @GET
    @Path("/{id}")
    public Product findProduct(@PathParam("id") Long id) {
        return productService.findProduct(id);
    }

    @POST
    public Response createdProduct(Product product) {
        final Product nuevo = productService.createdProduct(product);
        final URI uri = UriBuilder.fromResource(Product.class)
                .path("{id}")
                .build(nuevo.getId());
        return Response.created(uri).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateProduct(@PathParam("id") Long id, Product product) {
        return Response.accepted(productService.updateProduct(id, product)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteProduct(@PathParam("id") Long id) {
        return Response.accepted(productService.deleteProduct(id)).build();
    }

}
