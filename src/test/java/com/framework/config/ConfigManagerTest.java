package com.framework.config;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Properties;

/**
 * Unit tests for ConfigManager class
 * Tests the singleton pattern, property loading, and configuration management functionality
 */
public class ConfigManagerTest {
    
    private ConfigManager configManager;
    
    @BeforeMethod
    public void setUp() {
        configManager = ConfigManager.getInstance();
    }
    
    @Test(description = "Test ConfigManager singleton pattern")
    public void testSingletonPattern() {
        ConfigManager instance1 = ConfigManager.getInstance();
        ConfigManager instance2 = ConfigManager.getInstance();
        
        Assert.assertSame(instance1, instance2, "ConfigManager should follow singleton pattern");
        Assert.assertNotNull(instance1, "ConfigManager instance should not be null");
    }
    
    @Test(description = "Test loading of browser configuration properties")
    public void testBrowserConfiguration() {
        TestConfig config = configManager.getTestConfig();
        
        Assert.assertNotNull(config, "TestConfig should not be null");
        Assert.assertEquals(config.getBrowser(), "chrome", "Default browser should be chrome");
        Assert.assertFalse(config.isHeadless(), "Default headless should be false");
        Assert.assertEquals(config.getImplicitTimeout(), 10, "Default implicit timeout should be 10");
        Assert.assertEquals(config.getExplicitTimeout(), 20, "Default explicit timeout should be 20");
        Assert.assertEquals(config.getWindowWidth(), 1920, "Default window width should be 1920");
        Assert.assertEquals(config.getWindowHeight(), 1080, "Default window height should be 1080");
    }
    
    @Test(description = "Test loading of environment configuration properties")
    public void testEnvironmentConfiguration() {
        TestConfig config = configManager.getTestConfig();
        
        Assert.assertEquals(config.getEnvironment(), "dev", "Default environment should be dev");
        Assert.assertEquals(config.getBaseUrlDev(), "https://dev.example.com", "Dev URL should match config");
        Assert.assertEquals(config.getBaseUrlStaging(), "https://staging.example.com", "Staging URL should match config");
        Assert.assertEquals(config.getBaseUrlProd(), "https://prod.example.com", "Prod URL should match config");
        
        // Test getBaseUrl() method for current environment
        Assert.assertEquals(config.getBaseUrl(), "https://dev.example.com", "Base URL should return dev URL for dev environment");
    }
    
    @Test(description = "Test loading of test configuration properties")
    public void testTestConfiguration() {
        TestConfig config = configManager.getTestConfig();
        
        Assert.assertTrue(config.isParallelExecution(), "Default parallel execution should be true");
        Assert.assertEquals(config.getThreadCount(), 3, "Default thread count should be 3");
        Assert.assertEquals(config.getRetryCount(), 2, "Default retry count should be 2");
        Assert.assertTrue(config.isScreenshotOnFailure(), "Default screenshot on failure should be true");
    }
    
    @Test(description = "Test loading of reporting configuration properties")
    public void testReportingConfiguration() {
        TestConfig config = configManager.getTestConfig();
        
        Assert.assertEquals(config.getReportPath(), "reports", "Default report path should be reports");
        Assert.assertEquals(config.getScreenshotPath(), "screenshots", "Default screenshot path should be screenshots");
    }
    
    @Test(description = "Test getProperty method with default values")
    public void testGetPropertyWithDefaults() {
        String browser = configManager.getProperty("browser", "firefox");
        Assert.assertEquals(browser, "chrome", "Should return actual property value");
        
        String nonExistentProperty = configManager.getProperty("non.existent.property", "default_value");
        Assert.assertEquals(nonExistentProperty, "default_value", "Should return default value for non-existent property");
    }
    
    @Test(description = "Test getBooleanProperty method")
    public void testGetBooleanProperty() {
        boolean headless = configManager.getBooleanProperty("headless", true);
        Assert.assertFalse(headless, "Should return false for headless property");
        
        boolean nonExistentBoolean = configManager.getBooleanProperty("non.existent.boolean", true);
        Assert.assertTrue(nonExistentBoolean, "Should return default value for non-existent boolean property");
        
        boolean parallelExecution = configManager.getBooleanProperty("parallel.execution", false);
        Assert.assertTrue(parallelExecution, "Should return true for parallel.execution property");
    }
    
    @Test(description = "Test getIntProperty method")
    public void testGetIntProperty() {
        int implicitTimeout = configManager.getIntProperty("browser.timeout.implicit", 5);
        Assert.assertEquals(implicitTimeout, 10, "Should return 10 for implicit timeout property");
        
        int nonExistentInt = configManager.getIntProperty("non.existent.int", 99);
        Assert.assertEquals(nonExistentInt, 99, "Should return default value for non-existent int property");
        
        int threadCount = configManager.getIntProperty("thread.count", 1);
        Assert.assertEquals(threadCount, 3, "Should return 3 for thread count property");
    }
    
    @Test(description = "Test getLongProperty method")
    public void testGetLongProperty() {
        long timeout = configManager.getLongProperty("browser.timeout.implicit", 5L);
        Assert.assertEquals(timeout, 10L, "Should return 10 for implicit timeout as long");
        
        long nonExistentLong = configManager.getLongProperty("non.existent.long", 999L);
        Assert.assertEquals(nonExistentLong, 999L, "Should return default value for non-existent long property");
    }
    
    @Test(description = "Test hasProperty method")
    public void testHasProperty() {
        Assert.assertTrue(configManager.hasProperty("browser"), "Should return true for existing property");
        Assert.assertFalse(configManager.hasProperty("non.existent.property"), "Should return false for non-existent property");
    }
    
    @Test(description = "Test getAllProperties method")
    public void testGetAllProperties() {
        Properties allProperties = configManager.getAllProperties();
        
        Assert.assertNotNull(allProperties, "All properties should not be null");
        Assert.assertTrue(allProperties.size() > 0, "Should have at least one property");
        Assert.assertTrue(allProperties.containsKey("browser"), "Should contain browser property");
    }
    
    @Test(description = "Test setProperty method for runtime changes")
    public void testSetProperty() {
        // Set a new property at runtime
        configManager.setProperty("test.runtime.property", "test_value");
        
        String value = configManager.getProperty("test.runtime.property");
        Assert.assertEquals(value, "test_value", "Should return the runtime set property value");
        
        // Test updating existing property
        configManager.setProperty("browser", "firefox");
        TestConfig config = configManager.getTestConfig();
        Assert.assertEquals(config.getBrowser(), "firefox", "Browser should be updated to firefox");
        
        // Reset back to original value
        configManager.setProperty("browser", "chrome");
    }
    
    @Test(description = "Test system property override functionality")
    public void testSystemPropertyOverride() {
        // Set system property
        System.setProperty("browser", "edge");
        
        String browser = configManager.getProperty("browser", "chrome");
        Assert.assertEquals(browser, "edge", "System property should override config file property");
        
        // Clean up system property
        System.clearProperty("browser");
    }
    
    @Test(description = "Test invalid integer property handling")
    public void testInvalidIntegerProperty() {
        // Set an invalid integer property
        configManager.setProperty("invalid.int.property", "not_a_number");
        
        int value = configManager.getIntProperty("invalid.int.property", 42);
        Assert.assertEquals(value, 42, "Should return default value for invalid integer property");
    }
    
    @Test(description = "Test invalid long property handling")
    public void testInvalidLongProperty() {
        // Set an invalid long property
        configManager.setProperty("invalid.long.property", "not_a_number");
        
        long value = configManager.getLongProperty("invalid.long.property", 42L);
        Assert.assertEquals(value, 42L, "Should return default value for invalid long property");
    }
    
    @Test(description = "Test TestConfig toString method")
    public void testTestConfigToString() {
        TestConfig config = configManager.getTestConfig();
        String configString = config.toString();
        
        Assert.assertNotNull(configString, "ToString should not return null");
        Assert.assertTrue(configString.contains("browser="), "ToString should contain browser information");
        Assert.assertTrue(configString.contains("environment="), "ToString should contain environment information");
    }
    
    @Test(description = "Test getBaseUrl method for different environments")
    public void testGetBaseUrlForDifferentEnvironments() {
        TestConfig config = configManager.getTestConfig();
        
        // Test dev environment
        config.setEnvironment("dev");
        Assert.assertEquals(config.getBaseUrl(), "https://dev.example.com", "Should return dev URL for dev environment");
        
        // Test staging environment
        config.setEnvironment("staging");
        Assert.assertEquals(config.getBaseUrl(), "https://staging.example.com", "Should return staging URL for staging environment");
        
        // Test prod environment
        config.setEnvironment("prod");
        Assert.assertEquals(config.getBaseUrl(), "https://prod.example.com", "Should return prod URL for prod environment");
        
        // Test unknown environment (should default to dev)
        config.setEnvironment("unknown");
        Assert.assertEquals(config.getBaseUrl(), "https://dev.example.com", "Should return dev URL for unknown environment");
        
        // Reset to original environment
        config.setEnvironment("dev");
    }
}