package com.framework.driver;

/**
 * Enum representing supported browser types for test execution
 * Each browser type contains configuration for WebDriver initialization
 */
public enum BrowserType {
    CHROME("chrome", "Chrome"),
    FIREFOX("firefox", "Firefox"),
    EDGE("edge", "Microsoft Edge"),
    SAFARI("safari", "Safari"),
    CHROME_HEADLESS("chrome-headless", "Chrome Headless"),
    FIREFOX_HEADLESS("firefox-headless", "Firefox Headless");

    private final String browserName;
    private final String displayName;

    /**
     * Constructor for BrowserType enum
     * @param browserName the browser name used in configuration
     * @param displayName the display name for reporting
     */
    BrowserType(String browserName, String displayName) {
        this.browserName = browserName;
        this.displayName = displayName;
    }

    /**
     * Gets the browser name
     * @return browser name string
     */
    public String getBrowserName() {
        return browserName;
    }

    /**
     * Gets the display name
     * @return display name string
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets BrowserType from string value
     * @param browserName the browser name string
     * @return BrowserType enum value
     * @throws IllegalArgumentException if browser name is not supported
     */
    public static BrowserType fromString(String browserName) {
        if (browserName == null || browserName.trim().isEmpty()) {
            return CHROME; // Default browser
        }

        String normalizedName = browserName.toLowerCase().trim();
        
        for (BrowserType type : BrowserType.values()) {
            if (type.getBrowserName().equalsIgnoreCase(normalizedName)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("Unsupported browser: " + browserName + 
            ". Supported browsers: chrome, firefox, edge, safari, chrome-headless, firefox-headless");
    }

    /**
     * Checks if the browser type is headless
     * @return true if headless browser, false otherwise
     */
    public boolean isHeadless() {
        return this == CHROME_HEADLESS || this == FIREFOX_HEADLESS;
    }

    /**
     * Gets the base browser type (without headless modifier)
     * @return base BrowserType
     */
    public BrowserType getBaseBrowser() {
        switch (this) {
            case CHROME_HEADLESS:
                return CHROME;
            case FIREFOX_HEADLESS:
                return FIREFOX;
            default:
                return this;
        }
    }

    @Override
    public String toString() {
        return displayName;
    }
}