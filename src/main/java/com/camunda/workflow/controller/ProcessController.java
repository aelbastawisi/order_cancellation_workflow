package com.camunda.workflow.controller;

import com.camunda.workflow.controller.request.CancelProcessInstanceRequest;
import com.camunda.workflow.controller.request.SimpleRequest;
import com.camunda.workflow.controller.request.UserTaskRequest;
import com.camunda.workflow.domain.Order;
import com.camunda.workflow.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.MessageCorrelationResult;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.camunda.workflow.util.Constants.*;

@RestController
@RequestMapping("process")
@Slf4j
public class ProcessController implements Serializable {

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final OrderService orderService;

    public ProcessController(RuntimeService runtimeService, TaskService taskService, OrderService orderService) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.orderService = orderService;
    }

    private ProcessInstance startProcess(String businessKey) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION_KEY, businessKey);
        log.info("Process: {} started", processInstance.getProcessDefinitionId());
        return processInstance;
    }

    /**
     * Start the process instance
     *
     * @param simpleRequest contain business key for the process to be started
     * @return ResponseEntity
     */
    @PostMapping("start")
    public ResponseEntity<String> startProcessByMessage(@RequestBody SimpleRequest simpleRequest) {
        // correlate the message
        MessageCorrelationResult result = runtimeService.createMessageCorrelation(PROCESS_MESSAGE_KEY)
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
     * Complete Create Order Request task using the passed request body.
     *
     * @param userTaskRequest used in task completion
     * @return ResponseEntity
     */
    @PostMapping("request/create")
    public ResponseEntity<String> completeOrderTask(@RequestBody UserTaskRequest userTaskRequest) {
        Supplier<Map<String, Object>> variableSupplier = () ->
                new HashMap<String, Object>() {{
                    Order order = orderService.saveOrder(userTaskRequest.getOrder());
                    put(VARIABLE_ORDER_KEY, order);
                    put(VARIABLE_ORDER_ID_KEY, order.getId());
                }};
        return completeUserTask(USER_TASK_ORDER_ID, userTaskRequest.getBusinessKey(), variableSupplier);
    }


    /**
     * Complete Review Order task using the passed request body.
     *
     * @param simpleRequest     used in task completion
     * @param isReviewCompleted param
     * @return ResponseEntity
     */
    @PostMapping("review-order")
    public ResponseEntity<String> completeReviewOrderTask(@RequestBody SimpleRequest simpleRequest,
                                                          @RequestParam boolean isReviewCompleted) {
        Supplier<Map<String, Object>> variableSupplier = () ->
                new HashMap<String, Object>() {{
                    put(VARIABLE_IS_REVIEW_COMPLETED_KEY, isReviewCompleted);
                }};
        return completeUserTask(USER_TASK_REVIEW_ORDER_ID, simpleRequest.getBusinessKey(), variableSupplier);
    }

    /**
     * Complete Location Preview task using the passed request body.
     *
     * @param simpleRequest      used in task completion
     * @param isLocationNeedTest param
     * @return ResponseEntity
     */
    @PostMapping("location-preview")
    public ResponseEntity<String> completeLocationPreviewTask(@RequestBody SimpleRequest simpleRequest,
                                                              @RequestParam boolean isLocationNeedTest) {
        Supplier<Map<String, Object>> variableSupplier = () ->
                new HashMap<String, Object>() {{
                    put(VARIABLE_IS_LOCATION_NEED_TEST_KEY, isLocationNeedTest);
                }};
        return completeUserTask(USER_TASK_LOCATION_PREVIEW_ID, simpleRequest.getBusinessKey(), variableSupplier);
    }


    /**
     * Complete Make Test task using the passed request body.
     *
     * @param simpleRequest used in task completion
     * @return ResponseEntity
     */
    @PostMapping("make-test")
    public ResponseEntity<String> completeMakeTestTask(@RequestBody SimpleRequest simpleRequest) {
        return completeUserTask(USER_TASK_MAKE_TEST_ID, simpleRequest.getBusinessKey(), () -> null);
    }


    /**
     * Complete Review Report task using the passed request body.
     *
     * @param simpleRequest    used in task completion
     * @param isReviewReportOk param
     * @return ResponseEntity
     */
    @PostMapping("review-report")
    public ResponseEntity<String> completeReviewReportTask(@RequestBody SimpleRequest simpleRequest,
                                                           @RequestParam boolean isReviewReportOk) {
        Supplier<Map<String, Object>> variableSupplier = () ->
                new HashMap<String, Object>() {{
                    put(VARIABLE_IS_REVIEW_REPORT_OK_KEY, isReviewReportOk);
                }};
        return completeUserTask(USER_TASK_REVIEW_REPORT_ID, simpleRequest.getBusinessKey(), variableSupplier);
    }


    /**
     * Handle Business exception thrown in the process execution.
     * For now it update the Order object of the process and move the flow sequence to Review Order task to
     * process the order again after updating.
     *
     * @param userTaskRequest contain the updated order object.
     * @return ResponseEntity
     */
    @PostMapping("handle-businessEX")
    public ResponseEntity<String> handleBusinessEX(@RequestBody UserTaskRequest userTaskRequest) {
        Task task = getUserTask(USER_TASK_HANDLE_BUSINESS_EX_ID, userTaskRequest.getBusinessKey());
        Supplier<Map<String, Object>> variableSupplier = () ->
                new HashMap<String, Object>() {{
                    Long orderId = (Long) runtimeService.getVariable(task.getExecutionId(), VARIABLE_ORDER_ID_KEY);
                    Order order = orderService.updateOrderAccordingToBR(orderId, userTaskRequest.getOrder());
                    put(VARIABLE_ORDER_KEY, order);
                }};
        return completeUserTask(USER_TASK_HANDLE_BUSINESS_EX_ID, userTaskRequest.getBusinessKey(), variableSupplier);
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
    private ResponseEntity<String> completeUserTask(String taskDefinitionKey,
                                                    String businessKey,
                                                    Supplier<Map<String, Object>> variableSupplier) {
        Task task = getUserTask(taskDefinitionKey, businessKey);
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
    @DeleteMapping("delete")
    public ResponseEntity<String> cancelProcessInstance(@RequestBody CancelProcessInstanceRequest cancelProcessInstanceRequest) {
        List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(cancelProcessInstanceRequest.getBusinessKey())
                .processDefinitionKey(PROCESS_DEFINITION_KEY)
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
    private Task getUserTask(String taskDefinitionKey, String businessKey) {
        log.info("Retrieving task with taskDefinitionKey: {} and businessKey:{}",
                taskDefinitionKey, businessKey);
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processDefinitionKey(PROCESS_DEFINITION_KEY)
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
