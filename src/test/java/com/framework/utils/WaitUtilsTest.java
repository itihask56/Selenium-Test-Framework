package com.framework.utils;

import com.framework.config.ConfigManager;
import com.framework.exceptions.ElementNotFoundException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for WaitUtils class
 */
public class WaitUtilsTest {
    
    @Mock
    private WebDriver mockDriver;
    
    @Mock
    private WebElement mockElement;
    
    @Mock
    private ConfigManager mockConfigManager;
    
    private WaitUtils waitUtils;
    private By testLocator = By.id("testElement");
    
    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        waitUtils = new WaitUtils(mockDriver, 5); // 5 second timeout for tests
    }
    
    @Test
    public void testWaitForElementVisible_Success() {
        // Arrange
        when(mockDriver.findElement(testLocator)).thenReturn(mockElement);
        when(mockElement.isDisplayed()).thenReturn(true);
        
        // Act
        WebElement result = waitUtils.waitForElementVisible(testLocator, 1);
        
        // Assert
        Assert.assertNotNull(result);
        verify(mockDriver, atLeastOnce()).findElement(testLocator);
    }
    
    @Test(expectedExceptions = ElementNotFoundException.class)
    public void testWaitForElementVisible_Timeout() {
        // Arrange
        when(mockDriver.findElement(testLocator)).thenThrow(new NoSuchElementException("Element not found"));
        
        // Act
        waitUtils.waitForElementVisible(testLocator, 1);
    }
    
    @Test
    public void testWaitForElementClickable_Success() {
        // Arrange
        when(mockDriver.findElement(testLocator)).thenReturn(mockElement);
        when(mockElement.isDisplayed()).thenReturn(true);
        when(mockElement.isEnabled()).thenReturn(true);
        
        // Act
        WebElement result = waitUtils.waitForElementClickable(testLocator, 1);
        
        // Assert
        Assert.assertNotNull(result);
        verify(mockDriver, atLeastOnce()).findElement(testLocator);
    }
    
    @Test(expectedExceptions = ElementNotFoundException.class)
    public void testWaitForElementClickable_Timeout() {
        // Arrange
        when(mockDriver.findElement(testLocator)).thenReturn(mockElement);
        when(mockElement.isDisplayed()).thenReturn(false);
        when(mockElement.isEnabled()).thenReturn(false);
        
        // Act
        waitUtils.waitForElementClickable(testLocator, 1);
    }
    
    @Test
    public void testWaitForElementPresent_Success() {
        // Arrange
        when(mockDriver.findElement(testLocator)).thenReturn(mockElement);
        
        // Act
        WebElement result = waitUtils.waitForElementPresent(testLocator, 1);
        
        // Assert
        Assert.assertNotNull(result);
        verify(mockDriver, atLeastOnce()).findElement(testLocator);
    }
    
    @Test(expectedExceptions = ElementNotFoundException.class)
    public void testWaitForElementPresent_Timeout() {
        // Arrange
        when(mockDriver.findElement(testLocator)).thenThrow(new NoSuchElementException("Element not found"));
        
        // Act
        waitUtils.waitForElementPresent(testLocator, 1);
    }
    
    @Test
    public void testWaitForElementToDisappear_Success() {
        // Arrange
        when(mockDriver.findElement(testLocator))
                .thenReturn(mockElement)
                .thenThrow(new NoSuchElementException("Element not found"));
        when(mockElement.isDisplayed()).thenReturn(true).thenReturn(false);
        
        // Act
        boolean result = waitUtils.waitForElementToDisappear(testLocator, 1);
        
        // Assert
        Assert.assertTrue(result);
    }
    
    @Test
    public void testWaitForElementToDisappear_Timeout() {
        // Arrange
        when(mockDriver.findElement(testLocator)).thenReturn(mockElement);
        when(mockElement.isDisplayed()).thenReturn(true);
        
        // Act
        boolean result = waitUtils.waitForElementToDisappear(testLocator, 1);
        
        // Assert
        Assert.assertFalse(result);
    }
    
    @Test
    public void testWaitForTextToBePresentInElement_Success() {
        // Arrange
        String expectedText = "Test Text";
        when(mockDriver.findElement(testLocator)).thenReturn(mockElement);
        when(mockElement.getText()).thenReturn(expectedText);
        
        // Act
        boolean result = waitUtils.waitForTextToBePresentInElement(testLocator, expectedText, 1);
        
        // Assert
        Assert.assertTrue(result);
        verify(mockDriver, atLeastOnce()).findElement(testLocator);
    }
    
    @Test
    public void testWaitForTextToBePresentInElement_Timeout() {
        // Arrange
        String expectedText = "Test Text";
        String actualText = "Different Text";
        when(mockDriver.findElement(testLocator)).thenReturn(mockElement);
        when(mockElement.getText()).thenReturn(actualText);
        
        // Act
        boolean result = waitUtils.waitForTextToBePresentInElement(testLocator, expectedText, 1);
        
        // Assert
        Assert.assertFalse(result);
    }
    
    @Test
    public void testWaitForAttributeToContain_Success() {
        // Arrange
        String attribute = "class";
        String value = "active";
        when(mockDriver.findElement(testLocator)).thenReturn(mockElement);
        when(mockElement.getAttribute(attribute)).thenReturn("btn active primary");
        
        // Act
        boolean result = waitUtils.waitForAttributeToContain(testLocator, attribute, value, 1);
        
        // Assert
        Assert.assertTrue(result);
        verify(mockDriver, atLeastOnce()).findElement(testLocator);
    }
    
    @Test
    public void testWaitForTitleContains_Success() {
        // Arrange
        String expectedTitle = "Test Page";
        when(mockDriver.getTitle()).thenReturn("Test Page - Application");
        
        // Act
        boolean result = waitUtils.waitForTitleContains(expectedTitle, 1);
        
        // Assert
        Assert.assertTrue(result);
        verify(mockDriver, atLeastOnce()).getTitle();
    }
    
    @Test
    public void testWaitForUrlContains_Success() {
        // Arrange
        String expectedUrl = "/dashboard";
        when(mockDriver.getCurrentUrl()).thenReturn("https://example.com/dashboard?tab=1");
        
        // Act
        boolean result = waitUtils.waitForUrlContains(expectedUrl, 1);
        
        // Assert
        Assert.assertTrue(result);
        verify(mockDriver, atLeastOnce()).getCurrentUrl();
    }
    
    @Test
    public void testCreateFluentWait() {
        // Act
        FluentWait<WebDriver> fluentWait = waitUtils.createFluentWait(10, 500);
        
        // Assert
        Assert.assertNotNull(fluentWait);
    }
    
    @Test
    public void testCreateFluentWaitWithDefaults() {
        // Act
        FluentWait<WebDriver> fluentWait = waitUtils.createFluentWait();
        
        // Assert
        Assert.assertNotNull(fluentWait);
    }
    
    @Test
    public void testElementToBeEnabled_Success() {
        // Arrange
        when(mockDriver.findElement(testLocator)).thenReturn(mockElement);
        when(mockElement.isEnabled()).thenReturn(true);
        
        // Act
        ExpectedCondition<Boolean> condition = WaitUtils.elementToBeEnabled(testLocator);
        Boolean result = condition.apply(mockDriver);
        
        // Assert
        Assert.assertTrue(result);
        verify(mockDriver).findElement(testLocator);
        verify(mockElement).isEnabled();
    }
    
    @Test
    public void testElementToBeEnabled_ElementNotFound() {
        // Arrange
        when(mockDriver.findElement(testLocator)).thenThrow(new NoSuchElementException("Element not found"));
        
        // Act
        ExpectedCondition<Boolean> condition = WaitUtils.elementToBeEnabled(testLocator);
        Boolean result = condition.apply(mockDriver);
        
        // Assert
        Assert.assertFalse(result);
        verify(mockDriver).findElement(testLocator);
    }
    
    @Test
    public void testElementToBeSelected_Success() {
        // Arrange
        when(mockDriver.findElement(testLocator)).thenReturn(mockElement);
        when(mockElement.isSelected()).thenReturn(true);
        
        // Act
        ExpectedCondition<Boolean> condition = WaitUtils.elementToBeSelected(testLocator);
        Boolean result = condition.apply(mockDriver);
        
        // Assert
        Assert.assertTrue(result);
        verify(mockDriver).findElement(testLocator);
        verify(mockElement).isSelected();
    }
    
    @Test
    public void testNumberOfElementsToBe_Success() {
        // Arrange
        List<WebElement> elements = Arrays.asList(mockElement, mockElement);
        when(mockDriver.findElements(testLocator)).thenReturn(elements);
        
        // Act
        ExpectedCondition<List<WebElement>> condition = WaitUtils.numberOfElementsToBe(testLocator, 2);
        List<WebElement> result = condition.apply(mockDriver);
        
        // Assert
        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 2);
        verify(mockDriver).findElements(testLocator);
    }
    
    @Test
    public void testNumberOfElementsToBe_WrongCount() {
        // Arrange
        List<WebElement> elements = Arrays.asList(mockElement);
        when(mockDriver.findElements(testLocator)).thenReturn(elements);
        
        // Act
        ExpectedCondition<List<WebElement>> condition = WaitUtils.numberOfElementsToBe(testLocator, 2);
        List<WebElement> result = condition.apply(mockDriver);
        
        // Assert
        Assert.assertNull(result);
        verify(mockDriver).findElements(testLocator);
    }
    
    @Test
    public void testTextToMatchPattern_Success() {
        // Arrange
        String pattern = "\\d{3}-\\d{3}-\\d{4}"; // Phone number pattern
        String text = "123-456-7890";
        when(mockDriver.findElement(testLocator)).thenReturn(mockElement);
        when(mockElement.getText()).thenReturn(text);
        
        // Act
        ExpectedCondition<Boolean> condition = WaitUtils.textToMatchPattern(testLocator, pattern);
        Boolean result = condition.apply(mockDriver);
        
        // Assert
        Assert.assertTrue(result);
        verify(mockDriver).findElement(testLocator);
        verify(mockElement).getText();
    }
    
    @Test
    public void testTextToMatchPattern_NoMatch() {
        // Arrange
        String pattern = "\\d{3}-\\d{3}-\\d{4}"; // Phone number pattern
        String text = "invalid-phone";
        when(mockDriver.findElement(testLocator)).thenReturn(mockElement);
        when(mockElement.getText()).thenReturn(text);
        
        // Act
        ExpectedCondition<Boolean> condition = WaitUtils.textToMatchPattern(testLocator, pattern);
        Boolean result = condition.apply(mockDriver);
        
        // Assert
        Assert.assertFalse(result);
        verify(mockDriver).findElement(testLocator);
        verify(mockElement).getText();
    }
    
    @Test
    public void testIsElementDisplayed_True() {
        // Arrange
        when(mockDriver.findElement(testLocator)).thenReturn(mockElement);
        when(mockElement.isDisplayed()).thenReturn(true);
        
        // Act
        boolean result = waitUtils.isElementDisplayed(testLocator);
        
        // Assert
        Assert.assertTrue(result);
        verify(mockDriver).findElement(testLocator);
        verify(mockElement).isDisplayed();
    }
    
    @Test
    public void testIsElementDisplayed_False() {
        // Arrange
        when(mockDriver.findElement(testLocator)).thenThrow(new NoSuchElementException("Element not found"));
        
        // Act
        boolean result = waitUtils.isElementDisplayed(testLocator);
        
        // Assert
        Assert.assertFalse(result);
        verify(mockDriver).findElement(testLocator);
    }
    
    @Test
    public void testIsElementEnabled_True() {
        // Arrange
        when(mockDriver.findElement(testLocator)).thenReturn(mockElement);
        when(mockElement.isEnabled()).thenReturn(true);
        
        // Act
        boolean result = waitUtils.isElementEnabled(testLocator);
        
        // Assert
        Assert.assertTrue(result);
        verify(mockDriver).findElement(testLocator);
        verify(mockElement).isEnabled();
    }
    
    @Test
    public void testIsElementSelected_True() {
        // Arrange
        when(mockDriver.findElement(testLocator)).thenReturn(mockElement);
        when(mockElement.isSelected()).thenReturn(true);
        
        // Act
        boolean result = waitUtils.isElementSelected(testLocator);
        
        // Assert
        Assert.assertTrue(result);
        verify(mockDriver).findElement(testLocator);
        verify(mockElement).isSelected();
    }
    
    @Test
    public void testGetElementCount() {
        // Arrange
        List<WebElement> elements = Arrays.asList(mockElement, mockElement, mockElement);
        when(mockDriver.findElements(testLocator)).thenReturn(elements);
        
        // Act
        int count = waitUtils.getElementCount(testLocator);
        
        // Assert
        Assert.assertEquals(count, 3);
        verify(mockDriver).findElements(testLocator);
    }
    
    @Test
    public void testWaitForCondition_Success() {
        // Arrange
        when(mockDriver.getTitle()).thenReturn("Expected Title");
        
        // Act
        String result = waitUtils.waitForCondition(driver -> {
            String title = driver.getTitle();
            return "Expected Title".equals(title) ? title : null;
        }, 1);
        
        // Assert
        Assert.assertEquals(result, "Expected Title");
        verify(mockDriver, atLeastOnce()).getTitle();
    }
}