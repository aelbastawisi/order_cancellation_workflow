package com.camunda.workflow.service;

import com.camunda.workflow.domain.Order;
import com.camunda.workflow.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public class OrderService implements Serializable {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order saveOrder(Order order) {
        return this.orderRepository.save(order);
    }
}
