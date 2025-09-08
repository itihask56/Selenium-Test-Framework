package com.framework.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Unit tests for LoggingConfiguration class
 */
public class LoggingConfigurationTest {
    
    private Level originalFrameworkLevel;
    private Level originalSeleniumLevel;
    
    @BeforeMethod
    public void setUp() {
        // Store original logging levels
        originalFrameworkLevel = LoggingConfiguration.getFrameworkLogLevel();
        originalSeleniumLevel = LoggingConfiguration.getLogLevel("org.openqa.selenium");
        
        // Clear system properties
        System.clearProperty("framework.log.level");
        System.clearProperty("selenium.log.level");
        System.clearProperty("debug.mode");
    }
    
    @AfterMethod
    public void tearDown() {
        // Restore original logging levels
        LoggingConfiguration.setFrameworkLogLevel(originalFrameworkLevel);
        LoggingConfiguration.setSeleniumLogLevel(originalSeleniumLevel);
        
        // Clear system properties
        System.clearProperty("framework.log.level");
        System.clearProperty("selenium.log.level");
        System.clearProperty("debug.mode");
    }
    
    @Test
    public void testSetFrameworkLogLevel() {
        Level testLevel = Level.WARN;
        
        LoggingConfiguration.setFrameworkLogLevel(testLevel);
        
        assertEquals(LoggingConfiguration.getFrameworkLogLevel(), testLevel);
    }
    
    @Test
    public void testSetSeleniumLogLevel() {
        Level testLevel = Level.ERROR;
        
        LoggingConfiguration.setSeleniumLogLevel(testLevel);
        
        assertEquals(LoggingConfiguration.getLogLevel("org.openqa.selenium"), testLevel);
    }
    
    @Test
    public void testSetLogLevel() {
        String loggerName = "com.test.logger";
        Level testLevel = Level.DEBUG;
        
        LoggingConfiguration.setLogLevel(loggerName, testLevel);
        
        assertEquals(LoggingConfiguration.getLogLevel(loggerName), testLevel);
    }
    
    @Test
    public void testEnableDebugLogging() {
        LoggingConfiguration.enableDebugLogging();
        
        assertEquals(LoggingConfiguration.getFrameworkLogLevel(), Level.DEBUG);
    }
    
    @Test
    public void testEnableVerboseLogging() {
        LoggingConfiguration.enableVerboseLogging();
        
        assertEquals(LoggingConfiguration.getFrameworkLogLevel(), Level.TRACE);
    }
    
    @Test
    public void testSetProductionLogging() {
        LoggingConfiguration.setProductionLogging();
        
        assertEquals(LoggingConfiguration.getFrameworkLogLevel(), Level.INFO);
        assertEquals(LoggingConfiguration.getLogLevel("org.openqa.selenium"), Level.WARN);
    }
    
    @Test
    public void testSetDevelopmentLogging() {
        LoggingConfiguration.setDevelopmentLogging();
        
        assertEquals(LoggingConfiguration.getFrameworkLogLevel(), Level.DEBUG);
        assertEquals(LoggingConfiguration.getLogLevel("org.openqa.selenium"), Level.INFO);
    }
    
    @Test
    public void testDisableLogger() {
        String loggerName = "com.test.disabled";
        
        LoggingConfiguration.disableLogger(loggerName);
        
        assertEquals(LoggingConfiguration.getLogLevel(loggerName), Level.OFF);
    }
    
    @Test
    public void testGetFrameworkLogLevel() {
        Level currentLevel = LoggingConfiguration.getFrameworkLogLevel();
        assertNotNull(currentLevel);
    }
    
    @Test
    public void testGetLogLevel() {
        String loggerName = "com.framework";
        Level level = LoggingConfiguration.getLogLevel(loggerName);
        assertNotNull(level);
    }
    
    @Test
    public void testConfigureFromSystemPropertiesFrameworkLevel() {
        System.setProperty("framework.log.level", "ERROR");
        
        LoggingConfiguration.configureFromSystemProperties();
        
        assertEquals(LoggingConfiguration.getFrameworkLogLevel(), Level.ERROR);
    }
    
    @Test
    public void testConfigureFromSystemPropertiesSeleniumLevel() {
        System.setProperty("selenium.log.level", "FATAL");
        
        LoggingConfiguration.configureFromSystemProperties();
        
        assertEquals(LoggingConfiguration.getLogLevel("org.openqa.selenium"), Level.FATAL);
    }
    
    @Test
    public void testConfigureFromSystemPropertiesDebugMode() {
        System.setProperty("debug.mode", "true");
        
        LoggingConfiguration.configureFromSystemProperties();
        
        assertEquals(LoggingConfiguration.getFrameworkLogLevel(), Level.DEBUG);
    }
    
    @Test
    public void testConfigureFromSystemPropertiesInvalidFrameworkLevel() {
        Level originalLevel = LoggingConfiguration.getFrameworkLogLevel();
        System.setProperty("framework.log.level", "INVALID_LEVEL");
        
        LoggingConfiguration.configureFromSystemProperties();
        
        // Level should remain unchanged for invalid input
        assertEquals(LoggingConfiguration.getFrameworkLogLevel(), originalLevel);
    }
    
    @Test
    public void testConfigureFromSystemPropertiesInvalidSeleniumLevel() {
        Level originalLevel = LoggingConfiguration.getLogLevel("org.openqa.selenium");
        System.setProperty("selenium.log.level", "INVALID_LEVEL");
        
        LoggingConfiguration.configureFromSystemProperties();
        
        // Level should remain unchanged for invalid input
        assertEquals(LoggingConfiguration.getLogLevel("org.openqa.selenium"), originalLevel);
    }
    
    @Test
    public void testConfigureFromSystemPropertiesDebugModeFalse() {
        Level originalLevel = LoggingConfiguration.getFrameworkLogLevel();
        System.setProperty("debug.mode", "false");
        
        LoggingConfiguration.configureFromSystemProperties();
        
        // Level should remain unchanged when debug mode is false
        assertEquals(LoggingConfiguration.getFrameworkLogLevel(), originalLevel);
    }
    
    @Test
    public void testConfigureFromSystemPropertiesNoProperties() {
        Level originalFrameworkLevel = LoggingConfiguration.getFrameworkLogLevel();
        Level originalSeleniumLevel = LoggingConfiguration.getLogLevel("org.openqa.selenium");
        
        LoggingConfiguration.configureFromSystemProperties();
        
        // Levels should remain unchanged when no properties are set
        assertEquals(LoggingConfiguration.getFrameworkLogLevel(), originalFrameworkLevel);
        assertEquals(LoggingConfiguration.getLogLevel("org.openqa.selenium"), originalSeleniumLevel);
    }
    
    @Test
    public void testMultipleLoggerConfigurations() {
        String logger1 = "com.test.logger1";
        String logger2 = "com.test.logger2";
        Level level1 = Level.INFO;
        Level level2 = Level.WARN;
        
        LoggingConfiguration.setLogLevel(logger1, level1);
        LoggingConfiguration.setLogLevel(logger2, level2);
        
        assertEquals(LoggingConfiguration.getLogLevel(logger1), level1);
        assertEquals(LoggingConfiguration.getLogLevel(logger2), level2);
    }
    
    @Test
    public void testLoggerContextUpdate() {
        Level newLevel = Level.FATAL;
        
        LoggingConfiguration.setFrameworkLogLevel(newLevel);
        
        // Verify that the logger context was updated
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig("com.framework");
        
        assertEquals(loggerConfig.getLevel(), newLevel);
    }
}