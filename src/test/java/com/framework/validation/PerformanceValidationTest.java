package com.framework.validation;

import com.framework.config.ConfigManager;
import com.framework.driver.BrowserType;
import com.framework.driver.DriverManager;
import com.framework.tests.BaseTest;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * PerformanceValidationTest validates framework performance characteristics
 * Tests WebDriver initialization time, memory usage, and resource cleanup
 */
public class PerformanceValidationTest extends BaseTest {
    
    private static final int PERFORMANCE_THRESHOLD_MS = 10000; // 10 seconds max for driver init
    private static final int MEMORY_TEST_ITERATIONS = 5;
    
    @DataProvider(name = "browserPerformanceProvider")
    public Object[][] browserPerformanceProvider() {
        return new Object[][] {
            {BrowserType.CHROME.getBrowserName()},
            {BrowserType.FIREFOX.getBrowserName()}
        };
    }
    
    @Test(dataProvider = "browserPerformanceProvider",
          description = "Validate WebDriver initialization performance")
    public void testDriverInitializationPerformance(String browserName) {
        System.setProperty("browser", browserName);
        ConfigManager.getInstance().reloadConfiguration();
        
        List<Long> initTimes = new ArrayList<>();
        
        // Test multiple initializations to get average time
        for (int i = 0; i < 3; i++) {
            WebDriver testDriver = null;
            try {
                long startTime = System.currentTimeMillis();
                
                testDriver = DriverManager.getInstance().initializeDriver();
                
                long endTime = System.currentTimeMillis();
                long initTime = endTime - startTime;
                initTimes.add(initTime);
                
                testLogger.getLogger().info("Driver initialization #{} for {} took {} ms", 
                                           i + 1, browserName, initTime);
                
                // Validate initialization time is within acceptable threshold
                Assert.assertTrue(initTime < PERFORMANCE_THRESHOLD_MS, 
                                String.format("Driver initialization for %s took %d ms, which exceeds threshold of %d ms", 
                                            browserName, initTime, PERFORMANCE_THRESHOLD_MS));
                
            } finally {
                if (testDriver != null) {
                    try {
                        testDriver.quit();
                    } catch (Exception e) {
                        testLogger.getLogger().warn("Error closing performance test driver: {}", e.getMessage());
                    }
                }
            }
        }
        
        // Calculate average initialization time
        double avgTime = initTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        testLogger.getLogger().info("Average initialization time for {}: {:.2f} ms", browserName, avgTime);
        
        // Log performance metrics
        setTestData("avgInitTime_" + browserName, avgTime);
        setTestData("maxInitTime_" + browserName, initTimes.stream().mapToLong(Long::longValue).max().orElse(0L));
        setTestData("minInitTime_" + browserName, initTimes.stream().mapToLong(Long::longValue).min().orElse(0L));
    }
    
    @Test(description = "Validate memory usage and cleanup")
    public void testMemoryUsageAndCleanup() {
        Runtime runtime = Runtime.getRuntime();
        
        // Force garbage collection before test
        System.gc();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();
        
        testLogger.getLogger().info("Initial memory usage: {} MB", initialMemory / (1024 * 1024));
        
        List<Long> memoryUsages = new ArrayList<>();
        
        // Create and destroy multiple driver instances to test memory cleanup
        for (int i = 0; i < MEMORY_TEST_ITERATIONS; i++) {
            WebDriver testDriver = null;
            try {
                testDriver = DriverManager.getInstance().initializeDriver();
                
                // Perform some operations to use memory
                testDriver.get("data:text/html,<html><head><title>Memory Test " + i + "</title></head><body><h1>Memory Test Iteration: " + i + "</h1></body></html>");
                
                // Measure memory after driver creation and usage
                long currentMemory = runtime.totalMemory() - runtime.freeMemory();
                memoryUsages.add(currentMemory);
                
                testLogger.getLogger().info("Memory usage after iteration {}: {} MB", 
                                           i + 1, currentMemory / (1024 * 1024));
                
            } finally {
                if (testDriver != null) {
                    testDriver.quit();
                }
                
                // Force garbage collection after each iteration
                System.gc();
                
                // Small delay to allow cleanup
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        // Final memory check
        System.gc();
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        
        testLogger.getLogger().info("Final memory usage: {} MB", finalMemory / (1024 * 1024));
        
        // Calculate memory growth
        long memoryGrowth = finalMemory - initialMemory;
        double memoryGrowthMB = memoryGrowth / (1024.0 * 1024.0);
        
        testLogger.getLogger().info("Total memory growth: {:.2f} MB", memoryGrowthMB);
        
        // Validate memory growth is reasonable (less than 100MB for test iterations)
        Assert.assertTrue(memoryGrowthMB < 100, 
                         String.format("Memory growth of %.2f MB is too high, possible memory leak", memoryGrowthMB));
        
        // Store memory metrics
        setTestData("initialMemoryMB", initialMemory / (1024 * 1024));
        setTestData("finalMemoryMB", finalMemory / (1024 * 1024));
        setTestData("memoryGrowthMB", memoryGrowthMB);
    }
    
    @Test(description = "Validate resource cleanup efficiency")
    public void testResourceCleanupEfficiency() {
        int driverCount = 3;
        List<Long> cleanupTimes = new ArrayList<>();
        
        for (int i = 0; i < driverCount; i++) {
            WebDriver testDriver = null;
            try {
                testDriver = DriverManager.getInstance().initializeDriver();
                
                // Perform some operations
                testDriver.get("data:text/html,<html><head><title>Cleanup Test</title></head><body><h1>Testing Resource Cleanup</h1></body></html>");
                
                // Measure cleanup time
                long startCleanup = System.currentTimeMillis();
                testDriver.quit();
                long endCleanup = System.currentTimeMillis();
                
                long cleanupTime = endCleanup - startCleanup;
                cleanupTimes.add(cleanupTime);
                
                testLogger.getLogger().info("Driver cleanup #{} took {} ms", i + 1, cleanupTime);
                
                // Validate cleanup time is reasonable (less than 5 seconds)
                Assert.assertTrue(cleanupTime < 5000, 
                                String.format("Driver cleanup took %d ms, which is too long", cleanupTime));
                
            } catch (Exception e) {
                testLogger.getLogger().error("Error during resource cleanup test iteration {}: {}", i + 1, e.getMessage());
                Assert.fail("Resource cleanup test failed: " + e.getMessage());
            } finally {
                // Ensure driver is cleaned up even if test fails
                if (testDriver != null) {
                    try {
                        testDriver.quit();
                    } catch (Exception ignored) {
                        // Already handled above
                    }
                }
            }
        }
        
        // Calculate average cleanup time
        double avgCleanupTime = cleanupTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        testLogger.getLogger().info("Average cleanup time: {:.2f} ms", avgCleanupTime);
        
        // Store cleanup metrics
        setTestData("avgCleanupTime", avgCleanupTime);
        setTestData("maxCleanupTime", cleanupTimes.stream().mapToLong(Long::longValue).max().orElse(0L));
    }
    
    @Test(description = "Validate concurrent driver initialization performance")
    public void testConcurrentDriverInitialization() {
        int threadCount = 3;
        List<Thread> threads = new ArrayList<>();
        List<Long> initTimes = new ArrayList<>();
        List<Exception> exceptions = new ArrayList<>();
        
        // Create multiple threads to initialize drivers concurrently
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            Thread thread = new Thread(() -> {
                WebDriver testDriver = null;
                try {
                    long startTime = System.currentTimeMillis();
                    testDriver = DriverManager.getInstance().initializeDriver();
                    long endTime = System.currentTimeMillis();
                    
                    synchronized (initTimes) {
                        initTimes.add(endTime - startTime);
                    }
                    
                    // Perform basic operation
                    testDriver.get("data:text/html,<html><head><title>Concurrent Test " + threadIndex + "</title></head><body><h1>Thread: " + threadIndex + "</h1></body></html>");
                    
                    testLogger.getLogger().info("Concurrent driver initialization #{} completed", threadIndex + 1);
                    
                } catch (Exception e) {
                    synchronized (exceptions) {
                        exceptions.add(e);
                    }
                    testLogger.getLogger().error("Concurrent driver initialization #{} failed: {}", threadIndex + 1, e.getMessage());
                } finally {
                    if (testDriver != null) {
                        try {
                            testDriver.quit();
                        } catch (Exception e) {
                            testLogger.getLogger().warn("Error cleaning up concurrent test driver #{}: {}", threadIndex + 1, e.getMessage());
                        }
                    }
                }
            });
            
            threads.add(thread);
        }
        
        // Start all threads
        long overallStartTime = System.currentTimeMillis();
        threads.forEach(Thread::start);
        
        // Wait for all threads to complete
        threads.forEach(thread -> {
            try {
                thread.join(30000); // 30 second timeout
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Assert.fail("Concurrent test interrupted");
            }
        });
        
        long overallEndTime = System.currentTimeMillis();
        long totalTime = overallEndTime - overallStartTime;
        
        // Validate no exceptions occurred
        Assert.assertTrue(exceptions.isEmpty(), 
                         "Concurrent driver initialization should not throw exceptions. Exceptions: " + exceptions);
        
        // Validate all initializations completed
        Assert.assertEquals(initTimes.size(), threadCount, 
                           "All concurrent driver initializations should complete successfully");
        
        // Calculate performance metrics
        double avgConcurrentInitTime = initTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        
        testLogger.getLogger().info("Concurrent initialization - Total time: {} ms, Average per thread: {:.2f} ms", 
                                   totalTime, avgConcurrentInitTime);
        
        // Store concurrent performance metrics
        setTestData("concurrentTotalTime", totalTime);
        setTestData("concurrentAvgInitTime", avgConcurrentInitTime);
        setTestData("concurrentThreadCount", threadCount);
    }
}