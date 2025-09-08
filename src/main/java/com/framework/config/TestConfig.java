package com.framework.config;

/**
 * TestConfig data model class that holds configuration values
 * Used to store and access test configuration properties
 */
public class TestConfig {
    private String browser;
    private boolean headless;
    private int implicitTimeout;
    private int explicitTimeout;
    private int windowWidth;
    private int windowHeight;
    private String environment;
    private String baseUrlDev;
    private String baseUrlStaging;
    private String baseUrlProd;
    private boolean parallelExecution;
    private int threadCount;
    private int retryCount;
    private boolean screenshotOnFailure;
    private String reportPath;
    private String screenshotPath;
    private String logLevel;
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private String apiBaseUrl;
    private int apiTimeout;

    // Default constructor
    public TestConfig() {
    }

    // Browser configuration getters and setters
    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public boolean isHeadless() {
        return headless;
    }

    public void setHeadless(boolean headless) {
        this.headless = headless;
    }

    public int getImplicitTimeout() {
        return implicitTimeout;
    }

    public void setImplicitTimeout(int implicitTimeout) {
        this.implicitTimeout = implicitTimeout;
    }

    public int getExplicitTimeout() {
        return explicitTimeout;
    }

    public void setExplicitTimeout(int explicitTimeout) {
        this.explicitTimeout = explicitTimeout;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public void setWindowHeight(int windowHeight) {
        this.windowHeight = windowHeight;
    }

    // Environment configuration getters and setters
    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getBaseUrlDev() {
        return baseUrlDev;
    }

    public void setBaseUrlDev(String baseUrlDev) {
        this.baseUrlDev = baseUrlDev;
    }

    public String getBaseUrlStaging() {
        return baseUrlStaging;
    }

    public void setBaseUrlStaging(String baseUrlStaging) {
        this.baseUrlStaging = baseUrlStaging;
    }

    public String getBaseUrlProd() {
        return baseUrlProd;
    }

    public void setBaseUrlProd(String baseUrlProd) {
        this.baseUrlProd = baseUrlProd;
    }

    /**
     * Get the base URL for the current environment
     * @return base URL string for current environment
     */
    public String getBaseUrl() {
        switch (environment.toLowerCase()) {
            case "dev":
                return baseUrlDev;
            case "staging":
                return baseUrlStaging;
            case "prod":
                return baseUrlProd;
            default:
                return baseUrlDev;
        }
    }

    // Test execution configuration getters and setters
    public boolean isParallelExecution() {
        return parallelExecution;
    }

    public void setParallelExecution(boolean parallelExecution) {
        this.parallelExecution = parallelExecution;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public boolean isScreenshotOnFailure() {
        return screenshotOnFailure;
    }

    public void setScreenshotOnFailure(boolean screenshotOnFailure) {
        this.screenshotOnFailure = screenshotOnFailure;
    }

    // Reporting configuration getters and setters
    public String getReportPath() {
        return reportPath;
    }

    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }

    public String getScreenshotPath() {
        return screenshotPath;
    }

    public void setScreenshotPath(String screenshotPath) {
        this.screenshotPath = screenshotPath;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    // Database configuration getters and setters
    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    // API configuration getters and setters
    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }

    public int getApiTimeout() {
        return apiTimeout;
    }

    public void setApiTimeout(int apiTimeout) {
        this.apiTimeout = apiTimeout;
    }

    @Override
    public String toString() {
        return "TestConfig{" +
                "browser='" + browser + '\'' +
                ", headless=" + headless +
                ", implicitTimeout=" + implicitTimeout +
                ", explicitTimeout=" + explicitTimeout +
                ", environment='" + environment + '\'' +
                ", baseUrl='" + getBaseUrl() + '\'' +
                ", parallelExecution=" + parallelExecution +
                ", threadCount=" + threadCount +
                ", retryCount=" + retryCount +
                '}';
    }
}