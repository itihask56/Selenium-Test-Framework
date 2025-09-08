package com.framework.utils;

import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;

/**
 * Specialized logger for test execution tracking
 * Provides structured logging for test lifecycle events
 */
public class TestLogger {
    
    private final Logger logger;
    private final String testClass;
    
    /**
     * Constructor for TestLogger
     * @param testClass The test class using this logger
     */
    public TestLogger(Class<?> testClass) {
        this.logger = LoggerUtils.getLogger(testClass);
        this.testClass = testClass.getSimpleName();
    }
    
    /**
     * Log test start
     * @param testName Name of the test starting
     * @param description Test description
     */
    public void testStarted(String testName, String description) {
        logger.info("========== TEST STARTED ==========");
        logger.info("Test Class: {}", testClass);
        logger.info("Test Method: {}", testName);
        logger.info("Description: {}", description);
        logger.info("Start Time: {}", System.currentTimeMillis());
        logger.info("===================================");
    }
    
    /**
     * Log test completion
     * @param testName Name of the completed test
     * @param result Test result
     * @param duration Test execution duration in milliseconds
     */
    public void testCompleted(String testName, ITestResult result, long duration) {
        String status = getTestStatus(result);
        logger.info("========== TEST COMPLETED ==========");
        logger.info("Test Method: {}", testName);
        logger.info("Status: {}", status);
        logger.info("Duration: {} ms", duration);
        
        if (result.getThrowable() != null) {
            logger.error("Failure Reason: {}", result.getThrowable().getMessage());
        }
        
        logger.info("End Time: {}", System.currentTimeMillis());
        logger.info("====================================");
    }
    
    /**
     * Log test skipped
     * @param testName Name of the skipped test
     * @param reason Reason for skipping
     */
    public void testSkipped(String testName, String reason) {
        logger.warn("========== TEST SKIPPED ==========");
        logger.warn("Test Method: {}", testName);
        logger.warn("Reason: {}", reason);
        logger.warn("==================================");
    }
    
    /**
     * Log test retry attempt
     * @param testName Name of the test being retried
     * @param attemptNumber Current attempt number
     * @param maxRetries Maximum retry attempts
     */
    public void testRetry(String testName, int attemptNumber, int maxRetries) {
        logger.warn("========== TEST RETRY ==========");
        logger.warn("Test Method: {}", testName);
        logger.warn("Attempt: {} of {}", attemptNumber, maxRetries);
        logger.warn("===============================");
    }
    
    /**
     * Log suite start
     * @param suiteName Name of the test suite
     */
    public void suiteStarted(String suiteName) {
        logger.info("########## SUITE STARTED ##########");
        logger.info("Suite Name: {}", suiteName);
        logger.info("Start Time: {}", System.currentTimeMillis());
        logger.info("###################################");
    }
    
    /**
     * Log suite completion
     * @param suiteName Name of the test suite
     * @param totalTests Total number of tests
     * @param passed Number of passed tests
     * @param failed Number of failed tests
     * @param skipped Number of skipped tests
     */
    public void suiteCompleted(String suiteName, int totalTests, int passed, int failed, int skipped) {
        logger.info("########## SUITE COMPLETED ##########");
        logger.info("Suite Name: {}", suiteName);
        logger.info("Total Tests: {}", totalTests);
        logger.info("Passed: {}", passed);
        logger.info("Failed: {}", failed);
        logger.info("Skipped: {}", skipped);
        logger.info("Success Rate: {}%", calculateSuccessRate(passed, totalTests));
        logger.info("End Time: {}", System.currentTimeMillis());
        logger.info("####################################");
    }
    
    /**
     * Log assertion with details
     * @param assertion Description of the assertion
     * @param expected Expected value
     * @param actual Actual value
     * @param passed Whether assertion passed
     */
    public void logAssertion(String assertion, Object expected, Object actual, boolean passed) {
        if (passed) {
            logger.info("ASSERTION PASSED: {} - Expected: {}, Actual: {}", assertion, expected, actual);
        } else {
            logger.error("ASSERTION FAILED: {} - Expected: {}, Actual: {}", assertion, expected, actual);
        }
    }
    
    /**
     * Log data provider information
     * @param dataProviderName Name of the data provider
     * @param dataSetIndex Index of current data set
     * @param totalDataSets Total number of data sets
     */
    public void logDataProvider(String dataProviderName, int dataSetIndex, int totalDataSets) {
        logger.info("DATA PROVIDER: {} - Dataset {} of {}", dataProviderName, dataSetIndex + 1, totalDataSets);
    }
    
    /**
     * Get test status string from ITestResult
     * @param result TestNG test result
     * @return Status string
     */
    private String getTestStatus(ITestResult result) {
        switch (result.getStatus()) {
            case ITestResult.SUCCESS:
                return "PASSED";
            case ITestResult.FAILURE:
                return "FAILED";
            case ITestResult.SKIP:
                return "SKIPPED";
            default:
                return "UNKNOWN";
        }
    }
    
    /**
     * Calculate success rate percentage
     * @param passed Number of passed tests
     * @param total Total number of tests
     * @return Success rate as percentage
     */
    private double calculateSuccessRate(int passed, int total) {
        if (total == 0) return 0.0;
        return (double) passed / total * 100.0;
    }
    
    /**
     * Get the underlying logger instance
     * @return Logger instance
     */
    public Logger getLogger() {
        return logger;
    }
}