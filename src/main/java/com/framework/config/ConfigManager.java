package com.framework.config;

/**
 * Selenium Test Automation Framework
 * 
 * @author Itihas Verma
 * @version 1.0.0
 * @since 2024
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigManager is a singleton class responsible for loading and managing
 * configuration properties from config.properties file.
 * It provides thread-safe access to configuration values throughout the framework.
 */
public class ConfigManager {
    
    private static ConfigManager instance;
    private static final Object lock = new Object();
    private Properties properties;
    private TestConfig testConfig;
    private static final String CONFIG_FILE_PATH = "config.properties";
    
    // Private constructor to prevent instantiation
    private ConfigManager() {
        loadProperties();
        initializeTestConfig();
    }
    
    /**
     * Gets the singleton instance of ConfigManager
     * @return ConfigManager instance
     */
    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Loads properties from config.properties file
     */
    private void loadProperties() {
        properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_PATH)) {
            if (inputStream == null) {
                throw new RuntimeException("Configuration file '" + CONFIG_FILE_PATH + "' not found in classpath");
            }
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration file: " + CONFIG_FILE_PATH, e);
        }
    }
    
    /**
     * Initializes TestConfig object with loaded properties
     */
    private void initializeTestConfig() {
        testConfig = new TestConfig();
        
        // Browser Configuration
        testConfig.setBrowser(getProperty("browser", "chrome"));
        testConfig.setHeadless(getBooleanProperty("headless", false));
        testConfig.setImplicitTimeout(getIntProperty("browser.timeout.implicit", 10));
        testConfig.setExplicitTimeout(getIntProperty("browser.timeout.explicit", 20));
        testConfig.setWindowWidth(getIntProperty("browser.window.width", 1920));
        testConfig.setWindowHeight(getIntProperty("browser.window.height", 1080));
        
        // Environment Configuration
        testConfig.setEnvironment(getProperty("environment", "dev"));
        testConfig.setBaseUrlDev(getProperty("base.url.dev", ""));
        testConfig.setBaseUrlStaging(getProperty("base.url.staging", ""));
        testConfig.setBaseUrlProd(getProperty("base.url.prod", ""));
        
        // Test Configuration
        testConfig.setParallelExecution(getBooleanProperty("parallel.execution", true));
        testConfig.setThreadCount(getIntProperty("thread.count", 3));
        testConfig.setRetryCount(getIntProperty("retry.count", 2));
        testConfig.setScreenshotOnFailure(getBooleanProperty("screenshot.on.failure", true));
        
        // Reporting Configuration
        testConfig.setReportPath(getProperty("report.path", "reports"));
        testConfig.setScreenshotPath(getProperty("screenshot.path", "screenshots"));
        
        // Database Configuration
        testConfig.setDbUrl(getProperty("db.url", ""));
        testConfig.setDbUsername(getProperty("db.username", ""));
        testConfig.setDbPassword(getProperty("db.password", ""));
        
        // API Configuration
        testConfig.setApiBaseUrl(getProperty("api.base.url", ""));
        testConfig.setApiTimeout(getIntProperty("api.timeout", 30));
    }
    
    /**
     * Gets a property value as String
     * @param key property key
     * @param defaultValue default value if property not found
     * @return property value or default value
     */
    public String getProperty(String key, String defaultValue) {
        // Check system properties first (for command line overrides)
        String systemProperty = System.getProperty(key);
        if (systemProperty != null && !systemProperty.trim().isEmpty()) {
            return systemProperty.trim();
        }
        
        // Then check loaded properties file
        return properties.getProperty(key, defaultValue).trim();
    }
    
    /**
     * Gets a property value as String
     * @param key property key
     * @return property value or null if not found
     */
    public String getProperty(String key) {
        return getProperty(key, null);
    }
    
    /**
     * Gets a property value as boolean
     * @param key property key
     * @param defaultValue default value if property not found
     * @return property value as boolean or default value
     */
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }
    
    /**
     * Gets a property value as integer
     * @param key property key
     * @param defaultValue default value if property not found
     * @return property value as integer or default value
     */
    public int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.err.println("Invalid integer value for property '" + key + "': " + value + 
                             ". Using default value: " + defaultValue);
            return defaultValue;
        }
    }
    
    /**
     * Gets a property value as long
     * @param key property key
     * @param defaultValue default value if property not found
     * @return property value as long or default value
     */
    public long getLongProperty(String key, long defaultValue) {
        String value = getProperty(key);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            System.err.println("Invalid long value for property '" + key + "': " + value + 
                             ". Using default value: " + defaultValue);
            return defaultValue;
        }
    }
    
    /**
     * Gets the TestConfig object with all loaded configuration
     * @return TestConfig object
     */
    public TestConfig getTestConfig() {
        return testConfig;
    }
    
    /**
     * Reloads the configuration from properties file
     * This method can be used to refresh configuration during runtime
     */
    public synchronized void reloadConfiguration() {
        loadProperties();
        initializeTestConfig();
    }
    
    /**
     * Checks if a property exists
     * @param key property key
     * @return true if property exists, false otherwise
     */
    public boolean hasProperty(String key) {
        return properties.containsKey(key) || System.getProperty(key) != null;
    }
    
    /**
     * Gets all properties as Properties object
     * @return Properties object containing all loaded properties
     */
    public Properties getAllProperties() {
        return new Properties(properties);
    }
    
    /**
     * Sets a property value (runtime only, not persisted to file)
     * @param key property key
     * @param value property value
     */
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
        // Reinitialize test config to reflect the change
        initializeTestConfig();
    }
}