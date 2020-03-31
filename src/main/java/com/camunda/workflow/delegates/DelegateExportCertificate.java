package com.camunda.workflow.delegates;

import com.camunda.workflow.controller.ProcessController;
import com.camunda.workflow.domain.Report;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelegateExportCertificate implements JavaDelegate {
    private final Logger logger = LoggerFactory.getLogger(DelegateExportCertificate.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        ObjectValue retrievedTypedObjectValue = execution.getVariableTyped(ProcessController.VARIABLE_REPORT_KEY);
        String serializationDataFormat = retrievedTypedObjectValue.getSerializationDataFormat();
        String valueSerialized = retrievedTypedObjectValue.getValueSerialized();
        Class<?> objectType = retrievedTypedObjectValue.getObjectType();
        if (objectType.getName().equals(Report.class.getName())) {
            Report report = (Report) retrievedTypedObjectValue.getValue();
            logger.info("serializationDataFormat: {} \n" +
                            " valueSerialized: {}\n" +
                            " report: {} ",
                    serializationDataFormat, valueSerialized, report.toString());
        }
        logger.info("{} received execution to export certificate", this.getClass().getName());
    }
}
