package com.framework.pages;

import com.framework.config.ConfigManager;
import com.framework.driver.DriverManager;
import com.framework.exceptions.ElementNotFoundException;
import com.framework.exceptions.FrameworkException;
import com.framework.utils.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

/**
 * BasePage abstract class provides common page operations and utilities
 * for all page object classes in the framework.
 * Implements PageFactory initialization and common element interactions.
 */
public abstract class BasePage {
    
    protected static final Logger logger = LogManager.getLogger(BasePage.class);
    protected WebDriver driver;
    protected WaitUtils waitUtils;
    protected Actions actions;
    protected ConfigManager configManager;
    
    private static final int DEFAULT_TIMEOUT = 10;
    private static final String SCREENSHOT_DIR = "screenshots";
    
    /**
     * Constructor initializes the page with WebDriver and PageFactory
     * @param driver WebDriver instance
     */
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
        this.actions = new Actions(driver);
        this.configManager = ConfigManager.getInstance();
        
        // Initialize page elements using PageFactory
        PageFactory.initElements(driver, this);
        
        logger.debug("Initialized {} page", this.getClass().getSimpleName());
    }
    
    /**
     * Constructor with custom timeout
     * @param driver WebDriver instance
     * @param timeout custom timeout in seconds
     */
    public BasePage(WebDriver driver, int timeout) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver, timeout);
        this.actions = new Actions(driver);
        this.configManager = ConfigManager.getInstance();
        
        PageFactory.initElements(driver, this);
        
        logger.debug("Initialized {} page with custom timeout: {}s", 
                    this.getClass().getSimpleName(), timeout);
    }

    // ==================== ELEMENT INTERACTION METHODS ====================
    
    /**
     * Clicks on an element with wait and error handling
     * @param element WebElement to click
     */
    public void clickElement(WebElement element) {
        try {
            waitForElementToBeClickable(element);
            highlightElement(element);
            element.click();
            logger.info("Clicked element: {}", getElementDescription(element));
        } catch (Exception e) {
            logger.error("Failed to click element: {}", getElementDescription(element), e);
            captureScreenshot("click_failure");
            throw new FrameworkException("Failed to click element: " + getElementDescription(element), e);
        }
    }
    
    /**
     * Clicks on an element using locator
     * @param locator By locator for the element
     */
    public void clickElement(By locator) {
        try {
            WebElement element = waitUtils.waitForElementClickable(locator);
            highlightElement(element);
            element.click();
            logger.info("Clicked element using locator: {}", locator);
        } catch (Exception e) {
            logger.error("Failed to click element using locator: {}", locator, e);
            captureScreenshot("click_failure");
            throw new ElementNotFoundException(locator, e);
        }
    }
    
    /**
     * Types text into an element with clear and error handling
     * @param element WebElement to type into
     * @param text text to type
     */
    public void typeText(WebElement element, String text) {
        try {
            waitForElementToBeVisible(element);
            highlightElement(element);
            element.clear();
            element.sendKeys(text);
            logger.info("Typed text '{}' into element: {}", text, getElementDescription(element));
        } catch (Exception e) {
            logger.error("Failed to type text '{}' into element: {}", text, getElementDescription(element), e);
            captureScreenshot("type_failure");
            throw new FrameworkException("Failed to type text into element: " + getElementDescription(element), e);
        }
    }
    
    /**
     * Types text into an element using locator
     * @param locator By locator for the element
     * @param text text to type
     */
    public void typeText(By locator, String text) {
        try {
            WebElement element = waitUtils.waitForElementVisible(locator);
            highlightElement(element);
            element.clear();
            element.sendKeys(text);
            logger.info("Typed text '{}' into element using locator: {}", text, locator);
        } catch (Exception e) {
            logger.error("Failed to type text '{}' into element using locator: {}", text, locator, e);
            captureScreenshot("type_failure");
            throw new ElementNotFoundException(locator, e);
        }
    }
    
    /**
     * Gets text from an element
     * @param element WebElement to get text from
     * @return element text
     */
    public String getText(WebElement element) {
        try {
            waitForElementToBeVisible(element);
            String text = element.getText();
            logger.debug("Retrieved text '{}' from element: {}", text, getElementDescription(element));
            return text;
        } catch (Exception e) {
            logger.error("Failed to get text from element: {}", getElementDescription(element), e);
            throw new FrameworkException("Failed to get text from element: " + getElementDescription(element), e);
        }
    }
    
    /**
     * Gets text from an element using locator
     * @param locator By locator for the element
     * @return element text
     */
    public String getText(By locator) {
        try {
            WebElement element = waitUtils.waitForElementVisible(locator);
            String text = element.getText();
            logger.debug("Retrieved text '{}' from element using locator: {}", text, locator);
            return text;
        } catch (Exception e) {
            logger.error("Failed to get text from element using locator: {}", locator, e);
            throw new ElementNotFoundException(locator, e);
        }
    }
    
    /**
     * Gets attribute value from an element
     * @param element WebElement to get attribute from
     * @param attributeName name of the attribute
     * @return attribute value
     */
    public String getAttribute(WebElement element, String attributeName) {
        try {
            waitForElementToBeVisible(element);
            String value = element.getAttribute(attributeName);
            logger.debug("Retrieved attribute '{}' value '{}' from element: {}", 
                        attributeName, value, getElementDescription(element));
            return value;
        } catch (Exception e) {
            logger.error("Failed to get attribute '{}' from element: {}", 
                        attributeName, getElementDescription(element), e);
            throw new FrameworkException("Failed to get attribute from element: " + getElementDescription(element), e);
        }
    }
    
    /**
     * Gets attribute value from an element using locator
     * @param locator By locator for the element
     * @param attributeName name of the attribute
     * @return attribute value
     */
    public String getAttribute(By locator, String attributeName) {
        try {
            WebElement element = waitUtils.waitForElementVisible(locator);
            String value = element.getAttribute(attributeName);
            logger.debug("Retrieved attribute '{}' value '{}' from element using locator: {}", 
                        attributeName, value, locator);
            return value;
        } catch (Exception e) {
            logger.error("Failed to get attribute '{}' from element using locator: {}", 
                        attributeName, locator, e);
            throw new ElementNotFoundException(locator, e);
        }
    }
    
    // ==================== ELEMENT STATE VERIFICATION METHODS ====================
    
    /**
     * Checks if element is displayed
     * @param element WebElement to check
     * @return true if element is displayed
     */
    public boolean isElementDisplayed(WebElement element) {
        try {
            boolean isDisplayed = element.isDisplayed();
            logger.debug("Element display status: {} for element: {}", isDisplayed, getElementDescription(element));
            return isDisplayed;
        } catch (Exception e) {
            logger.debug("Element not displayed or not found: {}", getElementDescription(element));
            return false;
        }
    }
    
    /**
     * Checks if element is displayed using locator
     * @param locator By locator for the element
     * @return true if element is displayed
     */
    public boolean isElementDisplayed(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            boolean isDisplayed = element.isDisplayed();
            logger.debug("Element display status: {} for locator: {}", isDisplayed, locator);
            return isDisplayed;
        } catch (Exception e) {
            logger.debug("Element not displayed or not found using locator: {}", locator);
            return false;
        }
    }
    
    /**
     * Checks if element is enabled
     * @param element WebElement to check
     * @return true if element is enabled
     */
    public boolean isElementEnabled(WebElement element) {
        try {
            boolean isEnabled = element.isEnabled();
            logger.debug("Element enabled status: {} for element: {}", isEnabled, getElementDescription(element));
            return isEnabled;
        } catch (Exception e) {
            logger.debug("Element not enabled or not found: {}", getElementDescription(element));
            return false;
        }
    }
    
    /**
     * Checks if element is enabled using locator
     * @param locator By locator for the element
     * @return true if element is enabled
     */
    public boolean isElementEnabled(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            boolean isEnabled = element.isEnabled();
            logger.debug("Element enabled status: {} for locator: {}", isEnabled, locator);
            return isEnabled;
        } catch (Exception e) {
            logger.debug("Element not enabled or not found using locator: {}", locator);
            return false;
        }
    }
    
    /**
     * Checks if element is selected (for checkboxes, radio buttons)
     * @param element WebElement to check
     * @return true if element is selected
     */
    public boolean isElementSelected(WebElement element) {
        try {
            boolean isSelected = element.isSelected();
            logger.debug("Element selected status: {} for element: {}", isSelected, getElementDescription(element));
            return isSelected;
        } catch (Exception e) {
            logger.debug("Element not selected or not found: {}", getElementDescription(element));
            return false;
        }
    }
    
    // ==================== WAIT METHODS ====================
    
    /**
     * Waits for element to be visible
     * @param element WebElement to wait for
     * @return WebElement when visible
     */
    public WebElement waitForElementToBeVisible(WebElement element) {
        try {
            // Convert WebElement to By locator for wait utility
            By locator = getLocatorFromElement(element);
            return waitUtils.waitForElementVisible(locator);
        } catch (Exception e) {
            logger.error("Element not visible within timeout: {}", getElementDescription(element));
            By locator = getLocatorFromElement(element);
            throw new ElementNotFoundException(locator, e);
        }
    }
    
    /**
     * Waits for element to be clickable
     * @param element WebElement to wait for
     * @return WebElement when clickable
     */
    public WebElement waitForElementToBeClickable(WebElement element) {
        try {
            By locator = getLocatorFromElement(element);
            return waitUtils.waitForElementClickable(locator);
        } catch (Exception e) {
            logger.error("Element not clickable within timeout: {}", getElementDescription(element));
            By locator = getLocatorFromElement(element);
            throw new ElementNotFoundException(locator, e);
        }
    }
    
    /**
     * Waits for element to disappear
     * @param locator By locator for the element
     * @return true when element disappears
     */
    public boolean waitForElementToDisappear(By locator) {
        boolean disappeared = waitUtils.waitForElementToDisappear(locator);
        logger.debug("Element disappear status: {} for locator: {}", disappeared, locator);
        return disappeared;
    }
    
    /**
     * Waits for text to be present in element
     * @param locator By locator for the element
     * @param text expected text
     * @return true when text is present
     */
    public boolean waitForTextToBePresentInElement(By locator, String text) {
        boolean textPresent = waitUtils.waitForTextToBePresentInElement(locator, text);
        logger.debug("Text '{}' present status: {} for locator: {}", text, textPresent, locator);
        return textPresent;
    }
    
    // ==================== DROPDOWN/SELECT METHODS ====================
    
    /**
     * Selects option from dropdown by visible text
     * @param element Select element
     * @param text visible text to select
     */
    public void selectByVisibleText(WebElement element, String text) {
        try {
            waitForElementToBeVisible(element);
            Select select = new Select(element);
            select.selectByVisibleText(text);
            logger.info("Selected option '{}' from dropdown: {}", text, getElementDescription(element));
        } catch (Exception e) {
            logger.error("Failed to select option '{}' from dropdown: {}", text, getElementDescription(element), e);
            captureScreenshot("select_failure");
            throw new FrameworkException("Failed to select option from dropdown: " + getElementDescription(element), e);
        }
    }
    
    /**
     * Selects option from dropdown by value
     * @param element Select element
     * @param value value to select
     */
    public void selectByValue(WebElement element, String value) {
        try {
            waitForElementToBeVisible(element);
            Select select = new Select(element);
            select.selectByValue(value);
            logger.info("Selected option with value '{}' from dropdown: {}", value, getElementDescription(element));
        } catch (Exception e) {
            logger.error("Failed to select option with value '{}' from dropdown: {}", 
                        value, getElementDescription(element), e);
            captureScreenshot("select_failure");
            throw new FrameworkException("Failed to select option from dropdown: " + getElementDescription(element), e);
        }
    }
    
    /**
     * Selects option from dropdown by index
     * @param element Select element
     * @param index index to select
     */
    public void selectByIndex(WebElement element, int index) {
        try {
            waitForElementToBeVisible(element);
            Select select = new Select(element);
            select.selectByIndex(index);
            logger.info("Selected option at index '{}' from dropdown: {}", index, getElementDescription(element));
        } catch (Exception e) {
            logger.error("Failed to select option at index '{}' from dropdown: {}", 
                        index, getElementDescription(element), e);
            captureScreenshot("select_failure");
            throw new FrameworkException("Failed to select option from dropdown: " + getElementDescription(element), e);
        }
    }
    
    // ==================== ADVANCED INTERACTION METHODS ====================
    
    /**
     * Performs double click on element
     * @param element WebElement to double click
     */
    public void doubleClickElement(WebElement element) {
        try {
            waitForElementToBeClickable(element);
            highlightElement(element);
            actions.doubleClick(element).perform();
            logger.info("Double clicked element: {}", getElementDescription(element));
        } catch (Exception e) {
            logger.error("Failed to double click element: {}", getElementDescription(element), e);
            captureScreenshot("double_click_failure");
            throw new FrameworkException("Failed to double click element: " + getElementDescription(element), e);
        }
    }
    
    /**
     * Performs right click on element
     * @param element WebElement to right click
     */
    public void rightClickElement(WebElement element) {
        try {
            waitForElementToBeClickable(element);
            highlightElement(element);
            actions.contextClick(element).perform();
            logger.info("Right clicked element: {}", getElementDescription(element));
        } catch (Exception e) {
            logger.error("Failed to right click element: {}", getElementDescription(element), e);
            captureScreenshot("right_click_failure");
            throw new FrameworkException("Failed to right click element: " + getElementDescription(element), e);
        }
    }
    
    /**
     * Hovers over an element
     * @param element WebElement to hover over
     */
    public void hoverOverElement(WebElement element) {
        try {
            waitForElementToBeVisible(element);
            actions.moveToElement(element).perform();
            logger.info("Hovered over element: {}", getElementDescription(element));
        } catch (Exception e) {
            logger.error("Failed to hover over element: {}", getElementDescription(element), e);
            throw new FrameworkException("Failed to hover over element: " + getElementDescription(element), e);
        }
    }
    
    /**
     * Drags and drops from source to target element
     * @param sourceElement source WebElement
     * @param targetElement target WebElement
     */
    public void dragAndDrop(WebElement sourceElement, WebElement targetElement) {
        try {
            waitForElementToBeVisible(sourceElement);
            waitForElementToBeVisible(targetElement);
            actions.dragAndDrop(sourceElement, targetElement).perform();
            logger.info("Dragged element {} to {}", 
                       getElementDescription(sourceElement), getElementDescription(targetElement));
        } catch (Exception e) {
            logger.error("Failed to drag and drop from {} to {}", 
                        getElementDescription(sourceElement), getElementDescription(targetElement), e);
            captureScreenshot("drag_drop_failure");
            throw new FrameworkException("Failed to perform drag and drop operation", e);
        }
    }
    
    // ==================== SCREENSHOT METHODS ====================
    
    /**
     * Captures screenshot with default filename
     * @return screenshot file path
     */
    public String captureScreenshot() {
        return captureScreenshot("screenshot");
    }
    
    /**
     * Captures screenshot with custom filename
     * @param fileName custom filename (without extension)
     * @return screenshot file path
     */
    public String captureScreenshot(String fileName) {
        try {
            // Create screenshots directory if it doesn't exist
            String screenshotPath = configManager.getTestConfig().getScreenshotPath();
            if (screenshotPath == null || screenshotPath.isEmpty()) {
                screenshotPath = SCREENSHOT_DIR;
            }
            
            Path screenshotDir = Paths.get(screenshotPath);
            if (!Files.exists(screenshotDir)) {
                Files.createDirectories(screenshotDir);
            }
            
            // Generate timestamp for unique filename
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
            String fullFileName = String.format("%s_%s.png", fileName, timestamp);
            
            // Take screenshot
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
            
            // Save screenshot
            Path targetPath = screenshotDir.resolve(fullFileName);
            Files.copy(sourceFile.toPath(), targetPath);
            
            String screenshotFilePath = targetPath.toString();
            logger.info("Screenshot captured: {}", screenshotFilePath);
            
            return screenshotFilePath;
            
        } catch (IOException e) {
            logger.error("Failed to capture screenshot", e);
            throw new FrameworkException("Failed to capture screenshot", e);
        }
    }
    
    // ==================== BROWSER NAVIGATION METHODS ====================
    
    /**
     * Navigates to URL
     * @param url URL to navigate to
     */
    public void navigateToUrl(String url) {
        try {
            driver.get(url);
            logger.info("Navigated to URL: {}", url);
        } catch (Exception e) {
            logger.error("Failed to navigate to URL: {}", url, e);
            throw new FrameworkException("Failed to navigate to URL: " + url, e);
        }
    }
    
    /**
     * Gets current page title
     * @return page title
     */
    public String getPageTitle() {
        try {
            String title = driver.getTitle();
            logger.debug("Retrieved page title: {}", title);
            return title;
        } catch (Exception e) {
            logger.error("Failed to get page title", e);
            throw new FrameworkException("Failed to get page title", e);
        }
    }
    
    /**
     * Gets current page URL
     * @return current URL
     */
    public String getCurrentUrl() {
        try {
            String url = driver.getCurrentUrl();
            logger.debug("Retrieved current URL: {}", url);
            return url;
        } catch (Exception e) {
            logger.error("Failed to get current URL", e);
            throw new FrameworkException("Failed to get current URL", e);
        }
    }
    
    /**
     * Refreshes the current page
     */
    public void refreshPage() {
        try {
            driver.navigate().refresh();
            logger.info("Page refreshed");
        } catch (Exception e) {
            logger.error("Failed to refresh page", e);
            throw new FrameworkException("Failed to refresh page", e);
        }
    }
    
    /**
     * Navigates back in browser history
     */
    public void navigateBack() {
        try {
            driver.navigate().back();
            logger.info("Navigated back");
        } catch (Exception e) {
            logger.error("Failed to navigate back", e);
            throw new FrameworkException("Failed to navigate back", e);
        }
    }
    
    /**
     * Navigates forward in browser history
     */
    public void navigateForward() {
        try {
            driver.navigate().forward();
            logger.info("Navigated forward");
        } catch (Exception e) {
            logger.error("Failed to navigate forward", e);
            throw new FrameworkException("Failed to navigate forward", e);
        }
    }
    
    // ==================== WINDOW/TAB HANDLING METHODS ====================
    
    /**
     * Switches to window by title
     * @param windowTitle title of the window to switch to
     */
    public void switchToWindowByTitle(String windowTitle) {
        try {
            Set<String> windowHandles = driver.getWindowHandles();
            for (String handle : windowHandles) {
                driver.switchTo().window(handle);
                if (driver.getTitle().equals(windowTitle)) {
                    logger.info("Switched to window with title: {}", windowTitle);
                    return;
                }
            }
            throw new FrameworkException("Window with title '" + windowTitle + "' not found");
        } catch (Exception e) {
            logger.error("Failed to switch to window with title: {}", windowTitle, e);
            throw new FrameworkException("Failed to switch to window with title: " + windowTitle, e);
        }
    }
    
    /**
     * Closes current window and switches to main window
     */
    public void closeCurrentWindowAndSwitchToMain() {
        try {
            String mainWindow = driver.getWindowHandles().iterator().next();
            driver.close();
            driver.switchTo().window(mainWindow);
            logger.info("Closed current window and switched to main window");
        } catch (Exception e) {
            logger.error("Failed to close current window and switch to main", e);
            throw new FrameworkException("Failed to close current window and switch to main", e);
        }
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Scrolls to element
     * @param element WebElement to scroll to
     */
    public void scrollToElement(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            logger.debug("Scrolled to element: {}", getElementDescription(element));
        } catch (Exception e) {
            logger.error("Failed to scroll to element: {}", getElementDescription(element), e);
            throw new FrameworkException("Failed to scroll to element: " + getElementDescription(element), e);
        }
    }
    
    /**
     * Highlights element for visual debugging
     * @param element WebElement to highlight
     */
    private void highlightElement(WebElement element) {
        try {
            if (configManager.getBooleanProperty("highlight.elements", false)) {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].style.border='3px solid red'", element);
                Thread.sleep(200); // Brief pause to see highlight
                js.executeScript("arguments[0].style.border=''", element);
            }
        } catch (Exception e) {
            // Ignore highlighting errors
            logger.debug("Failed to highlight element", e);
        }
    }
    
    /**
     * Gets element description for logging
     * @param element WebElement to describe
     * @return element description string
     */
    private String getElementDescription(WebElement element) {
        try {
            String tagName = element.getTagName();
            String id = element.getAttribute("id");
            String className = element.getAttribute("class");
            String text = element.getText();
            
            StringBuilder description = new StringBuilder(tagName);
            if (id != null && !id.isEmpty()) {
                description.append("[id='").append(id).append("']");
            }
            if (className != null && !className.isEmpty()) {
                description.append("[class='").append(className).append("']");
            }
            if (text != null && !text.isEmpty() && text.length() <= 50) {
                description.append("[text='").append(text).append("']");
            }
            
            return description.toString();
        } catch (Exception e) {
            return "UnknownElement";
        }
    }
    
    /**
     * Converts WebElement to By locator (simplified approach)
     * Note: This is a basic implementation. In practice, you might want to store
     * the original locator when creating elements or use a more sophisticated approach.
     * @param element WebElement to convert
     * @return By locator
     */
    private By getLocatorFromElement(WebElement element) {
        try {
            String id = element.getAttribute("id");
            if (id != null && !id.isEmpty()) {
                return By.id(id);
            }
            
            String className = element.getAttribute("class");
            if (className != null && !className.isEmpty()) {
                return By.className(className.split(" ")[0]); // Use first class
            }
            
            String tagName = element.getTagName();
            return By.tagName(tagName);
            
        } catch (Exception e) {
            // Fallback to tag name
            return By.tagName("*");
        }
    }
    
    /**
     * Executes JavaScript code
     * @param script JavaScript code to execute
     * @param args arguments for the script
     * @return result of script execution
     */
    public Object executeJavaScript(String script, Object... args) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Object result = js.executeScript(script, args);
            logger.debug("Executed JavaScript: {}", script);
            return result;
        } catch (Exception e) {
            logger.error("Failed to execute JavaScript: {}", script, e);
            throw new FrameworkException("Failed to execute JavaScript: " + script, e);
        }
    }
    
    /**
     * Waits for page to load completely
     */
    public void waitForPageToLoad() {
        try {
            waitUtils.waitForCondition(driver -> 
                ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
            logger.debug("Page loaded completely");
        } catch (Exception e) {
            logger.warn("Page load wait timed out", e);
        }
    }
    
    /**
     * Gets all elements matching the locator
     * @param locator By locator for elements
     * @return List of WebElements
     */
    public List<WebElement> getElements(By locator) {
        try {
            List<WebElement> elements = driver.findElements(locator);
            logger.debug("Found {} elements using locator: {}", elements.size(), locator);
            return elements;
        } catch (Exception e) {
            logger.error("Failed to find elements using locator: {}", locator, e);
            throw new ElementNotFoundException(locator, e);
        }
    }
    
    /**
     * Gets element count for a locator
     * @param locator By locator for elements
     * @return number of elements found
     */
    public int getElementCount(By locator) {
        return getElements(locator).size();
    }
}