package com.prasadProjects.productservice.model;

public enum ProductType {
    ELECTRONICS("Electronics and Gadgets"),
    CLOTHING("Apparel and Accessories"),
    HOME_APPLIANCES("Home and Kitchen Appliances"),
    BOOKS("Books and Stationery"),
    BEAUTY_PERSONAL_CARE("Beauty and Personal Care"),
    FURNITURE("Home and Office Furniture"),
    GROCERY("Grocery and Gourmet Food"),
    TOYS_GAMES("Toys and Games"),
    SPORTS_OUTDOORS("Sports and Outdoor Equipment"),
    AUTOMOTIVE("Automotive Parts and Accessories");

    private final String productdescription;


    ProductType(String productdescription) {
        this.productdescription = productdescription;
    }

    public String getProductDescription() {
        return productdescription;
    }
}
