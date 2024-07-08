package com.prasadProjects.order_service.event;

import lombok.*;
import org.springframework.context.ApplicationEvent;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPlacedEvent{

    private String orderNumber;
    private String email;

}
