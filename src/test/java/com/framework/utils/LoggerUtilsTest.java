package com.framework.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.WriterAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.StringWriter;

import static org.testng.Assert.*;

/**
 * Unit tests for LoggerUtils class
 */
public class LoggerUtilsTest {
    
    private StringWriter logOutput;
    private WriterAppender testAppender;
    private Logger testLogger;
    
    @BeforeMethod
    public void setUp() {
        // Create a string writer to capture log output
        logOutput = new StringWriter();
        
        // Create a test appender
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();
        
        PatternLayout layout = PatternLayout.newBuilder()
                .withConfiguration(config)
                .withPattern("%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %X{testName} %X{browser} %X{environment} - %msg%n")
                .build();
        
        testAppender = WriterAppender.newBuilder()
                .setName("TestAppender")
                .setTarget(logOutput)
                .setLayout(layout)
                .build();
        
        testAppender.start();
        
        // Add appender to framework logger
        LoggerConfig loggerConfig = config.getLoggerConfig("com.framework");
        loggerConfig.addAppender(testAppender, Level.DEBUG, null);
        context.updateLoggers();
        
        testLogger = LogManager.getLogger(LoggerUtilsTest.class);
        
        // Clear any existing context
        ThreadContext.clearAll();
    }
    
    @AfterMethod
    public void tearDown() {
        // Remove test appender
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig("com.framework");
        loggerConfig.removeAppender("TestAppender");
        context.updateLoggers();
        
        testAppender.stop();
        ThreadContext.clearAll();
    }
    
    @Test
    public void testGetLogger() {
        Logger logger = LoggerUtils.getLogger(LoggerUtilsTest.class);
        assertNotNull(logger);
        assertEquals(logger.getName(), LoggerUtilsTest.class.getName());
    }
    
    @Test
    public void testSetTestContext() {
        String testName = "sampleTest";
        String browser = "chrome";
        String environment = "dev";
        
        LoggerUtils.setTestContext(testName, browser, environment);
        
        assertEquals(ThreadContext.get(LoggerUtils.TEST_NAME_KEY), testName);
        assertEquals(ThreadContext.get(LoggerUtils.BROWSER_KEY), browser);
        assertEquals(ThreadContext.get(LoggerUtils.ENVIRONMENT_KEY), environment);
        assertNotNull(ThreadContext.get(LoggerUtils.THREAD_ID_KEY));
        
        String logContent = logOutput.toString();
        assertTrue(logContent.contains("Test context set"));
        assertTrue(logContent.contains(testName));
        assertTrue(logContent.contains(browser));
        assertTrue(logContent.contains(environment));
    }
    
    @Test
    public void testClearTestContext() {
        // Set context first
        LoggerUtils.setTestContext("testMethod", "firefox", "staging");
        
        // Clear context
        LoggerUtils.clearTestContext();
        
        assertNull(ThreadContext.get(LoggerUtils.TEST_NAME_KEY));
        assertNull(ThreadContext.get(LoggerUtils.BROWSER_KEY));
        assertNull(ThreadContext.get(LoggerUtils.ENVIRONMENT_KEY));
        assertNull(ThreadContext.get(LoggerUtils.THREAD_ID_KEY));
        
        String logContent = logOutput.toString();
        assertTrue(logContent.contains("Clearing test context"));
    }
    
    @Test
    public void testLogTestStep() {
        String stepDescription = "Navigate to login page";
        
        LoggerUtils.logTestStep(stepDescription);
        
        String logContent = logOutput.toString();
        assertTrue(logContent.contains("TEST STEP: " + stepDescription));
    }
    
    @Test
    public void testLogTestStepWithMessage() {
        String stepDescription = "Enter username";
        String message = "Using test user credentials";
        
        LoggerUtils.logTestStep(stepDescription, message);
        
        String logContent = logOutput.toString();
        assertTrue(logContent.contains("TEST STEP: " + stepDescription + " - " + message));
    }
    
    @Test
    public void testLogPageAction() {
        String action = "click";
        String element = "loginButton";
        
        LoggerUtils.logPageAction(action, element);
        
        String logContent = logOutput.toString();
        assertTrue(logContent.contains("PAGE ACTION: " + action + " on element: " + element));
    }
    
    @Test
    public void testLogPageActionWithDetails() {
        String action = "type";
        String element = "usernameField";
        String details = "Entering valid username";
        
        LoggerUtils.logPageAction(action, element, details);
        
        String logContent = logOutput.toString();
        assertTrue(logContent.contains("PAGE ACTION: " + action + " on element: " + element + " - " + details));
    }
    
    @Test
    public void testLogDriverAction() {
        String action = "navigate";
        String details = "Opening URL: https://example.com";
        
        LoggerUtils.logDriverAction(action, details);
        
        String logContent = logOutput.toString();
        assertTrue(logContent.contains("DRIVER ACTION: " + action + " - " + details));
    }
    
    @Test
    public void testLogConfigurationLoad() {
        String configType = "browser settings";
        String source = "config.properties";
        
        LoggerUtils.logConfigurationLoad(configType, source);
        
        String logContent = logOutput.toString();
        assertTrue(logContent.contains("CONFIGURATION: Loading " + configType + " from " + source));
    }
    
    @Test
    public void testLogApiCall() {
        String method = "GET";
        String url = "https://api.example.com/users";
        int statusCode = 200;
        
        LoggerUtils.logApiCall(method, url, statusCode);
        
        String logContent = logOutput.toString();
        assertTrue(logContent.contains("API CALL: " + method + " " + url + " - Status: " + statusCode));
    }
    
    @Test
    public void testLogDatabaseOperation() {
        String operation = "SELECT";
        String query = "SELECT * FROM users WHERE id = 1";
        
        LoggerUtils.logDatabaseOperation(operation, query);
        
        String logContent = logOutput.toString();
        assertTrue(logContent.contains("DATABASE: " + operation + " - Query: " + query));
    }
    
    @Test
    public void testLogError() {
        LoggerUtils.setTestContext("errorTest", "chrome", "dev");
        String message = "Element not found";
        Exception exception = new RuntimeException("Test exception");
        
        LoggerUtils.logError(message, exception);
        
        String logContent = logOutput.toString();
        assertTrue(logContent.contains("ERROR in test: errorTest on browser: chrome"));
        assertTrue(logContent.contains(message));
    }
    
    @Test
    public void testLogWarning() {
        LoggerUtils.setTestContext("warningTest", "firefox", "staging");
        String message = "Element took longer than expected to load";
        
        LoggerUtils.logWarning(message);
        
        String logContent = logOutput.toString();
        assertTrue(logContent.contains("WARNING in test: warningTest"));
        assertTrue(logContent.contains(message));
    }
    
    @Test
    public void testLogPerformance() {
        String operation = "Page load";
        long duration = 2500;
        
        LoggerUtils.logPerformance(operation, duration);
        
        String logContent = logOutput.toString();
        assertTrue(logContent.contains("PERFORMANCE: " + operation + " completed in " + duration + " ms"));
    }
    
    @Test
    public void testLogScreenshot() {
        String screenshotPath = "/screenshots/test_failure_123.png";
        String reason = "Test failure";
        
        LoggerUtils.logScreenshot(screenshotPath, reason);
        
        String logContent = logOutput.toString();
        assertTrue(logContent.contains("SCREENSHOT: Captured at " + screenshotPath + " - Reason: " + reason));
    }
    
    @Test
    public void testContextInformation() {
        String testName = "contextTest";
        String browser = "edge";
        String environment = "prod";
        
        LoggerUtils.setTestContext(testName, browser, environment);
        LoggerUtils.logTestStep("Test step with context");
        
        String logContent = logOutput.toString();
        assertTrue(logContent.contains(testName));
        assertTrue(logContent.contains(browser));
        assertTrue(logContent.contains(environment));
    }
}