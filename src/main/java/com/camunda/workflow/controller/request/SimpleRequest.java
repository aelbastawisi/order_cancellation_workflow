package com.camunda.workflow.controller.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class SimpleRequest implements Serializable {
    private String businessKey;
}
