package com.framework.reporting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * NoRetry annotation can be used to disable retry mechanism for specific test methods
 * When a test method is annotated with @NoRetry, it will not be retried even if it fails
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NoRetry {
    
    /**
     * Optional reason for disabling retry
     * @return Reason for disabling retry
     */
    String reason() default "Retry disabled by annotation";
}