package com.framework.exceptions;

/**
 * Base exception class for all framework-specific exceptions.
 * Provides error codes and exception chaining support.
 */
public class FrameworkException extends RuntimeException {
    
    private final String errorCode;
    
    /**
     * Constructs a new FrameworkException with the specified detail message.
     *
     * @param message the detail message
     */
    public FrameworkException(String message) {
        super(message);
        this.errorCode = "FRAMEWORK_ERROR";
    }
    
    /**
     * Constructs a new FrameworkException with the specified detail message and error code.
     *
     * @param message the detail message
     * @param errorCode the error code
     */
    public FrameworkException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    /**
     * Constructs a new FrameworkException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "FRAMEWORK_ERROR";
    }
    
    /**
     * Constructs a new FrameworkException with the specified detail message, error code, and cause.
     *
     * @param message the detail message
     * @param errorCode the error code
     * @param cause the cause of the exception
     */
    public FrameworkException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    /**
     * Gets the error code associated with this exception.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * Returns a detailed string representation of this exception including error code.
     *
     * @return a string representation of this exception
     */
    @Override
    public String toString() {
        String className = getClass().getSimpleName();
        String message = getLocalizedMessage();
        return String.format("%s [%s]: %s", className, errorCode, message != null ? message : "");
    }
}