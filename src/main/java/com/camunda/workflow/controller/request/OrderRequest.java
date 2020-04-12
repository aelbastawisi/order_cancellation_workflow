package com.camunda.workflow.controller.request;

import com.camunda.workflow.domain.Order;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class OrderRequest extends SimpleRequest {
    private Order order;
}
