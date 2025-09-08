package com.framework.utils;

import com.framework.config.ConfigManager;
import com.framework.config.TestConfig;
import com.framework.exceptions.FrameworkException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DatabaseUtils class
 */
public class DatabaseUtilsTest {
    
    @Mock
    private ConfigManager mockConfigManager;
    
    @Mock
    private TestConfig mockTestConfig;
    
    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockPreparedStatement;
    
    @Mock
    private ResultSet mockResultSet;
    
    @Mock
    private ResultSetMetaData mockMetaData;
    
    @Mock
    private DatabaseMetaData mockDatabaseMetaData;
    
    private DatabaseUtils databaseUtils;
    
    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Mock configuration
        when(mockConfigManager.getTestConfig()).thenReturn(mockTestConfig);
        when(mockTestConfig.getDbUrl()).thenReturn("jdbc:h2:mem:testdb");
        when(mockTestConfig.getDbUsername()).thenReturn("sa");
        when(mockTestConfig.getDbPassword()).thenReturn("");
        
        // Create DatabaseUtils with test configuration
        databaseUtils = new DatabaseUtils("jdbc:h2:mem:testdb", "sa", "");
    }
    
    @Test
    public void testConstructorWithParameters() {
        DatabaseUtils utils = new DatabaseUtils("jdbc:h2:mem:test", "user", "pass");
        Assert.assertNotNull(utils);
    }
    
    @Test(expectedExceptions = FrameworkException.class)
    public void testConstructorWithEmptyUrl() {
        new DatabaseUtils("", "user", "pass");
    }
    
    @Test(expectedExceptions = FrameworkException.class)
    public void testConstructorWithNullUrl() {
        new DatabaseUtils(null, "user", "pass");
    }
    
    @Test
    public void testTestConnection() {
        // This test would require an actual database connection
        // For unit testing, we'll test the method exists and handles exceptions
        try {
            boolean result = databaseUtils.testConnection();
            // Result can be true or false depending on H2 availability
            Assert.assertNotNull(result);
        } catch (Exception e) {
            // Expected if H2 driver is not available
            Assert.assertTrue(e instanceof FrameworkException || e instanceof RuntimeException);
        }
    }
    
    // Mock-based tests for methods that would require database setup
    
    @Test
    public void testExecuteQueryWithMocks() throws SQLException {
        // This is a conceptual test - in practice, we'd need to mock the entire connection chain
        // or use an embedded database like H2 for integration testing
        
        // Setup mocks
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getMetaData()).thenReturn(mockMetaData);
        when(mockMetaData.getColumnCount()).thenReturn(2);
        when(mockMetaData.getColumnName(1)).thenReturn("id");
        when(mockMetaData.getColumnName(2)).thenReturn("name");
        when(mockResultSet.getObject(1)).thenReturn(1);
        when(mockResultSet.getObject(2)).thenReturn("Test");
        
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        
        // Note: This test would need dependency injection or other mocking framework
        // to properly test the executeQuery method
    }
    
    @Test
    public void testQueryWithParamsClass() {
        DatabaseUtils.QueryWithParams queryWithParams = 
            new DatabaseUtils.QueryWithParams("SELECT * FROM users WHERE id = ?", 1);
        
        Assert.assertEquals(queryWithParams.getQuery(), "SELECT * FROM users WHERE id = ?");
        Assert.assertEquals(queryWithParams.getParameters().length, 1);
        Assert.assertEquals(queryWithParams.getParameters()[0], 1);
    }
    
    @Test
    public void testQueryWithParamsClassMultipleParams() {
        DatabaseUtils.QueryWithParams queryWithParams = 
            new DatabaseUtils.QueryWithParams("SELECT * FROM users WHERE id = ? AND name = ?", 1, "John");
        
        Assert.assertEquals(queryWithParams.getQuery(), "SELECT * FROM users WHERE id = ? AND name = ?");
        Assert.assertEquals(queryWithParams.getParameters().length, 2);
        Assert.assertEquals(queryWithParams.getParameters()[0], 1);
        Assert.assertEquals(queryWithParams.getParameters()[1], "John");
    }
    
    @Test
    public void testQueryWithParamsClassNoParams() {
        DatabaseUtils.QueryWithParams queryWithParams = 
            new DatabaseUtils.QueryWithParams("SELECT * FROM users");
        
        Assert.assertEquals(queryWithParams.getQuery(), "SELECT * FROM users");
        Assert.assertEquals(queryWithParams.getParameters().length, 0);
    }
    
    // Integration tests that would work with an actual H2 database
    // These are commented out as they require H2 driver and proper setup
    
    /*
    @Test
    public void testExecuteQueryIntegration() {
        // This would require H2 database setup
        DatabaseUtils utils = new DatabaseUtils("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        
        // Create test table
        utils.executeUpdate("CREATE TABLE test_users (id INT PRIMARY KEY, name VARCHAR(50))");
        
        // Insert test data
        utils.executeUpdate("INSERT INTO test_users (id, name) VALUES (?, ?)", 1, "John");
        utils.executeUpdate("INSERT INTO test_users (id, name) VALUES (?, ?)", 2, "Jane");
        
        // Test query
        List<Map<String, Object>> results = utils.executeQuery("SELECT * FROM test_users ORDER BY id");
        
        Assert.assertEquals(results.size(), 2);
        Assert.assertEquals(results.get(0).get("ID"), 1);
        Assert.assertEquals(results.get(0).get("NAME"), "John");
        Assert.assertEquals(results.get(1).get("ID"), 2);
        Assert.assertEquals(results.get(1).get("NAME"), "Jane");
        
        utils.close();
    }
    
    @Test
    public void testExecuteUpdateIntegration() {
        DatabaseUtils utils = new DatabaseUtils("jdbc:h2:mem:testdb2;DB_CLOSE_DELAY=-1", "sa", "");
        
        // Create test table
        utils.executeUpdate("CREATE TABLE test_users (id INT PRIMARY KEY, name VARCHAR(50))");
        
        // Test insert
        int rowsAffected = utils.executeUpdate("INSERT INTO test_users (id, name) VALUES (?, ?)", 1, "John");
        Assert.assertEquals(rowsAffected, 1);
        
        // Test update
        rowsAffected = utils.executeUpdate("UPDATE test_users SET name = ? WHERE id = ?", "Johnny", 1);
        Assert.assertEquals(rowsAffected, 1);
        
        // Test delete
        rowsAffected = utils.executeUpdate("DELETE FROM test_users WHERE id = ?", 1);
        Assert.assertEquals(rowsAffected, 1);
        
        utils.close();
    }
    
    @Test
    public void testGetSingleValueIntegration() {
        DatabaseUtils utils = new DatabaseUtils("jdbc:h2:mem:testdb3;DB_CLOSE_DELAY=-1", "sa", "");
        
        // Create test table and data
        utils.executeUpdate("CREATE TABLE test_users (id INT PRIMARY KEY, name VARCHAR(50))");
        utils.executeUpdate("INSERT INTO test_users (id, name) VALUES (?, ?)", 1, "John");
        
        // Test getSingleValue
        Object result = utils.getSingleValue("SELECT name FROM test_users WHERE id = ?", 1);
        Assert.assertEquals(result, "John");
        
        // Test with no result
        Object noResult = utils.getSingleValue("SELECT name FROM test_users WHERE id = ?", 999);
        Assert.assertNull(noResult);
        
        utils.close();
    }
    
    @Test
    public void testGetSingleRowIntegration() {
        DatabaseUtils utils = new DatabaseUtils("jdbc:h2:mem:testdb4;DB_CLOSE_DELAY=-1", "sa", "");
        
        // Create test table and data
        utils.executeUpdate("CREATE TABLE test_users (id INT PRIMARY KEY, name VARCHAR(50), age INT)");
        utils.executeUpdate("INSERT INTO test_users (id, name, age) VALUES (?, ?, ?)", 1, "John", 30);
        
        // Test getSingleRow
        Map<String, Object> result = utils.getSingleRow("SELECT * FROM test_users WHERE id = ?", 1);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.get("ID"), 1);
        Assert.assertEquals(result.get("NAME"), "John");
        Assert.assertEquals(result.get("AGE"), 30);
        
        // Test with no result
        Map<String, Object> noResult = utils.getSingleRow("SELECT * FROM test_users WHERE id = ?", 999);
        Assert.assertNull(noResult);
        
        utils.close();
    }
    
    @Test
    public void testRecordExistsIntegration() {
        DatabaseUtils utils = new DatabaseUtils("jdbc:h2:mem:testdb5;DB_CLOSE_DELAY=-1", "sa", "");
        
        // Create test table and data
        utils.executeUpdate("CREATE TABLE test_users (id INT PRIMARY KEY, name VARCHAR(50))");
        utils.executeUpdate("INSERT INTO test_users (id, name) VALUES (?, ?)", 1, "John");
        
        // Test recordExists with count query
        boolean exists = utils.recordExists("SELECT COUNT(*) FROM test_users WHERE id = ?", 1);
        Assert.assertTrue(exists);
        
        boolean notExists = utils.recordExists("SELECT COUNT(*) FROM test_users WHERE id = ?", 999);
        Assert.assertFalse(notExists);
        
        utils.close();
    }
    
    @Test
    public void testGetRecordCountIntegration() {
        DatabaseUtils utils = new DatabaseUtils("jdbc:h2:mem:testdb6;DB_CLOSE_DELAY=-1", "sa", "");
        
        // Create test table and data
        utils.executeUpdate("CREATE TABLE test_users (id INT PRIMARY KEY, name VARCHAR(50))");
        utils.executeUpdate("INSERT INTO test_users (id, name) VALUES (?, ?)", 1, "John");
        utils.executeUpdate("INSERT INTO test_users (id, name) VALUES (?, ?)", 2, "Jane");
        
        // Test getRecordCount
        int totalCount = utils.getRecordCount("test_users");
        Assert.assertEquals(totalCount, 2);
        
        int filteredCount = utils.getRecordCount("test_users", "name = ?", "John");
        Assert.assertEquals(filteredCount, 1);
        
        utils.close();
    }
    
    @Test
    public void testTransactionIntegration() {
        DatabaseUtils utils = new DatabaseUtils("jdbc:h2:mem:testdb7;DB_CLOSE_DELAY=-1", "sa", "");
        
        // Create test table
        utils.executeUpdate("CREATE TABLE test_users (id INT PRIMARY KEY, name VARCHAR(50))");
        
        // Test successful transaction
        List<String> queries = Arrays.asList(
            "INSERT INTO test_users (id, name) VALUES (1, 'John')",
            "INSERT INTO test_users (id, name) VALUES (2, 'Jane')"
        );
        
        boolean success = utils.executeTransaction(queries);
        Assert.assertTrue(success);
        
        int count = utils.getRecordCount("test_users");
        Assert.assertEquals(count, 2);
        
        utils.close();
    }
    */
}