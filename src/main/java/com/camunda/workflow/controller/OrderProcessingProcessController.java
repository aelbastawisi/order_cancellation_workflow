package com.camunda.workflow.controller;

import com.camunda.workflow.controller.request.CancelProcessInstanceRequest;
import com.camunda.workflow.controller.request.OrderRequest;
import com.camunda.workflow.controller.request.SimpleRequest;
import com.camunda.workflow.domain.Order;
import com.camunda.workflow.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.camunda.workflow.util.Constants.*;

@RestController
@RequestMapping("order-process")
@Slf4j
public class OrderProcessingProcessController extends ProcessController {


    private final OrderService orderService;

    public OrderProcessingProcessController(OrderService orderService, RuntimeService runtimeService, TaskService taskService) {
        super(runtimeService, taskService);
        this.orderService = orderService;
    }

    /**
     * Start the process instance
     *
     * @param simpleRequest contain business key for the process to be started
     * @return ResponseEntity
     */
    @PostMapping("start")
    public ResponseEntity<String> startProcess(@RequestBody SimpleRequest simpleRequest) {
        return super.startProcessByMessage(simpleRequest, PROCESS_ORDER_PROCESSING_MESSAGE_KEY);
    }

    /**
     * Complete Create Order Request task using the passed request body.
     *
     * @param orderRequest used in task completion
     * @return ResponseEntity
     */
    @PostMapping("order")
    public ResponseEntity<String> completeOrderTask(@RequestBody OrderRequest orderRequest) {
        Supplier<Map<String, Object>> variableSupplier = () ->
                new HashMap<String, Object>() {{
                    Order order = orderService.saveOrder(orderRequest.getOrder());
                    put(VARIABLE_ORDER_KEY, order);
                    put(VARIABLE_ORDER_ID_KEY, order.getId());
                }};
        return completeUserTask(PROCESS_ORDER_PROCESSING_DEFINITION_KEY,
                USER_TASK_CREATE_ORDER, orderRequest.getBusinessKey(), variableSupplier);
    }


    /**
     * Complete Create Order Request task using the passed request body.
     *
     * @param orderRequest used in task completion
     * @return ResponseEntity
     */
    @PostMapping("prerequisites")
    public ResponseEntity<String> completePrerequisitesTask(@RequestBody OrderRequest orderRequest) {
        Supplier<Map<String, Object>> variableSupplier = () ->
                new HashMap<String, Object>() {{
                    Order order = orderService.saveOrder(orderRequest.getOrder());
                    put(VARIABLE_ORDER_KEY, order);
                }};
        return completeUserTask(PROCESS_ORDER_PROCESSING_DEFINITION_KEY,
                USER_TASK_PROVIDE_PREREQUISITES, orderRequest.getBusinessKey(), variableSupplier);
    }


    /**
     * Complete Review Order task using the passed request body.
     *
     * @param simpleRequest used in task completion
     * @return ResponseEntity
     */
    @PostMapping("receive-order")
    public ResponseEntity<String> completeReviewOrderTask(@RequestBody SimpleRequest simpleRequest) {
        Task task = getUserTask(PROCESS_ORDER_PROCESSING_DEFINITION_KEY,
                USER_TASK_RECEIVE_ORDER, simpleRequest.getBusinessKey());
        Order order = (Order) this.runtimeService.getVariable(task.getExecutionId(), VARIABLE_ORDER_KEY);
        // todo: check order
        return completeUserTask(PROCESS_ORDER_PROCESSING_DEFINITION_KEY,
                USER_TASK_RECEIVE_ORDER, simpleRequest.getBusinessKey(), () -> null);
    }

    /**
     * Complete Location Preview task using the passed request body.
     *
     * @param simpleRequest used in task completion
     * @return ResponseEntity
     */
    @PostMapping
    public ResponseEntity<String> completeProcessOrderTask(@RequestBody SimpleRequest simpleRequest) {
        Task task = getUserTask(PROCESS_ORDER_PROCESSING_DEFINITION_KEY,
                USER_TASK_PROCESS_ORDER, simpleRequest.getBusinessKey());
        Order order = (Order) this.runtimeService.getVariable(task.getExecutionId(), VARIABLE_ORDER_KEY);
        Supplier<Map<String, Object>> variableSupplier = () ->
                new HashMap<String, Object>() {{
                    if (order.getPrerequisites() == null) {
                        put(VARIABLE_REQUIRE_PREREQUISITES_KEY, true);
                    } else {
                        put(VARIABLE_Is_Order_COMPLETED_KEY, true);
                    }
                }};
        return super.completeUserTask(PROCESS_ORDER_PROCESSING_DEFINITION_KEY,
                USER_TASK_PROCESS_ORDER, simpleRequest.getBusinessKey(), variableSupplier);
    }


    /**
     * Complete Make Test task using the passed request body.
     *
     * @param simpleRequest used in task completion
     * @return ResponseEntity
     */
    @PostMapping("preview/appointment")
    public ResponseEntity<String> completeMakeTestTask(@RequestBody SimpleRequest simpleRequest) {
        Task task = getUserTask(PROCESS_ORDER_PROCESSING_DEFINITION_KEY,
                USER_TASK_CREATE_PREVIEW_APPOINTMENT, simpleRequest.getBusinessKey());
        Order order = (Order) this.runtimeService.getVariable(task.getExecutionId(), VARIABLE_ORDER_KEY);
        runtimeService.setVariable(task.getExecutionId(), VARIABLE_APPOINTMENT_KEY, order.getLocationPreviewTime().toString());
        return completeUserTask(PROCESS_ORDER_PROCESSING_DEFINITION_KEY,
                USER_TASK_CREATE_PREVIEW_APPOINTMENT, simpleRequest.getBusinessKey(), () -> null);
    }


    /**
     * Complete Review Report task using the passed request body.
     *
     * @param simpleRequest    used in task completion
     * @return ResponseEntity
     */
    @PostMapping("location-preview")
    public ResponseEntity<String> completeReviewReportTask(@RequestBody SimpleRequest simpleRequest) {
        return completeUserTask(PROCESS_ORDER_PROCESSING_DEFINITION_KEY,
                USER_TASK_PREVIEW_LOCATION, simpleRequest.getBusinessKey(), () -> null);
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
        return completeUserTask(PROCESS_ORDER_PROCESSING_DEFINITION_KEY,
                USER_TASK_REVIEW_REPORT, simpleRequest.getBusinessKey(), variableSupplier);
    }

    /**
     * Cancel current running instance by business key
     *
     * @param cancelProcessInstanceRequest contain business key and cancellation reason
     * @return ResponseEntity
     */
    @DeleteMapping("delete")
    public ResponseEntity<String> cancelProcessInstance(@RequestBody CancelProcessInstanceRequest cancelProcessInstanceRequest) {
        return super.deleteProcessInstance(cancelProcessInstanceRequest, PROCESS_ORDER_PROCESSING_DEFINITION_KEY);
    }
}