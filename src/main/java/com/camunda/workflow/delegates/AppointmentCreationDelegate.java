package com.camunda.workflow.delegates;

import com.camunda.workflow.controller.ProcessController;
import com.camunda.workflow.domain.Order;
import com.camunda.workflow.service.OrderService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class AppointmentCreationDelegate implements JavaDelegate {

    private final Logger logger = LoggerFactory.getLogger(AppointmentCreationDelegate.class);
    private final OrderService orderService;
    public AppointmentCreationDelegate(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        logger.info("AppointmentCreation Delegate received the execution for create appointment");
        Object orderValue = execution.getVariable(ProcessController.VARIABLE_ORDER_KEY);
        if (orderValue instanceof Order) {
            logger.info("Found orderValue variable {}", orderValue.toString());
            Order order = ((Order) orderValue);
            order.setAddress("Modified");
            orderService.saveOrder(order);
            if ("rollback".equals(order.getName())) {
                throw new RuntimeException("Testing rollback");
            }
        }
        execution.setVariable(ProcessController.VARIABLE_APPOINTMENT_KEY, LocalDate.now());
    }
}
