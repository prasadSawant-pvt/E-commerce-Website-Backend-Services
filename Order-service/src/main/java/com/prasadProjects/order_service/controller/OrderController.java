package com.prasadProjects.order_service.controller;


import com.prasadProjects.order_service.dto.OrderLineItemsDto;
import com.prasadProjects.order_service.dto.OrderRequest;
import com.prasadProjects.order_service.model.Order_items;
import com.prasadProjects.order_service.model.Status;
import com.prasadProjects.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequest orderRequest){
        try {
            orderService.placeOrder(orderRequest);
            return new ResponseEntity<>("Order_items Placed Successfully", HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>("Order_items Failed to Place ", HttpStatus.NOT_FOUND);
        }
    }

//    @PostMapping("/saveAll")
//    @ResponseStatus(HttpStatus.CREATED)
//    public String placeOrder(@RequestBody List<OrderRequest> orderRequest){
//        orderService.placeOrderForAll(orderRequest);
//        return "Order_items Placed Successfully for All";
//    }

    @GetMapping
    public ResponseEntity<List<Order_items>> getAllOrders() {
        List<Order_items> orderItems = orderService.getAllOrders();
        return new ResponseEntity<>(orderItems, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order_items> getOrderById(@PathVariable Long id) {
        Order_items orderItems=null;
        if(id!=null) {
            orderItems = orderService.getOrderById(id);
            return new ResponseEntity<>(orderItems, HttpStatus.OK);
        }else {
            return  new ResponseEntity<>(new Order_items(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return new ResponseEntity<>(id+" order deleted successfully",HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Order_items> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Status status) {
        Order_items updatedOrder = orderService.updateOrderStatus(orderId, status);
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }

    @GetMapping("/orderStatus/{status}")
    public ResponseEntity<List<Order_items>> getOrdersByStatus(@PathVariable Status status) {
        List<Order_items> orderItems = orderService.getOrderByStatus(status);
        return new ResponseEntity<>(orderItems, HttpStatus.OK);
    }


    @PostMapping("/{orderId}/items")
    public ResponseEntity<Order_items> addItemToOrder(
            @PathVariable Long orderId,
            @RequestBody OrderLineItemsDto newItem) {
        Order_items updatedOrder = orderService.addItemToOrder(orderId, newItem);
        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }


    @DeleteMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<String> removeItemFromOrder(
            @PathVariable Long orderId,
            @PathVariable Long itemId) {
        orderService.removeItemFromOrder(orderId, itemId);
        return new ResponseEntity<>("Order "+orderId+" item "+itemId+" deleted successfully.",HttpStatus.OK);
    }

}
