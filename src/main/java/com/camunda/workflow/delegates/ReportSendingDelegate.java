package com.camunda.workflow.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportSendingDelegate implements JavaDelegate {
    private final Logger logger = LoggerFactory.getLogger(ReportSendingDelegate.class);
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        logger.info("Delegate Report sending received the execution");
        execution.setVariable("Report", "Report 1");
    }
}
