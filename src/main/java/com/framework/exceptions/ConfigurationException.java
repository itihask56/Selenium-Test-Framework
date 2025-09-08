package com.framework.exceptions;

/**
 * Exception thrown when there are configuration-related errors.
 * Extends FrameworkException to provide specific error handling for configuration issues.
 */
public class ConfigurationException extends FrameworkException {
    
    private final String propertyName;
    private final String expectedFormat;
    
    /**
     * Constructs a new ConfigurationException with the specified message.
     *
     * @param message the detail message
     */
    public ConfigurationException(String message) {
        super(message, "CONFIGURATION_ERROR");
        this.propertyName = null;
        this.expectedFormat = null;
    }
    
    /**
     * Constructs a new ConfigurationException with the specified property name and message.
     *
     * @param propertyName the name of the configuration property that caused the error
     * @param message the detail message
     */
    public ConfigurationException(String propertyName, String message) {
        super(String.format("Configuration error for property '%s': %s", propertyName, message), 
              "CONFIGURATION_ERROR");
        this.propertyName = propertyName;
        this.expectedFormat = null;
    }
    
    /**
     * Constructs a new ConfigurationException with the specified property name, expected format, and message.
     *
     * @param propertyName the name of the configuration property that caused the error
     * @param expectedFormat the expected format for the property value
     * @param message the detail message
     */
    public ConfigurationException(String propertyName, String expectedFormat, String message) {
        super(String.format("Configuration error for property '%s': %s. Expected format: %s", 
              propertyName, message, expectedFormat), "CONFIGURATION_ERROR");
        this.propertyName = propertyName;
        this.expectedFormat = expectedFormat;
    }
    
    /**
     * Constructs a new ConfigurationException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public ConfigurationException(String message, Throwable cause) {
        super(message, "CONFIGURATION_ERROR", cause);
        this.propertyName = null;
        this.expectedFormat = null;
    }
    
    /**
     * Constructs a new ConfigurationException with the specified property name, message, and cause.
     *
     * @param propertyName the name of the configuration property that caused the error
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public ConfigurationException(String propertyName, String message, Throwable cause) {
        super(String.format("Configuration error for property '%s': %s", propertyName, message), 
              "CONFIGURATION_ERROR", cause);
        this.propertyName = propertyName;
        this.expectedFormat = null;
    }
    
    /**
     * Gets the name of the configuration property that caused the error.
     *
     * @return the property name, or null if not specified
     */
    public String getPropertyName() {
        return propertyName;
    }
    
    /**
     * Gets the expected format for the configuration property.
     *
     * @return the expected format, or null if not specified
     */
    public String getExpectedFormat() {
        return expectedFormat;
    }
    
    /**
     * Returns a detailed string representation of this exception including property information.
     *
     * @return a string representation of this exception
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName())
          .append(" [").append(getErrorCode()).append("]: ")
          .append(getLocalizedMessage());
        
        if (propertyName != null) {
            sb.append(" | Property: ").append(propertyName);
        }
        
        if (expectedFormat != null) {
            sb.append(" | Expected Format: ").append(expectedFormat);
        }
        
        return sb.toString();
    }
}