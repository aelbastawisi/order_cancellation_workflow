package com.camunda.workflow.delegates.order_cancellation;

import com.camunda.workflow.controller.exceptions.BusinessException;
import com.camunda.workflow.domain.Order;
import com.camunda.workflow.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import static com.camunda.workflow.util.Constants.*;

@Component
@Slf4j
public class AppointmentCreationDelegate implements JavaDelegate {

    private final OrderService orderService;

    public AppointmentCreationDelegate(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void execute(DelegateExecution execution) {
        try {
            log.info("AppointmentCreation Delegate received the execution for create appointment");
            Object orderValue = execution.getVariable(VARIABLE_ORDER_KEY);
            if (orderValue instanceof Order) {
                Order order = ((Order) orderValue);
                order.setName("Modified");
                orderService.saveOrder(order);
                execution.setVariable(VARIABLE_APPOINTMENT_KEY, order.getLocationPreviewTime().toString());
            }
        } catch (BusinessException ex) {
            execution.setVariable(BUSINESS_ERROR_CODE, ex.getMessage());
            throw new BpmnError(BUSINESS_ERROR_CODE);
        } catch (Exception ex) {
            execution.setVariable(TECH_ERROR_CODE, ex.getMessage());
            throw new BpmnError(TECH_ERROR_CODE);
        }
    }
}
