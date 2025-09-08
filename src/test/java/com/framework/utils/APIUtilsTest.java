package com.framework.utils;

import com.framework.config.ConfigManager;
import com.framework.config.TestConfig;
import io.restassured.response.Response;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * Unit tests for APIUtils class
 */
public class APIUtilsTest {
    
    @Mock
    private ConfigManager mockConfigManager;
    
    @Mock
    private TestConfig mockTestConfig;
    
    @Mock
    private Response mockResponse;
    
    private APIUtils apiUtils;
    
    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Mock configuration
        when(mockConfigManager.getTestConfig()).thenReturn(mockTestConfig);
        when(mockTestConfig.getApiBaseUrl()).thenReturn("https://api.example.com");
        when(mockTestConfig.getApiTimeout()).thenReturn(30);
        
        apiUtils = new APIUtils("https://api.example.com", 30);
    }
    
    @Test
    public void testConstructorWithDefaults() {
        // This test would require mocking static ConfigManager.getInstance()
        // For now, we'll test the parameterized constructor
        APIUtils utils = new APIUtils("https://test.com");
        Assert.assertNotNull(utils);
        Assert.assertEquals(utils.getBaseUrl(), "https://test.com");
    }
    
    @Test
    public void testConstructorWithBaseUrlAndTimeout() {
        APIUtils utils = new APIUtils("https://test.com", 60);
        Assert.assertNotNull(utils);
        Assert.assertEquals(utils.getBaseUrl(), "https://test.com");
        Assert.assertEquals(utils.getTimeout(), 60);
    }
    
    @Test
    public void testSetDefaultHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        
        apiUtils.setDefaultHeaders(headers);
        // Verify headers are set (would need to test through actual request)
    }
    
    @Test
    public void testAddDefaultHeader() {
        apiUtils.addDefaultHeader("X-API-Key", "test-key");
        // Verify header is added (would need to test through actual request)
    }
    
    @Test
    public void testRemoveDefaultHeader() {
        apiUtils.addDefaultHeader("X-API-Key", "test-key");
        apiUtils.removeDefaultHeader("X-API-Key");
        // Verify header is removed (would need to test through actual request)
    }
    
    @Test
    public void testValidateStatusCode() {
        when(mockResponse.getStatusCode()).thenReturn(200);
        
        boolean result = apiUtils.validateStatusCode(mockResponse, 200);
        Assert.assertTrue(result);
        
        boolean falseResult = apiUtils.validateStatusCode(mockResponse, 404);
        Assert.assertFalse(falseResult);
    }
    
    @Test
    public void testValidateHeader() {
        when(mockResponse.getHeader("Content-Type")).thenReturn("application/json");
        
        boolean result = apiUtils.validateHeader(mockResponse, "Content-Type", "application/json");
        Assert.assertTrue(result);
        
        boolean falseResult = apiUtils.validateHeader(mockResponse, "Content-Type", "text/html");
        Assert.assertFalse(falseResult);
    }
    
    @Test
    public void testValidateContentType() {
        when(mockResponse.getContentType()).thenReturn("application/json; charset=utf-8");
        
        boolean result = apiUtils.validateContentType(mockResponse, "application/json");
        Assert.assertTrue(result);
        
        boolean falseResult = apiUtils.validateContentType(mockResponse, "text/html");
        Assert.assertFalse(falseResult);
    }
    
    @Test
    public void testValidateResponseTime() {
        when(mockResponse.getTime()).thenReturn(500L);
        
        boolean result = apiUtils.validateResponseTime(mockResponse, 1000L);
        Assert.assertTrue(result);
        
        boolean falseResult = apiUtils.validateResponseTime(mockResponse, 300L);
        Assert.assertFalse(falseResult);
    }
    
    @Test
    public void testConvertToJson() {
        Map<String, Object> testObject = new HashMap<>();
        testObject.put("name", "John");
        testObject.put("age", 30);
        
        String json = apiUtils.convertToJson(testObject);
        Assert.assertNotNull(json);
        Assert.assertTrue(json.contains("\"name\":\"John\""));
        Assert.assertTrue(json.contains("\"age\":30"));
    }
    
    @Test
    public void testConvertFromJson() {
        String json = "{\"name\":\"John\",\"age\":30}";
        
        @SuppressWarnings("unchecked")
        Map<String, Object> result = apiUtils.convertFromJson(json, Map.class);
        
        Assert.assertNotNull(result);
        Assert.assertEquals(result.get("name"), "John");
        Assert.assertEquals(result.get("age"), 30);
    }
    
    @Test
    public void testParseJson() {
        String json = "{\"name\":\"John\",\"age\":30}";
        
        var jsonNode = apiUtils.parseJson(json);
        
        Assert.assertNotNull(jsonNode);
        Assert.assertEquals(jsonNode.get("name").asText(), "John");
        Assert.assertEquals(jsonNode.get("age").asInt(), 30);
    }
    
    @Test
    public void testValidateResponseBodyContains() {
        when(mockResponse.getBody()).thenReturn(mock(io.restassured.response.ResponseBody.class));
        when(mockResponse.getBody().asString()).thenReturn("This is a test response body");
        
        boolean result = apiUtils.validateResponseBodyContains(mockResponse, "test response");
        Assert.assertTrue(result);
        
        boolean falseResult = apiUtils.validateResponseBodyContains(mockResponse, "not found");
        Assert.assertFalse(falseResult);
    }
    
    @Test
    public void testValidateResponseBodyMatches() {
        when(mockResponse.getBody()).thenReturn(mock(io.restassured.response.ResponseBody.class));
        when(mockResponse.getBody().asString()).thenReturn("123-456-7890");
        
        boolean result = apiUtils.validateResponseBodyMatches(mockResponse, "\\d{3}-\\d{3}-\\d{4}");
        Assert.assertTrue(result);
        
        boolean falseResult = apiUtils.validateResponseBodyMatches(mockResponse, "\\d{4}-\\d{4}-\\d{4}");
        Assert.assertFalse(falseResult);
    }
    
    @Test
    public void testGetResponseBody() {
        when(mockResponse.getBody()).thenReturn(mock(io.restassured.response.ResponseBody.class));
        when(mockResponse.getBody().asString()).thenReturn("Test response body");
        
        String result = apiUtils.getResponseBody(mockResponse);
        Assert.assertEquals(result, "Test response body");
    }
    
    @Test
    public void testCreateBasicAuthHeader() {
        String authHeader = apiUtils.createBasicAuthHeader("user", "pass");
        Assert.assertNotNull(authHeader);
        Assert.assertTrue(authHeader.startsWith("Basic "));
        
        // Decode and verify
        String encoded = authHeader.substring(6);
        String decoded = new String(java.util.Base64.getDecoder().decode(encoded));
        Assert.assertEquals(decoded, "user:pass");
    }
    
    @Test
    public void testCreateBearerAuthHeader() {
        String token = "abc123token";
        String authHeader = apiUtils.createBearerAuthHeader(token);
        Assert.assertEquals(authHeader, "Bearer abc123token");
    }
    
    @Test
    public void testSetBasicAuth() {
        apiUtils.setBasicAuth("user", "pass");
        // Verify that the Authorization header is set (would need to test through actual request)
    }
    
    @Test
    public void testSetBearerAuth() {
        apiUtils.setBearerAuth("token123");
        // Verify that the Authorization header is set (would need to test through actual request)
    }
    
    @Test
    public void testClearAuth() {
        apiUtils.setBearerAuth("token123");
        apiUtils.clearAuth();
        // Verify that the Authorization header is removed (would need to test through actual request)
    }
    
    @Test
    public void testGetBaseUrl() {
        Assert.assertEquals(apiUtils.getBaseUrl(), "https://api.example.com");
    }
    
    @Test
    public void testGetTimeout() {
        Assert.assertEquals(apiUtils.getTimeout(), 30);
    }
    
    // Integration-style tests that would work with a real API endpoint
    // These are commented out as they require an actual API to test against
    
    /*
    @Test
    public void testGetRequest() {
        // This would require a real API endpoint
        APIUtils realApiUtils = new APIUtils("https://jsonplaceholder.typicode.com");
        Response response = realApiUtils.get("/posts/1");
        
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertTrue(realApiUtils.validateContentType(response, "application/json"));
    }
    
    @Test
    public void testPostRequest() {
        APIUtils realApiUtils = new APIUtils("https://jsonplaceholder.typicode.com");
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", "Test Post");
        requestBody.put("body", "Test Body");
        requestBody.put("userId", 1);
        
        Response response = realApiUtils.post("/posts", requestBody);
        
        Assert.assertEquals(response.getStatusCode(), 201);
        Assert.assertTrue(realApiUtils.validateJsonField(response, "title", "Test Post"));
    }
    */
}