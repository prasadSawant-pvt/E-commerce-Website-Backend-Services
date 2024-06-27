package com.prasadProjects.productservice.repository;

import ch.qos.logback.core.util.StringUtil;
import com.prasadProjects.productservice.model.FilterProductRequest;
import com.prasadProjects.productservice.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;


public abstract class ProductRepositoryImpl implements ProductRepository{
}
