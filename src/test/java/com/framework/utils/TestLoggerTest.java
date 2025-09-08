package com.framework.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.WriterAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.StringWriter;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Unit tests for TestLogger class
 */
public class TestLoggerTest {
    
    private StringWriter logOutput;
    private WriterAppender testAppender;
    private TestLogger testLogger;
    
    @BeforeMethod
    public void setUp() {
        // Create a string writer to capture log output
        logOutput = new StringWriter();
        
        // Create a test appender
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();
        
        PatternLayout layout = PatternLayout.newBuilder()
                .withConfiguration(config)
                .withPattern("%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n")
                .build();
        
        testAppender = WriterAppender.newBuilder()
                .setName("TestLoggerAppender")
                .setTarget(logOutput)
                .setLayout(layout)
                .build();
        
        testAppender.start();
        
        // Add appender to framework logger
        LoggerConfig loggerConfig = config.getLoggerConfig("com.framework");
        loggerConfig.addAppender(testAppender, Level.DEBUG, null);
        context.updateLoggers();
        
        testLogger = new TestLogger(TestLoggerTest.class);
    }
    
    @AfterMethod
    public void tearDown() {
        // Remove test appender
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig("com.framework");
        loggerConfig.removeAppender("TestLoggerAppender");
        context.updateLoggers();
        
        testAppender.stop();
    }
    
    @Test
    public void testTestStarted() {
        String testName = "sampleTest";
        String description = "Test login functionality";
        
        testLogger.testStarted(testName, description);
        
        String logContent = logOutput.toString();
        assertTrue(logContent.contains("TEST STARTED"));
        assertTrue(logContent.contains("Test Class: TestLoggerTest"));
        assertTrue(logContent.contains("Test Method: " + testName));
        assertTrue(logContent.contains("Description: " + description));
        assertTrue(logContent.contains("Start Time:"));
    }
    
    @Test
    public void testTestCompletedSuccess() {
        String testName = "successfulTest";
        ITestResult result = mock(ITestResult.class);
        when(result.getStatus()).thenReturn(ITestResult.SUCCESS);
        when(result.getThrowable()).thenReturn(null);
        long duration = 1500;
        
        testLogger.testCompleted(testName, result, duration);
        
        String logContent = logOutput.toString();
        assertTrue(logContent.contains("TEST COMPLETED"));
        assertTrue(logContent.contains("Test Method: " + testName));
        assertTrue(logContent.contains("Status: PASSED"));
        assertTrue(logContent.contains("Duration: " + duration + " ms"));
        assertTrue(logContent.contains("End Time:"));
    }
    
    @Test
    public void testTestCompletedFailure() {
        String testName = "failedTest";
        ITestResult result = mock(ITestResult.class);
        RuntimeException exception = new RuntimeException("Test failed");
        when(result.getStatus()).thenReturn(ITestResult.FAILURE);
        when(result.getThrowable()).thenReturn(exception);
        long duration = 2000;
        
        testLogger.testCompleted(testName, result, duration);
        
        String logContent = logOutput.toString();
        assertTrue(logContent.contains("TEST COMPLETED"));
        assertTrue(logContent.contains("Test Method: " + testName));
        assertTrue(logContent.contains("Status: FAILED"));
        assertTrue(logContent.contains("Duration: " + duration + " ms"));
        assertTrue(logContent.contains("Failure Reason: Test failed"));
    }
    
    @Test
    public void testTestSkipped() {
        String testName = "skippedTest";
        String reason = "Dependency not available";
        
        testLogger.testSkipped(testName, reason);
        
        String logContent = logOutput.toString();
        assertTrue(logContent.contains("TEST SKIPPED"));
        assertTrue(logContent.contains("Test Method: " + testName));
        assertTrue(logContent.contains("Reason: " + reason));
    }
    
    @Test
    public void testTestRetry() {
        String testName = "retryTest";
        int attemptNumber = 2;
        int maxRetries = 3;
        
        testLogger.testRetry(testName, attemptNumber, maxRetries);
        
        String logContent = logOutput.toString();
        assertTrue(logContent.contains("TEST RETRY"));
        assertTrue(logContent.contains("Test Method: " + testName));
        assertTrue(logContent.contains("Attempt: " + attemptNumber + " of " + maxRetries));
    }
    
    @Test
    public void testSuiteStarted() {
        String suiteName = "LoginTestSuite";
        
        testLogger.suiteStarted(suiteName);
        
        String logContent = logOutput.toString();
        assertTrue(logContent.contains("SUITE STARTED"));
        assertTrue(logContent.contains("Suite Name: " + suiteName));
        assertTrue(logContent.contains("Start Time:"));
    }
    
    @Test
    public void testSuiteCompleted() {
        String suiteName = "LoginTestSuite";
        int totalTests = 10;
        int passed = 8;
        int failed = 1;
        int skipped = 1;
        
        testLogger.suiteCompleted(suiteName, totalTests, passed, failed, skipped);
        
        String logContent = logOutput.toString();
        assertTrue(logContent.contains("SUITE COMPLETED"));
        assertTrue(logContent.contains("Suite Name: " + suiteName));
        assertTrue(logContent.contains("Total Tests: " + totalTests));
        assertTrue(logContent.contains("Passed: " + passed));
        assertTrue(logContent.contains("Failed: " + failed));
        assertTrue(logContent.contains("Skipped: " + skipped));
        assertTrue(logContent.contains("Success Rate: 80.0%"));
        assertTrue(logContent.contains("End Time:"));
    }
    
    @Test
    public void testLogAssertionPassed() {
        String assertion = "Login button is displayed";
        Object expected = true;
        Object actual = true;
        boolean passed = true;
        
        testLogger.logAssertion(assertion, expected, actual, passed);
        
        String logContent = logOutput.toString();
        assertTrue(logContent.contains("ASSERTION PASSED"));
        assertTrue(logContent.contains(assertion));
        assertTrue(logContent.contains("Expected: " + expected));
        assertTrue(logContent.contains("Actual: " + actual));
    }
    
    @Test
    public void testLogAssertionFailed() {
        String assertion = "Page title matches expected";
        Object expected = "Login Page";
        Object actual = "Home Page";
        boolean passed = false;
        
        testLogger.logAssertion(assertion, expected, actual, passed);
        
        String logContent = logOutput.toString();
        assertTrue(logContent.contains("ASSERTION FAILED"));
        assertTrue(logContent.contains(assertion));
        assertTrue(logContent.contains("Expected: " + expected));
        assertTrue(logContent.contains("Actual: " + actual));
    }
    
    @Test
    public void testLogDataProvider() {
        String dataProviderName = "loginDataProvider";
        int dataSetIndex = 2;
        int totalDataSets = 5;
        
        testLogger.logDataProvider(dataProviderName, dataSetIndex, totalDataSets);
        
        String logContent = logOutput.toString();
        assertTrue(logContent.contains("DATA PROVIDER: " + dataProviderName));
        assertTrue(logContent.contains("Dataset " + (dataSetIndex + 1) + " of " + totalDataSets));
    }
    
    @Test
    public void testGetLogger() {
        assertNotNull(testLogger.getLogger());
        assertEquals(testLogger.getLogger().getName(), TestLoggerTest.class.getName());
    }
    
    @Test
    public void testSuiteCompletedWithZeroTests() {
        String suiteName = "EmptyTestSuite";
        int totalTests = 0;
        int passed = 0;
        int failed = 0;
        int skipped = 0;
        
        testLogger.suiteCompleted(suiteName, totalTests, passed, failed, skipped);
        
        String logContent = logOutput.toString();
        assertTrue(logContent.contains("Success Rate: 0.0%"));
    }
}