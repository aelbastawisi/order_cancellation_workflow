# Camunda
> Camunda is a Java-based framework supporting BPMN for workflow and process automation, CMMN for Case Management and DMN for Business Decision Management
Here's an image of a drag racer in action:

###Overview of Camunda

![Camunda](https://docs.camunda.org/manual/7.7/introduction/img/architecture-overview.png)

* **Process Engine** <br />
The process engine is a Java library responsible for executing BPMN 2.0 processes, 
CMMN 1.1 cases and DMN 1.1 decisions.
It has a lightweight POJO core and uses a relational database for persistence. 
ORM mapping is provided by the MyBatis mapping framework.


* **Camunda Modeler** <br />
Camunda Modeler is a desktop application for modeling BPMN, DMN and CMMN. 
It allows you to model files located directly on your local file system.



* **Web Applications**
    * [REST API](https://docs.camunda.org/manual/7.7/reference/rest/): 
        The goal of the REST API is to provide access to all relevant interfaces of the engine.
    
    * [Camunda Tasklist](https://docs.camunda.org/manual/7.7/webapps/tasklist/) <br />
        Tasklist is a web application that allows you to work on User Tasks. It allows you
        Start a Process, Create a Filter, Claim Tasks, Working on Tasks,
         Set Follow-Up Date (reminder), Comment a Task and Set Due Date
        
     ![TaskList](https://docs.camunda.org/manual/7.7/webapps/tasklist/img/tasklist-dashboard.png)
        
    * [Camunda Cockpit](https://docs.camunda.org/manual/7.7/webapps/cockpit/) <br />
        Camunda BPM Cockpit is a web application for monitoring and operations. 
        It provides access to deployed BPMN processes and DMN decisions, 
        allows searching though running and ended instances and performing operations on these. <br />
        
        It consists of 
        [Dashboard](https://docs.camunda.org/manual/7.7/webapps/cockpit/dashboard/),
        [BPMN Processes](https://docs.camunda.org/manual/7.7/webapps/cockpit/bpmn/),
        [DMN Decisions](https://docs.camunda.org/manual/7.7/webapps/cockpit/dmn/),
        [CMMN Cases](https://docs.camunda.org/manual/7.7/webapps/cockpit/cmmn/), 
        [Deployments](https://docs.camunda.org/manual/7.7/webapps/cockpit/deployment-view/), 
        [Auditing](https://docs.camunda.org/manual/7.7/webapps/cockpit/auditing/) and 
        [Reports](https://docs.camunda.org/manual/7.7/webapps/cockpit/reporting/)
         ![Cockpit](https://docs.camunda.org/manual/7.7/webapps/cockpit/img/dashboard.png)
    
    * [Camunda Admin](https://docs.camunda.org/manual/7.7/webapps/admin/) <br />
         A web application that allows you to manage users, groups and authorizations.
        ![Admin](https://docs.camunda.org/manual/7.7/webapps/admin/img/admin-start-page-view.png)


###Architecture Overview
Camunda BPM can be used both as a standalone process engine server or embedded inside custom Java applications.

**Process Engine Architecture**<br />
![](https://docs.camunda.org/manual/7.7/introduction/img/process-engine-architecture.png)

* **[Process Engine Public API](https://docs.camunda.org/manual/7.7/user-guide/process-engine/process-engine-api/)**<br/>
Service-oriented API allowing Java applications to interact with the process engine. 
The different responsibilities of the process engine (i.e., Process Repository, Runtime Process Interaction, Task Management, …) are separated into individual services.
 The public API features a command-style access pattern.  ProcessEngine and the services objects are thread safe. So you can keep a reference to 1 of those for a whole server.
 
* **BPMN 2.0 Core Engine**<br/>
This is the core of the process engine. It features a lightweight execution engine for graph structures 
(PVM - Process Virtual Machine), a BPMN 2.0 parser which transforms BPMN 2.0 XML files into Java Objects 
and a set of BPMN Behavior implementations (providing the implementation for BPMN 2.0 constructs such as Gateways or Service Tasks).


* **[Job Executor](https://docs.camunda.org/manual/7.7/user-guide/process-engine/the-job-executor/)** <br/>
The Job Executor is responsible for processing asynchronous background work such as Timers or asynchronous continuations in a process.


* **The Persistence Layer**<br/>
The process engine features a persistence layer responsible for persisting process instance state to a relational database.
 Camunda uses the MyBatis mapping engine for object relational mapping.
