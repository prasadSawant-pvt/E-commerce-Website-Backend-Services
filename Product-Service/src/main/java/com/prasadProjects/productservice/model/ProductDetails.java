package com.prasadProjects.productservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetails {
    private String brand;
    private ProductType productType;
    private String yearOfManufacture;
}
