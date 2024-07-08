package com.prasadProjects.productservice.service;

import com.prasadProjects.productservice.dto.ProductRequest;
import com.prasadProjects.productservice.dto.ProductResponse;
import com.prasadProjects.productservice.model.Product;
import com.prasadProjects.productservice.model.ProductType;
import com.prasadProjects.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public void createProduct(ProductRequest productRequest) {
        log.info("Request received to create product: {}", productRequest);
        try {
            Product product = Product.builder()
                    .name(productRequest.getName())
                    .productDetails(productRequest.getProductDetails())
                    .price(productRequest.getPrice())
                    .build();
            productRepository.save(product);
            log.info("Product created successfully with id: {}", product.getId());
        } catch (Exception e) {
            log.error("Exception occurred while creating product: {}", productRequest.getName(), e);
            throw new RuntimeException("Unable to create product", e);
        }
    }

    public List<ProductResponse> getAllProducts() {
        log.info("Request received to get all products");
        try {
            List<Product> productList = productRepository.findAll();
            log.info("All products retrieved successfully");
            return mapProductToProductResponse(productList);
        } catch (Exception e) {
            log.error("Exception occurred while fetching all products", e);
            throw new RuntimeException("Unable to fetch all products", e);
        }
    }

    public List<ProductResponse> getProductsByCategory(ProductType productType) {
        log.info("Request received to get products by category: {}", productType.name());
        try {
            List<Product> productList = productRepository.findByProductDetailsProductType(productType);
            log.info("Products retrieved successfully for category: {}", productType.name());
            return mapProductToProductResponse(productList);
        } catch (Exception e) {
            log.error("Exception occurred while fetching products for category: {}", productType.name(), e);
            throw new RuntimeException("Unable to fetch products by category", e);
        }
    }

    public List<ProductResponse> filterProductList(String brand, String name, String year) {
        log.info("Request received to filter products by - brand: {}, name: {}, year: {}", brand, name, year);
        try {
            List<Product> productList = getProductsByFilter(brand, name, year);
            log.info("Products filtered successfully");
            return mapProductToProductResponse(productList);
        } catch (Exception e) {
            log.error("Exception occurred while filtering products", e);
            throw new RuntimeException("Unable to filter products", e);
        }
    }

    private List<Product> getProductsByFilter(String brand, String name, String year) {
        log.info("Executing custom query for filtering products");
        Query query = new Query();
        if (!StringUtils.isEmpty(name)) {
            query.addCriteria(Criteria.where("name").is(name));
        }
        if (!StringUtils.isEmpty(brand)) {
            query.addCriteria(Criteria.where("productDetails.brand").regex(Pattern.compile(brand, Pattern.CASE_INSENSITIVE)));
        }
        if (!StringUtils.isEmpty(year)) {
            query.addCriteria(Criteria.where("productDetails.yearOfManufacture").is(year));
        }

        List<Product> productList = mongoTemplate.find(query, Product.class);
        log.info("Custom query executed successfully");
        return productList;
    }

    private List<ProductResponse> mapProductToProductResponse(List<Product> productList) {
        return productList.stream().map(this::productMapper).toList();
    }

    private ProductResponse productMapper(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .price(product.getPrice())
                .productDetails(product.getProductDetails())
                .name(product.getName())
                .build();
    }
}
