package com.prasadProjects.order_service.controller;

import com.prasadProjects.order_service.dto.OrderLineItemsDto;
import com.prasadProjects.order_service.dto.OrderRequest;
import com.prasadProjects.order_service.model.Order_items;
import com.prasadProjects.order_service.model.Status;
import com.prasadProjects.order_service.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    @TimeLimiter(name = "inventory")
    @Retry(name = "inventory")
    public ResponseEntity<CompletableFuture<String>> placeOrder(@RequestBody OrderRequest orderRequest) {
        log.info("Request received to place order: {}", orderRequest);
        try {
            orderService.placeOrder(orderRequest);
            log.info("Order placed successfully");
            return new ResponseEntity<>(CompletableFuture.supplyAsync(() -> "Order placed successfully"), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred while placing order: {}", orderRequest, e);
            return new ResponseEntity<>(CompletableFuture.supplyAsync(() -> "Order failed to place"), HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<CompletableFuture<String>> fallbackMethod(OrderRequest orderRequest, RuntimeException runtimeException) {
        log.warn("Fallback method triggered for order: {}", orderRequest, runtimeException);
        return new ResponseEntity<>(CompletableFuture.supplyAsync(() -> "Oops! Something went wrong, please try ordering later!"), HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<Order_items>> getAllOrders() {
        log.info("Request received to get all orders");
        try {
            List<Order_items> orderItems = orderService.getAllOrders();
            log.info("All orders retrieved successfully");
            return new ResponseEntity<>(orderItems, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred while retrieving all orders", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order_items> getOrderById(@PathVariable Long id) {
        log.info("Request received to get order by ID: {}", id);
        if (id == null) {
            log.warn("Order ID is null");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            Order_items orderItems = orderService.getOrderById(id);
            if (orderItems != null) {
                log.info("Order retrieved successfully: {}", orderItems);
                return new ResponseEntity<>(orderItems, HttpStatus.OK);
            } else {
                log.warn("Order not found for ID: {}", id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error occurred while retrieving order by ID: {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        log.info("Request received to delete order by ID: {}", id);
        try {
            orderService.deleteOrder(id);
            log.info("Order deleted successfully: {}", id);
            return new ResponseEntity<>(id + " order deleted successfully", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.error("Error occurred while deleting order by ID: {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Order_items> updateOrderStatus(@PathVariable Long orderId, @RequestBody Status status) {
        log.info("Request received to update status for order ID: {}, status: {}", orderId, status);
        try {
            Order_items updatedOrder = orderService.updateOrderStatus(orderId, status);
            log.info("Order status updated successfully: {}", updatedOrder);
            return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred while updating status for order ID: {}", orderId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/orderStatus/{status}")
    public ResponseEntity<List<Order_items>> getOrdersByStatus(@PathVariable Status status) {
        log.info("Request received to get orders by status: {}", status);
        try {
            List<Order_items> orderItems = orderService.getOrderByStatus(status);
            log.info("Orders retrieved successfully by status: {}", status);
            return new ResponseEntity<>(orderItems, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred while retrieving orders by status: {}", status, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<Order_items> addItemToOrder(@PathVariable Long orderId, @RequestBody OrderLineItemsDto newItem) {
        log.info("Request received to add item to order ID: {}, item: {}", orderId, newItem);
        try {
            Order_items updatedOrder = orderService.addItemToOrder(orderId, newItem);
            log.info("Item added to order successfully: {}", updatedOrder);
            return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred while adding item to order ID: {}", orderId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<String> removeItemFromOrder(@PathVariable Long orderId, @PathVariable Long itemId) {
        log.info("Request received to remove item from order ID: {}, item ID: {}", orderId, itemId);
        try {
            orderService.removeItemFromOrder(orderId, itemId);
            log.info("Item removed from order successfully: order ID: {}, item ID: {}", orderId, itemId);
            return new ResponseEntity<>("Order " + orderId + " item " + itemId + " deleted successfully.", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred while removing item from order ID: {}, item ID: {}", orderId, itemId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
