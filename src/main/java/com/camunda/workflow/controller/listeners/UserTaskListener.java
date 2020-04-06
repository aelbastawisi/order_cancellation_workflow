package com.camunda.workflow.controller.listeners;

import com.camunda.workflow.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;

@Slf4j
public class UserTaskListener implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
       if(Constants.USER_TASK_LOCATION_PREVIEW_ID.equals( delegateTask.getTaskDefinitionKey())) {
           Object variable = delegateTask.getVariable(Constants.VARIABLE_APPOINTMENT_KEY);
       }
        log.info("Current user task {}",delegateTask.getTaskDefinitionKey());
    }
}
