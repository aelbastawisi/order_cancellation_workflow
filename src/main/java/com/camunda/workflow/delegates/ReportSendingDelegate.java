package com.camunda.workflow.delegates;

import com.camunda.workflow.controller.exceptions.BusinessException;
import com.camunda.workflow.domain.Order;
import com.camunda.workflow.domain.Report;
import com.camunda.workflow.util.BusinessValidator;
import com.camunda.workflow.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;

import static com.camunda.workflow.util.Constants.BUSINESS_ERROR_CODE;
import static com.camunda.workflow.util.Constants.TECH_ERROR_CODE;


@Slf4j
public class ReportSendingDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        try {
            log.info("Delegate Report sending received the execution");

            validateBusinessRule(execution);

            // Will not be available to the next task execution variables
            execution.setVariableLocal("LOCAL_VARIABLE", "bar");

            createReportVariable(execution);
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
    private void validateBusinessRule(DelegateExecution execution) {
        Order order = (Order) execution.getVariable(Constants.VARIABLE_ORDER_KEY);
        if (!BusinessValidator.validate(order.getAddress())) {
            String errorMessage = String.format("Invalid Email Address:<%s>", order.getAddress());
            BusinessException businessException = new BusinessException(errorMessage);
            log.error(errorMessage, businessException);
            throw businessException;
        }
    }


    /**
     * Create a new report and add it to execution variables.
     *
     * @param execution Delegate execution
     */
    private void createReportVariable(DelegateExecution execution) {
        // create new report and set to new Variable
        Report report = Report.builder().details("foo").build();
        ObjectValue reportObjectValue = Variables.objectValue(report)
                .serializationDataFormat(Variables.SerializationDataFormats.JAVA)
                .create();
        execution.setVariable(Constants.VARIABLE_REPORT_KEY, reportObjectValue);
    }
}
