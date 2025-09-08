package com.framework.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * SamplePage demonstrates the usage of the test automation framework
 * This page object represents a typical web page with common elements
 * and interactions that showcase framework capabilities.
 */
public class SamplePage extends BasePage {
    
    private static final Logger logger = LogManager.getLogger(SamplePage.class);
    
    // Page elements using PageFactory annotations
    @FindBy(id = "username")
    private WebElement usernameField;
    
    @FindBy(id = "password")
    private WebElement passwordField;
    
    @FindBy(id = "loginButton")
    private WebElement loginButton;
    
    @FindBy(className = "welcome-message")
    private WebElement welcomeMessage;
    
    @FindBy(xpath = "//h1[@class='page-title']")
    private WebElement pageTitle;
    
    @FindBy(css = ".error-message")
    private WebElement errorMessage;
    
    @FindBy(name = "search")
    private WebElement searchBox;
    
    @FindBy(id = "searchButton")
    private WebElement searchButton;
    
    @FindBy(className = "search-results")
    private WebElement searchResults;
    
    @FindBy(id = "dropdown")
    private WebElement dropdown;
    
    @FindBy(linkText = "Contact Us")
    private WebElement contactUsLink;
    
    @FindBy(id = "submitButton")
    private WebElement submitButton;
    
    @FindBy(className = "success-message")
    private WebElement successMessage;
    
    /**
     * Constructor
     * @param driver WebDriver instance
     */
    public SamplePage(WebDriver driver) {
        super(driver);
        logger.info("SamplePage initialized");
    }
    
    /**
     * Performs login with username and password
     * @param username user's username
     * @param password user's password
     * @return SamplePage instance for method chaining
     */
    public SamplePage login(String username, String password) {
        logger.info("Attempting to login with username: {}", username);
        
        typeText(usernameField, username);
        typeText(passwordField, password);
        clickElement(loginButton);
        
        logger.info("Login attempt completed");
        return this;
    }
    
    /**
     * Verifies if login was successful by checking welcome message
     * @return true if welcome message is displayed
     */
    public boolean isLoginSuccessful() {
        boolean isSuccessful = isElementDisplayed(welcomeMessage);
        logger.info("Login success status: {}", isSuccessful);
        return isSuccessful;
    }
    
    /**
     * Gets the welcome message text
     * @return welcome message text
     */
    public String getWelcomeMessage() {
        String message = getText(welcomeMessage);
        logger.info("Welcome message: {}", message);
        return message;
    }
    
    /**
     * Gets the page title text
     * @return page title text
     */
    public String getPageTitle() {
        String title = getText(pageTitle);
        logger.info("Page title: {}", title);
        return title;
    }
    
    /**
     * Checks if error message is displayed
     * @return true if error message is displayed
     */
    public boolean isErrorMessageDisplayed() {
        boolean isDisplayed = isElementDisplayed(errorMessage);
        logger.info("Error message displayed: {}", isDisplayed);
        return isDisplayed;
    }
    
    /**
     * Gets the error message text
     * @return error message text
     */
    public String getErrorMessage() {
        String message = getText(errorMessage);
        logger.info("Error message: {}", message);
        return message;
    }
    
    /**
     * Performs search operation
     * @param searchTerm term to search for
     * @return SamplePage instance for method chaining
     */
    public SamplePage performSearch(String searchTerm) {
        logger.info("Performing search for: {}", searchTerm);
        
        typeText(searchBox, searchTerm);
        clickElement(searchButton);
        
        // Wait for search results to appear
        waitForElementToBeVisible(searchResults);
        
        logger.info("Search completed for term: {}", searchTerm);
        return this;
    }
    
    /**
     * Gets search results text
     * @return search results text
     */
    public String getSearchResults() {
        String results = getText(searchResults);
        logger.info("Search results: {}", results);
        return results;
    }
    
    /**
     * Selects option from dropdown by visible text
     * @param optionText text of the option to select
     * @return SamplePage instance for method chaining
     */
    public SamplePage selectDropdownOption(String optionText) {
        logger.info("Selecting dropdown option: {}", optionText);
        
        selectByVisibleText(dropdown, optionText);
        
        logger.info("Dropdown option selected: {}", optionText);
        return this;
    }
    
    /**
     * Clicks on Contact Us link
     * @return SamplePage instance for method chaining
     */
    public SamplePage clickContactUs() {
        logger.info("Clicking Contact Us link");
        
        clickElement(contactUsLink);
        
        logger.info("Contact Us link clicked");
        return this;
    }
    
    /**
     * Clicks submit button
     * @return SamplePage instance for method chaining
     */
    public SamplePage clickSubmit() {
        logger.info("Clicking submit button");
        
        clickElement(submitButton);
        
        logger.info("Submit button clicked");
        return this;
    }
    
    /**
     * Checks if success message is displayed
     * @return true if success message is displayed
     */
    public boolean isSuccessMessageDisplayed() {
        boolean isDisplayed = isElementDisplayed(successMessage);
        logger.info("Success message displayed: {}", isDisplayed);
        return isDisplayed;
    }
    
    /**
     * Gets the success message text
     * @return success message text
     */
    public String getSuccessMessage() {
        String message = getText(successMessage);
        logger.info("Success message: {}", message);
        return message;
    }
    
    /**
     * Verifies page is loaded by checking page title element
     * @return true if page is loaded
     */
    public boolean isPageLoaded() {
        boolean isLoaded = isElementDisplayed(pageTitle);
        logger.info("Page loaded status: {}", isLoaded);
        return isLoaded;
    }
    
    /**
     * Waits for page to load completely
     * @return SamplePage instance for method chaining
     */
    public SamplePage waitForPageLoad() {
        logger.info("Waiting for page to load");
        
        waitForElementToBeVisible(pageTitle);
        
        logger.info("Page load completed");
        return this;
    }
    
    /**
     * Demonstrates advanced interactions - hover over element
     * @return SamplePage instance for method chaining
     */
    public SamplePage hoverOverContactUs() {
        logger.info("Hovering over Contact Us link");
        
        hoverOverElement(contactUsLink);
        
        logger.info("Hover action completed");
        return this;
    }
    
    /**
     * Demonstrates screenshot capture functionality
     * @param screenshotName name for the screenshot
     * @return screenshot file path
     */
    public String capturePageScreenshot(String screenshotName) {
        logger.info("Capturing screenshot: {}", screenshotName);
        
        String screenshotPath = captureScreenshot(screenshotName);
        
        logger.info("Screenshot captured: {}", screenshotPath);
        return screenshotPath;
    }
}