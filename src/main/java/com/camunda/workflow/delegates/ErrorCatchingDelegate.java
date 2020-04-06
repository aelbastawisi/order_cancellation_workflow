package com.camunda.workflow.delegates;

import com.camunda.workflow.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

@Slf4j
public class ErrorCatchingDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("Got an error: {}", execution.getVariable(Constants.TECH_ERROR_CODE));
    }
}
