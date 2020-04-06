package com.camunda.workflow.controller.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class CancelProcessInstanceRequest extends SimpleRequest {
    private String cancellationReason;
}
