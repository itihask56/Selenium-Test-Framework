package com.framework.driver;

import com.framework.config.ConfigManager;
import com.framework.config.TestConfig;
import com.framework.exceptions.ConfigurationException;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.Dimension;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * DriverManager handles WebDriver lifecycle and provides thread-safe access
 * Uses ThreadLocal to manage WebDriver instances for parallel test execution
 */
public class DriverManager implements DriverFactory {
    
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ConcurrentMap<Long, String> threadBrowserMap = new ConcurrentHashMap<>();
    private static DriverManager instance;
    private static final Object lock = new Object();
    
    private final ConfigManager configManager;
    private final TestConfig testConfig;
    
    // Private constructor for singleton pattern
    private DriverManager() {
        this.configManager = ConfigManager.getInstance();
        this.testConfig = configManager.getTestConfig();
    }
    
    /**
     * Gets the singleton instance of DriverManager
     * @return DriverManager instance
     */
    public static DriverManager getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new DriverManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Initializes WebDriver for current thread based on configuration
     * @return WebDriver instance
     */
    public WebDriver initializeDriver() {
        if (driverThreadLocal.get() != null) {
            return driverThreadLocal.get();
        }
        
        String browserName = testConfig.getBrowser();
        BrowserType browserType = BrowserType.fromString(browserName);
        
        // Override with headless if configured
        if (testConfig.isHeadless() && !browserType.isHeadless()) {
            switch (browserType) {
                case CHROME:
                    browserType = BrowserType.CHROME_HEADLESS;
                    break;
                case FIREFOX:
                    browserType = BrowserType.FIREFOX_HEADLESS;
                    break;
                default:
                    // For browsers that don't have explicit headless enum, we'll handle in createDriver
                    break;
            }
        }
        
        WebDriver driver = createDriver(browserType, testConfig.isHeadless(), 
                                      testConfig.getWindowWidth(), testConfig.getWindowHeight());
        
        setDriver(driver);
        threadBrowserMap.put(Thread.currentThread().getId(), browserType.getDisplayName());
        
        return driver;
    }
    
    /**
     * Initializes WebDriver with specific browser type
     * @param browserType the browser type to initialize
     * @return WebDriver instance
     */
    public WebDriver initializeDriver(BrowserType browserType) {
        if (driverThreadLocal.get() != null) {
            quitDriver();
        }
        
        WebDriver driver = createDriver(browserType, testConfig.isHeadless(), 
                                      testConfig.getWindowWidth(), testConfig.getWindowHeight());
        
        setDriver(driver);
        threadBrowserMap.put(Thread.currentThread().getId(), browserType.getDisplayName());
        
        return driver;
    }
    
    @Override
    public WebDriver createDriver(BrowserType browserType) {
        return createDriver(browserType, false, 1920, 1080);
    }
    
    @Override
    public WebDriver createDriver(BrowserType browserType, boolean headless, int windowWidth, 
                                int windowHeight, String... additionalArguments) {
        WebDriver driver;
        
        try {
            switch (browserType.getBaseBrowser()) {
                case CHROME:
                    driver = createChromeDriver(headless || browserType.isHeadless(), 
                                              windowWidth, windowHeight, additionalArguments);
                    break;
                case FIREFOX:
                    driver = createFirefoxDriver(headless || browserType.isHeadless(), 
                                               windowWidth, windowHeight, additionalArguments);
                    break;
                case EDGE:
                    driver = createEdgeDriver(headless, windowWidth, windowHeight, additionalArguments);
                    break;
                case SAFARI:
                    driver = createSafariDriver(windowWidth, windowHeight, additionalArguments);
                    break;
                default:
                    throw new ConfigurationException("Unsupported browser type: " + browserType);
            }
            
            // Set implicit timeout
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(testConfig.getImplicitTimeout()));
            
            // Set window size if not headless
            if (!headless && !browserType.isHeadless()) {
                driver.manage().window().setSize(new Dimension(windowWidth, windowHeight));
            }
            
            return driver;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to create WebDriver for browser: " + browserType, e);
        }
    }
    
    /**
     * Creates Chrome WebDriver with specified options
     */
    private WebDriver createChromeDriver(boolean headless, int windowWidth, int windowHeight, 
                                       String... additionalArguments) {
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        
        // Basic Chrome options
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        
        if (headless) {
            options.addArguments("--headless=new");
        }
        
        options.addArguments("--window-size=" + windowWidth + "," + windowHeight);
        
        // Add additional arguments if provided
        if (additionalArguments != null && additionalArguments.length > 0) {
            options.addArguments(Arrays.asList(additionalArguments));
        }
        
        return new ChromeDriver(options);
    }
    
    /**
     * Creates Firefox WebDriver with specified options
     */
    private WebDriver createFirefoxDriver(boolean headless, int windowWidth, int windowHeight, 
                                        String... additionalArguments) {
        WebDriverManager.firefoxdriver().setup();
        
        FirefoxOptions options = new FirefoxOptions();
        
        if (headless) {
            options.addArguments("--headless");
        }
        
        options.addArguments("--width=" + windowWidth);
        options.addArguments("--height=" + windowHeight);
        
        // Add additional arguments if provided
        if (additionalArguments != null && additionalArguments.length > 0) {
            options.addArguments(Arrays.asList(additionalArguments));
        }
        
        return new FirefoxDriver(options);
    }
    
    /**
     * Creates Edge WebDriver with specified options
     */
    private WebDriver createEdgeDriver(boolean headless, int windowWidth, int windowHeight, 
                                     String... additionalArguments) {
        WebDriverManager.edgedriver().setup();
        
        EdgeOptions options = new EdgeOptions();
        
        // Basic Edge options
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        
        if (headless) {
            options.addArguments("--headless");
        }
        
        options.addArguments("--window-size=" + windowWidth + "," + windowHeight);
        
        // Add additional arguments if provided
        if (additionalArguments != null && additionalArguments.length > 0) {
            options.addArguments(Arrays.asList(additionalArguments));
        }
        
        return new EdgeDriver(options);
    }
    
    /**
     * Creates Safari WebDriver with specified options
     * Note: Safari doesn't support headless mode
     */
    private WebDriver createSafariDriver(int windowWidth, int windowHeight, 
                                       String... additionalArguments) {
        SafariOptions options = new SafariOptions();
        
        // Safari has limited options compared to other browsers
        // Additional arguments are not supported in the same way
        
        WebDriver driver = new SafariDriver(options);
        
        // Set window size after driver creation for Safari
        driver.manage().window().setSize(new Dimension(windowWidth, windowHeight));
        
        return driver;
    }
    
    /**
     * Gets the current WebDriver instance for the thread
     * @return WebDriver instance or null if not initialized
     */
    public WebDriver getDriver() {
        return driverThreadLocal.get();
    }
    
    /**
     * Sets the WebDriver instance for current thread
     * @param driver WebDriver instance to set
     */
    public void setDriver(WebDriver driver) {
        driverThreadLocal.set(driver);
    }
    
    /**
     * Quits the WebDriver and removes it from ThreadLocal
     */
    public void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.err.println("Error while quitting WebDriver: " + e.getMessage());
            } finally {
                driverThreadLocal.remove();
                threadBrowserMap.remove(Thread.currentThread().getId());
            }
        }
    }
    
    /**
     * Gets the browser name for current thread
     * @return browser name string
     */
    public String getCurrentBrowser() {
        return threadBrowserMap.get(Thread.currentThread().getId());
    }
    
    /**
     * Checks if WebDriver is initialized for current thread
     * @return true if driver is initialized, false otherwise
     */
    public boolean isDriverInitialized() {
        return driverThreadLocal.get() != null;
    }
    
    /**
     * Quits all WebDriver instances and cleans up resources
     * Should be called during framework shutdown
     */
    public static void quitAllDrivers() {
        // This method can be enhanced to track all driver instances if needed
        // For now, it ensures current thread's driver is cleaned up
        if (instance != null) {
            instance.quitDriver();
        }
    }
    
    /**
     * Gets the number of active driver instances
     * @return number of active drivers
     */
    public int getActiveDriverCount() {
        return threadBrowserMap.size();
    }
}