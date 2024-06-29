package com.prasadProjects.order_service.repository;

import com.prasadProjects.order_service.model.Order_items;
import com.prasadProjects.order_service.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order_items,Long> {
    List<Order_items> findByStatus(Status status);
}
