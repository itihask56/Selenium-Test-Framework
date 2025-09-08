package com.framework.driver;

import org.openqa.selenium.WebDriver;

/**
 * Factory interface for creating WebDriver instances
 * Provides abstraction for browser-specific WebDriver creation
 */
public interface DriverFactory {
    
    /**
     * Creates a WebDriver instance for the specified browser type
     * @param browserType the type of browser to create
     * @return WebDriver instance
     * @throws RuntimeException if driver creation fails
     */
    WebDriver createDriver(BrowserType browserType);
    
    /**
     * Creates a WebDriver instance with custom options
     * @param browserType the type of browser to create
     * @param headless whether to run in headless mode
     * @param windowWidth browser window width
     * @param windowHeight browser window height
     * @param additionalArguments additional browser arguments
     * @return WebDriver instance
     * @throws RuntimeException if driver creation fails
     */
    WebDriver createDriver(BrowserType browserType, boolean headless, int windowWidth, 
                          int windowHeight, String... additionalArguments);
}