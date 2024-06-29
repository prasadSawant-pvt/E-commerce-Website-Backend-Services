package com.prasadProjects.order_service.dto;

import com.prasadProjects.order_service.model.OrderLineItems;
import com.prasadProjects.order_service.model.PaymentMethod;
import com.prasadProjects.order_service.model.Status;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private String customerName;
    private List<OrderLineItemsDto> orderLineItems;
    private String shippingAddress;
    private String billingAddress;
    private Status status; // e.g., enums PENDING, PROCESSING, SHIPPED, DELIVERED
    private PaymentMethod paymentMethod;// e.g enums  UPI,CARD,CASH,NET_BANKING,WALLET
}


