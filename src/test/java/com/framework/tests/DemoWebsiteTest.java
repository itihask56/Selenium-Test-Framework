package com.framework.tests;

import com.framework.pages.DemoWebsitePage;
import com.framework.utils.WaitUtils;
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

}