package com.framework.validation;

import com.framework.config.ConfigManager;
import com.framework.config.TestConfig;
import com.framework.exceptions.ConfigurationException;
import com.framework.tests.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * ConfigurationValidationTest validates framework configuration management
 * Tests property loading, validation, and environment-specific configurations
 */
public class ConfigurationValidationTest extends BaseTest {
    
    @Test(description = "Validate configuration file exists and is readable")
    public void testConfigurationFileExists() {
        // Test that config.properties file exists in classpath
        try {
            Properties testProps = new Properties();
            testProps.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
            
            Assert.assertNotNull(testProps, "Configuration properties should be loaded successfully");
            Assert.assertFalse(testProps.isEmpty(), "Configuration properties should not be empty");
            
            testLogger.getLogger().info("Configuration file loaded successfully with {} properties", testProps.size());
            
        } catch (IOException e) {
            Assert.fail("Failed to load configuration file: " + e.getMessage());
        }
    }
    
    @Test(description = "Validate ConfigManager singleton behavior")
    public void testConfigManagerSingleton() {
        ConfigManager instance1 = ConfigManager.getInstance();
        ConfigManager instance2 = ConfigManager.getInstance();
        
        Assert.assertNotNull(instance1, "ConfigManager instance should not be null");
        Assert.assertNotNull(instance2, "ConfigManager instance should not be null");
        Assert.assertSame(instance1, instance2, "ConfigManager should return same singleton instance");
        
        testLogger.getLogger().info("ConfigManager singleton behavior validated successfully");
    }
    
    @Test(description = "Validate required configuration properties exist")
    public void testRequiredPropertiesExist() {
        ConfigManager configManager = ConfigManager.getInstance();
        
        // Test browser configuration
        Assert.assertTrue(configManager.hasProperty("browser"), "Browser property should exist");
        Assert.assertTrue(configManager.hasProperty("headless"), "Headless property should exist");
        Assert.assertTrue(configManager.hasProperty("browser.timeout.implicit"), "Implicit timeout property should exist");
        Assert.assertTrue(configManager.hasProperty("browser.timeout.explicit"), "Explicit timeout property should exist");
        
        // Test environment configuration
        Assert.assertTrue(configManager.hasProperty("environment"), "Environment property should exist");
        Assert.assertTrue(configManager.hasProperty("base.url.dev"), "Dev base URL property should exist");
        Assert.assertTrue(configManager.hasProperty("base.url.staging"), "Staging base URL property should exist");
        Assert.assertTrue(configManager.hasProperty("base.url.prod"), "Prod base URL property should exist");
        
        // Test execution configuration
        Assert.assertTrue(configManager.hasProperty("parallel.execution"), "Parallel execution property should exist");
        Assert.assertTrue(configManager.hasProperty("thread.count"), "Thread count property should exist");
        Assert.assertTrue(configManager.hasProperty("retry.count"), "Retry count property should exist");
        
        testLogger.getLogger().info("All required configuration properties exist");
    }
    
    @DataProvider(name = "browserConfigProvider")
    public Object[][] browserConfigProvider() {
        return new Object[][] {
            {"chrome", true},
            {"firefox", true},
            {"edge", true},
            {"safari", true},
            {"invalid_browser", false}
        };
    }
    
    @Test(dataProvider = "browserConfigProvider",
          description = "Validate browser configuration values")
    public void testBrowserConfiguration(String browserName, boolean shouldBeValid) {
        // Set browser property temporarily
        String originalBrowser = System.getProperty("browser");
        System.setProperty("browser", browserName);
        
        try {
            ConfigManager.getInstance().reloadConfiguration();
            TestConfig testConfig = ConfigManager.getInstance().getTestConfig();
            
            String configuredBrowser = testConfig.getBrowser();
            Assert.assertEquals(configuredBrowser, browserName, 
                              "Configured browser should match set value");
            
            if (shouldBeValid) {
                testLogger.getLogger().info("Valid browser configuration validated: {}", browserName);
            } else {
                testLogger.getLogger().info("Invalid browser configuration handled: {}", browserName);
            }
            
        } finally {
            // Restore original browser property
            if (originalBrowser != null) {
                System.setProperty("browser", originalBrowser);
            } else {
                System.clearProperty("browser");
            }
            ConfigManager.getInstance().reloadConfiguration();
        }
    }
    
    @DataProvider(name = "environmentConfigProvider")
    public Object[][] environmentConfigProvider() {
        return new Object[][] {
            {"dev"},
            {"staging"},
            {"prod"}
        };
    }
    
    @Test(dataProvider = "environmentConfigProvider",
          description = "Validate environment-specific configuration")
    public void testEnvironmentConfiguration(String environment) {
        String originalEnvironment = System.getProperty("environment");
        System.setProperty("environment", environment);
        
        try {
            ConfigManager.getInstance().reloadConfiguration();
            TestConfig testConfig = ConfigManager.getInstance().getTestConfig();
            
            Assert.assertEquals(testConfig.getEnvironment(), environment, 
                              "Environment should be set correctly");
            
            // Validate environment-specific URLs exist
            switch (environment) {
                case "dev":
                    Assert.assertNotNull(testConfig.getBaseUrlDev(), "Dev URL should not be null");
                    break;
                case "staging":
                    Assert.assertNotNull(testConfig.getBaseUrlStaging(), "Staging URL should not be null");
                    break;
                case "prod":
                    Assert.assertNotNull(testConfig.getBaseUrlProd(), "Prod URL should not be null");
                    break;
            }
            
            testLogger.getLogger().info("Environment configuration validated for: {}", environment);
            
        } finally {
            if (originalEnvironment != null) {
                System.setProperty("environment", originalEnvironment);
            } else {
                System.clearProperty("environment");
            }
            ConfigManager.getInstance().reloadConfiguration();
        }
    }
    
    @Test(description = "Validate numeric configuration properties")
    public void testNumericConfigurationProperties() {
        ConfigManager configManager = ConfigManager.getInstance();
        
        // Test timeout configurations
        int implicitTimeout = configManager.getIntProperty("browser.timeout.implicit", -1);
        Assert.assertTrue(implicitTimeout > 0, "Implicit timeout should be positive integer");
        
        int explicitTimeout = configManager.getIntProperty("browser.timeout.explicit", -1);
        Assert.assertTrue(explicitTimeout > 0, "Explicit timeout should be positive integer");
        
        // Test thread configuration
        int threadCount = configManager.getIntProperty("thread.count", -1);
        Assert.assertTrue(threadCount > 0, "Thread count should be positive integer");
        
        int retryCount = configManager.getIntProperty("retry.count", -1);
        Assert.assertTrue(retryCount >= 0, "Retry count should be non-negative integer");
        
        // Test window size configuration
        int windowWidth = configManager.getIntProperty("browser.window.width", -1);
        Assert.assertTrue(windowWidth > 0, "Window width should be positive integer");
        
        int windowHeight = configManager.getIntProperty("browser.window.height", -1);
        Assert.assertTrue(windowHeight > 0, "Window height should be positive integer");
        
        testLogger.getLogger().info("Numeric configuration properties validated successfully");
    }
    
    @Test(description = "Validate boolean configuration properties")
    public void testBooleanConfigurationProperties() {
        ConfigManager configManager = ConfigManager.getInstance();
        
        // Test boolean properties - these should not throw exceptions
        boolean headless = configManager.getBooleanProperty("headless", false);
        boolean parallelExecution = configManager.getBooleanProperty("parallel.execution", false);
        boolean screenshotOnFailure = configManager.getBooleanProperty("screenshot.on.failure", true);
        
        // Log values for verification
        testLogger.getLogger().info("Boolean configurations - Headless: {}, Parallel: {}, Screenshot on failure: {}", 
                                   headless, parallelExecution, screenshotOnFailure);
        
        // Test with invalid boolean values
        String originalHeadless = System.getProperty("headless");
        System.setProperty("headless", "invalid_boolean");
        
        try {
            ConfigManager.getInstance().reloadConfiguration();
            boolean invalidBoolean = ConfigManager.getInstance().getBooleanProperty("headless", true);
            // Should use default value when invalid boolean is provided
            Assert.assertTrue(invalidBoolean, "Should use default value for invalid boolean");
            
        } finally {
            if (originalHeadless != null) {
                System.setProperty("headless", originalHeadless);
            } else {
                System.clearProperty("headless");
            }
            ConfigManager.getInstance().reloadConfiguration();
        }
        
        testLogger.getLogger().info("Boolean configuration properties validated successfully");
    }
    
    @Test(description = "Validate system property override functionality")
    public void testSystemPropertyOverride() {
        ConfigManager configManager = ConfigManager.getInstance();
        
        // Get original value
        String originalBrowser = configManager.getProperty("browser");
        
        // Set system property override
        String overrideBrowser = "firefox";
        System.setProperty("browser", overrideBrowser);
        
        try {
            // System property should override config file
            String actualBrowser = configManager.getProperty("browser");
            Assert.assertEquals(actualBrowser, overrideBrowser, 
                              "System property should override config file value");
            
            testLogger.getLogger().info("System property override validated - Original: {}, Override: {}", 
                                       originalBrowser, overrideBrowser);
            
        } finally {
            // Clean up system property
            System.clearProperty("browser");
        }
    }
    
    @Test(description = "Validate configuration reload functionality")
    public void testConfigurationReload() {
        ConfigManager configManager = ConfigManager.getInstance();
        
        // Get initial configuration
        TestConfig initialConfig = configManager.getTestConfig();
        String initialBrowser = initialConfig.getBrowser();
        
        // Modify system property
        System.setProperty("browser", "edge");
        
        try {
            // Reload configuration
            configManager.reloadConfiguration();
            
            // Get updated configuration
            TestConfig updatedConfig = configManager.getTestConfig();
            String updatedBrowser = updatedConfig.getBrowser();
            
            Assert.assertEquals(updatedBrowser, "edge", "Configuration should be reloaded with new values");
            Assert.assertNotEquals(initialBrowser, updatedBrowser, "Browser configuration should have changed");
            
            testLogger.getLogger().info("Configuration reload validated - Initial: {}, Updated: {}", 
                                       initialBrowser, updatedBrowser);
            
        } finally {
            System.clearProperty("browser");
            configManager.reloadConfiguration();
        }
    }
    
    @Test(description = "Validate default value handling")
    public void testDefaultValueHandling() {
        ConfigManager configManager = ConfigManager.getInstance();
        
        // Test string property with default
        String nonExistentProperty = configManager.getProperty("non.existent.property", "default_value");
        Assert.assertEquals(nonExistentProperty, "default_value", 
                           "Should return default value for non-existent property");
        
        // Test integer property with default
        int nonExistentIntProperty = configManager.getIntProperty("non.existent.int.property", 42);
        Assert.assertEquals(nonExistentIntProperty, 42, 
                           "Should return default value for non-existent integer property");
        
        // Test boolean property with default
        boolean nonExistentBoolProperty = configManager.getBooleanProperty("non.existent.bool.property", true);
        Assert.assertTrue(nonExistentBoolProperty, 
                         "Should return default value for non-existent boolean property");
        
        testLogger.getLogger().info("Default value handling validated successfully");
    }
    
    @Test(description = "Validate TestConfig object initialization")
    public void testTestConfigInitialization() {
        TestConfig testConfig = ConfigManager.getInstance().getTestConfig();
        
        Assert.assertNotNull(testConfig, "TestConfig should not be null");
        
        // Validate all required fields are initialized
        Assert.assertNotNull(testConfig.getBrowser(), "Browser should not be null");
        Assert.assertNotNull(testConfig.getEnvironment(), "Environment should not be null");
        Assert.assertTrue(testConfig.getImplicitTimeout() > 0, "Implicit timeout should be positive");
        Assert.assertTrue(testConfig.getExplicitTimeout() > 0, "Explicit timeout should be positive");
        Assert.assertTrue(testConfig.getThreadCount() > 0, "Thread count should be positive");
        Assert.assertTrue(testConfig.getRetryCount() >= 0, "Retry count should be non-negative");
        
        testLogger.getLogger().info("TestConfig initialization validated successfully");
        testLogger.getLogger().info("TestConfig details - Browser: {}, Environment: {}, Headless: {}", 
                                   testConfig.getBrowser(), testConfig.getEnvironment(), testConfig.isHeadless());
    }
}