package org.app.cosmosdb;

import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/findById". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/findById
     * 2. curl "{your host}/api/findById?id=1111"
     */
    @FunctionName("findById")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @CosmosDBInput(name = "database",
                    databaseName = "my-cosmos-db",
                    collectionName = "cosmos-db",
                    id = "{Query.id}",
                    partitionKey = "{Query.id}",
                    connectionStringSetting = "java_func_connectionString")
                    Optional<String> item,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Item list
        context.getLogger().info("Parameters are: " + request.getQueryParameters());
        context.getLogger().info("String from the database is " + item.get());

        // Convert and display
        if (!item.isPresent()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Document not found.")
                    .build();
        } else {
            // return JSON from Cosmos. Alternatively, we can parse the JSON string
            // and return an enriched JSON object.
            return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(item.get())
                    .build();
        }
    }

    /**
     * This function listens at endpoint "/api/save". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/save&desc={description}
     * 2. curl "{your host}/api/save?desc={description}"
     */
    @FunctionName("save")
    public String storeToDatabase(
            @HttpTrigger(name = "req",
                    methods = {HttpMethod.GET, HttpMethod.POST},
                    authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @CosmosDBOutput(name = "database",
                    databaseName = "my-cosmos-db",
                    collectionName = "cosmos-db",
                    connectionStringSetting = "java_func_connectionString") final ExecutionContext context) {

        // Item list
        context.getLogger().info("Parameters are: " + request.getQueryParameters());

        // Parse query parameter
        String query = request.getQueryParameters().get("full_name");
        String fullName = request.getBody().orElse(query);

        // Generate random ID
        final int id = Math.abs(new Random().nextInt());

        // Generate document
        final String jsonDocument = "{\"id\":\"" + id + "\", " +
                "\"full_name\": \"" + fullName + "\"}";

        context.getLogger().info("Document to be saved: " + jsonDocument);

        return jsonDocument;
    }
}
