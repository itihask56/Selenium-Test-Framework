package com.framework.validation;

import com.framework.config.ConfigManager;
import com.framework.driver.BrowserType;
import com.framework.driver.DriverManager;
import com.framework.tests.BaseTest;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * CrossBrowserValidationTest validates framework functionality across different browsers
 * Tests browser initialization, navigation, and basic operations
 */
public class CrossBrowserValidationTest extends BaseTest {
    
    @DataProvider(name = "browserProvider")
    public Object[][] browserProvider() {
        return new Object[][] {
            {BrowserType.CHROME.getBrowserName()},
            {BrowserType.FIREFOX.getBrowserName()},
            {BrowserType.EDGE.getBrowserName()}
        };
    }
    
    @Test(dataProvider = "browserProvider", 
          description = "Validate WebDriver initialization across different browsers")
    public void testBrowserInitialization(String browserName) {
        // Set browser in system properties to override config
        System.setProperty("browser", browserName);
        
        // Reinitialize configuration to pick up new browser
        ConfigManager.getInstance().reloadConfiguration();
        
        WebDriver testDriver = null;
        try {
            // Initialize driver for specific browser
            testDriver = DriverManager.getInstance().initializeDriver();
            
            // Validate driver is not null
            Assert.assertNotNull(testDriver, "WebDriver should be initialized for browser: " + browserName);
            
            // Validate driver session is active
            String sessionId = testDriver.getWindowHandle();
            Assert.assertNotNull(sessionId, "WebDriver session should be active for browser: " + browserName);
            
            testLogger.getLogger().info("Successfully initialized {} browser", browserName);
            
        } catch (Exception e) {
            Assert.fail("Failed to initialize browser: " + browserName + ". Error: " + e.getMessage());
        } finally {
            // Cleanup test driver
            if (testDriver != null) {
                try {
                    testDriver.quit();
                } catch (Exception e) {
                    testLogger.getLogger().warn("Error closing test driver for {}: {}", browserName, e.getMessage());
                }
            }
        }
    }
    
    @Test(dataProvider = "browserProvider",
          description = "Validate basic browser navigation across different browsers")
    public void testBrowserNavigation(String browserName) {
        // Set browser in system properties
        System.setProperty("browser", browserName);
        ConfigManager.getInstance().reloadConfiguration();
        
        WebDriver testDriver = null;
        try {
            testDriver = DriverManager.getInstance().initializeDriver();
            
            // Test navigation to Amazon
            testDriver.get("https://www.amazon.com");
            
            // Validate page title contains Amazon
            String title = testDriver.getTitle();
            Assert.assertTrue(title.contains("Amazon"), "Page title should contain Amazon for browser: " + browserName);
            
            // Validate current URL
            String currentUrl = testDriver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("amazon.com"), 
                            "Current URL should contain amazon.com for browser: " + browserName);
            
            testLogger.getLogger().info("Successfully validated navigation for {} browser", browserName);
            
        } catch (Exception e) {
            Assert.fail("Navigation test failed for browser: " + browserName + ". Error: " + e.getMessage());
        } finally {
            if (testDriver != null) {
                try {
                    testDriver.quit();
                } catch (Exception e) {
                    testLogger.getLogger().warn("Error closing test driver for {}: {}", browserName, e.getMessage());
                }
            }
        }
    }
    
    @Test(dataProvider = "browserProvider",
          description = "Validate headless mode functionality across browsers")
    public void testHeadlessMode(String browserName) {
        // Skip Safari for headless testing as it doesn't support headless mode
        if (browserName.equalsIgnoreCase("safari")) {
            testLogger.getLogger().info("Skipping headless test for Safari as it doesn't support headless mode");
            return;
        }
        
        // Set browser and headless mode
        System.setProperty("browser", browserName);
        System.setProperty("headless", "true");
        ConfigManager.getInstance().reloadConfiguration();
        
        WebDriver testDriver = null;
        try {
            testDriver = DriverManager.getInstance().initializeDriver();
            
            // Validate driver is initialized in headless mode
            Assert.assertNotNull(testDriver, "WebDriver should be initialized in headless mode for: " + browserName);
            
            // Test basic functionality in headless mode
            testDriver.get("https://www.amazon.com");
            
            String title = testDriver.getTitle();
            Assert.assertTrue(title.contains("Amazon"), "Headless mode should work correctly for: " + browserName);
            
            testLogger.getLogger().info("Successfully validated headless mode for {} browser", browserName);
            
        } catch (Exception e) {
            Assert.fail("Headless mode test failed for browser: " + browserName + ". Error: " + e.getMessage());
        } finally {
            // Reset headless property
            System.setProperty("headless", "false");
            if (testDriver != null) {
                try {
                    testDriver.quit();
                } catch (Exception e) {
                    testLogger.getLogger().warn("Error closing headless test driver for {}: {}", browserName, e.getMessage());
                }
            }
        }
    }
    
    @Test(description = "Validate parallel browser execution")
    public void testParallelBrowserExecution() {
        // This test validates that multiple browser instances can run in parallel
        // It's designed to be run with parallel execution enabled in TestNG
        
        String currentBrowser = configManager.getTestConfig().getBrowser();
        testLogger.getLogger().info("Testing parallel execution with browser: {}", currentBrowser);
        
        // Validate that driver is properly isolated per thread
        Assert.assertNotNull(driver, "Driver should be available in current thread");
        
        // Get thread-specific information
        String threadName = Thread.currentThread().getName();
        long threadId = Thread.currentThread().getId();
        
        testLogger.getLogger().info("Parallel test running on thread: {} (ID: {})", threadName, threadId);
        
        // Store thread information in test data
        setTestData("threadName", threadName);
        setTestData("threadId", threadId);
        
        // Validate thread isolation by checking driver instance
        String driverString = driver.toString();
        Assert.assertTrue(driverString.contains("WebDriver"), 
                         "Driver should be valid WebDriver instance in thread: " + threadName);
        
        // Test basic operation to ensure driver works in parallel
        driver.get("https://www.amazon.com");
        
        String title = driver.getTitle();
        Assert.assertTrue(title.contains("Amazon"), 
                         "Amazon page should load correctly in parallel execution");
        
        testLogger.getLogger().info("Parallel execution validated successfully for thread: {}", threadName);
    }
}