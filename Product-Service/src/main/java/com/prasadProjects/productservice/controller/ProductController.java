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

        log.info("Saving Product Details");
        productService.createProduct(productRequest);
    }

    @GetMapping("/{category}")
    public ResponseEntity<List<ProductResponse>> getProductBasedOnCategory(@PathVariable ProductType category) {
        List<ProductResponse> productResponses = null;
        log.info("Get Product Details based on {} category.", category.name());
        try {
            if (!ObjectUtils.isEmpty(category)) {
                productResponses = productService.getProductsByCategory(category);
                if (!ObjectUtils.isEmpty(productResponses)) {
                    return ResponseEntity.status(HttpStatus.OK).body(productResponses);
                }
            }
        } catch (Exception e) {
            log.error("Exception occur while fetching data", e);
        }

        return null;
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ProductResponse>> getProductListByFilter(@RequestParam(name = "brand",required = false) String brand
            , @RequestParam(name = "name",required = false) String name, @RequestParam(name = "year",required = false) String year) {
        List<ProductResponse> productResponses;
        productResponses = productService.filterProductList(brand, name, year);
        return ResponseEntity.status(HttpStatus.OK).body(productResponses);
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts() {
        log.info("Get All Product Details");
        return productService.getAllProducts();
    }

}
