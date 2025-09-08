package com.framework.reporting;

import com.framework.config.ConfigManager;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.ITestResult;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.internal.TestResult;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Unit tests for TestRetryAnalyzer class
 */
public class TestRetryAnalyzerTest {
    
    private TestRetryAnalyzer retryAnalyzer;
    private ITestResult mockResult;
    
    @BeforeMethod
    public void setUp() {
        retryAnalyzer = new TestRetryAnalyzer();
        mockResult = mock(ITestResult.class);
    }
    
    @Test
    public void testRetryWithinLimit() {
        // Mock test method
        org.testng.ITestNGMethod mockMethod = mock(org.testng.ITestNGMethod.class);
        when(mockMethod.getMethodName()).thenReturn("testMethod");
        when(mockResult.getMethod()).thenReturn(mockMethod);
        
        // Mock test class
        org.testng.IClass mockClass = mock(org.testng.IClass.class);
        when(mockClass.getName()).thenReturn("TestClass");
        when(mockResult.getTestClass()).thenReturn(mockClass);
        
        // Mock throwable
        RuntimeException exception = new RuntimeException("Test exception");
        when(mockResult.getThrowable()).thenReturn(exception);
        
        // Test retry within limit
        assertTrue(retryAnalyzer.retry(mockResult), "Should retry within limit");
        assertEquals(retryAnalyzer.getRetryCount(), 1, "Retry count should be 1");
        
        assertTrue(retryAnalyzer.retry(mockResult), "Should retry again within limit");
        assertEquals(retryAnalyzer.getRetryCount(), 2, "Retry count should be 2");
    }
    
    @Test
    public void testRetryExceedsLimit() {
        // Mock test method
        org.testng.ITestNGMethod mockMethod = mock(org.testng.ITestNGMethod.class);
        when(mockMethod.getMethodName()).thenReturn("testMethod");
        when(mockResult.getMethod()).thenReturn(mockMethod);
        
        // Mock test class
        org.testng.IClass mockClass = mock(org.testng.IClass.class);
        when(mockClass.getName()).thenReturn("TestClass");
        when(mockResult.getTestClass()).thenReturn(mockClass);
        
        // Mock throwable
        RuntimeException exception = new RuntimeException("Test exception");
        when(mockResult.getThrowable()).thenReturn(exception);
        
        // Exhaust retry attempts
        retryAnalyzer.retry(mockResult); // 1st retry
        retryAnalyzer.retry(mockResult); // 2nd retry
        
        // Should not retry after exceeding limit
        assertFalse(retryAnalyzer.retry(mockResult), "Should not retry after exceeding limit");
    }
    
    @Test
    public void testNonRetryableException() {
        // Mock test method
        org.testng.ITestNGMethod mockMethod = mock(org.testng.ITestNGMethod.class);
        when(mockMethod.getMethodName()).thenReturn("testMethod");
        when(mockResult.getMethod()).thenReturn(mockMethod);
        
        // Mock test class
        org.testng.IClass mockClass = mock(org.testng.IClass.class);
        when(mockClass.getName()).thenReturn("TestClass");
        when(mockResult.getTestClass()).thenReturn(mockClass);
        
        // Mock non-retryable exception
        AssertionError assertionError = new AssertionError("Assertion failed");
        when(mockResult.getThrowable()).thenReturn(assertionError);
        
        // Should not retry for AssertionError
        assertFalse(retryAnalyzer.retry(mockResult), "Should not retry for AssertionError");
        assertEquals(retryAnalyzer.getRetryCount(), 0, "Retry count should remain 0");
    }
    
    @Test
    public void testConfigurationRetryCount() {
        try (MockedStatic<ConfigManager> mockedConfigManager = Mockito.mockStatic(ConfigManager.class)) {
            ConfigManager mockConfig = mock(ConfigManager.class);
            when(mockConfig.getProperty("retry.count", "2")).thenReturn("3");
            mockedConfigManager.when(ConfigManager::getInstance).thenReturn(mockConfig);
            
            TestRetryAnalyzer customRetryAnalyzer = new TestRetryAnalyzer();
            assertEquals(customRetryAnalyzer.getMaxRetryCount(), 3, "Should use configured retry count");
        }
    }
    
    @Test
    public void testAddRemoveNonRetryableException() {
        // Add custom exception
        TestRetryAnalyzer.addNonRetryableException(IllegalStateException.class);
        
        // Mock test method
        org.testng.ITestNGMethod mockMethod = mock(org.testng.ITestNGMethod.class);
        when(mockMethod.getMethodName()).thenReturn("testMethod");
        when(mockResult.getMethod()).thenReturn(mockMethod);
        
        // Mock test class
        org.testng.IClass mockClass = mock(org.testng.IClass.class);
        when(mockClass.getName()).thenReturn("TestClass");
        when(mockResult.getTestClass()).thenReturn(mockClass);
        
        // Mock custom non-retryable exception
        IllegalStateException exception = new IllegalStateException("Custom exception");
        when(mockResult.getThrowable()).thenReturn(exception);
        
        // Should not retry for custom exception
        assertFalse(retryAnalyzer.retry(mockResult), "Should not retry for custom non-retryable exception");
        
        // Remove custom exception
        TestRetryAnalyzer.removeNonRetryableException(IllegalStateException.class);
        
        // Should retry after removing from exclusion list
        assertTrue(retryAnalyzer.retry(mockResult), "Should retry after removing from exclusion list");
    }
}