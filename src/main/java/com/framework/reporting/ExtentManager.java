package com.framework.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.framework.config.ConfigManager;
import com.framework.utils.LoggerUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ExtentManager class manages ExtentReports instance and configuration
 * Implements singleton pattern to ensure single instance across test execution
 */
public class ExtentManager {
    
    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    private static String reportPath;
    
    /**
     * Gets or creates ExtentReports instance
     * @return ExtentReports instance
     */
    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            createInstance();
        }
        return extent;
    }
    
    /**
     * Creates ExtentReports instance with configuration
     */
    private static void createInstance() {
        try {
            ConfigManager config = ConfigManager.getInstance();
            String reportDir = config.getProperty("report.path", "reports");
            
            // Create reports directory if it doesn't exist
            File reportsDir = new File(reportDir);
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
            }
            
            // Generate report file name with timestamp
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            reportPath = reportDir + File.separator + "ExtentReport_" + timestamp + ".html";
            
            // Create ExtentSparkReporter
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            
            // Configure reporter
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setDocumentTitle("Test Automation Report");
            sparkReporter.config().setReportName("Selenium Test Framework Report");
            sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");
            
            // Create ExtentReports instance
            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            
            // Set system information
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("Browser", config.getProperty("browser", "chrome"));
            extent.setSystemInfo("Environment", config.getProperty("environment", "dev"));
            extent.setSystemInfo("User", System.getProperty("user.name"));
            
            LoggerUtils.getLogger(ExtentManager.class).info("ExtentReports initialized successfully. Report path: " + reportPath);
            
        } catch (Exception e) {
            LoggerUtils.logError("Failed to initialize ExtentReports: " + e.getMessage(), e);
            throw new RuntimeException("ExtentReports initialization failed", e);
        }
    }
    
    /**
     * Creates a new test in ExtentReports
     * @param testName Name of the test
     * @param description Description of the test
     * @return ExtentTest instance
     */
    public static ExtentTest createTest(String testName, String description) {
        ExtentTest test = getInstance().createTest(testName, description);
        extentTest.set(test);
        LoggerUtils.getLogger(ExtentManager.class).info("Created ExtentTest: " + testName);
        return test;
    }
    
    /**
     * Creates a new test in ExtentReports with category
     * @param testName Name of the test
     * @param description Description of the test
     * @param category Category/tag for the test
     * @return ExtentTest instance
     */
    public static ExtentTest createTest(String testName, String description, String category) {
        ExtentTest test = getInstance().createTest(testName, description);
        test.assignCategory(category);
        extentTest.set(test);
        LoggerUtils.getLogger(ExtentManager.class).info("Created ExtentTest: " + testName + " with category: " + category);
        return test;
    }
    
    /**
     * Gets current ExtentTest instance for the thread
     * @return ExtentTest instance
     */
    public static ExtentTest getTest() {
        return extentTest.get();
    }
    
    /**
     * Removes ExtentTest instance from ThreadLocal
     */
    public static void removeTest() {
        extentTest.remove();
    }
    
    /**
     * Flushes the ExtentReports and generates the report
     */
    public static synchronized void flush() {
        if (extent != null) {
            extent.flush();
            LoggerUtils.getLogger(ExtentManager.class).info("ExtentReports flushed successfully");
        }
    }
    
    /**
     * Gets the report file path
     * @return Report file path
     */
    public static String getReportPath() {
        return reportPath;
    }
}