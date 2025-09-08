# API Documentation

This document provides comprehensive API documentation for the Selenium Test Automation Framework classes and methods.

## Table of Contents

1. [Configuration Management](#configuration-management)
2. [Driver Management](#driver-management)
3. [Page Object Base Class](#page-object-base-class)
4. [Utility Classes](#utility-classes)
5. [Exception Classes](#exception-classes)
6. [Reporting Classes](#reporting-classes)
7. [Test Base Classes](#test-base-classes)

## Configuration Management

### ConfigManager

Singleton class for managing configuration properties.

#### Methods

| Method                                                 | Parameters        | Return Type     | Description                        |
| ------------------------------------------------------ | ----------------- | --------------- | ---------------------------------- |
| `getInstance()`                                        | None              | `ConfigManager` | Gets singleton instance            |
| `getProperty(String key, String defaultValue)`         | key, defaultValue | `String`        | Gets property value with default   |
| `getProperty(String key)`                              | key               | `String`        | Gets property value                |
| `getBooleanProperty(String key, boolean defaultValue)` | key, defaultValue | `boolean`       | Gets boolean property              |
| `getIntProperty(String key, int defaultValue)`         | key, defaultValue | `int`           | Gets integer property              |
| `getLongProperty(String key, long defaultValue)`       | key, defaultValue | `long`          | Gets long property                 |
| `getTestConfig()`                                      | None              | `TestConfig`    | Gets test configuration object     |
| `reloadConfiguration()`                                | None              | `void`          | Reloads configuration from file    |
| `hasProperty(String key)`                              | key               | `boolean`       | Checks if property exists          |
| `setProperty(String key, String value)`                | key, value        | `void`          | Sets property value (runtime only) |

#### Example Usage

```java
ConfigManager config = ConfigManager.getInstance();
String browser = config.getProperty("browser", "chrome");
boolean headless = config.getBooleanProperty("headless", false);
int timeout = config.getIntProperty("timeout", 10);
```

### TestConfig

Data model class containing all test configuration properties.

#### Properties

| Property              | Type      | Description                                  |
| --------------------- | --------- | -------------------------------------------- |
| `browser`             | `String`  | Browser type (chrome, firefox, edge, safari) |
| `headless`            | `boolean` | Run in headless mode                         |
| `environment`         | `String`  | Test environment (dev, staging, prod)        |
| `baseUrlDev`          | `String`  | Development environment URL                  |
| `baseUrlStaging`      | `String`  | Staging environment URL                      |
| `baseUrlProd`         | `String`  | Production environment URL                   |
| `implicitTimeout`     | `int`     | Implicit wait timeout in seconds             |
| `explicitTimeout`     | `int`     | Explicit wait timeout in seconds             |
| `windowWidth`         | `int`     | Browser window width                         |
| `windowHeight`        | `int`     | Browser window height                        |
| `parallelExecution`   | `boolean` | Enable parallel test execution               |
| `threadCount`         | `int`     | Number of parallel threads                   |
| `retryCount`          | `int`     | Number of test retries on failure            |
| `screenshotOnFailure` | `boolean` | Capture screenshots on failure               |

## Driver Management

### DriverManager

Manages WebDriver lifecycle with thread-safe operations.

#### Methods

| Method                                      | Parameters  | Return Type     | Description                              |
| ------------------------------------------- | ----------- | --------------- | ---------------------------------------- |
| `getInstance()`                             | None        | `DriverManager` | Gets singleton instance                  |
| `initializeDriver()`                        | None        | `WebDriver`     | Initializes driver for current thread    |
| `initializeDriver(BrowserType browserType)` | browserType | `WebDriver`     | Initializes driver with specific browser |
| `getDriver()`                               | None        | `WebDriver`     | Gets current thread's driver             |
| `setDriver(WebDriver driver)`               | driver      | `void`          | Sets driver for current thread           |
| `quitDriver()`                              | None        | `void`          | Quits driver and cleans up               |
| `getCurrentBrowser()`                       | None        | `String`        | Gets current browser name                |
| `isDriverInitialized()`                     | None        | `boolean`       | Checks if driver is initialized          |
| `getActiveDriverCount()`                    | None        | `int`           | Gets number of active drivers            |

#### Example Usage

```java
DriverManager driverManager = DriverManager.getInstance();
WebDriver driver = driverManager.initializeDriver();

// Use driver for test operations
driver.get("https://example.com");

// Clean up
driverManager.quitDriver();
```

### BrowserType

Enum defining supported browser types.

#### Values

| Value              | Description              |
| ------------------ | ------------------------ |
| `CHROME`           | Google Chrome browser    |
| `CHROME_HEADLESS`  | Chrome in headless mode  |
| `FIREFOX`          | Mozilla Firefox browser  |
| `FIREFOX_HEADLESS` | Firefox in headless mode |
| `EDGE`             | Microsoft Edge browser   |
| `SAFARI`           | Apple Safari browser     |

#### Methods

| Method                           | Parameters  | Return Type   | Description                |
| -------------------------------- | ----------- | ------------- | -------------------------- |
| `fromString(String browserName)` | browserName | `BrowserType` | Creates enum from string   |
| `getDisplayName()`               | None        | `String`      | Gets display name          |
| `isHeadless()`                   | None        | `boolean`     | Checks if headless variant |
| `getBaseBrowser()`               | None        | `BrowserType` | Gets base browser type     |

## Page Object Base Class

### BasePage

Abstract base class for all page objects providing common functionality.

#### Constructor

```java
public BasePage(WebDriver driver)
public BasePage(WebDriver driver, int timeout)
```

#### Element Interaction Methods

| Method                                                   | Parameters             | Return Type | Description               |
| -------------------------------------------------------- | ---------------------- | ----------- | ------------------------- |
| `clickElement(WebElement element)`                       | element                | `void`      | Clicks element with wait  |
| `clickElement(By locator)`                               | locator                | `void`      | Clicks element by locator |
| `typeText(WebElement element, String text)`              | element, text          | `void`      | Types text into element   |
| `typeText(By locator, String text)`                      | locator, text          | `void`      | Types text by locator     |
| `getText(WebElement element)`                            | element                | `String`    | Gets element text         |
| `getText(By locator)`                                    | locator                | `String`    | Gets text by locator      |
| `getAttribute(WebElement element, String attributeName)` | element, attributeName | `String`    | Gets attribute value      |
| `getAttribute(By locator, String attributeName)`         | locator, attributeName | `String`    | Gets attribute by locator |

#### Element State Verification Methods

| Method                                   | Parameters | Return Type | Description                    |
| ---------------------------------------- | ---------- | ----------- | ------------------------------ |
| `isElementDisplayed(WebElement element)` | element    | `boolean`   | Checks if element is displayed |
| `isElementDisplayed(By locator)`         | locator    | `boolean`   | Checks display by locator      |
| `isElementEnabled(WebElement element)`   | element    | `boolean`   | Checks if element is enabled   |
| `isElementEnabled(By locator)`           | locator    | `boolean`   | Checks enabled by locator      |
| `isElementSelected(WebElement element)`  | element    | `boolean`   | Checks if element is selected  |

#### Wait Methods

| Method                                                     | Parameters    | Return Type  | Description                       |
| ---------------------------------------------------------- | ------------- | ------------ | --------------------------------- |
| `waitForElementToBeVisible(WebElement element)`            | element       | `WebElement` | Waits for element visibility      |
| `waitForElementToBeClickable(WebElement element)`          | element       | `WebElement` | Waits for element to be clickable |
| `waitForElementToDisappear(By locator)`                    | locator       | `boolean`    | Waits for element to disappear    |
| `waitForTextToBePresentInElement(By locator, String text)` | locator, text | `boolean`    | Waits for text in element         |

#### Dropdown/Select Methods

| Method                                                 | Parameters     | Return Type | Description             |
| ------------------------------------------------------ | -------------- | ----------- | ----------------------- |
| `selectByVisibleText(WebElement element, String text)` | element, text  | `void`      | Selects by visible text |
| `selectByValue(WebElement element, String value)`      | element, value | `void`      | Selects by value        |
| `selectByIndex(WebElement element, int index)`         | element, index | `void`      | Selects by index        |

#### Advanced Interaction Methods

| Method                                              | Parameters     | Return Type | Description              |
| --------------------------------------------------- | -------------- | ----------- | ------------------------ |
| `doubleClickElement(WebElement element)`            | element        | `void`      | Double clicks element    |
| `rightClickElement(WebElement element)`             | element        | `void`      | Right clicks element     |
| `hoverOverElement(WebElement element)`              | element        | `void`      | Hovers over element      |
| `dragAndDrop(WebElement source, WebElement target)` | source, target | `void`      | Drags and drops elements |

#### Screenshot Methods

| Method                               | Parameters | Return Type | Description                           |
| ------------------------------------ | ---------- | ----------- | ------------------------------------- |
| `captureScreenshot()`                | None       | `String`    | Captures screenshot with default name |
| `captureScreenshot(String fileName)` | fileName   | `String`    | Captures screenshot with custom name  |

#### Browser Navigation Methods

| Method                      | Parameters | Return Type | Description       |
| --------------------------- | ---------- | ----------- | ----------------- |
| `navigateToUrl(String url)` | url        | `void`      | Navigates to URL  |
| `getPageTitle()`            | None       | `String`    | Gets page title   |
| `getCurrentUrl()`           | None       | `String`    | Gets current URL  |
| `refreshPage()`             | None       | `void`      | Refreshes page    |
| `navigateBack()`            | None       | `void`      | Navigates back    |
| `navigateForward()`         | None       | `void`      | Navigates forward |

#### Example Usage

```java
public class LoginPage extends BasePage {
    @FindBy(id = "username")
    private WebElement usernameField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void login(String username, String password) {
        typeText(usernameField, username);
        typeText(passwordField, password);
        clickElement(loginButton);
    }

    public boolean isLoginButtonDisplayed() {
        return isElementDisplayed(loginButton);
    }
}
```

## Utility Classes

### WaitUtils

Provides advanced wait utilities for element interactions.

#### Methods

| Method                                                                 | Parameters                | Return Type  | Description                       |
| ---------------------------------------------------------------------- | ------------------------- | ------------ | --------------------------------- |
| `waitForElementVisible(By locator)`                                    | locator                   | `WebElement` | Waits for element to be visible   |
| `waitForElementClickable(By locator)`                                  | locator                   | `WebElement` | Waits for element to be clickable |
| `waitForElementToDisappear(By locator)`                                | locator                   | `boolean`    | Waits for element to disappear    |
| `waitForTextToBePresentInElement(By locator, String text)`             | locator, text             | `boolean`    | Waits for text in element         |
| `waitForAttributeContains(By locator, String attribute, String value)` | locator, attribute, value | `boolean`    | Waits for attribute value         |
| `waitForPageToLoad()`                                                  | None                      | `void`       | Waits for page load completion    |

### ExcelUtils

Utilities for reading and writing Excel files.

#### Methods

| Method                                                                          | Parameters                          | Return Type                 | Description                            |
| ------------------------------------------------------------------------------- | ----------------------------------- | --------------------------- | -------------------------------------- |
| `getTestData(String filePath, String sheetName)`                                | filePath, sheetName                 | `Object[][]`                | Gets test data for TestNG DataProvider |
| `readExcelData(String filePath, String sheetName)`                              | filePath, sheetName                 | `List<Map<String, String>>` | Reads Excel data as list of maps       |
| `writeTestResult(String filePath, String sheetName, int row, String result)`    | filePath, sheetName, row, result    | `void`                      | Writes test result to Excel            |
| `getCellData(String filePath, String sheetName, int row, int col)`              | filePath, sheetName, row, col       | `String`                    | Gets specific cell data                |
| `setCellData(String filePath, String sheetName, int row, int col, String data)` | filePath, sheetName, row, col, data | `void`                      | Sets specific cell data                |

### APIUtils

REST API testing utilities using RestAssured.

#### Methods

| Method                                                    | Parameters             | Return Type | Description                    |
| --------------------------------------------------------- | ---------------------- | ----------- | ------------------------------ |
| `get(String endpoint)`                                    | endpoint               | `Response`  | Performs GET request           |
| `post(String endpoint, String body)`                      | endpoint, body         | `Response`  | Performs POST request          |
| `put(String endpoint, String body)`                       | endpoint, body         | `Response`  | Performs PUT request           |
| `delete(String endpoint)`                                 | endpoint               | `Response`  | Performs DELETE request        |
| `validateStatusCode(Response response, int expectedCode)` | response, expectedCode | `void`      | Validates response status code |
| `validateResponseTime(Response response, long maxTime)`   | response, maxTime      | `void`      | Validates response time        |
| `extractJsonPath(Response response, String jsonPath)`     | response, jsonPath     | `String`    | Extracts value using JSON path |

### DatabaseUtils

Database connection and query utilities.

#### Methods

| Method                                          | Parameters    | Return Type                 | Description                   |
| ----------------------------------------------- | ------------- | --------------------------- | ----------------------------- |
| `getConnection()`                               | None          | `Connection`                | Gets database connection      |
| `executeQuery(String query, Object... params)`  | query, params | `List<Map<String, Object>>` | Executes SELECT query         |
| `executeUpdate(String query, Object... params)` | query, params | `int`                       | Executes INSERT/UPDATE/DELETE |
| `closeConnection(Connection connection)`        | connection    | `void`                      | Closes database connection    |

### DataGenerator

Test data generation utilities using JavaFaker.

#### Methods

| Method                                   | Parameters | Return Type | Description                   |
| ---------------------------------------- | ---------- | ----------- | ----------------------------- |
| `generateRandomEmail()`                  | None       | `String`    | Generates random email        |
| `generateRandomName()`                   | None       | `String`    | Generates random name         |
| `generateRandomPhone()`                  | None       | `String`    | Generates random phone number |
| `generateRandomAddress()`                | None       | `String`    | Generates random address      |
| `generateRandomText(int length)`         | length     | `String`    | Generates random text         |
| `generateRandomNumber(int min, int max)` | min, max   | `int`       | Generates random number       |

## Exception Classes

### FrameworkException

Base exception class for framework-specific errors.

#### Constructors

```java
public FrameworkException(String message)
public FrameworkException(String message, Throwable cause)
```

### ElementNotFoundException

Exception thrown when elements cannot be located.

#### Constructors

```java
public ElementNotFoundException(By locator)
public ElementNotFoundException(By locator, Throwable cause)
public ElementNotFoundException(String message, By locator)
```

### ConfigurationException

Exception thrown for configuration-related errors.

#### Constructors

```java
public ConfigurationException(String message)
public ConfigurationException(String message, Throwable cause)
```

## Reporting Classes

### ExtentManager

Manages ExtentReports instance and configuration.

#### Methods

| Method                                            | Parameters            | Return Type     | Description                   |
| ------------------------------------------------- | --------------------- | --------------- | ----------------------------- |
| `getInstance()`                                   | None                  | `ExtentReports` | Gets ExtentReports instance   |
| `createTest(String testName)`                     | testName              | `ExtentTest`    | Creates new test in report    |
| `createTest(String testName, String description)` | testName, description | `ExtentTest`    | Creates test with description |
| `flush()`                                         | None                  | `void`          | Writes report to file         |

### TestListener

TestNG listener for test execution events and reporting.

#### Methods

| Method                              | Parameters | Return Type | Description                 |
| ----------------------------------- | ---------- | ----------- | --------------------------- |
| `onTestStart(ITestResult result)`   | result     | `void`      | Called when test starts     |
| `onTestSuccess(ITestResult result)` | result     | `void`      | Called when test passes     |
| `onTestFailure(ITestResult result)` | result     | `void`      | Called when test fails      |
| `onTestSkipped(ITestResult result)` | result     | `void`      | Called when test is skipped |

### ScreenshotUtils

Screenshot capture utilities.

#### Methods

| Method                                                             | Parameters           | Return Type | Description                   |
| ------------------------------------------------------------------ | -------------------- | ----------- | ----------------------------- |
| `captureScreenshot(String testName)`                               | testName             | `String`    | Captures screenshot           |
| `captureScreenshotAsBase64()`                                      | None                 | `String`    | Captures screenshot as Base64 |
| `attachScreenshotToReport(ExtentTest test, String screenshotPath)` | test, screenshotPath | `void`      | Attaches screenshot to report |

## Test Base Classes

### BaseTest

Abstract base class for all test classes.

#### Methods

| Method                                           | Parameters | Return Type | Description                         |
| ------------------------------------------------ | ---------- | ----------- | ----------------------------------- |
| `setUp()`                                        | None       | `void`      | Test setup method (@BeforeMethod)   |
| `tearDown()`                                     | None       | `void`      | Test cleanup method (@AfterMethod)  |
| `getDriver()`                                    | None       | `WebDriver` | Gets current WebDriver instance     |
| `captureScreenshotOnFailure(ITestResult result)` | result     | `void`      | Captures screenshot on test failure |

#### Example Usage

```java
public class LoginTest extends BaseTest {
    private LoginPage loginPage;

    @BeforeMethod
    public void testSetup() {
        super.setUp();
        loginPage = new LoginPage(getDriver());
        loginPage.navigateToUrl(ConfigManager.getInstance().getProperty("base.url"));
    }

    @Test
    public void testValidLogin() {
        loginPage.login("valid@email.com", "password123");
        Assert.assertTrue(new DashboardPage(getDriver()).isDisplayed());
    }

    @AfterMethod
    public void testCleanup(ITestResult result) {
        captureScreenshotOnFailure(result);
        super.tearDown();
    }
}
```

## Usage Examples

### Basic Test Structure

```java
// 1. Create Page Object
public class HomePage extends BasePage {
    @FindBy(id = "search-box")
    private WebElement searchBox;

    @FindBy(css = "button[type='submit']")
    private WebElement searchButton;

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public SearchResultsPage search(String query) {
        typeText(searchBox, query);
        clickElement(searchButton);
        return new SearchResultsPage(driver);
    }
}

// 2. Create Test Class
public class SearchTest extends BaseTest {
    private HomePage homePage;

    @BeforeMethod
    public void setup() {
        super.setUp();
        homePage = new HomePage(getDriver());
    }

    @Test
    public void testSearch() {
        homePage.navigateToUrl("https://example.com");
        SearchResultsPage resultsPage = homePage.search("selenium");
        Assert.assertTrue(resultsPage.hasResults());
    }
}
```

### Data-Driven Test

```java
@Test(dataProvider = "searchData")
public void testSearchWithMultipleQueries(String query, int expectedResults) {
    homePage.navigateToUrl("https://example.com");
    SearchResultsPage resultsPage = homePage.search(query);
    Assert.assertTrue(resultsPage.getResultCount() >= expectedResults);
}

@DataProvider(name = "searchData")
public Object[][] getSearchData() {
    return ExcelUtils.getTestData("testdata/search-data.xlsx", "SearchQueries");
}
```

### API Test Integration

```java
@Test
public void testUserCreationEndToEnd() {
    // Create user via API
    String userJson = "{'name': 'John Doe', 'email': 'john@example.com'}";
    Response response = APIUtils.post("/api/users", userJson);
    APIUtils.validateStatusCode(response, 201);

    String userId = APIUtils.extractJsonPath(response, "id");

    // Verify user in UI
    loginPage.navigateToUrl("https://example.com/admin");
    UserListPage userListPage = loginPage.loginAsAdmin();
    Assert.assertTrue(userListPage.isUserDisplayed(userId));
}
```

This API documentation provides comprehensive coverage of all framework classes and methods, making it easy for developers to understand and use the framework effectively.
