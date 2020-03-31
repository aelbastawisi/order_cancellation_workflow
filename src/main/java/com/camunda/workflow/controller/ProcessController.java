package com.camunda.workflow.controller;

import com.camunda.workflow.controller.request.SimpleRequest;
import com.camunda.workflow.controller.request.UserTaskRequest;
import com.camunda.workflow.service.OrderService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.MessageCorrelationResult;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@RestController
@RequestMapping("process")
public class ProcessController implements Serializable {
    // PROCESS VARIABLES
    public static final String VARIABLE_PROCESS_DEFINITION_ID_KEY = "PROCESS_DEFINITION_ID";
    public static final String VARIABLE_ORDER_KEY = "ORDER";
    public static final String VARIABLE_APPOINTMENT_KEY = "Appointment";
    public static final String VARIABLE_REPORT_KEY = "Report";
    public static final String VARIABLE_IS_REVIEW_COMPLETED_KEY = "isReviewCompleted";
    public static final String VARIABLE_IS_LOCATION_NEED_TEST_KEY = "isLocationNeedTest";
    public static final String VARIABLE_IS_REVIEW_REPORT_OK_KEY = "isReviewReportOk";

    // CANCEL CERTIFICATION PROCESS KEY
    private static final String PROCESS_DEFINITION_KEY = "cancelCertificateProcess";
    private static final String PROCESS_MESSAGE_KEY = "Message_startProcess";

    // USER TASKS
    private static final String USER_TASK_REVIEW_ORDER_ID = "Activity_reviewOrder";
    private static final String USER_TASK_ORDER_ID = "Activity_order";
    private static final String USER_TASK_LOCATION_PREVIEW_ID = "Activity_locationPreview";
    private static final String USER_TASK_MAKE_TEST_ID = "Activity_makeTest";
    private static final String USER_TASK_REVIEW_REPORT_ID = "Activity_reviewReport";


    private final Logger logger = LoggerFactory.getLogger(ProcessController.class);

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
        logger.info("Process: {} started", processInstance.getProcessDefinitionId());
        return processInstance;
    }

    @PostMapping("start")
    public ResponseEntity<String> startProcessByMessage(@RequestBody SimpleRequest simpleRequest) {
        // correlate the message
        MessageCorrelationResult result = runtimeService.createMessageCorrelation(PROCESS_MESSAGE_KEY)
                .processInstanceBusinessKey(simpleRequest.getBusinessKey())
                .correlateWithResult();
        String response = String.format("Process: %s started", result.getProcessInstance().getProcessDefinitionId());
        logger.info(response);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("request/create")
    public ResponseEntity<String> completeOrderTask(@RequestBody UserTaskRequest userTaskRequest) {
        Supplier<Map<String, Object>> variableSupplier = () ->
                new HashMap<String, Object>() {{
                    put(VARIABLE_ORDER_KEY, orderService.saveOrder(userTaskRequest.getOrder()));
                }};
        return completeUserTask(USER_TASK_ORDER_ID, userTaskRequest.getBusinessKey(), variableSupplier);
    }


    @PostMapping("review-order")
    public ResponseEntity<String> completeReviewOrderTask(@RequestBody SimpleRequest simpleRequest,
                                                          @RequestParam boolean isReviewCompleted) {
        Supplier<Map<String, Object>> variableSupplier = () ->
                new HashMap<String, Object>() {{
                    put(VARIABLE_IS_REVIEW_COMPLETED_KEY, isReviewCompleted);
                }};
        return completeUserTask(USER_TASK_REVIEW_ORDER_ID, simpleRequest.getBusinessKey(), variableSupplier);
    }

    @PostMapping("location-preview")
    public ResponseEntity<String> completeLocationPreviewTask(@RequestBody SimpleRequest simpleRequest,
                                                              @RequestParam boolean isLocationNeedTest) {
        Supplier<Map<String, Object>> variableSupplier = () ->
                new HashMap<String, Object>() {{
                    put(VARIABLE_IS_LOCATION_NEED_TEST_KEY, isLocationNeedTest);
                }};
        return completeUserTask(USER_TASK_LOCATION_PREVIEW_ID, simpleRequest.getBusinessKey(), variableSupplier);
    }


    @PostMapping("make-test")
    public ResponseEntity<String> completeMakeTestTask(@RequestBody SimpleRequest simpleRequest) {
        return completeUserTask(USER_TASK_MAKE_TEST_ID, simpleRequest.getBusinessKey(), () -> null);
    }


    @PostMapping("review-report")
    public ResponseEntity<String> completeReviewReportTask(@RequestBody SimpleRequest simpleRequest,
                                                           @RequestParam boolean isReviewReportOk) {
        Supplier<Map<String, Object>> variableSupplier = () ->
                new HashMap<String, Object>() {{
                    put(VARIABLE_IS_REVIEW_REPORT_OK_KEY, isReviewReportOk);
                }};
        return completeUserTask(USER_TASK_REVIEW_REPORT_ID, simpleRequest.getBusinessKey(), variableSupplier);
    }

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


    private Task getUserTask(String taskDefinitionKey, String businessKey) {
        logger.info("Retrieving task with taskDefinitionKey: {} and businessKey:{}",
                taskDefinitionKey, businessKey);
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processDefinitionKey(PROCESS_DEFINITION_KEY)
                .taskDefinitionKey(taskDefinitionKey);
        if (businessKey != null) {
            taskQuery = taskQuery.processInstanceBusinessKey(businessKey);
        }
        List<Task> tasks = taskQuery.listPage(0, 1);
        Task foundedTask = tasks != null && !tasks.isEmpty() ? tasks.get(0) : null;
        logger.info("Founded task {}", foundedTask);
        return foundedTask;
    }
}
