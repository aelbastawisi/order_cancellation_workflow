package com.camunda.workflow.controller.listeners;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

@Slf4j
public class ProcessExecutionListener implements ExecutionListener {


    @Override
    public void notify(DelegateExecution execution) throws Exception {
        String currentActivityName = execution.getCurrentActivityName();
        log.info("Execution received {}", currentActivityName);
    }
}
