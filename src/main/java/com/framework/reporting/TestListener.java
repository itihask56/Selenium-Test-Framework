package com.framework.reporting;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.framework.config.ConfigManager;
import com.framework.driver.DriverManager;
import com.framework.utils.LoggerUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TestListener class implements TestNG listeners for test execution events
 * Integrates with ExtentReports for detailed test reporting
 */
public class TestListener implements ITestListener, ISuiteListener, IInvokedMethodListener {
    
    private static final String SCREENSHOT_DIR = "screenshots";
    
    @Override
    public void onStart(ISuite suite) {
        LoggerUtils.getLogger(TestListener.class).info("Test Suite started: " + suite.getName());
        // Initialize ExtentReports
        ExtentManager.getInstance();
    }
    
    @Override
    public void onFinish(ISuite suite) {
        LoggerUtils.getLogger(TestListener.class).info("Test Suite finished: " + suite.getName());
        // Flush ExtentReports
        ExtentManager.flush();
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        String description = result.getMethod().getDescription();
        
        if (description == null || description.isEmpty()) {
            description = "Test method: " + testName;
        }
        
        // Create test in ExtentReports
        ExtentTest test = ExtentManager.createTest(testName, description, className);
        
        // Add test information
        test.info("Test started: " + testName);
        test.info("Test class: " + className);
        
        // Add browser information if available
        try {
            ConfigManager config = ConfigManager.getInstance();
            String browser = config.getProperty("browser", "Unknown");
            String environment = config.getProperty("environment", "Unknown");
            
            test.info("Browser: " + browser);
            test.info("Environment: " + environment);
        } catch (Exception e) {
            LoggerUtils.logWarning("Could not retrieve configuration information: " + e.getMessage());
        }
        
        LoggerUtils.getLogger(TestListener.class).info("Test started: " + testName + " in class: " + className);
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        ExtentTest test = ExtentManager.getTest();
        
        if (test != null) {
            test.pass("Test passed successfully");
            long duration = result.getEndMillis() - result.getStartMillis();
            test.info("Execution time: " + duration + " ms");
        }
        
        LoggerUtils.getLogger(TestListener.class).info("Test passed: " + testName);
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        ExtentTest test = ExtentManager.getTest();
        
        if (test != null) {
            // Log failure details
            Throwable throwable = result.getThrowable();
            if (throwable != null) {
                test.fail("Test failed: " + throwable.getMessage());
                test.fail(throwable);
            } else {
                test.fail("Test failed with unknown reason");
            }
            
            // Add retry information if available
            if (result.getMethod().getRetryAnalyzer(result) != null) {
                try {
                    TestRetryAnalyzer retryAnalyzer = (TestRetryAnalyzer) result.getMethod().getRetryAnalyzer(result);
                    test.info("Retry attempt: " + (retryAnalyzer.getRetryCount() + 1) + 
                             " of " + (retryAnalyzer.getMaxRetryCount() + 1));
                } catch (Exception e) {
                    LoggerUtils.getLogger(TestListener.class).debug("Could not retrieve retry information: " + e.getMessage());
                }
            }
            
            // Capture screenshot on failure
            String screenshotPath = captureScreenshot(testName);
            if (screenshotPath != null) {
                try {
                    test.addScreenCaptureFromPath(screenshotPath, "Failure Screenshot");
                    test.info("Screenshot captured: " + screenshotPath);
                } catch (Exception e) {
                    LoggerUtils.logError("Failed to attach screenshot to report: " + e.getMessage(), e);
                }
            }
            
            long duration = result.getEndMillis() - result.getStartMillis();
            test.info("Execution time: " + duration + " ms");
        }
        
        LoggerUtils.getLogger(TestListener.class).error("Test failed: " + testName + " - " + 
                         (result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown error"));
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        ExtentTest test = ExtentManager.getTest();
        
        if (test != null) {
            Throwable throwable = result.getThrowable();
            if (throwable != null) {
                test.skip("Test skipped: " + throwable.getMessage());
            } else {
                test.skip("Test skipped");
            }
        }
        
        LoggerUtils.logWarning("Test skipped: " + testName);
    }
    
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        ExtentTest test = ExtentManager.getTest();
        
        if (test != null) {
            test.warning("Test failed but within success percentage");
        }
        
        LoggerUtils.logWarning("Test failed but within success percentage: " + testName);
    }
    
    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        // Clean up ExtentTest from ThreadLocal after test completion
        if (method.isTestMethod()) {
            ExtentManager.removeTest();
        }
    }
    
    /**
     * Captures screenshot and saves it to the screenshots directory
     * @param testName Name of the test for screenshot naming
     * @return Path to the captured screenshot, null if capture failed
     */
    private String captureScreenshot(String testName) {
        try {
            ConfigManager config = ConfigManager.getInstance();
            boolean screenshotOnFailure = Boolean.parseBoolean(
                config.getProperty("screenshot.on.failure", "true"));
            
            if (!screenshotOnFailure) {
                return null;
            }
            
            WebDriver driver = DriverManager.getInstance().getDriver();
            if (driver == null) {
                LoggerUtils.logWarning("WebDriver is null, cannot capture screenshot");
                return null;
            }
            
            // Create screenshots directory if it doesn't exist
            String screenshotDir = config.getProperty("screenshot.path", SCREENSHOT_DIR);
            File dir = new File(screenshotDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // Generate screenshot filename with timestamp
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(new Date());
            String fileName = testName + "_" + timestamp + ".png";
            String filePath = screenshotDir + File.separator + fileName;
            
            // Capture screenshot
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            byte[] screenshotBytes = takesScreenshot.getScreenshotAs(OutputType.BYTES);
            
            // Save screenshot to file
            Files.write(Paths.get(filePath), screenshotBytes);
            
            LoggerUtils.logScreenshot(filePath, "Test failure");
            return filePath;
            
        } catch (IOException e) {
            LoggerUtils.logError("Failed to capture screenshot: " + e.getMessage(), e);
            return null;
        } catch (Exception e) {
            LoggerUtils.logError("Unexpected error while capturing screenshot: " + e.getMessage(), e);
            return null;
        }
    }
}