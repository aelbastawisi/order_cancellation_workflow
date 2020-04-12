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
    public static final String VARIABLE_IS_REVIEW_REPORT_OK_KEY = "Is_Review_Report_OK";
    public static final String VARIABLE_REQUIRE_PREREQUISITES_KEY = "Are_Prerequisites_Required";
    public static final String VARIABLE_IS_CONFLICTED_KEY = "Is_Conflicted";
    public static final String VARIABLE_Is_Order_COMPLETED_KEY = "Is_Order_COMPLETED";

    //Error Keys
    public static final String TECH_ERROR_CODE = "Error_Technical";
    public static final String BUSINESS_ERROR_CODE = "Error_Business";

    // CANCEL CERTIFICATION PROCESS KEY
    public static final String PROCESS_CANCEL_CERTIFICATE_DEFINITION_KEY = "cancelCertificateProcess";
    public static final String PROCESS_MESSAGE_KEY = "Message_startProcess";

    // ORDER PROCESSING PROCESS KEY
    public static final String PROCESS_ORDER_PROCESSING_DEFINITION_KEY = "Process_Order_Processing";
    public static final String PROCESS_ORDER_PROCESSING_MESSAGE_KEY = "Message_Start_Order_Processing";

    // ORDER CANCELLATION USER TASKS
    public static final String USER_TASK_ORDER_ID = "Activity_createOrder";
    public static final String USER_TASK_REVIEW_ORDER_ID = "Activity_reviewOrder";
    public static final String USER_TASK_LOCATION_PREVIEW_ID = "Activity_locationPreview";
    public static final String USER_TASK_MAKE_TEST_ID = "Activity_makeTest";
    public static final String USER_TASK_REVIEW_REPORT_ID = "Activity_reviewReport";
    public static final String USER_TASK_HANDLE_BUSINESS_EX_ID = "Activity_handleBusinessEX";


    public static final String START_ORDER_PROCESSING_MESSAGE_KEY = "Message_Start_Order_Processing";


    // ORDER PROCESSING USER TASKS
    public static final String USER_TASK_CREATE_ORDER = "Activity_Create_Order";
    public static final String USER_TASK_PROVIDE_PREREQUISITES = "Activity_Provide_Prerequisites";
    public static final String USER_TASK_RECEIVE_ORDER = "Activity_Receive_Order";
    public static final String USER_TASK_PROCESS_ORDER = "Activity_Manual_Order_Processing";
    public static final String USER_TASK_CREATE_PREVIEW_APPOINTMENT = "Activity_Create_Preview_Appointment";
    public static final String USER_TASK_PREVIEW_LOCATION = "Activity_Preview_Location";
    public static final String USER_TASK_REVIEW_REPORT = "Activity_Review_Report";


}
