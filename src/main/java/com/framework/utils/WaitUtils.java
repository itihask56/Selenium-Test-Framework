package com.framework.utils;

import com.framework.config.ConfigManager;
import com.framework.exceptions.ElementNotFoundException;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

/**
 * WaitUtils provides custom explicit wait conditions and fluent wait utilities
 * for robust element interactions in Selenium tests
 */
public class WaitUtils {
    
    private final WebDriver driver;
    private final int defaultTimeout;
    private final int pollingInterval;
    
    /**
     * Constructor with WebDriver instance
     * @param driver WebDriver instance
     */
    public WaitUtils(WebDriver driver) {
        this.driver = driver;
        this.defaultTimeout = ConfigManager.getInstance().getTestConfig().getExplicitTimeout();
        this.pollingInterval = 500; // 500ms default polling interval
    }
    
    /**
     * Constructor with custom timeout
     * @param driver WebDriver instance
     * @param timeout custom timeout in seconds
     */
    public WaitUtils(WebDriver driver, int timeout) {
        this.driver = driver;
        this.defaultTimeout = timeout;
        this.pollingInterval = 500;
    }
    
    /**
     * Waits for element to be visible
     * @param locator element locator
     * @return WebElement when visible
     * @throws ElementNotFoundException if element not found within timeout
     */
    public WebElement waitForElementVisible(By locator) {
        return waitForElementVisible(locator, defaultTimeout);
    }
    
    /**
     * Waits for element to be visible with custom timeout
     * @param locator element locator
     * @param timeout timeout in seconds
     * @return WebElement when visible
     * @throws ElementNotFoundException if element not found within timeout
     */
    public WebElement waitForElementVisible(By locator, int timeout) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(locator, timeout, "Element not visible within " + timeout + " seconds: " + locator);
        }
    }
    
    /**
     * Waits for element to be clickable
     * @param locator element locator
     * @return WebElement when clickable
     * @throws ElementNotFoundException if element not clickable within timeout
     */
    public WebElement waitForElementClickable(By locator) {
        return waitForElementClickable(locator, defaultTimeout);
    }
    
    /**
     * Waits for element to be clickable with custom timeout
     * @param locator element locator
     * @param timeout timeout in seconds
     * @return WebElement when clickable
     * @throws ElementNotFoundException if element not clickable within timeout
     */
    public WebElement waitForElementClickable(By locator, int timeout) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            return wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(locator, timeout, "Element not clickable within " + timeout + " seconds: " + locator);
        }
    }
    
    /**
     * Waits for element to be present in DOM
     * @param locator element locator
     * @return WebElement when present
     * @throws ElementNotFoundException if element not present within timeout
     */
    public WebElement waitForElementPresent(By locator) {
        return waitForElementPresent(locator, defaultTimeout);
    }
    
    /**
     * Waits for element to be present in DOM with custom timeout
     * @param locator element locator
     * @param timeout timeout in seconds
     * @return WebElement when present
     * @throws ElementNotFoundException if element not present within timeout
     */
    public WebElement waitForElementPresent(By locator, int timeout) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException e) {
            throw new ElementNotFoundException(locator, timeout, "Element not present within " + timeout + " seconds: " + locator);
        }
    }
    
    /**
     * Waits for element to disappear from DOM
     * @param locator element locator
     * @return true when element is no longer present
     */
    public boolean waitForElementToDisappear(By locator) {
        return waitForElementToDisappear(locator, defaultTimeout);
    }
    
    /**
     * Waits for element to disappear from DOM with custom timeout
     * @param locator element locator
     * @param timeout timeout in seconds
     * @return true when element is no longer present
     */
    public boolean waitForElementToDisappear(By locator, int timeout) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            return false;
        }
    }
    
    /**
     * Waits for text to be present in element
     * @param locator element locator
     * @param text expected text
     * @return true when text is present
     */
    public boolean waitForTextToBePresentInElement(By locator, String text) {
        return waitForTextToBePresentInElement(locator, text, defaultTimeout);
    }
    
    /**
     * Waits for text to be present in element with custom timeout
     * @param locator element locator
     * @param text expected text
     * @param timeout timeout in seconds
     * @return true when text is present
     */
    public boolean waitForTextToBePresentInElement(By locator, String text, int timeout) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
        } catch (TimeoutException e) {
            return false;
        }
    }
    
    /**
     * Waits for attribute to contain specific value
     * @param locator element locator
     * @param attribute attribute name
     * @param value expected value
     * @return true when attribute contains value
     */
    public boolean waitForAttributeToContain(By locator, String attribute, String value) {
        return waitForAttributeToContain(locator, attribute, value, defaultTimeout);
    }
    
    /**
     * Waits for attribute to contain specific value with custom timeout
     * @param locator element locator
     * @param attribute attribute name
     * @param value expected value
     * @param timeout timeout in seconds
     * @return true when attribute contains value
     */
    public boolean waitForAttributeToContain(By locator, String attribute, String value, int timeout) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            return wait.until(ExpectedConditions.attributeContains(locator, attribute, value));
        } catch (TimeoutException e) {
            return false;
        }
    }
    
    /**
     * Waits for page title to contain specific text
     * @param title expected title text
     * @return true when title contains text
     */
    public boolean waitForTitleContains(String title) {
        return waitForTitleContains(title, defaultTimeout);
    }
    
    /**
     * Waits for page title to contain specific text with custom timeout
     * @param title expected title text
     * @param timeout timeout in seconds
     * @return true when title contains text
     */
    public boolean waitForTitleContains(String title, int timeout) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            return wait.until(ExpectedConditions.titleContains(title));
        } catch (TimeoutException e) {
            return false;
        }
    }
    
    /**
     * Waits for URL to contain specific text
     * @param urlFragment expected URL fragment
     * @return true when URL contains fragment
     */
    public boolean waitForUrlContains(String urlFragment) {
        return waitForUrlContains(urlFragment, defaultTimeout);
    }
    
    /**
     * Waits for URL to contain specific text with custom timeout
     * @param urlFragment expected URL fragment
     * @param timeout timeout in seconds
     * @return true when URL contains fragment
     */
    public boolean waitForUrlContains(String urlFragment, int timeout) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            return wait.until(ExpectedConditions.urlContains(urlFragment));
        } catch (TimeoutException e) {
            return false;
        }
    }
    
    /**
     * Creates a fluent wait with custom configuration
     * @param timeout timeout in seconds
     * @param pollingInterval polling interval in milliseconds
     * @return FluentWait instance
     */
    public FluentWait<WebDriver> createFluentWait(int timeout, int pollingInterval) {
        return new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeout))
                .pollingEvery(Duration.ofMillis(pollingInterval))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
    }
    
    /**
     * Creates a fluent wait with default configuration
     * @return FluentWait instance
     */
    public FluentWait<WebDriver> createFluentWait() {
        return createFluentWait(defaultTimeout, pollingInterval);
    }
    
    /**
     * Custom wait condition for element to be enabled
     * @param locator element locator
     * @return ExpectedCondition for element to be enabled
     */
    public static ExpectedCondition<Boolean> elementToBeEnabled(By locator) {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    WebElement element = driver.findElement(locator);
                    return element != null && element.isEnabled();
                } catch (NoSuchElementException | StaleElementReferenceException e) {
                    return false;
                }
            }
            
            @Override
            public String toString() {
                return "element to be enabled: " + locator;
            }
        };
    }
    
    /**
     * Custom wait condition for element to be selected
     * @param locator element locator
     * @return ExpectedCondition for element to be selected
     */
    public static ExpectedCondition<Boolean> elementToBeSelected(By locator) {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    WebElement element = driver.findElement(locator);
                    return element != null && element.isSelected();
                } catch (NoSuchElementException | StaleElementReferenceException e) {
                    return false;
                }
            }
            
            @Override
            public String toString() {
                return "element to be selected: " + locator;
            }
        };
    }
    
    /**
     * Custom wait condition for number of elements to be present
     * @param locator element locator
     * @param expectedCount expected number of elements
     * @return ExpectedCondition for specific number of elements
     */
    public static ExpectedCondition<List<WebElement>> numberOfElementsToBe(By locator, int expectedCount) {
        return new ExpectedCondition<List<WebElement>>() {
            @Override
            public List<WebElement> apply(WebDriver driver) {
                List<WebElement> elements = driver.findElements(locator);
                return elements.size() == expectedCount ? elements : null;
            }
            
            @Override
            public String toString() {
                return "number of elements to be " + expectedCount + ": " + locator;
            }
        };
    }
    
    /**
     * Custom wait condition for element text to match regex pattern
     * @param locator element locator
     * @param pattern regex pattern
     * @return ExpectedCondition for text to match pattern
     */
    public static ExpectedCondition<Boolean> textToMatchPattern(By locator, String pattern) {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    WebElement element = driver.findElement(locator);
                    String text = element.getText();
                    return text != null && text.matches(pattern);
                } catch (NoSuchElementException | StaleElementReferenceException e) {
                    return false;
                }
            }
            
            @Override
            public String toString() {
                return "element text to match pattern '" + pattern + "': " + locator;
            }
        };
    }
    
    /**
     * Waits for custom condition using fluent wait
     * @param condition custom condition function
     * @param <T> return type of condition
     * @return result of condition when met
     */
    public <T> T waitForCondition(Function<WebDriver, T> condition) {
        return waitForCondition(condition, defaultTimeout);
    }
    
    /**
     * Waits for custom condition using fluent wait with custom timeout
     * @param condition custom condition function
     * @param timeout timeout in seconds
     * @param <T> return type of condition
     * @return result of condition when met
     */
    public <T> T waitForCondition(Function<WebDriver, T> condition, int timeout) {
        FluentWait<WebDriver> wait = createFluentWait(timeout, pollingInterval);
        return wait.until(condition);
    }
    
    /**
     * Verifies element is displayed
     * @param locator element locator
     * @return true if element is displayed
     */
    public boolean isElementDisplayed(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            return element.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    /**
     * Verifies element is enabled
     * @param locator element locator
     * @return true if element is enabled
     */
    public boolean isElementEnabled(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            return element.isEnabled();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    /**
     * Verifies element is selected
     * @param locator element locator
     * @return true if element is selected
     */
    public boolean isElementSelected(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            return element.isSelected();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    /**
     * Gets element count
     * @param locator element locator
     * @return number of elements found
     */
    public int getElementCount(By locator) {
        return driver.findElements(locator).size();
    }
}