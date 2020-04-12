package com.camunda.workflow.delegates.order_processing;

import com.camunda.workflow.controller.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import static com.camunda.workflow.util.Constants.BUSINESS_ERROR_CODE;
import static com.camunda.workflow.util.Constants.TECH_ERROR_CODE;


@Slf4j
public class DelegateExportCertificate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        try {
            log.info("DelegateExportCertificate received the execution");
        } catch (BusinessException ex) {
            execution.setVariable(BUSINESS_ERROR_CODE, ex.getMessage());
            throw new BpmnError(BUSINESS_ERROR_CODE);
        } catch (Exception ex) {
            execution.setVariable(TECH_ERROR_CODE, ex.getMessage());
            throw new BpmnError(TECH_ERROR_CODE);
        }
    }

}
