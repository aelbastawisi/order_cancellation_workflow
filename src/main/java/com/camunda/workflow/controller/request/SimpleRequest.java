package com.camunda.workflow.controller.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
public class SimpleRequest implements Serializable {
    @NotBlank
    private String businessKey;
    private Map<String, Object> processVariables;
}
