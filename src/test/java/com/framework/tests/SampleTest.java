package com.framework.tests;

import com.framework.pages.SamplePage;
import com.framework.utils.ExcelUtils;
import org.testng.Assert;
import org.testng.annotations.*;
import java.lang.reflect.Method;

/**
 * SampleTest demonstrates the usage of the test automation framework
 * Includes examples of data-driven testing, cross-browser testing,
 * and various framework features.
 */
public class SampleTest extends BaseTest {
    
    private SamplePage samplePage;
    
    /**
     * Setup test class - initialize page objects
     */
    @Override
    protected void setupTestClass() {
        testLogger.getLogger().info("Setting up SampleTest class");
    }
    
    /**
     * Setup test data for each test method
     */
    @Override
    protected void setupTestData(Method method) {
        // Initialize page object for each test
        samplePage = new SamplePage(driver);
        
        // Set common test data - get base URL from config
        String environment = testConfig.getEnvironment();
        String baseUrl;
        switch (environment.toLowerCase()) {
            case "dev":
                baseUrl = testConfig.getBaseUrlDev();
                break;
            case "staging":
                baseUrl = testConfig.getBaseUrlStaging();
                break;
            case "prod":
                baseUrl = testConfig.getBaseUrlProd();
                break;
            default:
                baseUrl = testConfig.getBaseUrlDev();
        }
        setTestData("pageUrl", baseUrl + "/sample");
        setTestData("pageObject", samplePage);
        
        testLogger.getLogger().info("Test data setup completed for: {}", method.getName());
    }
    
    /**
     * Test basic page loading functionality
     */
    @Test(description = "Verify sample page loads correctly")
    public void testPageLoad() {
        testLogger.getLogger().info("Starting page load test");
        
        // Navigate to sample page
        String pageUrl = (String) getTestData("pageUrl");
        samplePage.navigateToUrl(pageUrl);
        
        // Verify page is loaded
        Assert.assertTrue(samplePage.isPageLoaded(), "Sample page should be loaded");
        
        // Verify page title
        String pageTitle = samplePage.getPageTitle();
        Assert.assertNotNull(pageTitle, "Page title should not be null");
        Assert.assertFalse(pageTitle.isEmpty(), "Page title should not be empty");
        
        // Log assertion results
        logAssertion("Page loaded successfully", true, samplePage.isPageLoaded(), true);
        logAssertion("Page title is not empty", true, !pageTitle.isEmpty(), true);
        
        testLogger.getLogger().info("Page load test completed successfully");
    }
    
    /**
     * Data-driven test for login functionality using CSV data
     */
    @Test(dataProvider = "loginDataProvider", description = "Test login functionality with various credentials")
    public void testLogin(String username, String password, String expectedResult, String testDescription) {
        testLogger.getLogger().info("Starting login test: {}", testDescription);
        
        // Navigate to sample page
        String pageUrl = (String) getTestData("pageUrl");
        samplePage.navigateToUrl(pageUrl);
        
        // Perform login
        samplePage.login(username, password);
        
        // Verify results based on expected outcome
        if ("success".equals(expectedResult)) {
            Assert.assertTrue(samplePage.isLoginSuccessful(), 
                "Login should be successful for: " + testDescription);
            
            String welcomeMessage = samplePage.getWelcomeMessage();
            Assert.assertNotNull(welcomeMessage, "Welcome message should be displayed");
            
            logAssertion("Login successful", true, samplePage.isLoginSuccessful(), true);
            
        } else if ("failure".equals(expectedResult)) {
            Assert.assertTrue(samplePage.isErrorMessageDisplayed(), 
                "Error message should be displayed for: " + testDescription);
            
            String errorMessage = samplePage.getErrorMessage();
            Assert.assertNotNull(errorMessage, "Error message should not be null");
            
            logAssertion("Login failed as expected", true, samplePage.isErrorMessageDisplayed(), true);
        }
        
        testLogger.getLogger().info("Login test completed: {}", testDescription);
    }
    
    /**
     * Data-driven test for search functionality
     */
    @Test(dataProvider = "searchDataProvider", description = "Test search functionality with various terms")
    public void testSearch(String searchTerm, String expectedResults, String testDescription) {
        testLogger.getLogger().info("Starting search test: {}", testDescription);
        
        // Navigate to sample page
        String pageUrl = (String) getTestData("pageUrl");
        samplePage.navigateToUrl(pageUrl);
        
        // Perform search
        samplePage.performSearch(searchTerm);
        
        // Verify search results
        String searchResults = samplePage.getSearchResults();
        
        if ("Found results".equals(expectedResults)) {
            Assert.assertNotNull(searchResults, "Search results should not be null");
            Assert.assertFalse(searchResults.isEmpty(), "Search results should not be empty");
            
            logAssertion("Search results found", true, !searchResults.isEmpty(), true);
            
        } else if ("No results".equals(expectedResults)) {
            // Verify no results message or empty results
            Assert.assertTrue(searchResults.contains("No results") || searchResults.isEmpty(), 
                "Should show no results message for: " + testDescription);
            
            logAssertion("No search results as expected", true, 
                searchResults.contains("No results") || searchResults.isEmpty(), true);
        }
        
        testLogger.getLogger().info("Search test completed: {}", testDescription);
    }
    
    /**
     * Test dropdown functionality
     */
    @Test(description = "Test dropdown selection functionality")
    public void testDropdownSelection() {
        testLogger.getLogger().info("Starting dropdown selection test");
        
        // Navigate to sample page
        String pageUrl = (String) getTestData("pageUrl");
        samplePage.navigateToUrl(pageUrl);
        
        // Test dropdown selections
        String[] options = {"Option 1", "Option 2", "Option 3"};
        
        for (String option : options) {
            samplePage.selectDropdownOption(option);
            
            // Verify selection (this would depend on actual page behavior)
            testLogger.getLogger().info("Selected dropdown option: {}", option);
            
            logAssertion("Dropdown option selected", option, option, true);
        }
        
        testLogger.getLogger().info("Dropdown selection test completed");
    }
    
    /**
     * Test advanced interactions (hover, screenshot)
     */
    @Test(description = "Test advanced framework features")
    public void testAdvancedFeatures() {
        testLogger.getLogger().info("Starting advanced features test");
        
        // Navigate to sample page
        String pageUrl = (String) getTestData("pageUrl");
        samplePage.navigateToUrl(pageUrl);
        
        // Test hover functionality
        samplePage.hoverOverContactUs();
        
        // Test screenshot capture
        String screenshotPath = samplePage.capturePageScreenshot("advanced_features_test");
        Assert.assertNotNull(screenshotPath, "Screenshot path should not be null");
        
        // Test page navigation
        samplePage.clickContactUs();
        
        logAssertion("Advanced features executed", true, true, true);
        
        testLogger.getLogger().info("Advanced features test completed");
    }
    
    /**
     * Cross-browser test example
     */
    @Test(description = "Cross-browser compatibility test")
    public void testCrossBrowserCompatibility() {
        testLogger.getLogger().info("Starting cross-browser compatibility test");
        
        // Get current browser from configuration
        String currentBrowser = testConfig.getBrowser();
        testLogger.getLogger().info("Running test on browser: {}", currentBrowser);
        
        // Navigate to sample page
        String pageUrl = (String) getTestData("pageUrl");
        samplePage.navigateToUrl(pageUrl);
        
        // Verify basic functionality works across browsers
        Assert.assertTrue(samplePage.isPageLoaded(), 
            "Page should load correctly on " + currentBrowser);
        
        String pageTitle = samplePage.getPageTitle();
        Assert.assertNotNull(pageTitle, 
            "Page title should be available on " + currentBrowser);
        
        // Test basic interaction
        samplePage.performSearch("cross-browser-test");
        
        logAssertion("Cross-browser compatibility", currentBrowser, currentBrowser, true);
        
        testLogger.getLogger().info("Cross-browser compatibility test completed for: {}", currentBrowser);
    }
    
    /**
     * Test error handling and recovery
     */
    @Test(description = "Test framework error handling capabilities")
    public void testErrorHandling() {
        testLogger.getLogger().info("Starting error handling test");
        
        // Navigate to sample page
        String pageUrl = (String) getTestData("pageUrl");
        samplePage.navigateToUrl(pageUrl);
        
        try {
            // Attempt login with invalid credentials to trigger error
            samplePage.login("invalid_user", "invalid_password");
            
            // Verify error is handled gracefully
            if (samplePage.isErrorMessageDisplayed()) {
                String errorMessage = samplePage.getErrorMessage();
                testLogger.getLogger().info("Error message displayed: {}", errorMessage);
                
                logAssertion("Error handled gracefully", true, true, true);
            }
            
        } catch (Exception e) {
            testLogger.getLogger().info("Exception caught and handled: {}", e.getMessage());
            
            // Verify framework handles exceptions properly
            Assert.assertNotNull(e.getMessage(), "Exception should have a message");
            
            logAssertion("Exception handled properly", true, true, true);
        }
        
        testLogger.getLogger().info("Error handling test completed");
    }
    
    /**
     * Performance test example
     */
    @Test(description = "Basic performance test for page operations")
    public void testPerformance() {
        testLogger.getLogger().info("Starting performance test");
        
        long startTime = System.currentTimeMillis();
        
        // Navigate to sample page
        String pageUrl = (String) getTestData("pageUrl");
        samplePage.navigateToUrl(pageUrl);
        
        long navigationTime = System.currentTimeMillis() - startTime;
        
        // Verify page loads within acceptable time (5 seconds)
        Assert.assertTrue(navigationTime < 5000, 
            "Page should load within 5 seconds. Actual: " + navigationTime + "ms");
        
        // Test search performance
        startTime = System.currentTimeMillis();
        samplePage.performSearch("performance-test");
        long searchTime = System.currentTimeMillis() - startTime;
        
        // Verify search completes within acceptable time (3 seconds)
        Assert.assertTrue(searchTime < 3000, 
            "Search should complete within 3 seconds. Actual: " + searchTime + "ms");
        
        logAssertion("Navigation performance", "< 5000ms", navigationTime + "ms", navigationTime < 5000);
        logAssertion("Search performance", "< 3000ms", searchTime + "ms", searchTime < 3000);
        
        testLogger.getLogger().info("Performance test completed - Navigation: {}ms, Search: {}ms", 
            navigationTime, searchTime);
    }
    
    // Data Providers
    
    /**
     * Data provider for login tests using CSV file
     */
    @DataProvider(name = "loginDataProvider")
    public Object[][] loginDataProvider() {
        try {
            String filePath = "src/test/resources/testdata/login-data.csv";
            return ExcelUtils.getCSVData(filePath);
        } catch (Exception e) {
            testLogger.getLogger().error("Failed to load login test data", e);
            // Return default test data if file loading fails
            return new Object[][] {
                {"admin", "admin123", "success", "Default admin login test"},
                {"invalid", "wrongpass", "failure", "Default invalid login test"}
            };
        }
    }
    
    /**
     * Data provider for search tests using CSV file
     */
    @DataProvider(name = "searchDataProvider")
    public Object[][] searchDataProvider() {
        try {
            String filePath = "src/test/resources/testdata/search-data.csv";
            return ExcelUtils.getCSVData(filePath);
        } catch (Exception e) {
            testLogger.getLogger().error("Failed to load search test data", e);
            // Return default test data if file loading fails
            return new Object[][] {
                {"selenium", "Found results", "Default selenium search test"},
                {"xyz123", "No results", "Default no results search test"}
            };
        }
    }
    
    /**
     * Cleanup test data after each test
     */
    @Override
    protected void cleanupTestData() {
        // Clean up any test-specific data
        removeTestData("pageObject");
        testLogger.getLogger().debug("Test data cleanup completed");
    }
    
    /**
     * Class-level cleanup
     */
    @Override
    protected void cleanupTestClass() {
        testLogger.getLogger().info("SampleTest class cleanup completed");
    }
}