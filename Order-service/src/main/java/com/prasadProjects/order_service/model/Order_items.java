package com.prasadProjects.order_service.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(schema = "t_order")
public class Order_items {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String customerName;
    private String billingAddress;
    private LocalDateTime orderDate;
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    private LocalDateTime paymentDate;
    private String shippingAddress;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private List<OrderLineItems> orderLineItems;
    private BigDecimal totalAmount;

}
