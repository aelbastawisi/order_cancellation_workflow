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
@RequestMapping("process")
@Slf4j
public class OrderCancellationProcessController extends ProcessController {

    private final OrderService orderService;

    public OrderCancellationProcessController(OrderService orderService, RuntimeService runtimeService, TaskService taskService) {
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
        return super.startProcessByMessage(simpleRequest, PROCESS_MESSAGE_KEY);
    }

    /**
     * Complete Create Order Request task using the passed request body.
     *
     * @param orderRequest used in task completion
     * @return ResponseEntity
     */
    @PostMapping("request/create")
    public ResponseEntity<String> completeOrderTask(@RequestBody OrderRequest orderRequest) {
        Supplier<Map<String, Object>> variableSupplier = () ->
                new HashMap<String, Object>() {{
                    Order order = orderService.saveOrder(orderRequest.getOrder());
                    put(VARIABLE_ORDER_KEY, order);
                    put(VARIABLE_ORDER_ID_KEY, order.getId());
                }};
        return completeUserTask(PROCESS_CANCEL_CERTIFICATE_DEFINITION_KEY,
                USER_TASK_ORDER_ID, orderRequest.getBusinessKey(), variableSupplier);
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
        return completeUserTask(PROCESS_CANCEL_CERTIFICATE_DEFINITION_KEY,
                USER_TASK_REVIEW_ORDER_ID, simpleRequest.getBusinessKey(), variableSupplier);
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
        return super.completeUserTask(PROCESS_CANCEL_CERTIFICATE_DEFINITION_KEY,
                USER_TASK_LOCATION_PREVIEW_ID, simpleRequest.getBusinessKey(), variableSupplier);
    }


    /**
     * Complete Make Test task using the passed request body.
     *
     * @param simpleRequest used in task completion
     * @return ResponseEntity
     */
    @PostMapping("make-test")
    public ResponseEntity<String> completeMakeTestTask(@RequestBody SimpleRequest simpleRequest) {
        return completeUserTask(PROCESS_CANCEL_CERTIFICATE_DEFINITION_KEY,
                USER_TASK_MAKE_TEST_ID, simpleRequest.getBusinessKey(), () -> null);
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
        return completeUserTask(PROCESS_CANCEL_CERTIFICATE_DEFINITION_KEY,
                USER_TASK_REVIEW_REPORT_ID, simpleRequest.getBusinessKey(), variableSupplier);
    }


    /**
     * Handle Business exception thrown in the process execution.
     * For now it update the Order object of the process and move the flow sequence to Review Order task to
     * process the order again after updating.
     *
     * @param orderRequest contain the updated order object.
     * @return ResponseEntity
     */
    @PostMapping("handle-businessEX")
    public ResponseEntity<String> handleBusinessEX(@RequestBody OrderRequest orderRequest) {
        Task task = getUserTask(PROCESS_CANCEL_CERTIFICATE_DEFINITION_KEY,
                USER_TASK_HANDLE_BUSINESS_EX_ID, orderRequest.getBusinessKey());
        Supplier<Map<String, Object>> variableSupplier = () ->
                new HashMap<String, Object>() {{
                    Long orderId = (Long) runtimeService.getVariable(task.getExecutionId(), VARIABLE_ORDER_ID_KEY);
                    Order order = orderService.updateOrderAccordingToBR(orderId, orderRequest.getOrder());
                    put(VARIABLE_ORDER_KEY, order);
                }};
        return completeUserTask(PROCESS_CANCEL_CERTIFICATE_DEFINITION_KEY,
                USER_TASK_HANDLE_BUSINESS_EX_ID, orderRequest.getBusinessKey(), variableSupplier);
    }


    /**
     * Cancel current running instance by business key
     *
     * @param cancelProcessInstanceRequest contain business key and cancellation reason
     * @return ResponseEntity
     */
    @DeleteMapping("delete")
    public ResponseEntity<String> cancelProcessInstance(@RequestBody CancelProcessInstanceRequest cancelProcessInstanceRequest) {
        return super.deleteProcessInstance(cancelProcessInstanceRequest, PROCESS_CANCEL_CERTIFICATE_DEFINITION_KEY);
    }


}
