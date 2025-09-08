package com.framework.exceptions;

import org.openqa.selenium.By;

/**
 * Exception thrown when a web element cannot be located on the page.
 * Extends FrameworkException to provide specific error handling for element location failures.
 */
public class ElementNotFoundException extends FrameworkException {
    
    private final By locator;
    private final int timeoutInSeconds;
    
    /**
     * Constructs a new ElementNotFoundException with the specified locator.
     *
     * @param locator the By locator that failed to find the element
     */
    public ElementNotFoundException(By locator) {
        super(String.format("Element not found using locator: %s", locator), "ELEMENT_NOT_FOUND");
        this.locator = locator;
        this.timeoutInSeconds = 0;
    }
    
    /**
     * Constructs a new ElementNotFoundException with the specified locator and timeout.
     *
     * @param locator the By locator that failed to find the element
     * @param timeoutInSeconds the timeout duration in seconds
     */
    public ElementNotFoundException(By locator, int timeoutInSeconds) {
        super(String.format("Element not found using locator: %s after waiting %d seconds", 
              locator, timeoutInSeconds), "ELEMENT_NOT_FOUND");
        this.locator = locator;
        this.timeoutInSeconds = timeoutInSeconds;
    }
    
    /**
     * Constructs a new ElementNotFoundException with the specified locator and custom message.
     *
     * @param locator the By locator that failed to find the element
     * @param message custom error message
     */
    public ElementNotFoundException(By locator, String message) {
        super(message, "ELEMENT_NOT_FOUND");
        this.locator = locator;
        this.timeoutInSeconds = 0;
    }
    
    /**
     * Constructs a new ElementNotFoundException with the specified locator, timeout, and custom message.
     *
     * @param locator the By locator that failed to find the element
     * @param timeoutInSeconds the timeout duration in seconds
     * @param message custom error message
     */
    public ElementNotFoundException(By locator, int timeoutInSeconds, String message) {
        super(message, "ELEMENT_NOT_FOUND");
        this.locator = locator;
        this.timeoutInSeconds = timeoutInSeconds;
    }
    
    /**
     * Constructs a new ElementNotFoundException with the specified locator and cause.
     *
     * @param locator the By locator that failed to find the element
     * @param cause the cause of the exception
     */
    public ElementNotFoundException(By locator, Throwable cause) {
        super(String.format("Element not found using locator: %s", locator), "ELEMENT_NOT_FOUND", cause);
        this.locator = locator;
        this.timeoutInSeconds = 0;
    }
    
    /**
     * Gets the locator that failed to find the element.
     *
     * @return the By locator
     */
    public By getLocator() {
        return locator;
    }
    
    /**
     * Gets the timeout duration in seconds.
     *
     * @return the timeout in seconds
     */
    public int getTimeoutInSeconds() {
        return timeoutInSeconds;
    }
    
    /**
     * Returns a detailed string representation of this exception including locator and timeout information.
     *
     * @return a string representation of this exception
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName())
          .append(" [").append(getErrorCode()).append("]: ")
          .append(getLocalizedMessage());
        
        if (locator != null) {
            sb.append(" | Locator: ").append(locator);
        }
        
        if (timeoutInSeconds > 0) {
            sb.append(" | Timeout: ").append(timeoutInSeconds).append("s");
        }
        
        return sb.toString();
    }
}