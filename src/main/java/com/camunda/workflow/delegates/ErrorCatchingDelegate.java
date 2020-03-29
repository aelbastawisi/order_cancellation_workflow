package com.camunda.workflow.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorCatchingDelegate implements JavaDelegate {
    private final Logger logger = LoggerFactory.getLogger(ErrorCatchingDelegate.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        logger.info("Got an error: {}", execution.getVariable(ReportSendingDelegate.ERROR_CODE));

    }
}
