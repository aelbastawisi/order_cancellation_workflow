package com.camunda.workflow.util;

public class Constants {
    // PROCESS VARIABLES
    public static final String VARIABLE_PROCESS_INSTANCE_ID_KEY = "PROCESS_INSTANCE_ID";
    public static final String VARIABLE_ORDER_KEY = "ORDER";
    public static final String VARIABLE_ORDER_ID_KEY = "ORDER_ID";
    public static final String VARIABLE_APPOINTMENT_KEY = "Appointment";
    public static final String VARIABLE_REPORT_KEY = "Report";
    public static final String VARIABLE_IS_REVIEW_COMPLETED_KEY = "isReviewCompleted";
    public static final String VARIABLE_IS_LOCATION_NEED_TEST_KEY = "isLocationNeedTest";
    public static final String VARIABLE_IS_REVIEW_REPORT_OK_KEY = "isReviewReportOk";

    //Error Keys
    public static final String TECH_ERROR_CODE = "Error_Technical";
    public static final String BUSINESS_ERROR_CODE = "Error_Business";

    // CANCEL CERTIFICATION PROCESS KEY
    public static final String PROCESS_DEFINITION_KEY = "cancelCertificateProcess";
    public static final String PROCESS_MESSAGE_KEY = "Message_startProcess";

    // USER TASKS
    public static final String USER_TASK_ORDER_ID = "Activity_createOrder";
    public static final String USER_TASK_REVIEW_ORDER_ID = "Activity_reviewOrder";
    public static final String USER_TASK_LOCATION_PREVIEW_ID = "Activity_locationPreview";
    public static final String USER_TASK_MAKE_TEST_ID = "Activity_makeTest";
    public static final String USER_TASK_REVIEW_REPORT_ID = "Activity_reviewReport";
    public static final String USER_TASK_HANDLE_BUSINESS_EX_ID = "Activity_handleBusinessEX";

}
