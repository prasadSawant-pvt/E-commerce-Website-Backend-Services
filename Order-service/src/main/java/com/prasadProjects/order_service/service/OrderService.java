package com.prasadProjects.order_service.service;

import com.prasadProjects.order_service.dto.InventoryResponse;
import com.prasadProjects.order_service.dto.OrderLineItemsDto;
import com.prasadProjects.order_service.dto.OrderRequest;
import com.prasadProjects.order_service.event.OrderPlacedEvent;
import com.prasadProjects.order_service.model.Order_items;
import com.prasadProjects.order_service.model.OrderLineItems;
import com.prasadProjects.order_service.model.Status;
import com.prasadProjects.order_service.repository.OrderRepository;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
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

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final ObservationRegistry observationRegistry;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public void placeOrder(OrderRequest orderRequest) {
        log.info("Placing order for customer: {}", orderRequest.getCustomerName());
        try {
            BigDecimal totalBillAmount = getTotalBillAmount(orderRequest);
            Order_items orderItems = buildOrderItems(orderRequest, totalBillAmount);

            List<String> skuCodeList = orderItems.getOrderLineItems().stream()
                    .map(OrderLineItems::getSkuCode)
                    .toList();

            log.info("Calling inventory service to check stock availability");

            Observation inventoryServiceObservation = Observation.createNotStarted("inventory-service-lookup", this.observationRegistry);
            inventoryServiceObservation.lowCardinalityKeyValue("call", "inventory-service");
            inventoryServiceObservation.observe(() -> {
                InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
                        .uri("http://inventory-service/api/inventory", uriBuilder -> uriBuilder.queryParam("skuCodes", skuCodeList).build())
                        .retrieve()
                        .bodyToMono(InventoryResponse[].class)
                        .block();

                boolean allItemsInStocks = areAllItemsInStock(inventoryResponses, skuCodeList);
                if (allItemsInStocks) {
                    orderRepository.save(orderItems);
                    kafkaTemplate.send("notificationTopic", OrderPlacedEvent.builder().orderNumber(orderItems.getOrderNumber()).email(orderRequest.getEmail()).build());
                    log.info("Order placed and notification sent successfully for order number: {}", orderItems.getOrderNumber());
                } else {
                    throw new IllegalArgumentException("Products are not in stock");
                }
            });
        } catch (Exception e) {
            log.error("Error occurred while placing order for customer: {}", orderRequest.getCustomerName(), e);
            throw e;
        }
    }

    private boolean areAllItemsInStock(InventoryResponse[] inventoryResponses, List<String> skuCodeList) {
        return !ObjectUtils.isEmpty(inventoryResponses) && skuCodeList.size() == Arrays.stream(inventoryResponses).count() && Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);
    }

    private Order_items buildOrderItems(OrderRequest orderRequest, BigDecimal totalBillAmount) {
        return Order_items.builder()
                .orderNumber(UUID.randomUUID().toString())
                .orderDate(LocalDateTime.now())
                .billingAddress(orderRequest.getBillingAddress())
                .customerName(orderRequest.getCustomerName())
                .paymentDate(LocalDateTime.now())
                .shippingAddress(orderRequest.getShippingAddress())
                .paymentMethod(orderRequest.getPaymentMethod())
                .status(orderRequest.getStatus())
                .totalAmount(totalBillAmount)
                .orderLineItems(orderRequest.getOrderLineItems().stream()
                        .map(this::mapToDTO).toList())
                .build();
    }

    public Order_items saveOrder(Order_items orderItems) {
        log.info("Saving order: {}", orderItems.getOrderNumber());
        return orderRepository.save(orderItems);
    }

    public List<Order_items> getAllOrders() {
        log.info("Fetching all orders");
        return orderRepository.findAll();
    }

    public Order_items getOrderById(Long id) {
        log.info("Fetching order by ID: {}", id);
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found for ID: " + id));
    }

    public void deleteOrder(Long id) {
        log.info("Deleting order by ID: {}", id);
        try {
            orderRepository.deleteById(id);
            log.info("Order deleted successfully for ID: {}", id);
        } catch (Exception e) {
            log.error("Error occurred while deleting order by ID: {}", id, e);
            throw e;
        }
    }

    public void placeOrderForAll(List<OrderRequest> orderRequests) {
        log.info("Placing multiple orders");
        orderRequests.forEach(this::placeOrder);
    }

    public Order_items updateOrderStatus(Long orderId, Status newStatus) {
        log.info("Updating status for order ID: {}, new status: {}", orderId, newStatus);
        Order_items order = getOrderById(orderId);
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    public List<Order_items> getOrderByStatus(Status status) {
        log.info("Fetching orders by status: {}", status);
        return orderRepository.findByStatus(status);
    }

    public Order_items addItemToOrder(Long orderId, OrderLineItemsDto newItem) {
        log.info("Adding item to order ID: {}, item: {}", orderId, newItem);
        Order_items order = getOrderById(orderId);

        OrderLineItems orderLineItem = mapToDTO(newItem);
        order.getOrderLineItems().add(orderLineItem);
        order.setTotalAmount(order.getOrderLineItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return orderRepository.save(order);
    }

    public void removeItemFromOrder(Long orderId, Long itemId) {
        log.info("Removing item from order ID: {}, item ID: {}", orderId, itemId);
        Order_items order = getOrderById(orderId);

        order.getOrderLineItems().removeIf(item -> item.getId().equals(itemId));

        orderRepository.save(order);
    }

    private static BigDecimal getTotalBillAmount(OrderRequest orderRequest) {
        return orderRequest.getOrderLineItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderLineItems mapToDTO(OrderLineItemsDto orderLineItemsDto) {
        return OrderLineItems.builder()
                .quantity(orderLineItemsDto.getQuantity())
                .price(orderLineItemsDto.getPrice())
                .skuCode(orderLineItemsDto.getSkuCode())
                .build();
    }
}
