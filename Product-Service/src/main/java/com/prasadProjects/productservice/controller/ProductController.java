package com.prasadProjects.productservice.controller;

import com.prasadProjects.productservice.dto.ProductRequest;
import com.prasadProjects.productservice.dto.ProductResponse;
import com.prasadProjects.productservice.model.ProductType;
import com.prasadProjects.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody ProductRequest productRequest) {
        log.info("Request received to create product: {}", productRequest);
        try {
            productService.createProduct(productRequest);
            log.info("Product created successfully: {}", productRequest);
        } catch (Exception e) {
            log.error("Error occurred while creating product: {}", productRequest, e);
            throw new RuntimeException("Unable to create product", e);
        }
    }

    @GetMapping("/{category}")
    public ResponseEntity<List<ProductResponse>> getProductBasedOnCategory(@PathVariable ProductType category) {
        log.info("Request received to get products based on category: {}", category.name());
        try {
            if (ObjectUtils.isEmpty(category)) {
                log.warn("Category is empty");
                return ResponseEntity.badRequest().build();
            }

            List<ProductResponse> productResponses = productService.getProductsByCategory(category);
            if (ObjectUtils.isEmpty(productResponses)) {
                log.warn("No products found for category: {}", category.name());
                return ResponseEntity.noContent().build();
            }

            log.info("Products retrieved successfully for category: {}", category.name());
            return ResponseEntity.ok(productResponses);
        } catch (Exception e) {
            log.error("Error occurred while fetching products for category: {}", category.name(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ProductResponse>> getProductListByFilter(
            @RequestParam(name = "brand", required = false) String brand,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "year", required = false) String year) {
        log.info("Request received to get products by filter - brand: {}, name: {}, year: {}", brand, name, year);
        try {
            List<ProductResponse> productResponses = productService.filterProductList(brand, name, year);
            log.info("Products retrieved successfully by filter");
            return ResponseEntity.ok(productResponses);
        } catch (Exception e) {
            log.error("Error occurred while filtering products", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts() {
        log.info("Request received to get all products");
        try {
            List<ProductResponse> productResponses = productService.getAllProducts();
            log.info("All products retrieved successfully");
            return productResponses;
        } catch (Exception e) {
            log.error("Error occurred while fetching all products", e);
            throw new RuntimeException("Unable to fetch all products", e);
        }
    }
}
