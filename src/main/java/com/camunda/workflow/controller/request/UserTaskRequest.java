package com.camunda.workflow.controller.request;

import com.camunda.workflow.domain.Order;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserTaskRequest extends SimpleRequest {
    private Order order;
}
