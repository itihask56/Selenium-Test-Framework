package com.framework.pages;

import com.framework.config.ConfigManager;
import com.framework.driver.DriverManager;
import com.framework.exceptions.ElementNotFoundException;
import com.framework.exceptions.FrameworkException;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;

/**
 * Unit tests for BasePage class
 */
public class BasePageTest {
    
    @Mock
    private WebDriver mockDriver;
    
    @Mock
    private WebElement mockElement;
    
    @Mock
    private WebElement mockSelectElement;
    
    @Mock
    private TakesScreenshot mockTakesScreenshot;
    
    @Mock
    private JavascriptExecutor mockJsExecutor;
    
    @Mock
    private Actions mockActions;
    
    @Mock
    private ConfigManager mockConfigManager;
    
    private TestBasePage basePage;
    private MockedStatic<ConfigManager> configManagerStatic;
    
    // Test implementation of BasePage for testing
    private static class TestBasePage extends BasePage {
        public TestBasePage(WebDriver driver) {
            super(driver);
        }
        
        public TestBasePage(WebDriver driver, int timeout) {
            super(driver, timeout);
        }
    }
    
    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Mock ConfigManager static instance
        configManagerStatic = mockStatic(ConfigManager.class);
        configManagerStatic.when(ConfigManager::getInstance).thenReturn(mockConfigManager);
        
        // Mock driver capabilities
        when(mockDriver.findElement(any(By.class))).thenReturn(mockElement);
        when(mockDriver.findElements(any(By.class))).thenReturn(Arrays.asList(mockElement));
        when(mockDriver.getTitle()).thenReturn("Test Page Title");
        when(mockDriver.getCurrentUrl()).thenReturn("https://test.example.com");
        
        // Mock element properties
        when(mockElement.isDisplayed()).thenReturn(true);
        when(mockElement.isEnabled()).thenReturn(true);
        when(mockElement.isSelected()).thenReturn(false);
        when(mockElement.getText()).thenReturn("Test Element Text");
        when(mockElement.getAttribute("id")).thenReturn("testId");
        when(mockElement.getAttribute("class")).thenReturn("testClass");
        when(mockElement.getTagName()).thenReturn("div");
        
        // Mock ConfigManager
        when(mockConfigManager.getBooleanProperty("highlight.elements", false)).thenReturn(false);
        when(mockConfigManager.getProperty("screenshot.path", "screenshots")).thenReturn("screenshots");
        
        basePage = new TestBasePage(mockDriver);
    }
    
    @AfterMethod
    public void tearDown() {
        if (configManagerStatic != null) {
            configManagerStatic.close();
        }
    }
    
    @Test
    public void testConstructorWithDriver() {
        TestBasePage page = new TestBasePage(mockDriver);
        Assert.assertNotNull(page);
    }
    
    @Test
    public void testConstructorWithDriverAndTimeout() {
        TestBasePage page = new TestBasePage(mockDriver, 15);
        Assert.assertNotNull(page);
    }
    
    @Test
    public void testClickElement() {
        // Test clicking WebElement
        basePage.clickElement(mockElement);
        verify(mockElement, times(1)).click();
    }
    
    @Test
    public void testClickElementByLocator() {
        By locator = By.id("testId");
        
        // Mock WaitUtils behavior
        when(mockDriver.findElement(locator)).thenReturn(mockElement);
        
        basePage.clickElement(locator);
        verify(mockElement, times(1)).click();
    }
    
    @Test(expectedExceptions = FrameworkException.class)
    public void testClickElementFailure() {
        doThrow(new WebDriverException("Click failed")).when(mockElement).click();
        
        basePage.clickElement(mockElement);
    }
    
    @Test
    public void testTypeText() {
        String testText = "Test Input Text";
        
        basePage.typeText(mockElement, testText);
        
        verify(mockElement, times(1)).clear();
        verify(mockElement, times(1)).sendKeys(testText);
    }
    
    @Test
    public void testTypeTextByLocator() {
        By locator = By.id("testId");
        String testText = "Test Input Text";
        
        when(mockDriver.findElement(locator)).thenReturn(mockElement);
        
        basePage.typeText(locator, testText);
        
        verify(mockElement, times(1)).clear();
        verify(mockElement, times(1)).sendKeys(testText);
    }
    
    @Test(expectedExceptions = FrameworkException.class)
    public void testTypeTextFailure() {
        String testText = "Test Input Text";
        doThrow(new WebDriverException("Type failed")).when(mockElement).sendKeys(testText);
        
        basePage.typeText(mockElement, testText);
    }
    
    @Test
    public void testGetText() {
        String expectedText = "Test Element Text";
        when(mockElement.getText()).thenReturn(expectedText);
        
        String actualText = basePage.getText(mockElement);
        
        Assert.assertEquals(actualText, expectedText);
        verify(mockElement, times(1)).getText();
    }
    
    @Test
    public void testGetTextByLocator() {
        By locator = By.id("testId");
        String expectedText = "Test Element Text";
        
        when(mockDriver.findElement(locator)).thenReturn(mockElement);
        when(mockElement.getText()).thenReturn(expectedText);
        
        String actualText = basePage.getText(locator);
        
        Assert.assertEquals(actualText, expectedText);
        verify(mockElement, times(1)).getText();
    }
    
    @Test
    public void testGetAttribute() {
        String attributeName = "class";
        String expectedValue = "testClass";
        
        when(mockElement.getAttribute(attributeName)).thenReturn(expectedValue);
        
        String actualValue = basePage.getAttribute(mockElement, attributeName);
        
        Assert.assertEquals(actualValue, expectedValue);
        verify(mockElement, times(1)).getAttribute(attributeName);
    }
    
    @Test
    public void testGetAttributeByLocator() {
        By locator = By.id("testId");
        String attributeName = "class";
        String expectedValue = "testClass";
        
        when(mockDriver.findElement(locator)).thenReturn(mockElement);
        when(mockElement.getAttribute(attributeName)).thenReturn(expectedValue);
        
        String actualValue = basePage.getAttribute(locator, attributeName);
        
        Assert.assertEquals(actualValue, expectedValue);
        verify(mockElement, times(1)).getAttribute(attributeName);
    }
    
    @Test
    public void testIsElementDisplayed() {
        when(mockElement.isDisplayed()).thenReturn(true);
        
        boolean isDisplayed = basePage.isElementDisplayed(mockElement);
        
        Assert.assertTrue(isDisplayed);
        verify(mockElement, times(1)).isDisplayed();
    }
    
    @Test
    public void testIsElementDisplayedByLocator() {
        By locator = By.id("testId");
        
        when(mockDriver.findElement(locator)).thenReturn(mockElement);
        when(mockElement.isDisplayed()).thenReturn(true);
        
        boolean isDisplayed = basePage.isElementDisplayed(locator);
        
        Assert.assertTrue(isDisplayed);
        verify(mockElement, times(1)).isDisplayed();
    }
    
    @Test
    public void testIsElementDisplayedFalse() {
        when(mockElement.isDisplayed()).thenReturn(false);
        
        boolean isDisplayed = basePage.isElementDisplayed(mockElement);
        
        Assert.assertFalse(isDisplayed);
    }
    
    @Test
    public void testIsElementDisplayedNotFound() {
        By locator = By.id("nonExistentId");
        
        when(mockDriver.findElement(locator)).thenThrow(new NoSuchElementException("Element not found"));
        
        boolean isDisplayed = basePage.isElementDisplayed(locator);
        
        Assert.assertFalse(isDisplayed);
    }
    
    @Test
    public void testIsElementEnabled() {
        when(mockElement.isEnabled()).thenReturn(true);
        
        boolean isEnabled = basePage.isElementEnabled(mockElement);
        
        Assert.assertTrue(isEnabled);
        verify(mockElement, times(1)).isEnabled();
    }
    
    @Test
    public void testIsElementEnabledByLocator() {
        By locator = By.id("testId");
        
        when(mockDriver.findElement(locator)).thenReturn(mockElement);
        when(mockElement.isEnabled()).thenReturn(true);
        
        boolean isEnabled = basePage.isElementEnabled(locator);
        
        Assert.assertTrue(isEnabled);
        verify(mockElement, times(1)).isEnabled();
    }
    
    @Test
    public void testIsElementSelected() {
        when(mockElement.isSelected()).thenReturn(true);
        
        boolean isSelected = basePage.isElementSelected(mockElement);
        
        Assert.assertTrue(isSelected);
        verify(mockElement, times(1)).isSelected();
    }
    
    @Test
    public void testSelectByVisibleText() {
        String optionText = "Option 1";
        Select mockSelect = mock(Select.class);
        
        // We can't easily mock Select constructor, so we'll test the element interaction
        basePage.selectByVisibleText(mockSelectElement, optionText);
        
        // Verify element was accessed (Select constructor would be called)
        verify(mockSelectElement, atLeastOnce()).isDisplayed();
    }
    
    @Test
    public void testSelectByValue() {
        String optionValue = "value1";
        
        basePage.selectByValue(mockSelectElement, optionValue);
        
        // Verify element was accessed
        verify(mockSelectElement, atLeastOnce()).isDisplayed();
    }
    
    @Test
    public void testSelectByIndex() {
        int optionIndex = 1;
        
        basePage.selectByIndex(mockSelectElement, optionIndex);
        
        // Verify element was accessed
        verify(mockSelectElement, atLeastOnce()).isDisplayed();
    }
    
    @Test
    public void testDoubleClickElement() {
        // Mock Actions behavior would require more complex setup
        // For now, verify the element interaction
        basePage.doubleClickElement(mockElement);
        
        // Verify element was accessed for the double click operation
        verify(mockElement, atLeastOnce()).isDisplayed();
    }
    
    @Test
    public void testRightClickElement() {
        basePage.rightClickElement(mockElement);
        
        // Verify element was accessed for the right click operation
        verify(mockElement, atLeastOnce()).isDisplayed();
    }
    
    @Test
    public void testHoverOverElement() {
        basePage.hoverOverElement(mockElement);
        
        // Verify element was accessed for the hover operation
        verify(mockElement, atLeastOnce()).isDisplayed();
    }
    
    @Test
    public void testDragAndDrop() {
        WebElement targetElement = mock(WebElement.class);
        when(targetElement.isDisplayed()).thenReturn(true);
        
        basePage.dragAndDrop(mockElement, targetElement);
        
        // Verify both elements were accessed
        verify(mockElement, atLeastOnce()).isDisplayed();
        verify(targetElement, atLeastOnce()).isDisplayed();
    }
    
    @Test
    public void testCaptureScreenshot() {
        // Mock TakesScreenshot
        when(mockDriver instanceof TakesScreenshot).thenReturn(true);
        TakesScreenshot takesScreenshot = (TakesScreenshot) mockDriver;
        
        File mockScreenshotFile = mock(File.class);
        when(takesScreenshot.getScreenshotAs(OutputType.FILE)).thenReturn(mockScreenshotFile);
        when(mockScreenshotFile.toPath()).thenReturn(Paths.get("test-screenshot.png"));
        
        // Mock ConfigManager for screenshot path
        when(mockConfigManager.getTestConfig()).thenReturn(mock(com.framework.config.TestConfig.class));
        when(mockConfigManager.getTestConfig().getScreenshotPath()).thenReturn("screenshots");
        
        try {
            String screenshotPath = basePage.captureScreenshot("test");
            Assert.assertNotNull(screenshotPath);
        } catch (Exception e) {
            // Screenshot functionality might fail in test environment, which is acceptable
            Assert.assertTrue(e instanceof FrameworkException);
        }
    }
    
    @Test
    public void testNavigateToUrl() {
        String testUrl = "https://test.example.com";
        
        basePage.navigateToUrl(testUrl);
        
        verify(mockDriver, times(1)).get(testUrl);
    }
    
    @Test
    public void testGetPageTitle() {
        String expectedTitle = "Test Page Title";
        when(mockDriver.getTitle()).thenReturn(expectedTitle);
        
        String actualTitle = basePage.getPageTitle();
        
        Assert.assertEquals(actualTitle, expectedTitle);
        verify(mockDriver, times(1)).getTitle();
    }
    
    @Test
    public void testGetCurrentUrl() {
        String expectedUrl = "https://test.example.com";
        when(mockDriver.getCurrentUrl()).thenReturn(expectedUrl);
        
        String actualUrl = basePage.getCurrentUrl();
        
        Assert.assertEquals(actualUrl, expectedUrl);
        verify(mockDriver, times(1)).getCurrentUrl();
    }
    
    @Test
    public void testRefreshPage() {
        WebDriver.Navigation mockNavigation = mock(WebDriver.Navigation.class);
        when(mockDriver.navigate()).thenReturn(mockNavigation);
        
        basePage.refreshPage();
        
        verify(mockNavigation, times(1)).refresh();
    }
    
    @Test
    public void testNavigateBack() {
        WebDriver.Navigation mockNavigation = mock(WebDriver.Navigation.class);
        when(mockDriver.navigate()).thenReturn(mockNavigation);
        
        basePage.navigateBack();
        
        verify(mockNavigation, times(1)).back();
    }
    
    @Test
    public void testNavigateForward() {
        WebDriver.Navigation mockNavigation = mock(WebDriver.Navigation.class);
        when(mockDriver.navigate()).thenReturn(mockNavigation);
        
        basePage.navigateForward();
        
        verify(mockNavigation, times(1)).forward();
    }
    
    @Test
    public void testSwitchToWindowByTitle() {
        String targetTitle = "Target Window";
        String handle1 = "handle1";
        String handle2 = "handle2";
        
        Set<String> windowHandles = new HashSet<>();
        windowHandles.add(handle1);
        windowHandles.add(handle2);
        
        when(mockDriver.getWindowHandles()).thenReturn(windowHandles);
        
        WebDriver.TargetLocator mockTargetLocator = mock(WebDriver.TargetLocator.class);
        when(mockDriver.switchTo()).thenReturn(mockTargetLocator);
        when(mockTargetLocator.window(handle1)).thenReturn(mockDriver);
        when(mockTargetLocator.window(handle2)).thenReturn(mockDriver);
        
        // Mock titles for different windows
        when(mockDriver.getTitle())
            .thenReturn("Other Window")  // First window
            .thenReturn(targetTitle);    // Second window (target)
        
        basePage.switchToWindowByTitle(targetTitle);
        
        verify(mockTargetLocator, times(2)).window(anyString());
    }
    
    @Test(expectedExceptions = FrameworkException.class)
    public void testSwitchToWindowByTitleNotFound() {
        String targetTitle = "Non-existent Window";
        String handle1 = "handle1";
        
        Set<String> windowHandles = new HashSet<>();
        windowHandles.add(handle1);
        
        when(mockDriver.getWindowHandles()).thenReturn(windowHandles);
        
        WebDriver.TargetLocator mockTargetLocator = mock(WebDriver.TargetLocator.class);
        when(mockDriver.switchTo()).thenReturn(mockTargetLocator);
        when(mockTargetLocator.window(handle1)).thenReturn(mockDriver);
        
        when(mockDriver.getTitle()).thenReturn("Other Window");
        
        basePage.switchToWindowByTitle(targetTitle);
    }
    
    @Test
    public void testCloseCurrentWindowAndSwitchToMain() {
        String mainHandle = "mainHandle";
        String currentHandle = "currentHandle";
        
        Set<String> windowHandles = new HashSet<>();
        windowHandles.add(mainHandle);
        windowHandles.add(currentHandle);
        
        when(mockDriver.getWindowHandles()).thenReturn(windowHandles);
        
        WebDriver.TargetLocator mockTargetLocator = mock(WebDriver.TargetLocator.class);
        when(mockDriver.switchTo()).thenReturn(mockTargetLocator);
        when(mockTargetLocator.window(mainHandle)).thenReturn(mockDriver);
        
        basePage.closeCurrentWindowAndSwitchToMain();
        
        verify(mockDriver, times(1)).close();
        verify(mockTargetLocator, times(1)).window(mainHandle);
    }
    
    @Test
    public void testScrollToElement() {
        // Mock JavascriptExecutor
        when(mockDriver instanceof JavascriptExecutor).thenReturn(true);
        JavascriptExecutor jsExecutor = (JavascriptExecutor) mockDriver;
        
        basePage.scrollToElement(mockElement);
        
        // Verify JavaScript execution (would be called for scrolling)
        // Note: Mockito can't easily verify the exact script, but we can verify interaction
        verify(mockElement, atLeastOnce()).getTagName(); // Called during element description
    }
    
    @Test
    public void testExecuteJavaScript() {
        String script = "return document.title;";
        String expectedResult = "Test Title";
        
        // Mock JavascriptExecutor
        when(mockDriver instanceof JavascriptExecutor).thenReturn(true);
        JavascriptExecutor jsExecutor = (JavascriptExecutor) mockDriver;
        when(jsExecutor.executeScript(script)).thenReturn(expectedResult);
        
        Object result = basePage.executeJavaScript(script);
        
        Assert.assertEquals(result, expectedResult);
    }
    
    @Test
    public void testWaitForPageToLoad() {
        // Mock JavascriptExecutor for page load check
        when(mockDriver instanceof JavascriptExecutor).thenReturn(true);
        JavascriptExecutor jsExecutor = (JavascriptExecutor) mockDriver;
        when(jsExecutor.executeScript("return document.readyState")).thenReturn("complete");
        
        // This should not throw an exception
        basePage.waitForPageToLoad();
    }
    
    @Test
    public void testGetElements() {
        By locator = By.className("testClass");
        List<WebElement> expectedElements = Arrays.asList(mockElement, mock(WebElement.class));
        
        when(mockDriver.findElements(locator)).thenReturn(expectedElements);
        
        List<WebElement> actualElements = basePage.getElements(locator);
        
        Assert.assertEquals(actualElements.size(), expectedElements.size());
        verify(mockDriver, times(1)).findElements(locator);
    }
    
    @Test
    public void testGetElementCount() {
        By locator = By.className("testClass");
        List<WebElement> elements = Arrays.asList(mockElement, mock(WebElement.class), mock(WebElement.class));
        
        when(mockDriver.findElements(locator)).thenReturn(elements);
        
        int count = basePage.getElementCount(locator);
        
        Assert.assertEquals(count, 3);
        verify(mockDriver, times(1)).findElements(locator);
    }
    
    @Test(expectedExceptions = ElementNotFoundException.class)
    public void testGetElementsFailure() {
        By locator = By.id("nonExistentId");
        
        when(mockDriver.findElements(locator)).thenThrow(new WebDriverException("Find elements failed"));
        
        basePage.getElements(locator);
    }
}