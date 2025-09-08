# Troubleshooting Guide

This guide covers common issues you might encounter while using the Selenium Test Automation Framework and their solutions.

## WebDriver Issues

### Issue: WebDriverException - Browser not found

**Symptoms:**

- Tests fail with "WebDriverException: unknown error: cannot find Chrome binary"
- Browser doesn't launch

**Solutions:**

1. **Install the browser**: Ensure Chrome/Firefox/Edge is installed on your system
2. **Update WebDriverManager**: The framework uses WebDriverManager for automatic driver management
3. **Check browser version**: Ensure your browser version is supported
4. **Manual driver path**: If automatic detection fails, set the driver path manually:

```java
System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
```

### Issue: SessionNotCreatedException - Driver version mismatch

**Symptoms:**

- "SessionNotCreatedException: Could not start a new session"
- Version compatibility errors

**Solutions:**

1. **Update WebDriverManager**: Ensure you're using the latest version
2. **Clear driver cache**: Delete cached drivers in `~/.cache/selenium`
3. **Force driver update**:

```java
WebDriverManager.chromedriver().clearDriverCache().setup();
```

### Issue: WebDriver hangs or becomes unresponsive

**Symptoms:**

- Tests hang indefinitely
- Browser windows remain open after test completion

**Solutions:**

1. **Check timeouts**: Verify implicit and explicit timeout configurations
2. **Proper cleanup**: Ensure WebDriver.quit() is called in @AfterMethod
3. **Kill hanging processes**:

```bash
# Kill Chrome processes
pkill -f chrome
# Kill Firefox processes
pkill -f firefox
```

## Element Location Issues

### Issue: NoSuchElementException

**Symptoms:**

- "Unable to locate element"
- Tests fail when trying to interact with elements

**Solutions:**

1. **Add explicit waits**:

```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id("elementId")));
```

2. **Verify locators**: Use browser developer tools to validate selectors
3. **Check for iframes**: Element might be inside an iframe:

```java
driver.switchTo().frame("frameName");
// Interact with element
driver.switchTo().defaultContent();
```

4. **Wait for page load**: Ensure page is fully loaded before element interaction

### Issue: ElementNotInteractableException

**Symptoms:**

- Element is found but cannot be clicked or typed into
- "Element is not interactable" error

**Solutions:**

1. **Wait for element to be clickable**:

```java
wait.until(ExpectedConditions.elementToBeClickable(element));
```

2. **Scroll element into view**:

```java
((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
```

3. **Check for overlapping elements**: Another element might be covering the target element
4. **Use JavaScript click as fallback**:

```java
((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
```

## Configuration Issues

### Issue: Configuration properties not loading

**Symptoms:**

- Default values are used instead of configured values
- NullPointerException when accessing configuration

**Solutions:**

1. **Check file path**: Ensure `config.properties` is in `src/test/resources/`
2. **Verify property names**: Check for typos in property keys
3. **File encoding**: Ensure properties file is UTF-8 encoded
4. **Debug configuration loading**:

```java
Properties props = new Properties();
InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");
if (input == null) {
    System.out.println("Config file not found!");
}
```

### Issue: Environment-specific configuration not working

**Symptoms:**

- Wrong environment URLs are used
- System properties are ignored

**Solutions:**

1. **Check system property syntax**:

```bash
mvn test -Denvironment=staging -Dbrowser=firefox
```

2. **Verify property precedence**: System properties should override file properties
3. **Debug property resolution**:

```java
String env = System.getProperty("environment", ConfigManager.getProperty("environment"));
System.out.println("Using environment: " + env);
```

## Parallel Execution Issues

### Issue: Tests interfere with each other

**Symptoms:**

- Tests pass individually but fail when run in parallel
- Shared data corruption

**Solutions:**

1. **Use ThreadLocal WebDriver**: Ensure DriverManager uses ThreadLocal
2. **Avoid shared test data**: Use unique data for each test thread
3. **Independent test design**: Each test should be completely independent
4. **Check for static variables**: Avoid static variables that might be shared

### Issue: Resource contention

**Symptoms:**

- Tests timeout in parallel execution
- Performance degradation with multiple threads

**Solutions:**

1. **Reduce thread count**: Lower the parallel thread count
2. **Increase timeouts**: Adjust timeout values for parallel execution
3. **Resource monitoring**: Monitor CPU and memory usage
4. **Database connection pooling**: Use connection pooling for database tests

## Reporting Issues

### Issue: Screenshots not captured on failure

**Symptoms:**

- Test reports don't include failure screenshots
- Screenshot files are not generated

**Solutions:**

1. **Check TestListener configuration**: Ensure TestListener is properly configured in TestNG
2. **Verify screenshot directory**: Ensure screenshots directory exists and is writable
3. **WebDriver availability**: Ensure WebDriver is still available when screenshot is taken
4. **Debug screenshot capture**:

```java
try {
    TakesScreenshot screenshot = (TakesScreenshot) driver;
    File sourceFile = screenshot.getScreenshotAs(OutputType.FILE);
    System.out.println("Screenshot captured: " + sourceFile.getAbsolutePath());
} catch (Exception e) {
    System.out.println("Screenshot failed: " + e.getMessage());
}
```

### Issue: ExtentReports not generating

**Symptoms:**

- No HTML reports in reports directory
- Report files are empty or corrupted

**Solutions:**

1. **Check ExtentManager initialization**: Verify ExtentReports is properly initialized
2. **File permissions**: Ensure write permissions for reports directory
3. **TestNG listener registration**: Verify TestListener is registered in TestNG suite
4. **Flush reports**: Ensure ExtentReports.flush() is called

## Data-Driven Testing Issues

### Issue: Excel data not loading

**Symptoms:**

- DataProvider returns empty data
- FileNotFoundException for Excel files

**Solutions:**

1. **Check file path**: Ensure Excel files are in correct location
2. **File format**: Verify Excel file format (.xlsx vs .xls)
3. **Sheet names**: Check sheet names match the code
4. **File permissions**: Ensure read permissions for Excel files
5. **Debug data loading**:

```java
try {
    Workbook workbook = WorkbookFactory.create(new File("path/to/file.xlsx"));
    Sheet sheet = workbook.getSheet("SheetName");
    System.out.println("Rows found: " + sheet.getLastRowNum());
} catch (Exception e) {
    System.out.println("Excel loading failed: " + e.getMessage());
}
```

## API Testing Issues

### Issue: RestAssured connection failures

**Symptoms:**

- Connection timeout errors
- SSL certificate errors

**Solutions:**

1. **Check base URI**: Verify API base URL is correct
2. **SSL configuration**: For HTTPS APIs with self-signed certificates:

```java
RestAssured.useRelaxedHTTPSValidation();
```

3. **Proxy configuration**: If behind corporate proxy:

```java
RestAssured.proxy("proxy.company.com", 8080);
```

4. **Timeout configuration**:

```java
given()
    .config(RestAssured.config().httpClient(HttpClientConfig.httpClientConfig()
        .setParam(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000)
        .setParam(CoreConnectionPNames.SO_TIMEOUT, 60000)))
```

## Database Connection Issues

### Issue: SQLException - Connection refused

**Symptoms:**

- Cannot connect to database
- Connection timeout errors

**Solutions:**

1. **Check database URL**: Verify JDBC URL format and parameters
2. **Database availability**: Ensure database server is running
3. **Credentials**: Verify username and password
4. **Network connectivity**: Check firewall and network settings
5. **Driver dependency**: Ensure correct JDBC driver is in classpath

## Performance Issues

### Issue: Slow test execution

**Symptoms:**

- Tests take much longer than expected
- Browser operations are slow

**Solutions:**

1. **Reduce implicit waits**: Use explicit waits instead of long implicit waits
2. **Optimize locators**: Use efficient locator strategies (ID > CSS > XPath)
3. **Headless mode**: Run tests in headless mode for faster execution
4. **Disable images/CSS**: Configure browser to disable image loading
5. **Page load strategy**: Use different page load strategies:

```java
ChromeOptions options = new ChromeOptions();
options.setPageLoadStrategy(PageLoadStrategy.EAGER);
```

## Memory Issues

### Issue: OutOfMemoryError

**Symptoms:**

- Tests fail with heap space errors
- System becomes unresponsive

**Solutions:**

1. **Increase heap size**:

```bash
export MAVEN_OPTS="-Xmx2g -XX:MaxPermSize=512m"
mvn test
```

2. **Proper resource cleanup**: Ensure WebDriver instances are properly closed
3. **Reduce parallel threads**: Lower thread count to reduce memory usage
4. **Monitor memory usage**: Use profiling tools to identify memory leaks

## CI/CD Issues

### Issue: Tests pass locally but fail in CI

**Symptoms:**

- Tests work on developer machine but fail in CI pipeline
- Environment-specific failures

**Solutions:**

1. **Headless mode**: Ensure CI runs in headless mode
2. **Display configuration**: For Linux CI, configure virtual display:

```bash
export DISPLAY=:99
Xvfb :99 -screen 0 1024x768x24 > /dev/null 2>&1 &
```

3. **Browser installation**: Ensure browsers are installed in CI environment
4. **Timeout adjustments**: Increase timeouts for slower CI environments
5. **Environment variables**: Check environment-specific configurations

## Debugging Tips

### Enable Debug Logging

Add to `log4j2.xml`:

```xml
<Logger name="com.framework" level="DEBUG"/>
<Logger name="org.openqa.selenium" level="DEBUG"/>
```

### Browser Developer Tools

1. **Inspect elements**: Use F12 to inspect and validate locators
2. **Console errors**: Check browser console for JavaScript errors
3. **Network tab**: Monitor network requests and responses

### Remote Debugging

For headless Chrome debugging:

```java
ChromeOptions options = new ChromeOptions();
options.addArguments("--remote-debugging-port=9222");
```

Then connect to `http://localhost:9222` in another browser.

### Test Isolation

Run single test to isolate issues:

```bash
mvn test -Dtest=ClassName#methodName
```

## Getting Help

If you're still experiencing issues:

1. **Check logs**: Review application logs in `logs/` directory
2. **Enable debug mode**: Set log level to DEBUG for detailed information
3. **Minimal reproduction**: Create a minimal test case that reproduces the issue
4. **Environment details**: Gather OS, Java version, browser version information
5. **Stack traces**: Include full stack traces when reporting issues

## Common Error Messages and Solutions

| Error Message                                 | Likely Cause                          | Solution                                         |
| --------------------------------------------- | ------------------------------------- | ------------------------------------------------ |
| `WebDriverException: chrome not reachable`    | Chrome crashed or not responding      | Restart Chrome, check system resources           |
| `TimeoutException: Expected condition failed` | Element not found within timeout      | Increase timeout, verify locator                 |
| `StaleElementReferenceException`              | Element reference is outdated         | Re-find element after page changes               |
| `InvalidSelectorException`                    | Malformed CSS/XPath selector          | Validate selector syntax                         |
| `UnhandledAlertException`                     | Unexpected alert/popup                | Handle alerts explicitly                         |
| `NoSuchWindowException`                       | Window/tab was closed                 | Check window handles                             |
| `ElementClickInterceptedException`            | Element is covered by another element | Scroll to element, wait for overlay to disappear |

Remember to always check the full stack trace and logs for additional context when troubleshooting issues.
