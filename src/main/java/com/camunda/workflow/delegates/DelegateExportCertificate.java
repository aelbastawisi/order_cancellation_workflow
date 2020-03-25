package com.camunda.workflow.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelegateExportCertificate implements JavaDelegate {
    private final Logger logger = LoggerFactory.getLogger(DelegateExportCertificate.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        logger.info("{} received execution to export certificate", this.getClass().getName());
    }
}
