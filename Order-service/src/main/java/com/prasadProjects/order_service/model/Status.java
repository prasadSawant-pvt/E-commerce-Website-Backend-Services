package com.prasadProjects.order_service.model;


public enum Status {
    PENDING("PENDING"), PROCESSING("PROCESSING"), SHIPPED("SHIPPED"), DELIVERED("DELIVERED");

    Status(String delivered) {

    }
}
