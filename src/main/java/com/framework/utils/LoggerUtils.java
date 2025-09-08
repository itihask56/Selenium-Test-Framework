package com.framework.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

/**
 * Utility class for logging operations across the framework
 * Provides centralized logging functionality with context information
 */
public class LoggerUtils {
    
    private static final Logger logger = LogManager.getLogger(LoggerUtils.class);
    
    // Context keys for additional information
    public static final String TEST_NAME_KEY = "testName";
    public static final String BROWSER_KEY = "browser";
    public static final String ENVIRONMENT_KEY = "environment";
    public static final String THREAD_ID_KEY = "threadId";
    
    /**
     * Private constructor to prevent instantiation
     */
    private LoggerUtils() {
        throw new IllegalStateException("Utility class");
    }
    
    /**
     * Get logger instance for a specific class
     * @param clazz The class for which to get the logger
     * @return Logger instance
     */
    public static Logger getLogger(Class<?> clazz) {
        return LogManager.getLogger(clazz);
    }
    
    /**
     * Set test context information for logging
     * @param testName Name of the current test
     * @param browser Browser being used
     * @param environment Environment being tested
     */
    public static void setTestContext(String testName, String browser, String environment) {
        ThreadContext.put(TEST_NAME_KEY, testName);
        ThreadContext.put(BROWSER_KEY, browser);
        ThreadContext.put(ENVIRONMENT_KEY, environment);
        ThreadContext.put(THREAD_ID_KEY, String.valueOf(Thread.currentThread().getId()));
        
        logger.info("Test context set - Test: {}, Browser: {}, Environment: {}, Thread: {}", 
                   testName, browser, environment, Thread.currentThread().getId());
    }
    
    /**
     * Clear test context information
     */
    public static void clearTestContext() {
        String testName = ThreadContext.get(TEST_NAME_KEY);
        logger.info("Clearing test context for test: {}", testName);
        ThreadContext.clearAll();
    }
    
    /**
     * Log test step with INFO level
     * @param stepDescription Description of the test step
     */
    public static void logTestStep(String stepDescription) {
        logger.info("TEST STEP: {}", stepDescription);
    }
    
    /**
     * Log test step with custom message and INFO level
     * @param stepDescription Description of the test step
     * @param message Additional message
     */
    public static void logTestStep(String stepDescription, String message) {
        logger.info("TEST STEP: {} - {}", stepDescription, message);
    }
    
    /**
     * Log page action with INFO level
     * @param action Action being performed
     * @param element Element being acted upon
     */
    public static void logPageAction(String action, String element) {
        logger.info("PAGE ACTION: {} on element: {}", action, element);
    }
    
    /**
     * Log page action with additional details
     * @param action Action being performed
     * @param element Element being acted upon
     * @param details Additional details about the action
     */
    public static void logPageAction(String action, String element, String details) {
        logger.info("PAGE ACTION: {} on element: {} - {}", action, element, details);
    }
    
    /**
     * Log WebDriver action with DEBUG level
     * @param action WebDriver action being performed
     * @param details Details about the action
     */
    public static void logDriverAction(String action, String details) {
        logger.debug("DRIVER ACTION: {} - {}", action, details);
    }
    
    /**
     * Log configuration loading with INFO level
     * @param configType Type of configuration being loaded
     * @param source Source of the configuration
     */
    public static void logConfigurationLoad(String configType, String source) {
        logger.info("CONFIGURATION: Loading {} from {}", configType, source);
    }
    
    /**
     * Log API call with INFO level
     * @param method HTTP method
     * @param url API endpoint URL
     * @param statusCode Response status code
     */
    public static void logApiCall(String method, String url, int statusCode) {
        logger.info("API CALL: {} {} - Status: {}", method, url, statusCode);
    }
    
    /**
     * Log database operation with INFO level
     * @param operation Database operation type
     * @param query SQL query or operation details
     */
    public static void logDatabaseOperation(String operation, String query) {
        logger.info("DATABASE: {} - Query: {}", operation, query);
    }
    
    /**
     * Log error with context information
     * @param message Error message
     * @param throwable Exception that occurred
     */
    public static void logError(String message, Throwable throwable) {
        String testName = ThreadContext.get(TEST_NAME_KEY);
        String browser = ThreadContext.get(BROWSER_KEY);
        logger.error("ERROR in test: {} on browser: {} - Message: {}", 
                    testName, browser, message, throwable);
    }
    
    /**
     * Log warning with context information
     * @param message Warning message
     */
    public static void logWarning(String message) {
        String testName = ThreadContext.get(TEST_NAME_KEY);
        logger.warn("WARNING in test: {} - Message: {}", testName, message);
    }
    
    /**
     * Log performance metrics
     * @param operation Operation being measured
     * @param duration Duration in milliseconds
     */
    public static void logPerformance(String operation, long duration) {
        logger.info("PERFORMANCE: {} completed in {} ms", operation, duration);
    }
    
    /**
     * Log screenshot capture
     * @param screenshotPath Path to the captured screenshot
     * @param reason Reason for taking the screenshot
     */
    public static void logScreenshot(String screenshotPath, String reason) {
        logger.info("SCREENSHOT: Captured at {} - Reason: {}", screenshotPath, reason);
    }
}