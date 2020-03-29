package com.camunda.workflow.delegates;

import com.camunda.workflow.controller.ProcessController;
import com.camunda.workflow.domain.Order;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class AppointmentCreationDelegate implements JavaDelegate {

    private final Logger logger = LoggerFactory.getLogger(AppointmentCreationDelegate.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        logger.info("AppointmentCreateion Delegate received the execution for create appointment");
        Object order = execution.getVariable(ProcessController.VARIABLE_ORDER_KEY);
        if (order instanceof Order) {
            logger.info("Found order variable {}", order.toString());
        }
        execution.setVariable("appointment", LocalDate.now());
    }
}
