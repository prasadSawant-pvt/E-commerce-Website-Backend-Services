package com.prasadProjects.productservice.dto;

import com.prasadProjects.productservice.model.ProductDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private String id;
    private String name;
    private ProductDetails productDetails;
    private BigDecimal price;
}
