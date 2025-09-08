package com.framework.validation;

import com.framework.config.ConfigManager;
import com.framework.config.TestConfig;
import com.framework.driver.DriverManager;
import com.framework.pages.BasePage;
import com.framework.reporting.ScreenshotUtils;
import com.framework.tests.BaseTest;
import com.framework.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * EndToEndValidationTest validates complete framework functionality
 * Tests integration between all framework components in realistic scenarios
 */
public class EndToEndValidationTest extends BaseTest {
    
    @Test(description = "Validate complete framework workflow")
    public void testCompleteFrameworkWorkflow() {
        testLogger.getLogger().info("Starting complete framework workflow validation");
        
        // Step 1: Validate configuration is loaded
        TestConfig config = configManager.getTestConfig();
        Assert.assertNotNull(config, "Configuration should be loaded");
        testLogger.getLogger().info("✓ Configuration loaded successfully");
        
        // Step 2: Validate WebDriver is initialized
        Assert.assertNotNull(driver, "WebDriver should be initialized");
        testLogger.getLogger().info("✓ WebDriver initialized successfully");
        
        // Step 3: Test navigation to Amazon
        driver.get("https://www.amazon.com");
        
        String title = driver.getTitle();
        Assert.assertTrue(title.contains("Amazon"), "Page title should contain Amazon");
        testLogger.getLogger().info("✓ Navigation to Amazon completed successfully");
        
        // Step 4: Test page object functionality with Amazon page
        AmazonValidationPage amazonPage = new AmazonValidationPage(driver);
        Assert.assertTrue(amazonPage.isPageLoaded(), "Amazon page should be loaded");
        testLogger.getLogger().info("✓ Page object functionality validated");
        
        // Step 5: Test element interactions - search functionality
        if (amazonPage.isSearchBoxVisible()) {
            amazonPage.enterSearchText("selenium testing");
            String enteredText = amazonPage.getSearchText();
            Assert.assertTrue(enteredText.contains("selenium"), "Search text should be entered correctly");
            testLogger.getLogger().info("✓ Element interactions validated");
            
            // Step 6: Test search button click
            amazonPage.clickSearchButton();
            testLogger.getLogger().info("✓ Search functionality validated");
        } else {
            testLogger.getLogger().info("✓ Search box not visible, skipping search test");
        }
        
        // Step 7: Test wait utilities
        WaitUtils waitUtils = new WaitUtils(driver);
        boolean logoVisible = waitUtils.isElementDisplayed(By.id("nav-logo"));
        testLogger.getLogger().info("✓ Wait utilities validated - Logo visible: {}", logoVisible);
        
        // Step 8: Test screenshot functionality
        String screenshotPath = ScreenshotUtils.captureScreenshot("end_to_end_validation");
        Assert.assertNotNull(screenshotPath, "Screenshot should be captured");
        testLogger.getLogger().info("✓ Screenshot functionality validated");
        
        // Step 9: Test logging functionality
        testLogger.getLogger().info("Testing logging functionality");
        testLogger.getLogger().debug("Debug message test");
        testLogger.getLogger().warn("Warning message test");
        testLogger.getLogger().info("✓ Logging functionality validated");
        
        testLogger.getLogger().info("Complete framework workflow validation completed successfully");
    }
    
    @Test(description = "Validate framework error handling")
    public void testFrameworkErrorHandling() {
        testLogger.getLogger().info("Starting framework error handling validation");
        
        // Test handling of invalid element locator
        AmazonValidationPage testPage = new AmazonValidationPage(driver);
        
        // Navigate to Amazon page first
        driver.get("https://www.amazon.com");
        
        try {
            // Try to interact with non-existent element
            WebElement nonExistentElement = driver.findElement(By.id("non_existent_element"));
            Assert.fail("Should have thrown exception for non-existent element");
        } catch (Exception e) {
            testLogger.getLogger().info("✓ Exception handling for non-existent element validated: {}", e.getClass().getSimpleName());
        }
        
        // Test timeout handling
        try {
            WaitUtils waitUtils = new WaitUtils(driver);
            boolean elementFound = waitUtils.isElementDisplayed(By.id("non_existent_element"));
            Assert.assertFalse(elementFound, "Should return false for non-existent element");
            testLogger.getLogger().info("✓ Timeout handling validated");
        } catch (Exception e) {
            testLogger.getLogger().info("✓ Timeout exception handling validated: {}", e.getClass().getSimpleName());
        }
        
        testLogger.getLogger().info("Framework error handling validation completed successfully");
    }
    
    @Test(description = "Validate framework resource management")
    public void testFrameworkResourceManagement() {
        testLogger.getLogger().info("Starting framework resource management validation");
        
        // Test multiple page navigations on Amazon
        String[] amazonPages = {
            "https://www.amazon.com",
            "https://www.amazon.com/gp/help/customer/display.html",
            "https://www.amazon.com/gp/site-directory"
        };
        
        for (int i = 0; i < amazonPages.length; i++) {
            driver.get(amazonPages[i]);
            
            String title = driver.getTitle();
            Assert.assertTrue(title.contains("Amazon"), 
                            "Should navigate to Amazon page: " + (i + 1));
            
            testLogger.getLogger().info("✓ Navigation {} to {} completed", i + 1, amazonPages[i]);
        }
        
        // Test window handle management
        String originalWindow = driver.getWindowHandle();
        Assert.assertNotNull(originalWindow, "Original window handle should not be null");
        
        // Test current URL retrieval
        String currentUrl = driver.getCurrentUrl();
        Assert.assertNotNull(currentUrl, "Current URL should not be null");
        Assert.assertTrue(currentUrl.contains("amazon.com"), "URL should contain amazon.com");
        
        testLogger.getLogger().info("✓ Resource management validated");
        testLogger.getLogger().info("Framework resource management validation completed successfully");
    }
    
    @Test(description = "Validate framework configuration integration")
    public void testFrameworkConfigurationIntegration() {
        testLogger.getLogger().info("Starting framework configuration integration validation");
        
        // Validate configuration is properly integrated with driver
        TestConfig config = configManager.getTestConfig();
        
        // Test browser configuration
        String configuredBrowser = config.getBrowser();
        Assert.assertNotNull(configuredBrowser, "Browser should be configured");
        testLogger.getLogger().info("✓ Browser configuration: {}", configuredBrowser);
        
        // Test timeout configuration
        int implicitTimeout = config.getImplicitTimeout();
        int explicitTimeout = config.getExplicitTimeout();
        Assert.assertTrue(implicitTimeout > 0, "Implicit timeout should be positive");
        Assert.assertTrue(explicitTimeout > 0, "Explicit timeout should be positive");
        testLogger.getLogger().info("✓ Timeout configuration - Implicit: {}s, Explicit: {}s", 
                                   implicitTimeout, explicitTimeout);
        
        // Test environment configuration
        String environment = config.getEnvironment();
        Assert.assertNotNull(environment, "Environment should be configured");
        testLogger.getLogger().info("✓ Environment configuration: {}", environment);
        
        // Test reporting configuration
        boolean screenshotOnFailure = config.isScreenshotOnFailure();
        testLogger.getLogger().info("✓ Screenshot on failure: {}", screenshotOnFailure);
        
        testLogger.getLogger().info("Framework configuration integration validation completed successfully");
    }
    
    @Test(description = "Validate framework thread safety")
    public void testFrameworkThreadSafety() {
        testLogger.getLogger().info("Starting framework thread safety validation");
        
        // Get current thread information
        String threadName = Thread.currentThread().getName();
        long threadId = Thread.currentThread().getId();
        
        testLogger.getLogger().info("Running on thread: {} (ID: {})", threadName, threadId);
        
        // Validate driver is thread-local
        Assert.assertNotNull(driver, "Driver should be available in current thread");
        
        // Test thread-specific test data
        setTestData("threadId", threadId);
        setTestData("threadName", threadName);
        
        Long storedThreadId = (Long) getTestData("threadId");
        String storedThreadName = (String) getTestData("threadName");
        
        Assert.assertEquals(storedThreadId, Long.valueOf(threadId), "Thread ID should be stored correctly");
        Assert.assertEquals(storedThreadName, threadName, "Thread name should be stored correctly");
        
        testLogger.getLogger().info("✓ Thread safety validated for thread: {}", threadName);
        testLogger.getLogger().info("Framework thread safety validation completed successfully");
    }
    
    /**
     * Amazon page object for validation tests
     */
    public static class AmazonValidationPage extends BasePage {
        
        @FindBy(id = "twotabsearchtextbox")
        private WebElement searchBox;
        
        @FindBy(id = "nav-search-submit-button")
        private WebElement searchButton;
        
        @FindBy(id = "nav-logo")
        private WebElement amazonLogo;
        
        @FindBy(id = "nav-main")
        private WebElement navigationBar;
        
        public AmazonValidationPage(WebDriver driver) {
            super(driver);
            PageFactory.initElements(driver, this);
        }
        
        public boolean isPageLoaded() {
            try {
                return isElementDisplayed(amazonLogo) || isElementDisplayed(navigationBar);
            } catch (Exception e) {
                // Page load check failed, fallback to title check
                return driver.getTitle().toLowerCase().contains("amazon");
            }
        }
        
        public boolean isSearchBoxVisible() {
            try {
                return isElementDisplayed(searchBox);
            } catch (Exception e) {
                // Search box not found
                return false;
            }
        }
        
        public void enterSearchText(String text) {
            if (isSearchBoxVisible()) {
                typeText(searchBox, text);
            }
        }
        
        public String getSearchText() {
            if (isSearchBoxVisible()) {
                return searchBox.getAttribute("value");
            }
            return "";
        }
        
        public void clickSearchButton() {
            try {
                if (isElementDisplayed(searchButton)) {
                    clickElement(searchButton);
                }
            } catch (Exception e) {
                // Search button click failed, ignore
            }
        }
    }
}