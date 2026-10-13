package org.digitalthinking.graphql;

import jakarta.inject.Inject;
import org.digitalthinking.entites.Product;
import org.digitalthinking.services.ProductService;
import org.eclipse.microprofile.graphql.*;

import java.util.List;

@GraphQLApi
public class ProductResource {

    private final ProductService productService;

    @Inject
    public ProductResource(ProductService productService) {
        this.productService = productService;
    }


    @Query("allProducts")
    @Description("Get all products from database")
    public List<Product> getProducts() {
        return productService.listProduct();
    }

    @Query
    @Description("Get a product from database")
    public Product getProductById(@Name("productId") Long id) {
        return productService.findProduct(id);
    }

    @Mutation
    public Product createProduct(Product product) {
        return productService.createdProduct(product);
    }

    @Mutation
    public Product updateProduct(Product product) {
        return productService.updateProduct(product.getId(), product);
    }

    @Mutation
    public Product deletProduct(@Name("productId") Long id) {
        return productService.deleteProduct(id);
    }
}
