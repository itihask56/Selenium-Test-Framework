package com.framework.exceptions;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit tests for FrameworkException class.
 */
public class FrameworkExceptionTest {
    
    @Test
    public void testFrameworkExceptionWithMessage() {
        String message = "Test framework exception";
        FrameworkException exception = new FrameworkException(message);
        
        Assert.assertEquals(exception.getMessage(), message);
        Assert.assertEquals(exception.getErrorCode(), "FRAMEWORK_ERROR");
        Assert.assertNull(exception.getCause());
    }
    
    @Test
    public void testFrameworkExceptionWithMessageAndErrorCode() {
        String message = "Test framework exception";
        String errorCode = "CUSTOM_ERROR";
        FrameworkException exception = new FrameworkException(message, errorCode);
        
        Assert.assertEquals(exception.getMessage(), message);
        Assert.assertEquals(exception.getErrorCode(), errorCode);
        Assert.assertNull(exception.getCause());
    }
    
    @Test
    public void testFrameworkExceptionWithMessageAndCause() {
        String message = "Test framework exception";
        RuntimeException cause = new RuntimeException("Root cause");
        FrameworkException exception = new FrameworkException(message, cause);
        
        Assert.assertEquals(exception.getMessage(), message);
        Assert.assertEquals(exception.getErrorCode(), "FRAMEWORK_ERROR");
        Assert.assertEquals(exception.getCause(), cause);
    }
    
    @Test
    public void testFrameworkExceptionWithMessageErrorCodeAndCause() {
        String message = "Test framework exception";
        String errorCode = "CUSTOM_ERROR";
        RuntimeException cause = new RuntimeException("Root cause");
        FrameworkException exception = new FrameworkException(message, errorCode, cause);
        
        Assert.assertEquals(exception.getMessage(), message);
        Assert.assertEquals(exception.getErrorCode(), errorCode);
        Assert.assertEquals(exception.getCause(), cause);
    }
    
    @Test
    public void testToStringMethod() {
        String message = "Test framework exception";
        String errorCode = "CUSTOM_ERROR";
        FrameworkException exception = new FrameworkException(message, errorCode);
        
        String expectedString = "FrameworkException [CUSTOM_ERROR]: Test framework exception";
        Assert.assertEquals(exception.toString(), expectedString);
    }
    
    @Test
    public void testToStringMethodWithNullMessage() {
        String errorCode = "CUSTOM_ERROR";
        FrameworkException exception = new FrameworkException(null, errorCode);
        
        String expectedString = "FrameworkException [CUSTOM_ERROR]: ";
        Assert.assertEquals(exception.toString(), expectedString);
    }
    
    @Test
    public void testExceptionChaining() {
        RuntimeException rootCause = new RuntimeException("Root cause");
        IllegalArgumentException intermediateCause = new IllegalArgumentException("Intermediate cause", rootCause);
        FrameworkException exception = new FrameworkException("Framework exception", "CHAIN_ERROR", intermediateCause);
        
        Assert.assertEquals(exception.getCause(), intermediateCause);
        Assert.assertEquals(exception.getCause().getCause(), rootCause);
    }
}