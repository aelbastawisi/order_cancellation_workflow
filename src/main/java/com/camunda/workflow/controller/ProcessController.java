package com.camunda.workflow.controller;

import com.camunda.workflow.controller.request.CancelProcessInstanceRequest;
import com.camunda.workflow.controller.request.SimpleRequest;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.MessageCorrelationResult;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.camunda.workflow.util.Constants.PROCESS_CANCEL_CERTIFICATE_DEFINITION_KEY;
import static com.camunda.workflow.util.Constants.VARIABLE_PROCESS_INSTANCE_ID_KEY;

@Slf4j
public class ProcessController implements Serializable {

    protected final RuntimeService runtimeService;
    protected final TaskService taskService;

    public ProcessController(RuntimeService runtimeService, TaskService taskService) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
    }

    private ProcessInstance startProcess(String businessKey) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_CANCEL_CERTIFICATE_DEFINITION_KEY, businessKey);
        log.info("Process: {} started", processInstance.getProcessDefinitionId());
        return processInstance;
    }

    /**
     * Start the process instance
     *
     * @param simpleRequest contain business key for the process to be started
     * @return ResponseEntity
     */
    protected ResponseEntity<String> startProcessByMessage(@Valid SimpleRequest simpleRequest, String processStartMessageKey) {
        // correlate the message
        MessageCorrelationResult result = runtimeService.createMessageCorrelation(processStartMessageKey)
                .processInstanceBusinessKey(simpleRequest.getBusinessKey())
                .correlateWithResult();
        ProcessInstance processInstance = result.getProcessInstance();
        String response = String.format("Process: %s started", processInstance.getProcessDefinitionId());
        runtimeService.setVariable(processInstance.getProcessInstanceId(),
                VARIABLE_PROCESS_INSTANCE_ID_KEY, processInstance.getProcessInstanceId());
        log.info(response);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Search for task by taskDefinitionKey and businessKey and if task exists, complete it using
     * the variable supplied by the passed variableSupplier
     *
     * @param taskDefinitionKey for the task
     * @param businessKey       for the process instance
     * @param variableSupplier  to supply the variable to be committed when completing the task
     * @return ResponseEntity
     */
    protected ResponseEntity<String> completeUserTask(String processDefinitionKey,
                                                      String taskDefinitionKey,
                                                      String businessKey,
                                                      Supplier<Map<String, Object>> variableSupplier) {
        Task task = getUserTask(processDefinitionKey, taskDefinitionKey, businessKey);
        if (task != null) {
            taskService.complete(task.getId(), variableSupplier.get());
            return ResponseEntity.status(HttpStatus.OK).body("Task: " + taskDefinitionKey + " completed");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task: " + taskDefinitionKey + " not found");
    }

    /**
     * Cancel current running instance by business key
     *
     * @param cancelProcessInstanceRequest contain business key and cancellation reason
     * @return ResponseEntity
     */
    protected ResponseEntity<String> deleteProcessInstance(@Valid CancelProcessInstanceRequest cancelProcessInstanceRequest,
                                                           String processDefinitionKey) {
        List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(cancelProcessInstanceRequest.getBusinessKey())
                .processDefinitionKey(processDefinitionKey)
                .list();
        if (processInstances != null && !processInstances.isEmpty()) {
            StringBuilder deletedStringBuilder = new StringBuilder("Deleted Process Instances: \n");
            processInstances.forEach(processInstance -> {
                runtimeService.deleteProcessInstance(
                        processInstance.getProcessInstanceId(), cancelProcessInstanceRequest.getCancellationReason()
                );
                deletedStringBuilder.append(processInstance.getProcessInstanceId()).append("\n");
            });
            return ResponseEntity.status(HttpStatus.OK).body(deletedStringBuilder.toString());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Process instances found");
    }


    /**
     * Find task by taskDefinitionKey and process instance businessKey
     *
     * @param taskDefinitionKey for task
     * @param businessKey       for process instance
     * @return task if found or null
     */
    protected Task getUserTask(String processDefinitionKey, String taskDefinitionKey, String businessKey) {
        log.info("Retrieving task with taskDefinitionKey: {} and businessKey:{}",
                taskDefinitionKey, businessKey);
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processDefinitionKey(processDefinitionKey)
                .taskDefinitionKey(taskDefinitionKey);
        if (businessKey != null) {
            taskQuery = taskQuery.processInstanceBusinessKey(businessKey);
        }
        List<Task> tasks = taskQuery.listPage(0, 1);
        Task foundedTask = tasks != null && !tasks.isEmpty() ? tasks.get(0) : null;
        log.info("Founded task {}", foundedTask);
        return foundedTask;
    }
}
