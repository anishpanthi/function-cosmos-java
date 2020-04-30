package org.app.cosmosdb;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.CosmosDBInput;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Optional;

/**
 * @author Anish
 * Azure Functions with HTTP Trigger.
 */
public class ReadFunction {
    /**
     * This function listens at endpoint "/api/findById". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/findById
     * 2. curl "{your host}/api/findById?id=1111"
     */
    @FunctionName("findById")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = AuthorizationLevel.ANONYMOUS)
                    HttpRequestMessage<Optional<String>> request,
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
}
