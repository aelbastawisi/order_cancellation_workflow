package com.camunda.workflow.delegates;

import com.camunda.workflow.controller.exceptions.BusinessException;
import com.camunda.workflow.domain.Report;
import com.camunda.workflow.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.value.ObjectValue;

import static com.camunda.workflow.util.Constants.BUSINESS_ERROR_CODE;
import static com.camunda.workflow.util.Constants.TECH_ERROR_CODE;

@Slf4j
public class DelegateExportCertificate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        try {
            ObjectValue retrievedTypedObjectValue = execution.getVariableTyped(Constants.VARIABLE_REPORT_KEY);
            String serializationDataFormat = retrievedTypedObjectValue.getSerializationDataFormat();
            String valueSerialized = retrievedTypedObjectValue.getValueSerialized();
            Class<?> objectType = retrievedTypedObjectValue.getObjectType();
            if (objectType.getName().equals(Report.class.getName())) {
                Report report = (Report) retrievedTypedObjectValue.getValue();
                log.info("serializationDataFormat: {} \n" +
                                " valueSerialized: {}\n" +
                                " report: {} ",
                        serializationDataFormat, valueSerialized, report.toString());
            }
            log.info("{} received execution to export certificate", this.getClass().getName());
        } catch (BusinessException ex) {
            execution.setVariable(BUSINESS_ERROR_CODE, ex.getMessage());
            throw new BpmnError(BUSINESS_ERROR_CODE);
        } catch (Exception ex) {
            execution.setVariable(TECH_ERROR_CODE, ex.getMessage());
            throw new BpmnError(TECH_ERROR_CODE);
        }
    }
}
