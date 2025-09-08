package com.framework.exceptions;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit tests for ConfigurationException class.
 */
public class ConfigurationExceptionTest {
    
    @Test
    public void testConfigurationExceptionWithMessage() {
        String message = "Configuration file not found";
        ConfigurationException exception = new ConfigurationException(message);
        
        Assert.assertEquals(exception.getMessage(), message);
        Assert.assertEquals(exception.getErrorCode(), "CONFIGURATION_ERROR");
        Assert.assertNull(exception.getPropertyName());
        Assert.assertNull(exception.getExpectedFormat());
    }
    
    @Test
    public void testConfigurationExceptionWithPropertyNameAndMessage() {
        String propertyName = "browser.timeout";
        String message = "Invalid timeout value";
        ConfigurationException exception = new ConfigurationException(propertyName, message);
        
        Assert.assertTrue(exception.getMessage().contains("Configuration error for property 'browser.timeout'"));
        Assert.assertTrue(exception.getMessage().contains("Invalid timeout value"));
        Assert.assertEquals(exception.getErrorCode(), "CONFIGURATION_ERROR");
        Assert.assertEquals(exception.getPropertyName(), propertyName);
        Assert.assertNull(exception.getExpectedFormat());
    }
    
    @Test
    public void testConfigurationExceptionWithPropertyNameExpectedFormatAndMessage() {
        String propertyName = "parallel.execution";
        String expectedFormat = "true/false";
        String message = "Must be a boolean value";
        ConfigurationException exception = new ConfigurationException(propertyName, expectedFormat, message);
        
        Assert.assertTrue(exception.getMessage().contains("Configuration error for property 'parallel.execution'"));
        Assert.assertTrue(exception.getMessage().contains("Must be a boolean value"));
        Assert.assertTrue(exception.getMessage().contains("Expected format: true/false"));
        Assert.assertEquals(exception.getErrorCode(), "CONFIGURATION_ERROR");
        Assert.assertEquals(exception.getPropertyName(), propertyName);
        Assert.assertEquals(exception.getExpectedFormat(), expectedFormat);
    }
    
    @Test
    public void testConfigurationExceptionWithMessageAndCause() {
        String message = "Failed to load configuration";
        RuntimeException cause = new RuntimeException("File I/O error");
        ConfigurationException exception = new ConfigurationException(message, cause);
        
        Assert.assertEquals(exception.getMessage(), message);
        Assert.assertEquals(exception.getErrorCode(), "CONFIGURATION_ERROR");
        Assert.assertEquals(exception.getCause(), cause);
        Assert.assertNull(exception.getPropertyName());
        Assert.assertNull(exception.getExpectedFormat());
    }
    
    @Test
    public void testConfigurationExceptionWithPropertyNameMessageAndCause() {
        String propertyName = "base.url";
        String message = "Invalid URL format";
        RuntimeException cause = new RuntimeException("Malformed URL");
        ConfigurationException exception = new ConfigurationException(propertyName, message, cause);
        
        Assert.assertTrue(exception.getMessage().contains("Configuration error for property 'base.url'"));
        Assert.assertTrue(exception.getMessage().contains("Invalid URL format"));
        Assert.assertEquals(exception.getErrorCode(), "CONFIGURATION_ERROR");
        Assert.assertEquals(exception.getCause(), cause);
        Assert.assertEquals(exception.getPropertyName(), propertyName);
        Assert.assertNull(exception.getExpectedFormat());
    }
    
    @Test
    public void testToStringMethod() {
        String propertyName = "thread.count";
        String expectedFormat = "positive integer";
        String message = "Value must be greater than 0";
        ConfigurationException exception = new ConfigurationException(propertyName, expectedFormat, message);
        
        String result = exception.toString();
        Assert.assertTrue(result.contains("ConfigurationException"));
        Assert.assertTrue(result.contains("CONFIGURATION_ERROR"));
        Assert.assertTrue(result.contains("Property: thread.count"));
        Assert.assertTrue(result.contains("Expected Format: positive integer"));
    }
    
    @Test
    public void testToStringMethodWithoutPropertyInfo() {
        String message = "General configuration error";
        ConfigurationException exception = new ConfigurationException(message);
        
        String result = exception.toString();
        Assert.assertTrue(result.contains("ConfigurationException"));
        Assert.assertTrue(result.contains("CONFIGURATION_ERROR"));
        Assert.assertTrue(result.contains("General configuration error"));
        Assert.assertFalse(result.contains("Property:"));
        Assert.assertFalse(result.contains("Expected Format:"));
    }
    
    @Test
    public void testInheritanceFromFrameworkException() {
        ConfigurationException exception = new ConfigurationException("Test message");
        
        Assert.assertTrue(exception instanceof FrameworkException);
        Assert.assertTrue(exception instanceof RuntimeException);
    }
    
    @Test
    public void testExceptionChaining() {
        RuntimeException rootCause = new RuntimeException("Root cause");
        String propertyName = "environment";
        String message = "Invalid environment value";
        ConfigurationException exception = new ConfigurationException(propertyName, message, rootCause);
        
        Assert.assertEquals(exception.getCause(), rootCause);
        Assert.assertEquals(exception.getPropertyName(), propertyName);
    }
}