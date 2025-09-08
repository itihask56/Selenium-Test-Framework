package com.framework.tests;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.testng.Assert.*;

/**
 * Unit tests for BaseTest class functionality
 * Uses integration testing approach to avoid mocking issues
 */
public class BaseTestTest {
    
    private TestableBaseTest baseTest;
    
    /**
     * Testable implementation of BaseTest for testing purposes
     * Overrides WebDriver initialization to avoid actual browser startup
     */
    private static class TestableBaseTest extends BaseTest {
        private boolean classSetupCalled = false;
        private boolean testDataSetupCalled = false;
        private boolean testDataCleanupCalled = false;
        private boolean classCleanupCalled = false;
        private boolean suiteCleanupCalled = false;
        private boolean skipDriverInit = false;
        
        public void setSkipDriverInit(boolean skip) {
            this.skipDriverInit = skip;
        }
        
        @Override
        protected void setupTestClass() {
            classSetupCalled = true;
        }
        
        @Override
        protected void setupTestData(Method method) {
            testDataSetupCalled = true;
        }
        
        @Override
        protected void cleanupTestData() {
            testDataCleanupCalled = true;
        }
        
        @Override
        protected void cleanupTestClass() {
            classCleanupCalled = true;
        }
        
        @Override
        protected void cleanupSuite() {
            suiteCleanupCalled = true;
        }
        
        // Override method setup to skip WebDriver initialization for testing
        public void testMethodSetup(Method method) {
            long startTime = System.currentTimeMillis();
            
            // Initialize test data storage
            testData.set(new java.util.HashMap<>());
            
            setTestData("startTime", startTime);
            setTestData("testName", method.getName());
            setTestData("description", getTestDescription(method));
            
            // Setup test-specific data
            setupTestData(method);
            
            testLogger.getLogger().info("Test setup completed for: {}", method.getName());
        }
        
        // Helper method to get test description
        private String getTestDescription(Method method) {
            Test testAnnotation = method.getAnnotation(Test.class);
            if (testAnnotation != null && !testAnnotation.description().isEmpty()) {
                return testAnnotation.description();
            }
            return "Test method: " + method.getName();
        }
        
        // Getters for testing
        public boolean isClassSetupCalled() { return classSetupCalled; }
        public boolean isTestDataSetupCalled() { return testDataSetupCalled; }
        public boolean isTestDataCleanupCalled() { return testDataCleanupCalled; }
        public boolean isClassCleanupCalled() { return classCleanupCalled; }
        public boolean isSuiteCleanupCalled() { return suiteCleanupCalled; }
    }
    
    @BeforeMethod
    public void setUp() {
        baseTest = new TestableBaseTest();
        baseTest.setSkipDriverInit(true);
    }
    
    @AfterMethod
    public void tearDown() {
        // Clean up test instance
        baseTest = null;
    }
    
    @Test(description = "Test suite setup initializes configuration correctly")
    public void testSuiteSetup() {
        // Execute suite setup
        baseTest.suiteSetup();
        
        // Verify configuration is initialized
        assertNotNull(baseTest.configManager);
        assertNotNull(baseTest.testConfig);
        assertNotNull(baseTest.testLogger);
    }
    
    @Test(description = "Test class setup calls setupTestClass method")
    public void testClassSetup() {
        // Initialize base test first
        baseTest.suiteSetup();
        
        // Execute class setup
        baseTest.classSetup();
        
        // Verify setupTestClass was called
        assertTrue(baseTest.isClassSetupCalled());
    }
    
    @Test(description = "Test method setup initializes test data")
    public void testMethodSetup() throws Exception {
        // Initialize base test
        baseTest.suiteSetup();
        
        // Create a mock method
        Method mockMethod = this.getClass().getDeclaredMethod("testMethodSetup");
        
        // Execute method setup (using our test version that skips WebDriver)
        baseTest.testMethodSetup(mockMethod);
        
        // Verify test data setup was called
        assertTrue(baseTest.isTestDataSetupCalled());
        
        // Verify test data is initialized
        assertNotNull(baseTest.getTestData("startTime"));
        assertNotNull(baseTest.getTestData("testName"));
        assertNotNull(baseTest.getTestData("description"));
        assertEquals("testMethodSetup", baseTest.getTestData("testName"));
    }
    
    @Test(description = "Test cleanup methods are called")
    public void testCleanupMethods() throws Exception {
        // Setup base test
        baseTest.suiteSetup();
        
        // Test class teardown
        baseTest.classTeardown();
        assertTrue(baseTest.isClassCleanupCalled());
        
        // Test suite teardown
        baseTest.suiteTeardown();
        assertTrue(baseTest.isSuiteCleanupCalled());
    }
    

    

    
    @Test(description = "Test data management methods work correctly")
    public void testDataManagement() throws Exception {
        // Setup base test
        baseTest.suiteSetup();
        Method mockMethod = this.getClass().getDeclaredMethod("testDataManagement");
        baseTest.testMethodSetup(mockMethod);
        
        // Test setting and getting test data
        baseTest.setTestData("testKey", "testValue");
        assertEquals("testValue", baseTest.getTestData("testKey"));
        
        // Test getting with default value
        assertEquals("defaultValue", baseTest.getTestData("nonExistentKey", "defaultValue"));
        
        // Test checking if data exists
        assertTrue(baseTest.hasTestData("testKey"));
        assertFalse(baseTest.hasTestData("nonExistentKey"));
        
        // Test removing data
        baseTest.removeTestData("testKey");
        assertFalse(baseTest.hasTestData("testKey"));
        
        // Test clearing all data
        baseTest.setTestData("key1", "value1");
        baseTest.setTestData("key2", "value2");
        baseTest.clearTestData();
        assertFalse(baseTest.hasTestData("key1"));
        assertFalse(baseTest.hasTestData("key2"));
    }
    
    @Test(description = "Test utility methods work correctly")
    public void testUtilityMethods() throws Exception {
        // Setup base test
        baseTest.suiteSetup();
        Method mockMethod = this.getClass().getDeclaredMethod("testUtilityMethods");
        baseTest.testMethodSetup(mockMethod);
        
        // Test getCurrentTestName
        assertEquals("testUtilityMethods", baseTest.getCurrentTestName());
        
        // Test getTestDuration (should be greater than 0)
        assertTrue(baseTest.getTestDuration() >= 0);
        
        // Test waitFor method (should not throw exception)
        try {
            baseTest.waitFor(10);
            // If we reach here, no exception was thrown
            assertTrue(true);
        } catch (Exception e) {
            fail("waitFor method should not throw exception: " + e.getMessage());
        }
    }
    
    @Test(description = "Test configuration loading")
    public void testConfigurationLoading() throws Exception {
        // Setup base test
        baseTest.suiteSetup();
        
        // Verify configuration is loaded
        assertNotNull(baseTest.testConfig);
        assertNotNull(baseTest.testConfig.getBrowser());
        assertNotNull(baseTest.testConfig.getEnvironment());
    }
    
    @Test(description = "Test test data initialization")
    public void testTestDataInitialization() throws Exception {
        // Setup base test
        baseTest.suiteSetup();
        Method mockMethod = this.getClass().getDeclaredMethod("testTestDataInitialization");
        baseTest.testMethodSetup(mockMethod);
        
        // Verify test data is properly initialized
        assertNotNull(baseTest.getTestData("startTime"));
        assertNotNull(baseTest.getTestData("testName"));
        assertNotNull(baseTest.getTestData("description"));
        
        // Verify test name is correct
        assertEquals("testTestDataInitialization", baseTest.getTestData("testName"));
    }
}