package com.camunda.workflow.delegates.order_processing;

import com.camunda.workflow.controller.exceptions.BusinessException;
import com.camunda.workflow.domain.Order;
import com.camunda.workflow.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import static com.camunda.workflow.util.Constants.*;


@Slf4j
public class DelegateAutoProcessOrder implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        try {
            // remove VARIABLE_REQUIRE_PREREQUISITES_KEY if exists
            execution.removeVariable(VARIABLE_REQUIRE_PREREQUISITES_KEY);
            log.info("DelegateAutoProcessOrder received the execution");
            checkConflict(execution);
        } catch (BusinessException ex) {
            execution.setVariable(BUSINESS_ERROR_CODE, ex.getMessage());
            throw new BpmnError(BUSINESS_ERROR_CODE);
        } catch (Exception ex) {
            execution.setVariable(TECH_ERROR_CODE, ex.getMessage());
            throw new BpmnError(TECH_ERROR_CODE);
        }
    }

    /**
     * Validate variables against business rules
     *
     * @param execution delegate execution
     */
    private void checkConflict(DelegateExecution execution) {
        Order order = (Order) execution.getVariable(Constants.VARIABLE_ORDER_KEY);
        execution.setVariable(VARIABLE_IS_CONFLICTED_KEY, order.getIsConflict() == null || order.getIsConflict());
    }
}
