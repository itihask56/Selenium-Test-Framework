package com.framework.tests;

import com.framework.pages.DemoWebsitePage;
import com.framework.utils.WaitUtils;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Comprehensive test suite for The Internet Demo Website
 * Demonstrates various testing scenarios using the framework
 */
public class DemoWebsiteTest extends BaseTest {
    
    private DemoWebsitePage demoPage;
    private WaitUtils waitUtils;
    
    @BeforeMethod
    public void setupPage() {
        demoPage = new DemoWebsitePage(driver);
        waitUtils = new WaitUtils(driver);
    }
    
    @Test(description = "Verify homepage loads correctly", priority = 1)
    public void testHomepageLoad() {
        testLogger.getLogger().info("Testing homepage load functionality");
        
        // Verify page loads
        Assert.assertTrue(demoPage.isHomepageLoaded(), "Homepage should load successfully");
        
        // Verify page title
        String pageTitle = demoPage.getPageTitle();
        Assert.assertTrue(pageTitle.contains("Welcome to the-internet"), 
                         "Page title should contain welcome message");
        
        testLogger.getLogger().info("✓ Homepage loaded successfully with title: {}", pageTitle);
    }
    
    @Test(description = "Test successful login functionality", priority = 2)
    public void testSuccessfulLogin() {
        testLogger.getLogger().info("Testing successful login functionality");
        
        // Navigate to form authentication
        demoPage.clickFormAuthentication();
        
        // Perform login with valid credentials
        demoPage.login("tomsmith", "SuperSecretPassword!");
        
        // Verify successful login
        Assert.assertTrue(demoPage.isLoginSuccessful(), "Login should be successful");
        
        testLogger.getLogger().info("✓ Login successful with valid credentials");
        
        // Test logout
        demoPage.logout();
        Assert.assertTrue(demoPage.isLoggedOut(), "Logout should be successful");
        
        testLogger.getLogger().info("✓ Logout successful");
    }
    
    @Test(description = "Test failed login functionality", priority = 3)
    public void testFailedLogin() {
        testLogger.getLogger().info("Testing failed login functionality");
        
        // Navigate to form authentication
        demoPage.clickFormAuthentication();
        
        // Perform login with invalid credentials
        demoPage.login("invaliduser", "wrongpassword");
        
        // Verify failed login
        Assert.assertTrue(demoPage.isLoginFailed(), "Login should fail with invalid credentials");
        
        testLogger.getLogger().info("✓ Login correctly failed with invalid credentials");
    }
    
    @Test(description = "Test checkbox interactions", priority = 4)
    public void testCheckboxFunctionality() {
        testLogger.getLogger().info("Testing checkbox functionality");
        
        // Navigate to checkboxes page
        demoPage.clickCheckboxes();
        
        // Test checkbox 1 (initially unchecked)
        boolean initialState1 = demoPage.isCheckbox1Selected();
        demoPage.toggleCheckbox1();
        boolean newState1 = demoPage.isCheckbox1Selected();
        
        Assert.assertNotEquals(initialState1, newState1, "Checkbox 1 state should change after click");
        testLogger.getLogger().info("✓ Checkbox 1 toggled from {} to {}", initialState1, newState1);
        
        // Test checkbox 2 (initially checked)
        boolean initialState2 = demoPage.isCheckbox2Selected();
        demoPage.toggleCheckbox2();
        boolean newState2 = demoPage.isCheckbox2Selected();
        
        Assert.assertNotEquals(initialState2, newState2, "Checkbox 2 state should change after click");
        testLogger.getLogger().info("✓ Checkbox 2 toggled from {} to {}", initialState2, newState2);
    }
    
    @Test(description = "Test dropdown selection", priority = 5)
    public void testDropdownFunctionality() {
        testLogger.getLogger().info("Testing dropdown functionality");
        
        // Navigate to dropdown page
        demoPage.clickDropdown();
        
        // Test selecting Option 1
        demoPage.selectDropdownOption("Option 1");
        String selectedOption = demoPage.getSelectedDropdownOption();
        Assert.assertEquals(selectedOption, "Option 1", "Should select Option 1");
        
        testLogger.getLogger().info("✓ Successfully selected: {}", selectedOption);
        
        // Test selecting Option 2
        demoPage.selectDropdownOption("Option 2");
        selectedOption = demoPage.getSelectedDropdownOption();
        Assert.assertEquals(selectedOption, "Option 2", "Should select Option 2");
        
        testLogger.getLogger().info("✓ Successfully selected: {}", selectedOption);
    }
    
    @Test(description = "Test dynamic loading with explicit waits", priority = 6)
    public void testDynamicLoading() {
        testLogger.getLogger().info("Testing dynamic loading functionality");
        
        // Navigate to dynamic loading
        demoPage.clickDynamicLoading();
        
        // Click on Example 1 (Element on page that is hidden)
        driver.get(driver.getCurrentUrl() + "/1");
        
        // Click start button
        demoPage.clickStart();
        
        // Wait for loading to complete and verify finish message
        waitUtils.waitForElementVisible(By.id("finish"), 10);
        
        Assert.assertTrue(demoPage.isFinishMessageDisplayed(), "Finish message should be displayed");
        
        String finishMessage = demoPage.getFinishMessage();
        Assert.assertEquals(finishMessage, "Hello World!", "Finish message should be 'Hello World!'");
        
        testLogger.getLogger().info("✓ Dynamic loading completed with message: {}", finishMessage);
    }
    
    @Test(description = "Test JavaScript alert handling", priority = 7)
    public void testJavaScriptAlerts() {
        testLogger.getLogger().info("Testing JavaScript alerts functionality");
        
        // Navigate to JavaScript alerts page
        demoPage.clickJavaScriptAlerts();
        
        // Test JS Alert
        demoPage.clickJsAlert();
        String alertText = demoPage.getAlertText();
        Assert.assertEquals(alertText, "I am a JS Alert", "Alert text should match expected");
        demoPage.acceptAlert();
        
        String result = demoPage.getAlertResult();
        Assert.assertEquals(result, "You successfully clicked an alert", "Alert result should be correct");
        
        testLogger.getLogger().info("✓ JS Alert handled successfully: {}", result);
        
        // Test JS Confirm - Accept
        demoPage.clickJsConfirm();
        demoPage.acceptAlert();
        result = demoPage.getAlertResult();
        Assert.assertEquals(result, "You clicked: Ok", "Confirm accept result should be correct");
        
        testLogger.getLogger().info("✓ JS Confirm (Accept) handled successfully: {}", result);
        
        // Test JS Confirm - Dismiss
        demoPage.clickJsConfirm();
        demoPage.dismissAlert();
        result = demoPage.getAlertResult();
        Assert.assertEquals(result, "You clicked: Cancel", "Confirm dismiss result should be correct");
        
        testLogger.getLogger().info("✓ JS Confirm (Dismiss) handled successfully: {}", result);
        
        // Test JS Prompt
        demoPage.clickJsPrompt();
        demoPage.sendTextToAlert("Framework Test");
        result = demoPage.getAlertResult();
        Assert.assertEquals(result, "You entered: Framework Test", "Prompt result should contain entered text");
        
        testLogger.getLogger().info("✓ JS Prompt handled successfully: {}", result);
    }
    
    @Test(description = "Test multiple page navigation", priority = 8)
    public void testMultiplePageNavigation() {
        testLogger.getLogger().info("Testing multiple page navigation");
        
        // Test navigation to different pages
        String[] pages = {"Form Authentication", "Checkboxes", "Dropdown", "JavaScript Alerts"};
        
        for (String pageName : pages) {
            // Go back to homepage
            driver.get(configManager.getTestConfig().getBaseUrlDev());
            Assert.assertTrue(demoPage.isHomepageLoaded(), "Should return to homepage");
            
            // Navigate to specific page
            switch (pageName) {
                case "Form Authentication":
                    demoPage.clickFormAuthentication();
                    break;
                case "Checkboxes":
                    demoPage.clickCheckboxes();
                    break;
                case "Dropdown":
                    demoPage.clickDropdown();
                    break;
                case "JavaScript Alerts":
                    demoPage.clickJavaScriptAlerts();
                    break;
            }
            
            // Verify navigation
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains(pageName.toLowerCase().replace(" ", "_")), 
                            "Should navigate to " + pageName + " page");
            
            testLogger.getLogger().info("✓ Successfully navigated to: {}", pageName);
        }
    }
    
    @Test(description = "Test framework error handling", priority = 9)
    public void testErrorHandling() {
        testLogger.getLogger().info("Testing framework error handling");
        
        // Test handling of non-existent elements
        boolean elementExists = waitUtils.isElementDisplayed(By.id("non_existent_element"));
        Assert.assertFalse(elementExists, "Non-existent element should not be found");
        
        // Test timeout handling
        boolean timeoutResult = waitUtils.waitForElementToDisappear(By.id("non_existent_element"), 2);
        Assert.assertTrue(timeoutResult, "Timeout should handle gracefully");
        
        testLogger.getLogger().info("✓ Error handling validated successfully");
    }
    
    @Test(description = "Test framework performance", priority = 10)
    public void testFrameworkPerformance() {
        testLogger.getLogger().info("Testing framework performance");
        
        long startTime = System.currentTimeMillis();
        
        // Perform multiple operations
        demoPage.clickFormAuthentication();
        demoPage.login("tomsmith", "SuperSecretPassword!");
        demoPage.logout();
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Verify performance is reasonable (less than 30 seconds)
        Assert.assertTrue(duration < 30000, "Operations should complete within 30 seconds");
        
        testLogger.getLogger().info("✓ Performance test completed in {} ms", duration);
        
        // Store performance data
        setTestData("performanceTestDuration", duration);
    }
}