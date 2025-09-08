package com.framework.reporting;

import com.framework.utils.LoggerUtils;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * RetryTransformer automatically applies TestRetryAnalyzer to all test methods
 * This transformer is used to enable retry mechanism across all tests without manual configuration
 */
public class RetryTransformer implements IAnnotationTransformer {
    
    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        // Apply retry analyzer to all test methods that don't already have one
        if (annotation.getRetryAnalyzerClass() == null) {
            annotation.setRetryAnalyzer(TestRetryAnalyzer.class);
            
            if (testMethod != null) {
                LoggerUtils.getLogger(RetryTransformer.class).debug("Applied TestRetryAnalyzer to test method: " + 
                                testClass.getSimpleName() + "." + testMethod.getName());
            }
        }
    }
}