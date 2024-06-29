package com.prasadProjects.order_service.service;


import com.prasadProjects.order_service.dto.InventoryResponse;
import com.prasadProjects.order_service.dto.OrderLineItemsDto;
import com.prasadProjects.order_service.dto.OrderRequest;
import com.prasadProjects.order_service.model.Order_items;
import com.prasadProjects.order_service.model.OrderLineItems;
import com.prasadProjects.order_service.model.Status;
import com.prasadProjects.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
private final OrderRepository orderRepository;
private final WebClient webClient;

    public void placeOrder(OrderRequest orderRequest){
        BigDecimal totalBillAmount = getTotalBillAmount(orderRequest);
        Order_items orderItems = Order_items.builder()
                .orderNumber(UUID.randomUUID().toString())
                .orderDate(LocalDateTime.now())
                .billingAddress(orderRequest.getBillingAddress())
                .customerName(orderRequest.getCustomerName())
                .paymentDate(LocalDateTime.now())
                .shippingAddress(orderRequest.getShippingAddress())
                .paymentMethod(orderRequest.getPaymentMethod())
                .status(orderRequest.getStatus())
                .totalAmount(totalBillAmount)
                .orderLineItems(orderRequest.getOrderLineItems()
                        .stream()
                        .map(this::mapToDTO).toList())
                .build();

                List<String> skuCodeList= orderItems
                        .getOrderLineItems()
                        .stream()
                        .map(OrderLineItems::getSkuCode)
                        .toList();

        InventoryResponse[] inventoryResponses = webClient.get()
                .uri("http://localhost:8082/api/inventory",uriBuilder -> uriBuilder.queryParam("skuCodes",skuCodeList).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();
        boolean allItemsInStocks= ObjectUtils.isEmpty(inventoryResponses) || skuCodeList.size()!= Arrays.stream(inventoryResponses).count()?false:Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);
        if(allItemsInStocks){
            orderRepository.save(orderItems);
        }else{
        throw new IllegalArgumentException(" Products are not in Stock");
        }

    }

    public Order_items saveOrder(Order_items orderItems) {
        return orderRepository.save(orderItems);
    }

    public List<Order_items> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order_items getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order_items not found"));
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    public void placeOrderForAll(List<OrderRequest> orderRequest) {
        orderRequest.stream().forEach(orderRequest1 -> placeOrder(orderRequest1));
    }

    public Order_items updateOrderStatus(Long orderId, Status newStatus) {
        Order_items order = getOrderById(orderId);
        order.setStatus(newStatus); // Assuming 'status' is a field in Order entity
        return orderRepository.save(order);
    }

    public List<Order_items> getOrderByStatus(Status status) {
        List<Order_items> orderItems;
        orderItems=orderRepository.findByStatus(status);
        return orderItems;
    }

    public Order_items addItemToOrder(Long orderId, OrderLineItemsDto newItem) {
        Order_items order = getOrderById(orderId);

        OrderLineItems orderLineItem = mapToDTO(newItem);
        order.getOrderLineItems().add(orderLineItem);
        order.setTotalAmount(order.getOrderLineItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return orderRepository.save(order);
    }


    public void removeItemFromOrder(Long orderId, Long itemId) {
        Order_items order = getOrderById(orderId);

        order.getOrderLineItems().removeIf(item -> item.getId().equals(itemId));

        orderRepository.save(order);
    }
    private static BigDecimal getTotalBillAmount(OrderRequest orderRequest) {
        BigDecimal totalBillAmount = orderRequest.getOrderLineItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalBillAmount;
    }

    private OrderLineItems mapToDTO(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
