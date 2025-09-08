package com.framework.reporting;

import com.framework.config.ConfigManager;
import com.framework.utils.LoggerUtils;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.util.HashSet;
import java.util.Set;

/**
 * TestRetryAnalyzer implements IRetryAnalyzer to provide retry mechanism for failed tests
 * Supports configurable retry count and exclusion logic for specific exceptions
 */
public class TestRetryAnalyzer implements IRetryAnalyzer {
    
    private int retryCount = 0;
    private int maxRetryCount;
    
    // Set of exception types that should not be retried
    private static final Set<Class<? extends Throwable>> NON_RETRYABLE_EXCEPTIONS = new HashSet<>();
    
    static {
        // Add exception types that should not trigger retries
        NON_RETRYABLE_EXCEPTIONS.add(AssertionError.class);
        NON_RETRYABLE_EXCEPTIONS.add(IllegalArgumentException.class);
        NON_RETRYABLE_EXCEPTIONS.add(NullPointerException.class);
    }
    
    public TestRetryAnalyzer() {
        // Get retry count from configuration
        try {
            ConfigManager config = ConfigManager.getInstance();
            this.maxRetryCount = Integer.parseInt(config.getProperty("retry.count", "2"));
        } catch (Exception e) {
            LoggerUtils.logWarning("Failed to read retry count from configuration, using default value: 2");
            this.maxRetryCount = 2;
        }
    }
    
    @Override
    public boolean retry(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        
        // Check if we've exceeded the maximum retry count
        if (retryCount >= maxRetryCount) {
            LoggerUtils.getLogger(TestRetryAnalyzer.class).info("Maximum retry count (" + maxRetryCount + ") reached for test: " + 
                           className + "." + testName);
            return false;
        }
        
        // Check if the exception type should be excluded from retry
        Throwable throwable = result.getThrowable();
        if (throwable != null && shouldExcludeFromRetry(throwable)) {
            LoggerUtils.getLogger(TestRetryAnalyzer.class).info("Test failure excluded from retry due to exception type: " + 
                           throwable.getClass().getSimpleName() + " for test: " + 
                           className + "." + testName);
            return false;
        }
        
        // Check if retry is disabled for this specific test
        if (isRetryDisabled(result)) {
            LoggerUtils.getLogger(TestRetryAnalyzer.class).info("Retry disabled for test: " + className + "." + testName);
            return false;
        }
        
        // Increment retry count and retry the test
        retryCount++;
        LoggerUtils.getLogger(TestRetryAnalyzer.class).info("Retrying test: " + className + "." + testName + 
                        " (Attempt " + (retryCount + 1) + " of " + (maxRetryCount + 1) + ")");
        
        // Log the reason for retry
        if (throwable != null) {
            LoggerUtils.getLogger(TestRetryAnalyzer.class).info("Retry reason: " + throwable.getMessage());
        }
        
        return true;
    }
    
    /**
     * Checks if the exception type should be excluded from retry
     * @param throwable The exception that caused the test failure
     * @return true if the exception should be excluded from retry
     */
    private boolean shouldExcludeFromRetry(Throwable throwable) {
        Class<? extends Throwable> exceptionClass = throwable.getClass();
        
        // Check direct match
        if (NON_RETRYABLE_EXCEPTIONS.contains(exceptionClass)) {
            return true;
        }
        
        // Check if it's a subclass of any non-retryable exception
        for (Class<? extends Throwable> nonRetryableException : NON_RETRYABLE_EXCEPTIONS) {
            if (nonRetryableException.isAssignableFrom(exceptionClass)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Checks if retry is disabled for a specific test method
     * Checks for @NoRetry annotation on the test method
     * @param result The test result
     * @return true if retry is disabled for this test
     */
    private boolean isRetryDisabled(ITestResult result) {
        try {
            java.lang.reflect.Method method = result.getMethod().getConstructorOrMethod().getMethod();
            
            // Check for @NoRetry annotation
            if (method.isAnnotationPresent(NoRetry.class)) {
                NoRetry noRetry = method.getAnnotation(NoRetry.class);
                LoggerUtils.getLogger(TestRetryAnalyzer.class).info("Retry disabled for test method: " + method.getName() + 
                               ". Reason: " + noRetry.reason());
                return true;
            }
            
            return false;
        } catch (Exception e) {
            LoggerUtils.logWarning("Error checking retry disabled status: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets the current retry count
     * @return Current retry count
     */
    public int getRetryCount() {
        return retryCount;
    }
    
    /**
     * Gets the maximum retry count
     * @return Maximum retry count
     */
    public int getMaxRetryCount() {
        return maxRetryCount;
    }
    
    /**
     * Adds an exception type to the non-retryable exceptions set
     * @param exceptionClass Exception class to exclude from retry
     */
    public static void addNonRetryableException(Class<? extends Throwable> exceptionClass) {
        NON_RETRYABLE_EXCEPTIONS.add(exceptionClass);
        LoggerUtils.getLogger(TestRetryAnalyzer.class).info("Added non-retryable exception: " + exceptionClass.getSimpleName());
    }
    
    /**
     * Removes an exception type from the non-retryable exceptions set
     * @param exceptionClass Exception class to remove from exclusion
     */
    public static void removeNonRetryableException(Class<? extends Throwable> exceptionClass) {
        NON_RETRYABLE_EXCEPTIONS.remove(exceptionClass);
        LoggerUtils.getLogger(TestRetryAnalyzer.class).info("Removed non-retryable exception: " + exceptionClass.getSimpleName());
    }
}