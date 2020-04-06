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

    public Order getOrder(Long orderId) {
        return this.orderRepository.findById(orderId).orElse(null);
    }

    public Order updateOrderAccordingToBR(Long orderId, Order current){
        Order order = getOrder(orderId);
        if(order != null){
            order.setName(current.getName());
            order.setAddress(current.getAddress());
            return saveOrder(order);
        }
        return null;
    }
}
