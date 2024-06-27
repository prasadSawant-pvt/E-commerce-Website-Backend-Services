package com.prasadProjects.productservice.repository;

import com.prasadProjects.productservice.model.FilterProductRequest;
import com.prasadProjects.productservice.model.Product;
import com.prasadProjects.productservice.model.ProductType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product,String> {

    List<Product> findByProductDetailsProductType(ProductType productType);

}
