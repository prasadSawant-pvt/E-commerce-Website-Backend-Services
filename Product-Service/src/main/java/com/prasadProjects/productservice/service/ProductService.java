package com.prasadProjects.productservice.service;

import ch.qos.logback.core.util.StringUtil;
import com.prasadProjects.productservice.dto.ProductRequest;
import com.prasadProjects.productservice.dto.ProductResponse;
import com.prasadProjects.productservice.model.Product;
import com.prasadProjects.productservice.model.ProductType;
import com.prasadProjects.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j//for logging
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

//    above @RequiredArgsConstructor is doing same as below commented code on runTime
//    @Autowired
//    public ProductService(ProductRepository productRepository) {
//        this.productRepository = productRepository;
//    }

    public void createProduct(ProductRequest productRequest) {
        try {
            Product product = Product.builder()
                    .name(productRequest.getName())
                    .productDetails(productRequest.getProductDetails())
                    .price(productRequest.getPrice())
                    .build();
            productRepository.save(product);
            log.info("Product id {} is saved Successfully ", product.getId());
        }catch (Exception e){
            log.error("Exception occurred while saving product {} ,",productRequest.getName(),e);
        }
    }

    public List<ProductResponse> getAllProducts() {
        List<ProductResponse> productResponses;
        try{
           List<Product> productList=  productRepository.findAll();
            return mapProductToProductResponse(productList);
        }catch(Exception e){
            log.error("Exception occurred while getting product :",e);
        }
        return null;
    }

    public List<ProductResponse> getProductsByCategory(ProductType productType) {

        try {
            List<Product> productList = productRepository.findByProductDetailsProductType(productType);
            return mapProductToProductResponse(productList);
        }catch (Exception e){
            log.error("Exception occur while fetching product list",e);
        }
        return null;
    }
    private List<ProductResponse> mapProductToProductResponse(List<Product> productList) {
        List<ProductResponse> productResponses;
        productResponses= productList.stream().map(this::productMapper).toList();
        return productResponses;
    }

    private ProductResponse productMapper(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .price(product.getPrice())
                .productDetails(product.getProductDetails())
                .name(product.getName())
                .build();
    }

    public List<ProductResponse> filterProductList(String brand, String name, String year) {
        List<Product> productList = getProductsByFilter(brand, name, year);

        return mapProductToProductResponse(productList);

    }

    private List<Product> getProductsByFilter(String brand, String name, String year) {
        List<Product> productList;

//        Custom Query
        Query query=new Query();
        if(!StringUtil.isNullOrEmpty(name)){
            query.addCriteria(Criteria.where("name").is(name));
        }
        if(!StringUtil.isNullOrEmpty(brand)){
            query.addCriteria(Criteria.where("productDetails.brand").regex(Pattern.compile(brand, Pattern.CASE_INSENSITIVE)));
        }
        if(!StringUtil.isNullOrEmpty(year)){
            query.addCriteria(Criteria.where("productDetails.yearOfManufacture").is(year));
        }

        productList= mongoTemplate.find(query, Product.class);
        return productList;
    }


    }

