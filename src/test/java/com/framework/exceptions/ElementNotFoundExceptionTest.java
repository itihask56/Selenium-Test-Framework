package com.framework.exceptions;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit tests for ElementNotFoundException class.
 */
public class ElementNotFoundExceptionTest {
    
    @Test
    public void testElementNotFoundExceptionWithLocator() {
        By locator = By.id("testId");
        ElementNotFoundException exception = new ElementNotFoundException(locator);
        
        Assert.assertTrue(exception.getMessage().contains("Element not found using locator: By.id: testId"));
        Assert.assertEquals(exception.getErrorCode(), "ELEMENT_NOT_FOUND");
        Assert.assertEquals(exception.getLocator(), locator);
        Assert.assertEquals(exception.getTimeoutInSeconds(), 0);
    }
    
    @Test
    public void testElementNotFoundExceptionWithLocatorAndTimeout() {
        By locator = By.className("testClass");
        int timeout = 10;
        ElementNotFoundException exception = new ElementNotFoundException(locator, timeout);
        
        Assert.assertTrue(exception.getMessage().contains("Element not found using locator: By.className: testClass"));
        Assert.assertTrue(exception.getMessage().contains("after waiting 10 seconds"));
        Assert.assertEquals(exception.getErrorCode(), "ELEMENT_NOT_FOUND");
        Assert.assertEquals(exception.getLocator(), locator);
        Assert.assertEquals(exception.getTimeoutInSeconds(), timeout);
    }
    
    @Test
    public void testElementNotFoundExceptionWithLocatorAndCustomMessage() {
        By locator = By.xpath("//div[@class='test']");
        String customMessage = "Custom element not found message";
        ElementNotFoundException exception = new ElementNotFoundException(locator, customMessage);
        
        Assert.assertEquals(exception.getMessage(), customMessage);
        Assert.assertEquals(exception.getErrorCode(), "ELEMENT_NOT_FOUND");
        Assert.assertEquals(exception.getLocator(), locator);
        Assert.assertEquals(exception.getTimeoutInSeconds(), 0);
    }
    
    @Test
    public void testElementNotFoundExceptionWithLocatorTimeoutAndCustomMessage() {
        By locator = By.name("testName");
        int timeout = 15;
        String customMessage = "Element with name 'testName' not found";
        ElementNotFoundException exception = new ElementNotFoundException(locator, timeout, customMessage);
        
        Assert.assertEquals(exception.getMessage(), customMessage);
        Assert.assertEquals(exception.getErrorCode(), "ELEMENT_NOT_FOUND");
        Assert.assertEquals(exception.getLocator(), locator);
        Assert.assertEquals(exception.getTimeoutInSeconds(), timeout);
    }
    
    @Test
    public void testElementNotFoundExceptionWithLocatorAndCause() {
        By locator = By.tagName("button");
        RuntimeException cause = new RuntimeException("Selenium timeout");
        ElementNotFoundException exception = new ElementNotFoundException(locator, cause);
        
        Assert.assertTrue(exception.getMessage().contains("Element not found using locator: By.tagName: button"));
        Assert.assertEquals(exception.getErrorCode(), "ELEMENT_NOT_FOUND");
        Assert.assertEquals(exception.getLocator(), locator);
        Assert.assertEquals(exception.getCause(), cause);
        Assert.assertEquals(exception.getTimeoutInSeconds(), 0);
    }
    
    @Test
    public void testToStringMethod() {
        By locator = By.cssSelector(".test-class");
        int timeout = 20;
        ElementNotFoundException exception = new ElementNotFoundException(locator, timeout);
        
        String result = exception.toString();
        Assert.assertTrue(result.contains("ElementNotFoundException"));
        Assert.assertTrue(result.contains("ELEMENT_NOT_FOUND"));
        Assert.assertTrue(result.contains("Locator: By.cssSelector: .test-class"));
        Assert.assertTrue(result.contains("Timeout: 20s"));
    }
    
    @Test
    public void testToStringMethodWithoutTimeout() {
        By locator = By.linkText("Click here");
        ElementNotFoundException exception = new ElementNotFoundException(locator);
        
        String result = exception.toString();
        Assert.assertTrue(result.contains("ElementNotFoundException"));
        Assert.assertTrue(result.contains("ELEMENT_NOT_FOUND"));
        Assert.assertTrue(result.contains("Locator: By.linkText: Click here"));
        Assert.assertFalse(result.contains("Timeout:"));
    }
    
    @Test
    public void testInheritanceFromFrameworkException() {
        By locator = By.id("test");
        ElementNotFoundException exception = new ElementNotFoundException(locator);
        
        Assert.assertTrue(exception instanceof FrameworkException);
        Assert.assertTrue(exception instanceof RuntimeException);
    }
}