package com.camunda.workflow.delegates;

import com.camunda.workflow.controller.ProcessController;
import com.camunda.workflow.domain.Order;
import com.camunda.workflow.domain.Report;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportSendingDelegate implements JavaDelegate {
    public static final String ERROR_CODE = "Error_sendReport";
    private final Logger logger = LoggerFactory.getLogger(ReportSendingDelegate.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        logger.info("Delegate Report sending received the execution");
        Order order = (Order) execution.getVariable(ProcessController.VARIABLE_ORDER_KEY);
        if (order.getId() % 2 == 0L) {
            execution.setVariable(ERROR_CODE, "Some Dummy error message");
            throw new BpmnError(ERROR_CODE);
        }
        // Will not be available to the next task execution variables
        execution.setVariableLocal("LOCAL_VARIABLE", "bar");

        // create new report and set to new Variable
        Report report = Report.builder().id(123L).details("foo").build();
        ObjectValue reportObjectValue = Variables.objectValue(report)
                .serializationDataFormat(Variables.SerializationDataFormats.JAVA)
                .create();
        execution.setVariable(ProcessController.VARIABLE_REPORT_KEY, reportObjectValue);
    }
}
