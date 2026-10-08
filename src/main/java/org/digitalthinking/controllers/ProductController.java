package org.digitalthinking.controllers;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.digitalthinking.entites.Product;
import org.digitalthinking.services.ProductService;

import java.util.List;

@Path("/product")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductController {

    @Inject
    private ProductService productService;

    @GET
    public List<Product> listProduct() {
        return productService.listProduct();
    }

    @GET
    @Path("/{id}")
    public Product findProduct(@PathParam("id") Long id) {
        return productService.findProduct(id);
    }

    @POST
    public Response createdProduct(Product product) {
        Product nuevo = productService.createdProduct(product);
        return Response.status(Response.Status.CREATED).entity(nuevo).build();
    }

    @PUT
    @Path("/{id}")
    public Product updateProduct(@PathParam("id") Long id, Product product) {
        return productService.updateProduct(id, product);
    }

    @DELETE
    @Path("/{id}")
    public Response deleteProduct(@PathParam("id") Long id) {
        productService.deleteProduct(id);
        return Response.noContent().build();
    }

}
