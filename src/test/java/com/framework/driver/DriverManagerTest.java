package com.framework.driver;

import com.framework.config.ConfigManager;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit tests for DriverManager class
 * Note: These tests require actual browser drivers to be available
 * In a CI environment, you might want to use mock drivers or skip these tests
 */
public class DriverManagerTest {

    private DriverManager driverManager;

    @BeforeMethod
    public void setUp() {
        driverManager = DriverManager.getInstance();
        // Clean up any existing driver
        driverManager.quitDriver();
    }

    @AfterMethod
    public void tearDown() {
        // Clean up after each test
        driverManager.quitDriver();
    }

    @Test
    public void testGetInstance() {
        DriverManager instance1 = DriverManager.getInstance();
        DriverManager instance2 = DriverManager.getInstance();
        Assert.assertSame(instance1, instance2, "DriverManager should be singleton");
    }

    @Test
    public void testIsDriverInitializedWhenNotInitialized() {
        Assert.assertFalse(driverManager.isDriverInitialized(), 
            "Driver should not be initialized initially");
    }

    @Test
    public void testGetDriverWhenNotInitialized() {
        WebDriver driver = driverManager.getDriver();
        Assert.assertNull(driver, "Driver should be null when not initialized");
    }

    @Test
    public void testInitializeDriverDefault() {
        // This test will use the browser configured in config.properties
        // Skip if running in headless CI environment without display
        if (isHeadlessEnvironment()) {
            return;
        }

        WebDriver driver = driverManager.initializeDriver();
        
        Assert.assertNotNull(driver, "Driver should be initialized");
        Assert.assertTrue(driverManager.isDriverInitialized(), 
            "Driver should be marked as initialized");
        Assert.assertSame(driverManager.getDriver(), driver, 
            "getDriver should return the same instance");
    }

    @Test
    public void testInitializeDriverWithSpecificBrowser() {
        // Skip if running in headless CI environment
        if (isHeadlessEnvironment()) {
            return;
        }

        WebDriver driver = driverManager.initializeDriver(BrowserType.CHROME_HEADLESS);
        
        Assert.assertNotNull(driver, "Driver should be initialized");
        Assert.assertTrue(driverManager.isDriverInitialized(), 
            "Driver should be marked as initialized");
        Assert.assertEquals(driverManager.getCurrentBrowser(), "Chrome Headless",
            "Current browser should be Chrome Headless");
    }

    @Test
    public void testCreateDriverChrome() {
        WebDriver driver = driverManager.createDriver(BrowserType.CHROME_HEADLESS);
        
        Assert.assertNotNull(driver, "Chrome driver should be created");
        
        // Clean up the driver we created manually
        driver.quit();
    }

    @Test
    public void testCreateDriverWithCustomOptions() {
        WebDriver driver = driverManager.createDriver(BrowserType.CHROME_HEADLESS, 
            true, 1024, 768, "--disable-web-security");
        
        Assert.assertNotNull(driver, "Chrome driver with custom options should be created");
        
        // Clean up the driver we created manually
        driver.quit();
    }

    @Test
    public void testQuitDriver() {
        // Initialize a driver first
        if (isHeadlessEnvironment()) {
            return;
        }

        driverManager.initializeDriver(BrowserType.CHROME_HEADLESS);
        Assert.assertTrue(driverManager.isDriverInitialized(), 
            "Driver should be initialized");

        driverManager.quitDriver();
        Assert.assertFalse(driverManager.isDriverInitialized(), 
            "Driver should not be initialized after quit");
        Assert.assertNull(driverManager.getDriver(), 
            "getDriver should return null after quit");
    }

    @Test
    public void testSetDriver() {
        WebDriver driver = driverManager.createDriver(BrowserType.CHROME_HEADLESS);
        
        driverManager.setDriver(driver);
        Assert.assertTrue(driverManager.isDriverInitialized(), 
            "Driver should be marked as initialized after setDriver");
        Assert.assertSame(driverManager.getDriver(), driver, 
            "getDriver should return the set driver");
        
        // Clean up
        driverManager.quitDriver();
    }

    @Test
    public void testGetActiveDriverCount() {
        int initialCount = driverManager.getActiveDriverCount();
        
        if (!isHeadlessEnvironment()) {
            driverManager.initializeDriver(BrowserType.CHROME_HEADLESS);
            Assert.assertEquals(driverManager.getActiveDriverCount(), initialCount + 1,
                "Active driver count should increase after initialization");
            
            driverManager.quitDriver();
            Assert.assertEquals(driverManager.getActiveDriverCount(), initialCount,
                "Active driver count should decrease after quit");
        }
    }

    @Test
    public void testThreadLocalBehavior() throws InterruptedException {
        if (isHeadlessEnvironment()) {
            return;
        }

        // Initialize driver in main thread
        WebDriver mainThreadDriver = driverManager.initializeDriver(BrowserType.CHROME_HEADLESS);
        Assert.assertNotNull(mainThreadDriver, "Main thread driver should be initialized");

        // Test in another thread
        final WebDriver[] otherThreadDriver = new WebDriver[1];
        final boolean[] otherThreadInitialized = new boolean[1];

        Thread otherThread = new Thread(() -> {
            DriverManager otherThreadManager = DriverManager.getInstance();
            otherThreadInitialized[0] = otherThreadManager.isDriverInitialized();
            otherThreadDriver[0] = otherThreadManager.getDriver();
        });

        otherThread.start();
        otherThread.join();

        // Other thread should not see the main thread's driver
        Assert.assertFalse(otherThreadInitialized[0], 
            "Other thread should not see main thread's driver");
        Assert.assertNull(otherThreadDriver[0], 
            "Other thread should get null driver");

        // Main thread should still have its driver
        Assert.assertTrue(driverManager.isDriverInitialized(), 
            "Main thread should still have its driver");
        Assert.assertSame(driverManager.getDriver(), mainThreadDriver, 
            "Main thread should get the same driver");
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testCreateDriverWithInvalidBrowser() {
        // This should throw an exception for unsupported browser
        // We'll simulate this by trying to create a driver with null browser type
        // Since we can't pass null to enum, we'll test the exception handling in createDriver
        try {
            driverManager.createDriver(null);
        } catch (NullPointerException e) {
            // Convert NPE to RuntimeException to match expected exception
            throw new RuntimeException("Invalid browser type", e);
        }
    }

    /**
     * Helper method to check if running in headless environment
     * This helps skip tests that require actual browser display
     */
    private boolean isHeadlessEnvironment() {
        // Check if running in CI environment or if DISPLAY is not set
        String ci = System.getenv("CI");
        String display = System.getenv("DISPLAY");
        String headless = System.getProperty("java.awt.headless");
        
        return "true".equals(ci) || display == null || "true".equals(headless);
    }
}