package com.framework.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.framework.config.ConfigManager;
import com.framework.exceptions.FrameworkException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

/**
 * APIUtils provides REST API testing utilities using RestAssured
 * Includes request/response validation and JSON/XML parsing capabilities
 */
public class APIUtils {
    
    private final String baseUrl;
    private final int timeout;
    private final ObjectMapper objectMapper;
    private Map<String, String> defaultHeaders;
    
    /**
     * Constructor with default configuration
     */
    public APIUtils() {
        ConfigManager configManager = ConfigManager.getInstance();
        this.baseUrl = configManager.getTestConfig().getApiBaseUrl();
        this.timeout = configManager.getTestConfig().getApiTimeout();
        this.objectMapper = new ObjectMapper();
        this.defaultHeaders = new HashMap<>();
        
        // Set base URI for RestAssured
        if (baseUrl != null && !baseUrl.isEmpty()) {
            RestAssured.baseURI = baseUrl;
        }
        
        // Set default timeout
        RestAssured.config = RestAssured.config().httpClient(
            RestAssured.config().getHttpClientConfig().setParam("http.connection.timeout", timeout * 1000)
                .setParam("http.socket.timeout", timeout * 1000)
        );
    }
    
    /**
     * Constructor with custom base URL
     * @param baseUrl custom base URL for API
     */
    public APIUtils(String baseUrl) {
        this.baseUrl = baseUrl;
        this.timeout = ConfigManager.getInstance().getTestConfig().getApiTimeout();
        this.objectMapper = new ObjectMapper();
        this.defaultHeaders = new HashMap<>();
        
        RestAssured.baseURI = baseUrl;
        RestAssured.config = RestAssured.config().httpClient(
            RestAssured.config().getHttpClientConfig().setParam("http.connection.timeout", timeout * 1000)
                .setParam("http.socket.timeout", timeout * 1000)
        );
    }
    
    /**
     * Constructor with custom base URL and timeout
     * @param baseUrl custom base URL for API
     * @param timeout custom timeout in seconds
     */
    public APIUtils(String baseUrl, int timeout) {
        this.baseUrl = baseUrl;
        this.timeout = timeout;
        this.objectMapper = new ObjectMapper();
        this.defaultHeaders = new HashMap<>();
        
        RestAssured.baseURI = baseUrl;
        RestAssured.config = RestAssured.config().httpClient(
            RestAssured.config().getHttpClientConfig().setParam("http.connection.timeout", timeout * 1000)
                .setParam("http.socket.timeout", timeout * 1000)
        );
    }
    
    /**
     * Sets default headers for all requests
     * @param headers map of header name-value pairs
     */
    public void setDefaultHeaders(Map<String, String> headers) {
        this.defaultHeaders = new HashMap<>(headers);
    }
    
    /**
     * Adds a default header
     * @param name header name
     * @param value header value
     */
    public void addDefaultHeader(String name, String value) {
        this.defaultHeaders.put(name, value);
    }
    
    /**
     * Removes a default header
     * @param name header name to remove
     */
    public void removeDefaultHeader(String name) {
        this.defaultHeaders.remove(name);
    }
    
    /**
     * Creates a request specification with default headers
     * @return RequestSpecification with default configuration
     */
    private RequestSpecification createRequestSpec() {
        RequestSpecification requestSpec = RestAssured.given();
        
        // Add default headers
        for (Map.Entry<String, String> header : defaultHeaders.entrySet()) {
            requestSpec.header(header.getKey(), header.getValue());
        }
        
        return requestSpec;
    }
    
    /**
     * Performs GET request
     * @param endpoint API endpoint
     * @return Response object
     */
    public Response get(String endpoint) {
        return createRequestSpec().get(endpoint);
    }
    
    /**
     * Performs GET request with query parameters
     * @param endpoint API endpoint
     * @param queryParams query parameters
     * @return Response object
     */
    public Response get(String endpoint, Map<String, Object> queryParams) {
        return createRequestSpec().queryParams(queryParams).get(endpoint);
    }
    
    /**
     * Performs GET request with headers
     * @param endpoint API endpoint
     * @param headers request headers
     * @return Response object
     */
    public Response get(String endpoint, Map<String, String> headers, Map<String, Object> queryParams) {
        RequestSpecification requestSpec = createRequestSpec();
        
        if (headers != null) {
            requestSpec.headers(headers);
        }
        
        if (queryParams != null) {
            requestSpec.queryParams(queryParams);
        }
        
        return requestSpec.get(endpoint);
    }
    
    /**
     * Performs POST request with JSON body
     * @param endpoint API endpoint
     * @param requestBody request body object
     * @return Response object
     */
    public Response post(String endpoint, Object requestBody) {
        return createRequestSpec()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post(endpoint);
    }
    
    /**
     * Performs POST request with JSON body and headers
     * @param endpoint API endpoint
     * @param requestBody request body object
     * @param headers request headers
     * @return Response object
     */
    public Response post(String endpoint, Object requestBody, Map<String, String> headers) {
        RequestSpecification requestSpec = createRequestSpec()
                .contentType(ContentType.JSON)
                .body(requestBody);
        
        if (headers != null) {
            requestSpec.headers(headers);
        }
        
        return requestSpec.post(endpoint);
    }
    
    /**
     * Performs POST request with form parameters
     * @param endpoint API endpoint
     * @param formParams form parameters
     * @return Response object
     */
    public Response postForm(String endpoint, Map<String, Object> formParams) {
        return createRequestSpec()
                .contentType(ContentType.URLENC)
                .formParams(formParams)
                .post(endpoint);
    }
    
    /**
     * Performs PUT request with JSON body
     * @param endpoint API endpoint
     * @param requestBody request body object
     * @return Response object
     */
    public Response put(String endpoint, Object requestBody) {
        return createRequestSpec()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .put(endpoint);
    }
    
    /**
     * Performs PUT request with JSON body and headers
     * @param endpoint API endpoint
     * @param requestBody request body object
     * @param headers request headers
     * @return Response object
     */
    public Response put(String endpoint, Object requestBody, Map<String, String> headers) {
        RequestSpecification requestSpec = createRequestSpec()
                .contentType(ContentType.JSON)
                .body(requestBody);
        
        if (headers != null) {
            requestSpec.headers(headers);
        }
        
        return requestSpec.put(endpoint);
    }
    
    /**
     * Performs PATCH request with JSON body
     * @param endpoint API endpoint
     * @param requestBody request body object
     * @return Response object
     */
    public Response patch(String endpoint, Object requestBody) {
        return createRequestSpec()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .patch(endpoint);
    }
    
    /**
     * Performs DELETE request
     * @param endpoint API endpoint
     * @return Response object
     */
    public Response delete(String endpoint) {
        return createRequestSpec().delete(endpoint);
    }
    
    /**
     * Performs DELETE request with headers
     * @param endpoint API endpoint
     * @param headers request headers
     * @return Response object
     */
    public Response delete(String endpoint, Map<String, String> headers) {
        RequestSpecification requestSpec = createRequestSpec();
        
        if (headers != null) {
            requestSpec.headers(headers);
        }
        
        return requestSpec.delete(endpoint);
    }
    
    /**
     * Validates response status code
     * @param response Response object
     * @param expectedStatusCode expected status code
     * @return true if status code matches
     */
    public boolean validateStatusCode(Response response, int expectedStatusCode) {
        return response.getStatusCode() == expectedStatusCode;
    }
    
    /**
     * Validates response contains specific header
     * @param response Response object
     * @param headerName header name
     * @param expectedValue expected header value
     * @return true if header matches
     */
    public boolean validateHeader(Response response, String headerName, String expectedValue) {
        String actualValue = response.getHeader(headerName);
        return expectedValue.equals(actualValue);
    }
    
    /**
     * Validates response content type
     * @param response Response object
     * @param expectedContentType expected content type
     * @return true if content type matches
     */
    public boolean validateContentType(Response response, String expectedContentType) {
        String actualContentType = response.getContentType();
        return actualContentType != null && actualContentType.contains(expectedContentType);
    }
    
    /**
     * Validates response time is within limit
     * @param response Response object
     * @param maxResponseTime maximum response time in milliseconds
     * @return true if response time is within limit
     */
    public boolean validateResponseTime(Response response, long maxResponseTime) {
        return response.getTime() <= maxResponseTime;
    }
    
    /**
     * Extracts JSON value from response using JSONPath
     * @param response Response object
     * @param jsonPath JSONPath expression
     * @return extracted value
     */
    public Object extractJsonValue(Response response, String jsonPath) {
        try {
            return response.jsonPath().get(jsonPath);
        } catch (Exception e) {
            throw new FrameworkException("Failed to extract JSON value using path: " + jsonPath, e);
        }
    }
    
    /**
     * Extracts XML value from response using XPath
     * @param response Response object
     * @param xpath XPath expression
     * @return extracted value
     */
    public Object extractXmlValue(Response response, String xpath) {
        try {
            return response.xmlPath().get(xpath);
        } catch (Exception e) {
            throw new FrameworkException("Failed to extract XML value using XPath: " + xpath, e);
        }
    }
    
    /**
     * Converts response body to POJO
     * @param response Response object
     * @param clazz target class
     * @param <T> type parameter
     * @return deserialized object
     */
    public <T> T convertResponseToPojo(Response response, Class<T> clazz) {
        try {
            return response.as(clazz);
        } catch (Exception e) {
            throw new FrameworkException("Failed to convert response to POJO: " + clazz.getSimpleName(), e);
        }
    }
    
    /**
     * Converts object to JSON string
     * @param object object to convert
     * @return JSON string
     */
    public String convertToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new FrameworkException("Failed to convert object to JSON", e);
        }
    }
    
    /**
     * Converts JSON string to object
     * @param json JSON string
     * @param clazz target class
     * @param <T> type parameter
     * @return deserialized object
     */
    public <T> T convertFromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new FrameworkException("Failed to convert JSON to object: " + clazz.getSimpleName(), e);
        }
    }
    
    /**
     * Parses JSON string to JsonNode
     * @param json JSON string
     * @return JsonNode object
     */
    public JsonNode parseJson(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new FrameworkException("Failed to parse JSON string", e);
        }
    }
    
    /**
     * Validates JSON schema (basic validation)
     * @param response Response object
     * @param requiredFields array of required field names
     * @return true if all required fields are present
     */
    public boolean validateJsonSchema(Response response, String[] requiredFields) {
        try {
            JsonNode jsonNode = parseJson(response.getBody().asString());
            
            for (String field : requiredFields) {
                if (!jsonNode.has(field)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Validates JSON field value
     * @param response Response object
     * @param fieldPath JSON path to field
     * @param expectedValue expected value
     * @return true if field value matches
     */
    public boolean validateJsonField(Response response, String fieldPath, Object expectedValue) {
        try {
            Object actualValue = extractJsonValue(response, fieldPath);
            return expectedValue.equals(actualValue);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Validates response body contains specific text
     * @param response Response object
     * @param expectedText expected text
     * @return true if response body contains text
     */
    public boolean validateResponseBodyContains(Response response, String expectedText) {
        String responseBody = response.getBody().asString();
        return responseBody.contains(expectedText);
    }
    
    /**
     * Validates response body matches regex pattern
     * @param response Response object
     * @param pattern regex pattern
     * @return true if response body matches pattern
     */
    public boolean validateResponseBodyMatches(Response response, String pattern) {
        String responseBody = response.getBody().asString();
        return responseBody.matches(pattern);
    }
    
    /**
     * Gets response body as string
     * @param response Response object
     * @return response body as string
     */
    public String getResponseBody(Response response) {
        return response.getBody().asString();
    }
    
    /**
     * Gets response headers as map
     * @param response Response object
     * @return headers as map
     */
    public Map<String, String> getResponseHeaders(Response response) {
        Map<String, String> headers = new HashMap<>();
        response.getHeaders().forEach(header -> 
            headers.put(header.getName(), header.getValue())
        );
        return headers;
    }
    
    /**
     * Logs request and response details
     * @param response Response object
     */
    public void logRequestResponse(Response response) {
        System.out.println("=== API Request/Response Log ===");
        System.out.println("Request URL: " + response.getSessionId());
        System.out.println("Response Status: " + response.getStatusCode());
        System.out.println("Response Time: " + response.getTime() + "ms");
        System.out.println("Response Headers: " + getResponseHeaders(response));
        System.out.println("Response Body: " + getResponseBody(response));
        System.out.println("================================");
    }
    
    /**
     * Creates authentication header for Basic Auth
     * @param username username
     * @param password password
     * @return authorization header value
     */
    public String createBasicAuthHeader(String username, String password) {
        String credentials = username + ":" + password;
        return "Basic " + java.util.Base64.getEncoder().encodeToString(credentials.getBytes());
    }
    
    /**
     * Creates authentication header for Bearer token
     * @param token bearer token
     * @return authorization header value
     */
    public String createBearerAuthHeader(String token) {
        return "Bearer " + token;
    }
    
    /**
     * Sets basic authentication
     * @param username username
     * @param password password
     */
    public void setBasicAuth(String username, String password) {
        addDefaultHeader("Authorization", createBasicAuthHeader(username, password));
    }
    
    /**
     * Sets bearer token authentication
     * @param token bearer token
     */
    public void setBearerAuth(String token) {
        addDefaultHeader("Authorization", createBearerAuthHeader(token));
    }
    
    /**
     * Clears authentication headers
     */
    public void clearAuth() {
        removeDefaultHeader("Authorization");
    }
    
    /**
     * Gets the base URL
     * @return base URL
     */
    public String getBaseUrl() {
        return baseUrl;
    }
    
    /**
     * Gets the timeout
     * @return timeout in seconds
     */
    public int getTimeout() {
        return timeout;
    }
}