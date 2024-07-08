package com.prasadProjects.productservice.util;

import com.prasadProjects.productservice.model.Product;
import com.prasadProjects.productservice.model.ProductDetails;
import com.prasadProjects.productservice.model.ProductType;
import com.prasadProjects.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() < 1) {
            Product product = new Product();
            ProductDetails productDetails = ProductDetails.builder()
                    .yearOfManufacture("2013")
                    .brand("Apple")
                    .productType(ProductType.ELECTRONICS)
                    .build();

            product.setName("iPhone 13");
            product.setProductDetails(productDetails);
            product.setPrice(BigDecimal.valueOf(1000));

            productRepository.save(product);
        }
    }
}
