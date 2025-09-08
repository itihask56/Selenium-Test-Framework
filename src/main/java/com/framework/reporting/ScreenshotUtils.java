package com.framework.reporting;

import com.framework.config.ConfigManager;
import com.framework.driver.DriverManager;
import com.framework.utils.LoggerUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ScreenshotUtils provides utility methods for capturing and managing screenshots
 */
public class ScreenshotUtils {
    
    private static final String DEFAULT_SCREENSHOT_DIR = "screenshots";
    
    /**
     * Captures screenshot with default naming convention
     * @param testName Name of the test for screenshot naming
     * @return Path to the captured screenshot, null if capture failed
     */
    public static String captureScreenshot(String testName) {
        return captureScreenshot(testName, null);
    }
    
    /**
     * Captures screenshot with custom suffix
     * @param testName Name of the test for screenshot naming
     * @param suffix Additional suffix for the filename
     * @return Path to the captured screenshot, null if capture failed
     */
    public static String captureScreenshot(String testName, String suffix) {
        try {
            WebDriver driver = DriverManager.getInstance().getDriver();
            if (driver == null) {
                LoggerUtils.logWarning("WebDriver is null, cannot capture screenshot");
                return null;
            }
            
            ConfigManager config = ConfigManager.getInstance();
            String screenshotDir = config.getProperty("screenshot.path", DEFAULT_SCREENSHOT_DIR);
            
            // Create screenshots directory if it doesn't exist
            File dir = new File(screenshotDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // Generate screenshot filename with timestamp
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(new Date());
            String fileName = testName + (suffix != null ? "_" + suffix : "") + "_" + timestamp + ".png";
            String filePath = screenshotDir + File.separator + fileName;
            
            // Capture screenshot
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            byte[] screenshotBytes = takesScreenshot.getScreenshotAs(OutputType.BYTES);
            
            // Save screenshot to file
            Files.write(Paths.get(filePath), screenshotBytes);
            
            LoggerUtils.logScreenshot(filePath, "Screenshot captured");
            return filePath;
            
        } catch (IOException e) {
            LoggerUtils.logError("Failed to capture screenshot: " + e.getMessage(), e);
            return null;
        } catch (Exception e) {
            LoggerUtils.logError("Unexpected error while capturing screenshot: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Captures screenshot and returns as base64 string
     * @return Base64 encoded screenshot, null if capture failed
     */
    public static String captureScreenshotAsBase64() {
        try {
            WebDriver driver = DriverManager.getInstance().getDriver();
            if (driver == null) {
                LoggerUtils.logWarning("WebDriver is null, cannot capture screenshot");
                return null;
            }
            
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            return takesScreenshot.getScreenshotAs(OutputType.BASE64);
            
        } catch (Exception e) {
            LoggerUtils.logError("Failed to capture screenshot as base64: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Cleans up old screenshot files based on retention policy
     * @param retentionDays Number of days to retain screenshots
     */
    public static void cleanupOldScreenshots(int retentionDays) {
        try {
            ConfigManager config = ConfigManager.getInstance();
            String screenshotDir = config.getProperty("screenshot.path", DEFAULT_SCREENSHOT_DIR);
            
            File dir = new File(screenshotDir);
            if (!dir.exists()) {
                return;
            }
            
            long cutoffTime = System.currentTimeMillis() - (retentionDays * 24L * 60L * 60L * 1000L);
            
            File[] files = dir.listFiles((file, name) -> name.toLowerCase().endsWith(".png"));
            if (files != null) {
                int deletedCount = 0;
                for (File file : files) {
                    if (file.lastModified() < cutoffTime) {
                        if (file.delete()) {
                            deletedCount++;
                        }
                    }
                }
                LoggerUtils.getLogger(ScreenshotUtils.class).info("Cleaned up " + deletedCount + " old screenshot files");
            }
            
        } catch (Exception e) {
            LoggerUtils.logError("Failed to cleanup old screenshots: " + e.getMessage(), e);
        }
    }
}