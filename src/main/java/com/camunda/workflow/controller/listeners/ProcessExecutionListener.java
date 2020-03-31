package com.camunda.workflow.controller.listeners;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

public class ProcessExecutionListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) throws Exception {
        System.out.println(execution);
    }
}
