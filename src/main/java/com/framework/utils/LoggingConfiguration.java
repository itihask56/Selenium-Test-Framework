package com.framework.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

/**
 * Configuration class for managing logging levels and settings dynamically
 */
public class LoggingConfiguration {
    
    private static final String FRAMEWORK_LOGGER_NAME = "com.framework";
    private static final String SELENIUM_LOGGER_NAME = "org.openqa.selenium";
    
    /**
     * Private constructor to prevent instantiation
     */
    private LoggingConfiguration() {
        throw new IllegalStateException("Utility class");
    }
    
    /**
     * Set logging level for framework components
     * @param level The logging level to set
     */
    public static void setFrameworkLogLevel(Level level) {
        setLogLevel(FRAMEWORK_LOGGER_NAME, level);
        LoggerUtils.getLogger(LoggingConfiguration.class)
                   .info("Framework logging level set to: {}", level);
    }
    
    /**
     * Set logging level for Selenium components
     * @param level The logging level to set
     */
    public static void setSeleniumLogLevel(Level level) {
        setLogLevel(SELENIUM_LOGGER_NAME, level);
        LoggerUtils.getLogger(LoggingConfiguration.class)
                   .info("Selenium logging level set to: {}", level);
    }
    
    /**
     * Set logging level for a specific logger
     * @param loggerName Name of the logger
     * @param level The logging level to set
     */
    public static void setLogLevel(String loggerName, Level level) {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(loggerName);
        
        if (!loggerConfig.getName().equals(loggerName)) {
            // Create new logger config if it doesn't exist
            loggerConfig = new LoggerConfig(loggerName, level, true);
            config.addLogger(loggerName, loggerConfig);
        } else {
            loggerConfig.setLevel(level);
        }
        
        context.updateLoggers();
    }
    
    /**
     * Enable debug logging for framework components
     */
    public static void enableDebugLogging() {
        setFrameworkLogLevel(Level.DEBUG);
    }
    
    /**
     * Enable verbose logging (TRACE level) for framework components
     */
    public static void enableVerboseLogging() {
        setFrameworkLogLevel(Level.TRACE);
    }
    
    /**
     * Set production logging levels (INFO and above)
     */
    public static void setProductionLogging() {
        setFrameworkLogLevel(Level.INFO);
        setSeleniumLogLevel(Level.WARN);
    }
    
    /**
     * Set development logging levels (DEBUG and above)
     */
    public static void setDevelopmentLogging() {
        setFrameworkLogLevel(Level.DEBUG);
        setSeleniumLogLevel(Level.INFO);
    }
    
    /**
     * Disable logging for a specific logger
     * @param loggerName Name of the logger to disable
     */
    public static void disableLogger(String loggerName) {
        setLogLevel(loggerName, Level.OFF);
    }
    
    /**
     * Get current logging level for framework components
     * @return Current logging level
     */
    public static Level getFrameworkLogLevel() {
        return getLogLevel(FRAMEWORK_LOGGER_NAME);
    }
    
    /**
     * Get current logging level for a specific logger
     * @param loggerName Name of the logger
     * @return Current logging level
     */
    public static Level getLogLevel(String loggerName) {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(loggerName);
        return loggerConfig.getLevel();
    }
    
    /**
     * Configure logging based on system properties
     * Checks for system properties to set logging levels:
     * - framework.log.level: Sets framework logging level
     * - selenium.log.level: Sets Selenium logging level
     * - debug.mode: Enables debug logging if set to true
     */
    public static void configureFromSystemProperties() {
        String frameworkLogLevel = System.getProperty("framework.log.level");
        if (frameworkLogLevel != null) {
            try {
                Level level = Level.valueOf(frameworkLogLevel.toUpperCase());
                setFrameworkLogLevel(level);
            } catch (IllegalArgumentException e) {
                LoggerUtils.getLogger(LoggingConfiguration.class)
                           .warn("Invalid framework log level: {}", frameworkLogLevel);
            }
        }
        
        String seleniumLogLevel = System.getProperty("selenium.log.level");
        if (seleniumLogLevel != null) {
            try {
                Level level = Level.valueOf(seleniumLogLevel.toUpperCase());
                setSeleniumLogLevel(level);
            } catch (IllegalArgumentException e) {
                LoggerUtils.getLogger(LoggingConfiguration.class)
                           .warn("Invalid Selenium log level: {}", seleniumLogLevel);
            }
        }
        
        String debugMode = System.getProperty("debug.mode");
        if ("true".equalsIgnoreCase(debugMode)) {
            enableDebugLogging();
        }
    }
}