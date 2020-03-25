package com.camunda.workflow;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

//@Configuration
public class CamundaConfig implements Serializable {

    @Bean
    public ProcessEngine processEngine(){
        return ProcessEngines.getDefaultProcessEngine();
    }


    @Bean
    public RuntimeService camundaRuntimeService(ProcessEngine processEngine){
        return processEngine.getRuntimeService();
    }

    @Bean
    public TaskService taskService(ProcessEngine processEngine){
        return processEngine.getTaskService();
    }
}
