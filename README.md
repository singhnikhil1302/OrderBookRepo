# OrderBookRepo
The Application is OrderBook Management. The technology stack used to develop the OrderBook Management are: 
1. JDK 8 SpringBoot Mockito Libraries (Junit) The APIs for OrderBookManagement app are built using Spring Rest. 
2. The APIs developed are: 
    CreateOrderBook : Creates an OrderBook
    AddOrder : Creates new Orders for the OrderBook
    CloseOrderBook : Closes an OrderBook 
    AddExecutions: Adds execution to the closed OrderBooks and verfies the valid demand Logic 
    GetOrderDetails : For a given Order Id, all the Order attributes viz. will be fetched.
    
 3. Swagger plugins are injected as dpendencies which will help the user with API documentation and also in Testing the abopve mentioned       APIs
 4. Lombok Dependencies are also included. Incase of any compilation errors due to Lombok follow the steps as mentioned:
    -- After cloning the repository, build the workspace using maven.
    -- The Lombok jar will be created by maven in the .m2 folder of the user's machine. To locate it just do a basic search on your PC.
    -- Now run the Lombok.jar as an executable and mention the IDEs (Eclipse, STS) in which you want to configure Lombok.
    -- Post this step include the Lombok.jar in the buildpath of your project.
    -- restart the IDE and do a clean build. All the compilation errors will go
    
 5. An in-memory DB dependency in terms of H2 has been added.This makes the application extensible , if the user wants any other backend repository.  The only additional effort user will haved to do is to configure the application.properties file with details about the new repository.
 6. For Online Streaming of logs, in cloud environments like PCF follow the below steps:
    -- Use either LogBack / Log4j2 to so that the logs can be easily indexed by Lostash and sent to elastic search for storage.
    -- Configure the Kibana console to poll the elastic search at regular intervals to stream the real time log data.
  
 7. For scaling the application, client side load balancing can be used. In order to configure it use Ribbon and Spring Cloud components.
    
    
    
    
