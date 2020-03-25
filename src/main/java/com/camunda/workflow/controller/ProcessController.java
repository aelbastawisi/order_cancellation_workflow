package com.camunda.workflow.controller;

import com.camunda.workflow.domain.Order;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("process")
public class ProcessController implements Serializable {
    // CANCEL CERTIFICATION PROCESS KEY
    private static final String PROCESS_DEFINITION_KEY = "cancelCertificateProcess";

    // USER TASKS
    private static final String USER_TASK_REVIEW_ORDER_ID = "Activity_reviewOrder";
    private static final String USER_TASK_ORDER_ID = "Activity_order";
    private static final String USER_TASK_LOCATION_PREVIEW_ID = "Activity_locationPreview";
    private static final String USER_TASK_MAKE_TEST_ID = "Activity_makeTest";
    private static final String USER_TASK_REVIEW_REPORT_ID = "Activity_reviewReport";

    // PROCESS VARIABLES
    public static final String VARIABLE_PROCESS_DEFINITION_ID_KEY = "PROCESS_DEFINITION_ID";
    public static final String VARIABLE_ORDER_KEY = "ORDER";
    public static final String VARIABLE_IS_REVIEW_COMPLETED_KEY = "isReviewCompleted";
    public static final String VARIABLE_IS_LOCATION_NEED_TEST_KEY = "isLocationNeedTest";
    public static final String VARIABLE_IS_REVIEW_REPORT_OK_KEY = "isReviewReportOk";

    private final Logger logger = LoggerFactory.getLogger(ProcessController.class);

    private final RuntimeService runtimeService;

    private final TaskService taskService;

    public ProcessController(RuntimeService runtimeService, TaskService taskService) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
    }

    private ProcessInstance startProcess(String businessKey) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION_KEY, businessKey);
        logger.info("Process: {} started", processInstance.getProcessDefinitionId());
        return processInstance;
    }

    @GetMapping("order")
    public ResponseEntity<String> completeOrderTask(@RequestParam(defaultValue = "false") boolean startNewProcessInstance) {
        Order dummyOrder = Order.createDummyOrder();
        ProcessInstance processInstance = null;
        if(startNewProcessInstance) {
            processInstance = startProcess(dummyOrder.getId());
        }
        Map<String, Object> variables = new HashMap<>();
        variables.put(VARIABLE_PROCESS_DEFINITION_ID_KEY, processInstance !=null ? processInstance.getProcessDefinitionId(): null);
        variables.put(VARIABLE_ORDER_KEY, dummyOrder);
        return completeUserTask(processInstance !=null ? processInstance.getProcessDefinitionId() : null,
                USER_TASK_ORDER_ID, dummyOrder.getId(),
                variables);
    }


    @GetMapping("review-order")
    public ResponseEntity<String> completeReviewOrderTask(@RequestParam boolean isReviewCompleted) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(VARIABLE_IS_REVIEW_COMPLETED_KEY, isReviewCompleted);
        return completeUserTask(null, USER_TASK_REVIEW_ORDER_ID, null, variables);
    }

    @GetMapping("location-preview")
    public ResponseEntity<String> completeLocationPreviewTask(@RequestParam boolean isLocationNeedTest) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(VARIABLE_IS_LOCATION_NEED_TEST_KEY, isLocationNeedTest);
        return completeUserTask(null, USER_TASK_LOCATION_PREVIEW_ID, null, variables);
    }


    @GetMapping("make-test")
    public ResponseEntity<String> completeMakeTestTask() {
        return completeUserTask(null, USER_TASK_MAKE_TEST_ID, null, null);
    }


    @GetMapping("review-report")
    public ResponseEntity<String> completeReviewReportTask(@RequestParam boolean isReviewReportOk) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(VARIABLE_IS_REVIEW_REPORT_OK_KEY, isReviewReportOk);
        return completeUserTask(null, USER_TASK_REVIEW_REPORT_ID, null, variables);
    }

    private ResponseEntity<String> completeUserTask(String processDefinitionId, String taskDefinitionKey, String businessKey, Map<String, Object> variables) {
        Task task = getUserTask(processDefinitionId, taskDefinitionKey, businessKey);
        if (task != null) {
            taskService.complete(task.getId(), variables);
            return ResponseEntity.status(HttpStatus.OK).body("Task: " + taskDefinitionKey + " completed");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task: " + taskDefinitionKey + " not found");
    }


    private Task getUserTask(String processDefinitionId, String taskDefinitionKey, String businessKey) {
        logger.info("Retrieving task with processDefinitionId: {} and taskDefinitionKey: {} and businessKey:{}",
                processDefinitionId, taskDefinitionKey, businessKey);
        TaskQuery taskQuery = taskService.createTaskQuery()
                .processDefinitionKey(PROCESS_DEFINITION_KEY)
                .taskDefinitionKey(taskDefinitionKey);
        if (processDefinitionId != null) {
            taskQuery = taskQuery.processDefinitionId(processDefinitionId);
        }
        if (businessKey != null) {
            taskQuery = taskQuery.processInstanceBusinessKey(businessKey);
        }
        List<Task> tasks = taskQuery.listPage(0, 1);
        Task foundedTask = tasks != null && !tasks.isEmpty() ? tasks.get(0) : null;
        logger.info("Founded task {}", foundedTask);
        return foundedTask;
    }
}
