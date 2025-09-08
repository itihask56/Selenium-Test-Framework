package com.framework.tests;

import com.framework.config.ConfigManager;
import com.framework.config.TestConfig;
import com.framework.driver.DriverManager;
import com.framework.reporting.ScreenshotUtils;
import com.framework.utils.TestLogger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * BaseTest abstract class provides common functionality for all test classes
 * Handles WebDriver lifecycle, test data management, and logging
 */
public abstract class BaseTest {
    
    protected WebDriver driver;
    protected ConfigManager configManager;
    protected TestConfig testConfig;
    protected TestLogger testLogger;
    
    // Thread-safe storage for test data
    protected static final ThreadLocal<Map<String, Object>> testData = new ThreadLocal<>();
    
    /**
     * Suite-level setup - runs once before all tests in the suite
     */
    @BeforeSuite(alwaysRun = true)
    public void suiteSetup() {
        testLogger = new TestLogger(this.getClass());
        testLogger.suiteStarted(this.getClass().getSimpleName());
        
        // Initialize configuration
        configManager = ConfigManager.getInstance();
        testConfig = configManager.getTestConfig();
        
        // Log suite configuration
        testLogger.getLogger().info("Test Configuration:");
        testLogger.getLogger().info("Browser: {}", testConfig.getBrowser());
        testLogger.getLogger().info("Environment: {}", testConfig.getEnvironment());
        testLogger.getLogger().info("Headless: {}", testConfig.isHeadless());
        testLogger.getLogger().info("Parallel Execution: {}", testConfig.isParallelExecution());
        testLogger.getLogger().info("Thread Count: {}", testConfig.getThreadCount());
        testLogger.getLogger().info("Retry Count: {}", testConfig.getRetryCount());
    }
    
    /**
     * Class-level setup - runs once before all test methods in the class
     */
    @BeforeClass(alwaysRun = true)
    public void classSetup() {
        testLogger.getLogger().info("Setting up test class: {}", this.getClass().getSimpleName());
        
        // Perform any class-level initialization
        setupTestClass();
    }
    
    /**
     * Method-level setup - runs before each test method
     */
    @BeforeMethod(alwaysRun = true)
    public void methodSetup(Method method) {
        long startTime = System.currentTimeMillis();
        
        // Initialize test data storage
        testData.set(new HashMap<>());
        
        // Get test information
        String testName = method.getName();
        String description = getTestDescription(method);
        
        // Log test start
        testLogger.testStarted(testName, description);
        
        // Initialize WebDriver
        try {
            driver = DriverManager.getInstance().initializeDriver();
            testLogger.getLogger().info("WebDriver initialized successfully for test: {}", testName);
            
            // Navigate to base URL if configured
            String baseUrl = getBaseUrl();
            if (baseUrl != null && !baseUrl.isEmpty()) {
                driver.get(baseUrl);
                testLogger.getLogger().info("Navigated to base URL: {}", baseUrl);
            }
            
        } catch (Exception e) {
            testLogger.getLogger().error("Failed to initialize WebDriver for test: {}", testName, e);
            throw new RuntimeException("WebDriver initialization failed", e);
        }
        
        // Setup test-specific data
        setupTestData(method);
        
        // Store test start time
        setTestData("startTime", startTime);
        setTestData("testName", testName);
        setTestData("description", description);
        
        testLogger.getLogger().info("Test setup completed for: {}", testName);
    }
    
    /**
     * Method-level teardown - runs after each test method
     */
    @AfterMethod(alwaysRun = true)
    public void methodTeardown(ITestResult result) {
        String testName = (String) getTestData("testName");
        long startTime = (Long) getTestData("startTime");
        long duration = System.currentTimeMillis() - startTime;
        
        try {
            // Handle test failure
            if (result.getStatus() == ITestResult.FAILURE) {
                handleTestFailure(result);
            }
            
            // Log test completion
            testLogger.testCompleted(testName, result, duration);
            
            // Cleanup test-specific data
            cleanupTestData();
            
        } catch (Exception e) {
            testLogger.getLogger().error("Error during test teardown for: {}", testName, e);
        } finally {
            // Always cleanup WebDriver
            cleanupWebDriver();
            
            // Clear test data
            testData.remove();
        }
    }
    
    /**
     * Class-level teardown - runs once after all test methods in the class
     */
    @AfterClass(alwaysRun = true)
    public void classTeardown() {
        testLogger.getLogger().info("Tearing down test class: {}", this.getClass().getSimpleName());
        
        // Perform any class-level cleanup
        cleanupTestClass();
    }
    
    /**
     * Suite-level teardown - runs once after all tests in the suite
     */
    @AfterSuite(alwaysRun = true)
    public void suiteTeardown() {
        // Log suite completion (statistics will be handled by TestNG listeners)
        testLogger.getLogger().info("Test suite completed: {}", this.getClass().getSimpleName());
        
        // Cleanup any suite-level resources
        cleanupSuite();
    }
    
    /**
     * Handles test failure - captures screenshot and logs error details
     */
    private void handleTestFailure(ITestResult result) {
        String testName = (String) getTestData("testName");
        
        try {
            // Capture screenshot if enabled and driver is available
            if (testConfig.isScreenshotOnFailure() && driver != null) {
                String screenshotPath = ScreenshotUtils.captureScreenshot(testName, "failure");
                if (screenshotPath != null) {
                    testLogger.getLogger().info("Failure screenshot captured: {}", screenshotPath);
                    setTestData("screenshotPath", screenshotPath);
                }
            }
            
            // Log failure details
            Throwable throwable = result.getThrowable();
            if (throwable != null) {
                testLogger.getLogger().error("Test failed with exception: {}", throwable.getMessage(), throwable);
            }
            
        } catch (Exception e) {
            testLogger.getLogger().error("Error while handling test failure: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Cleanup WebDriver instance
     */
    private void cleanupWebDriver() {
        try {
            if (driver != null) {
                DriverManager.getInstance().quitDriver();
                driver = null;
                testLogger.getLogger().debug("WebDriver cleaned up successfully");
            }
        } catch (Exception e) {
            testLogger.getLogger().error("Error while cleaning up WebDriver: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Get base URL based on current environment
     */
    private String getBaseUrl() {
        String environment = testConfig.getEnvironment();
        switch (environment.toLowerCase()) {
            case "dev":
                return testConfig.getBaseUrlDev();
            case "staging":
                return testConfig.getBaseUrlStaging();
            case "prod":
                return testConfig.getBaseUrlProd();
            default:
                return testConfig.getBaseUrlDev(); // Default to dev
        }
    }
    
    /**
     * Get test description from method annotation or method name
     */
    private String getTestDescription(Method method) {
        Test testAnnotation = method.getAnnotation(Test.class);
        if (testAnnotation != null && !testAnnotation.description().isEmpty()) {
            return testAnnotation.description();
        }
        return "Test method: " + method.getName();
    }
    
    // Test data management methods
    
    /**
     * Set test data for current thread
     */
    protected void setTestData(String key, Object value) {
        Map<String, Object> data = testData.get();
        if (data != null) {
            data.put(key, value);
        }
    }
    
    /**
     * Get test data for current thread
     */
    protected Object getTestData(String key) {
        Map<String, Object> data = testData.get();
        return data != null ? data.get(key) : null;
    }
    
    /**
     * Get test data with default value
     */
    protected <T> T getTestData(String key, T defaultValue) {
        Object value = getTestData(key);
        return value != null ? (T) value : defaultValue;
    }
    
    /**
     * Check if test data exists
     */
    protected boolean hasTestData(String key) {
        Map<String, Object> data = testData.get();
        return data != null && data.containsKey(key);
    }
    
    /**
     * Remove test data
     */
    protected void removeTestData(String key) {
        Map<String, Object> data = testData.get();
        if (data != null) {
            data.remove(key);
        }
    }
    
    /**
     * Clear all test data
     */
    protected void clearTestData() {
        Map<String, Object> data = testData.get();
        if (data != null) {
            data.clear();
        }
    }
    
    // Abstract methods for subclasses to implement
    
    /**
     * Setup test class - called once before all test methods in the class
     * Subclasses can override this method for class-specific setup
     */
    protected void setupTestClass() {
        // Default implementation - can be overridden by subclasses
    }
    
    /**
     * Setup test data - called before each test method
     * Subclasses can override this method for test-specific data setup
     */
    protected void setupTestData(Method method) {
        // Default implementation - can be overridden by subclasses
    }
    
    /**
     * Cleanup test data - called after each test method
     * Subclasses can override this method for test-specific cleanup
     */
    protected void cleanupTestData() {
        // Default implementation - can be overridden by subclasses
    }
    
    /**
     * Cleanup test class - called once after all test methods in the class
     * Subclasses can override this method for class-specific cleanup
     */
    protected void cleanupTestClass() {
        // Default implementation - can be overridden by subclasses
    }
    
    /**
     * Cleanup suite - called once after all tests in the suite
     * Subclasses can override this method for suite-specific cleanup
     */
    protected void cleanupSuite() {
        // Default implementation - can be overridden by subclasses
    }
    
    // Utility methods for test classes
    
    /**
     * Wait for a specified amount of time
     */
    protected void waitFor(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            testLogger.getLogger().warn("Wait interrupted: {}", e.getMessage());
        }
    }
    
    /**
     * Capture screenshot with custom name
     */
    protected String captureScreenshot(String name) {
        return ScreenshotUtils.captureScreenshot(name);
    }
    
    /**
     * Log assertion result
     */
    protected void logAssertion(String description, Object expected, Object actual, boolean passed) {
        testLogger.logAssertion(description, expected, actual, passed);
    }
    
    /**
     * Get current test name
     */
    protected String getCurrentTestName() {
        return (String) getTestData("testName");
    }
    
    /**
     * Get test execution duration
     */
    protected long getTestDuration() {
        Long startTime = (Long) getTestData("startTime");
        return startTime != null ? System.currentTimeMillis() - startTime : 0;
    }
}