package com.framework.validation;

import com.framework.driver.DriverManager;
import com.framework.tests.BaseTest;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * MemoryResourceValidationTest validates framework memory usage and resource cleanup
 * Tests memory leaks, resource cleanup efficiency, and concurrent resource management
 */
public class MemoryResourceValidationTest extends BaseTest {
    
    private static final int STRESS_TEST_ITERATIONS = 10;
    private static final int CONCURRENT_THREADS = 5;
    private static final long MEMORY_THRESHOLD_MB = 200; // 200MB threshold for memory growth
    
    @Test(description = "Validate memory usage during driver lifecycle")
    public void testDriverLifecycleMemoryUsage() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        
        // Force garbage collection and get baseline memory
        System.gc();
        Thread.yield();
        MemoryUsage initialMemory = memoryBean.getHeapMemoryUsage();
        long initialUsedMemory = initialMemory.getUsed();
        
        testLogger.getLogger().info("Initial memory usage: {} MB", initialUsedMemory / (1024 * 1024));
        
        List<Long> memorySnapshots = new ArrayList<>();
        
        // Test multiple driver creation and destruction cycles
        for (int i = 0; i < STRESS_TEST_ITERATIONS; i++) {
            WebDriver testDriver = null;
            try {
                // Create driver
                testDriver = DriverManager.getInstance().initializeDriver();
                
                // Perform some operations to use memory
                testDriver.get("data:text/html,<html><head><title>Memory Test " + i + "</title></head>" +
                              "<body><h1>Memory Test Iteration: " + i + "</h1>" +
                              "<div>Testing memory usage during driver operations</div></body></html>");
                
                // Navigate to another page
                testDriver.get("data:text/html,<html><head><title>Memory Test Page 2</title></head>" +
                              "<body><h1>Second Page</h1><p>Additional content for memory testing</p></body></html>");
                
                // Take memory snapshot
                MemoryUsage currentMemory = memoryBean.getHeapMemoryUsage();
                long currentUsedMemory = currentMemory.getUsed();
                memorySnapshots.add(currentUsedMemory);
                
                testLogger.getLogger().debug("Iteration {}: Memory usage {} MB", 
                                            i + 1, currentUsedMemory / (1024 * 1024));
                
            } finally {
                // Cleanup driver
                if (testDriver != null) {
                    testDriver.quit();
                }
                
                // Force garbage collection after each iteration
                System.gc();
                Thread.yield();
                
                // Small delay to allow cleanup
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        // Final memory check
        System.gc();
        Thread.yield();
        MemoryUsage finalMemory = memoryBean.getHeapMemoryUsage();
        long finalUsedMemory = finalMemory.getUsed();
        
        long memoryGrowth = finalUsedMemory - initialUsedMemory;
        double memoryGrowthMB = memoryGrowth / (1024.0 * 1024.0);
        
        testLogger.getLogger().info("Final memory usage: {} MB", finalUsedMemory / (1024 * 1024));
        testLogger.getLogger().info("Memory growth: {:.2f} MB", memoryGrowthMB);
        
        // Validate memory growth is within acceptable limits
        Assert.assertTrue(memoryGrowthMB < MEMORY_THRESHOLD_MB, 
                         String.format("Memory growth of %.2f MB exceeds threshold of %d MB. Possible memory leak detected.", 
                                     memoryGrowthMB, MEMORY_THRESHOLD_MB));
        
        // Calculate memory statistics
        double avgMemoryUsage = memorySnapshots.stream().mapToLong(Long::longValue).average().orElse(0.0) / (1024 * 1024);
        long maxMemoryUsage = memorySnapshots.stream().mapToLong(Long::longValue).max().orElse(0L) / (1024 * 1024);
        long minMemoryUsage = memorySnapshots.stream().mapToLong(Long::longValue).min().orElse(0L) / (1024 * 1024);
        
        testLogger.getLogger().info("Memory statistics - Avg: {:.2f} MB, Max: {} MB, Min: {} MB", 
                                   avgMemoryUsage, maxMemoryUsage, minMemoryUsage);
        
        // Store memory metrics for reporting
        setTestData("memoryGrowthMB", memoryGrowthMB);
        setTestData("avgMemoryUsageMB", avgMemoryUsage);
        setTestData("maxMemoryUsageMB", maxMemoryUsage);
    }
    
    @Test(description = "Validate resource cleanup efficiency")
    public void testResourceCleanupEfficiency() {
        List<Long> cleanupTimes = new ArrayList<>();
        List<Long> initTimes = new ArrayList<>();
        
        for (int i = 0; i < 5; i++) {
            WebDriver testDriver = null;
            try {
                // Measure initialization time
                long initStart = System.currentTimeMillis();
                testDriver = DriverManager.getInstance().initializeDriver();
                long initEnd = System.currentTimeMillis();
                long initTime = initEnd - initStart;
                initTimes.add(initTime);
                
                // Perform operations
                testDriver.get("data:text/html,<html><head><title>Cleanup Test</title></head>" +
                              "<body><h1>Resource Cleanup Test</h1></body></html>");
                
                // Measure cleanup time
                long cleanupStart = System.currentTimeMillis();
                testDriver.quit();
                long cleanupEnd = System.currentTimeMillis();
                long cleanupTime = cleanupEnd - cleanupStart;
                cleanupTimes.add(cleanupTime);
                
                testDriver = null; // Prevent double cleanup
                
                testLogger.getLogger().debug("Iteration {}: Init time {} ms, Cleanup time {} ms", 
                                            i + 1, initTime, cleanupTime);
                
                // Validate cleanup time is reasonable
                Assert.assertTrue(cleanupTime < 10000, 
                                String.format("Cleanup time of %d ms is too long for iteration %d", cleanupTime, i + 1));
                
            } catch (Exception e) {
                Assert.fail("Resource cleanup test failed at iteration " + (i + 1) + ": " + e.getMessage());
            } finally {
                if (testDriver != null) {
                    try {
                        testDriver.quit();
                    } catch (Exception ignored) {
                        // Already handled
                    }
                }
            }
        }
        
        // Calculate averages
        double avgInitTime = initTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        double avgCleanupTime = cleanupTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        long maxCleanupTime = cleanupTimes.stream().mapToLong(Long::longValue).max().orElse(0L);
        
        testLogger.getLogger().info("Resource timing - Avg init: {:.2f} ms, Avg cleanup: {:.2f} ms, Max cleanup: {} ms", 
                                   avgInitTime, avgCleanupTime, maxCleanupTime);
        
        // Store cleanup metrics
        setTestData("avgInitTime", avgInitTime);
        setTestData("avgCleanupTime", avgCleanupTime);
        setTestData("maxCleanupTime", maxCleanupTime);
    }
    
    @Test(description = "Validate concurrent resource management")
    public void testConcurrentResourceManagement() {
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS);
        CountDownLatch latch = new CountDownLatch(CONCURRENT_THREADS);
        List<Exception> exceptions = new ArrayList<>();
        List<Long> executionTimes = new ArrayList<>();
        
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        System.gc();
        MemoryUsage initialMemory = memoryBean.getHeapMemoryUsage();
        long initialUsedMemory = initialMemory.getUsed();
        
        testLogger.getLogger().info("Starting concurrent resource management test with {} threads", CONCURRENT_THREADS);
        
        // Submit concurrent tasks
        for (int i = 0; i < CONCURRENT_THREADS; i++) {
            final int threadIndex = i;
            executor.submit(() -> {
                WebDriver testDriver = null;
                try {
                    long startTime = System.currentTimeMillis();
                    
                    // Initialize driver
                    testDriver = DriverManager.getInstance().initializeDriver();
                    
                    // Perform operations
                    testDriver.get("data:text/html,<html><head><title>Concurrent Test " + threadIndex + "</title></head>" +
                                  "<body><h1>Concurrent Thread: " + threadIndex + "</h1>" +
                                  "<p>Testing concurrent resource management</p></body></html>");
                    
                    // Navigate to another page
                    testDriver.get("data:text/html,<html><head><title>Page 2 - Thread " + threadIndex + "</title></head>" +
                                  "<body><h1>Second Page - Thread: " + threadIndex + "</h1></body></html>");
                    
                    // Validate operations
                    String title = testDriver.getTitle();
                    if (!title.contains("Thread " + threadIndex)) {
                        throw new RuntimeException("Title validation failed for thread " + threadIndex);
                    }
                    
                    long endTime = System.currentTimeMillis();
                    synchronized (executionTimes) {
                        executionTimes.add(endTime - startTime);
                    }
                    
                } catch (Exception e) {
                    synchronized (exceptions) {
                        exceptions.add(new RuntimeException("Thread " + threadIndex + " failed: " + e.getMessage(), e));
                    }
                } finally {
                    if (testDriver != null) {
                        try {
                            testDriver.quit();
                        } catch (Exception e) {
                            synchronized (exceptions) {
                                exceptions.add(new RuntimeException("Cleanup failed for thread " + threadIndex + ": " + e.getMessage(), e));
                            }
                        }
                    }
                    latch.countDown();
                }
            });
        }
        
        // Wait for all tasks to complete
        try {
            boolean completed = latch.await(60, TimeUnit.SECONDS);
            Assert.assertTrue(completed, "All concurrent tasks should complete within timeout");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Assert.fail("Concurrent test was interrupted");
        } finally {
            executor.shutdown();
        }
        
        // Check for exceptions
        if (!exceptions.isEmpty()) {
            testLogger.getLogger().error("Concurrent resource management test had {} exceptions:", exceptions.size());
            for (Exception e : exceptions) {
                testLogger.getLogger().error("Exception: {}", e.getMessage());
            }
            Assert.fail("Concurrent resource management failed with " + exceptions.size() + " exceptions");
        }
        
        // Validate all tasks completed successfully
        Assert.assertEquals(executionTimes.size(), CONCURRENT_THREADS, 
                           "All concurrent tasks should complete successfully");
        
        // Check memory usage after concurrent operations
        System.gc();
        Thread.yield();
        MemoryUsage finalMemory = memoryBean.getHeapMemoryUsage();
        long finalUsedMemory = finalMemory.getUsed();
        long memoryGrowth = finalUsedMemory - initialUsedMemory;
        double memoryGrowthMB = memoryGrowth / (1024.0 * 1024.0);
        
        testLogger.getLogger().info("Concurrent test memory growth: {:.2f} MB", memoryGrowthMB);
        
        // Calculate performance metrics
        double avgExecutionTime = executionTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        long maxExecutionTime = executionTimes.stream().mapToLong(Long::longValue).max().orElse(0L);
        long minExecutionTime = executionTimes.stream().mapToLong(Long::longValue).min().orElse(0L);
        
        testLogger.getLogger().info("Concurrent execution times - Avg: {:.2f} ms, Max: {} ms, Min: {} ms", 
                                   avgExecutionTime, maxExecutionTime, minExecutionTime);
        
        // Validate memory growth is reasonable for concurrent operations
        Assert.assertTrue(memoryGrowthMB < MEMORY_THRESHOLD_MB * 2, 
                         String.format("Concurrent memory growth of %.2f MB is too high", memoryGrowthMB));
        
        // Store concurrent metrics
        setTestData("concurrentMemoryGrowthMB", memoryGrowthMB);
        setTestData("concurrentAvgExecutionTime", avgExecutionTime);
        setTestData("concurrentMaxExecutionTime", maxExecutionTime);
        setTestData("concurrentThreadCount", CONCURRENT_THREADS);
        
        testLogger.getLogger().info("Concurrent resource management test completed successfully");
    }
    
    @Test(description = "Validate memory leak detection")
    public void testMemoryLeakDetection() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        
        // Baseline memory measurement
        System.gc();
        Thread.yield();
        MemoryUsage baselineMemory = memoryBean.getHeapMemoryUsage();
        long baselineUsed = baselineMemory.getUsed();
        
        testLogger.getLogger().info("Baseline memory: {} MB", baselineUsed / (1024 * 1024));
        
        List<Long> memoryMeasurements = new ArrayList<>();
        
        // Perform operations that should not cause memory leaks
        for (int cycle = 0; cycle < 3; cycle++) {
            testLogger.getLogger().info("Starting memory leak detection cycle {}", cycle + 1);
            
            // Create and destroy multiple drivers in this cycle
            for (int i = 0; i < 3; i++) {
                WebDriver testDriver = null;
                try {
                    testDriver = DriverManager.getInstance().initializeDriver();
                    
                    // Perform memory-intensive operations
                    for (int page = 0; page < 3; page++) {
                        testDriver.get("data:text/html,<html><head><title>Leak Test Cycle " + cycle + " Page " + page + "</title></head>" +
                                      "<body><h1>Memory Leak Detection</h1>" +
                                      "<div>Cycle: " + cycle + ", Iteration: " + i + ", Page: " + page + "</div>" +
                                      "<p>Testing for memory leaks in framework components</p></body></html>");
                    }
                    
                } finally {
                    if (testDriver != null) {
                        testDriver.quit();
                    }
                }
            }
            
            // Force garbage collection after each cycle
            System.gc();
            Thread.yield();
            
            // Wait for cleanup
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Measure memory after cycle
            MemoryUsage cycleMemory = memoryBean.getHeapMemoryUsage();
            long cycleUsed = cycleMemory.getUsed();
            memoryMeasurements.add(cycleUsed);
            
            testLogger.getLogger().info("Memory after cycle {}: {} MB", cycle + 1, cycleUsed / (1024 * 1024));
        }
        
        // Analyze memory trend
        boolean memoryIncreasing = true;
        for (int i = 1; i < memoryMeasurements.size(); i++) {
            if (memoryMeasurements.get(i) <= memoryMeasurements.get(i - 1)) {
                memoryIncreasing = false;
                break;
            }
        }
        
        // Calculate total memory growth
        long finalMemory = memoryMeasurements.get(memoryMeasurements.size() - 1);
        long totalGrowth = finalMemory - baselineUsed;
        double totalGrowthMB = totalGrowth / (1024.0 * 1024.0);
        
        testLogger.getLogger().info("Total memory growth: {:.2f} MB", totalGrowthMB);
        testLogger.getLogger().info("Memory consistently increasing: {}", memoryIncreasing);
        
        // Validate no significant memory leak
        Assert.assertTrue(totalGrowthMB < 50, 
                         String.format("Total memory growth of %.2f MB suggests possible memory leak", totalGrowthMB));
        
        // If memory is consistently increasing, it might indicate a leak
        if (memoryIncreasing && totalGrowthMB > 20) {
            testLogger.getLogger().warn("Memory consistently increasing by {:.2f} MB - monitor for potential leak", totalGrowthMB);
        }
        
        // Store leak detection metrics
        setTestData("memoryLeakGrowthMB", totalGrowthMB);
        setTestData("memoryConsistentlyIncreasing", memoryIncreasing);
        
        testLogger.getLogger().info("Memory leak detection test completed");
    }
}