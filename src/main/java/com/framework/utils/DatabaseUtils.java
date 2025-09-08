package com.framework.utils;

import com.framework.config.ConfigManager;
import com.framework.exceptions.FrameworkException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.*;

/**
 * DatabaseUtils provides JDBC connection management utilities
 * Includes query execution, result processing, and connection pooling
 */
public class DatabaseUtils {
    
    private static DatabaseUtils instance;
    private static final Object lock = new Object();
    
    private HikariDataSource dataSource;
    private final String dbUrl;
    private final String dbUsername;
    private final String dbPassword;
    
    /**
     * Private constructor for singleton pattern
     */
    private DatabaseUtils() {
        ConfigManager configManager = ConfigManager.getInstance();
        this.dbUrl = configManager.getTestConfig().getDbUrl();
        this.dbUsername = configManager.getTestConfig().getDbUsername();
        this.dbPassword = configManager.getTestConfig().getDbPassword();
        
        initializeConnectionPool();
    }
    
    /**
     * Constructor with custom database configuration
     * @param dbUrl database URL
     * @param dbUsername database username
     * @param dbPassword database password
     */
    public DatabaseUtils(String dbUrl, String dbUsername, String dbPassword) {
        this.dbUrl = dbUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        
        initializeConnectionPool();
    }
    
    /**
     * Gets the singleton instance of DatabaseUtils
     * @return DatabaseUtils instance
     */
    public static DatabaseUtils getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new DatabaseUtils();
                }
            }
        }
        return instance;
    }
    
    /**
     * Initializes HikariCP connection pool
     */
    private void initializeConnectionPool() {
        if (dbUrl == null || dbUrl.isEmpty()) {
            throw new FrameworkException("Database URL is not configured");
        }
        
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dbUrl);
            config.setUsername(dbUsername);
            config.setPassword(dbPassword);
            
            // Connection pool settings
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000); // 30 seconds
            config.setIdleTimeout(600000); // 10 minutes
            config.setMaxLifetime(1800000); // 30 minutes
            config.setLeakDetectionThreshold(60000); // 1 minute
            
            // Connection validation
            config.setConnectionTestQuery("SELECT 1");
            config.setValidationTimeout(5000);
            
            // Performance settings
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            
            this.dataSource = new HikariDataSource(config);
            
        } catch (Exception e) {
            throw new FrameworkException("Failed to initialize database connection pool", e);
        }
    }
    
    /**
     * Gets a connection from the pool
     * @return Connection object
     * @throws SQLException if connection cannot be obtained
     */
    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new FrameworkException("Database connection pool is not initialized");
        }
        return dataSource.getConnection();
    }
    
    /**
     * Executes a SELECT query and returns results as List of Maps
     * @param query SQL SELECT query
     * @return List of Maps representing rows
     */
    public List<Map<String, Object>> executeQuery(String query) {
        return executeQuery(query, new Object[]{});
    }
    
    /**
     * Executes a parameterized SELECT query and returns results as List of Maps
     * @param query SQL SELECT query with placeholders
     * @param parameters query parameters
     * @return List of Maps representing rows
     */
    public List<Map<String, Object>> executeQuery(String query, Object... parameters) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            // Set parameters
            setParameters(statement, parameters);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                
                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        Object value = resultSet.getObject(i);
                        row.put(columnName, value);
                    }
                    results.add(row);
                }
            }
            
        } catch (SQLException e) {
            throw new FrameworkException("Failed to execute query: " + query, e);
        }
        
        return results;
    }
    
    /**
     * Executes an INSERT, UPDATE, or DELETE query
     * @param query SQL query
     * @return number of affected rows
     */
    public int executeUpdate(String query) {
        return executeUpdate(query, new Object[]{});
    }
    
    /**
     * Executes a parameterized INSERT, UPDATE, or DELETE query
     * @param query SQL query with placeholders
     * @param parameters query parameters
     * @return number of affected rows
     */
    public int executeUpdate(String query, Object... parameters) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            // Set parameters
            setParameters(statement, parameters);
            
            return statement.executeUpdate();
            
        } catch (SQLException e) {
            throw new FrameworkException("Failed to execute update: " + query, e);
        }
    }
    
    /**
     * Executes an INSERT query and returns generated keys
     * @param query SQL INSERT query
     * @param parameters query parameters
     * @return List of generated keys
     */
    public List<Object> executeInsertWithGeneratedKeys(String query, Object... parameters) {
        List<Object> generatedKeys = new ArrayList<>();
        
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            // Set parameters
            setParameters(statement, parameters);
            
            statement.executeUpdate();
            
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                while (resultSet.next()) {
                    generatedKeys.add(resultSet.getObject(1));
                }
            }
            
        } catch (SQLException e) {
            throw new FrameworkException("Failed to execute insert with generated keys: " + query, e);
        }
        
        return generatedKeys;
    }
    
    /**
     * Executes multiple queries in a transaction
     * @param queries List of SQL queries
     * @return true if all queries executed successfully
     */
    public boolean executeTransaction(List<String> queries) {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            
            for (String query : queries) {
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.executeUpdate();
                }
            }
            
            connection.commit();
            return true;
            
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    throw new FrameworkException("Failed to rollback transaction", rollbackException);
                }
            }
            throw new FrameworkException("Transaction failed", e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    // Log error but don't throw
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Executes multiple parameterized queries in a transaction
     * @param queriesWithParams List of QueryWithParams objects
     * @return true if all queries executed successfully
     */
    public boolean executeTransactionWithParams(List<QueryWithParams> queriesWithParams) {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            
            for (QueryWithParams queryWithParams : queriesWithParams) {
                try (PreparedStatement statement = connection.prepareStatement(queryWithParams.getQuery())) {
                    setParameters(statement, queryWithParams.getParameters());
                    statement.executeUpdate();
                }
            }
            
            connection.commit();
            return true;
            
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    throw new FrameworkException("Failed to rollback transaction", rollbackException);
                }
            }
            throw new FrameworkException("Transaction failed", e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Gets a single value from query result
     * @param query SQL query
     * @param parameters query parameters
     * @return single value or null if no result
     */
    public Object getSingleValue(String query, Object... parameters) {
        List<Map<String, Object>> results = executeQuery(query, parameters);
        
        if (results.isEmpty()) {
            return null;
        }
        
        Map<String, Object> firstRow = results.get(0);
        if (firstRow.isEmpty()) {
            return null;
        }
        
        return firstRow.values().iterator().next();
    }
    
    /**
     * Gets a single row from query result
     * @param query SQL query
     * @param parameters query parameters
     * @return Map representing the row or null if no result
     */
    public Map<String, Object> getSingleRow(String query, Object... parameters) {
        List<Map<String, Object>> results = executeQuery(query, parameters);
        return results.isEmpty() ? null : results.get(0);
    }
    
    /**
     * Checks if a record exists
     * @param query SQL query (should return count or boolean)
     * @param parameters query parameters
     * @return true if record exists
     */
    public boolean recordExists(String query, Object... parameters) {
        Object result = getSingleValue(query, parameters);
        
        if (result instanceof Number) {
            return ((Number) result).intValue() > 0;
        } else if (result instanceof Boolean) {
            return (Boolean) result;
        }
        
        return result != null;
    }
    
    /**
     * Gets count of records
     * @param tableName table name
     * @param whereClause WHERE clause (without WHERE keyword)
     * @param parameters parameters for WHERE clause
     * @return count of records
     */
    public int getRecordCount(String tableName, String whereClause, Object... parameters) {
        String query = "SELECT COUNT(*) FROM " + tableName;
        if (whereClause != null && !whereClause.trim().isEmpty()) {
            query += " WHERE " + whereClause;
        }
        
        Object result = getSingleValue(query, parameters);
        return result instanceof Number ? ((Number) result).intValue() : 0;
    }
    
    /**
     * Gets count of all records in a table
     * @param tableName table name
     * @return count of records
     */
    public int getRecordCount(String tableName) {
        return getRecordCount(tableName, null);
    }
    
    /**
     * Executes a stored procedure
     * @param procedureName stored procedure name
     * @param parameters procedure parameters
     * @return List of Maps representing results
     */
    public List<Map<String, Object>> executeStoredProcedure(String procedureName, Object... parameters) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        StringBuilder callQuery = new StringBuilder("{call ").append(procedureName).append("(");
        for (int i = 0; i < parameters.length; i++) {
            if (i > 0) callQuery.append(",");
            callQuery.append("?");
        }
        callQuery.append(")}");
        
        try (Connection connection = getConnection();
             CallableStatement statement = connection.prepareCall(callQuery.toString())) {
            
            // Set parameters
            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i + 1, parameters[i]);
            }
            
            boolean hasResultSet = statement.execute();
            
            if (hasResultSet) {
                try (ResultSet resultSet = statement.getResultSet()) {
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    
                    while (resultSet.next()) {
                        Map<String, Object> row = new HashMap<>();
                        for (int i = 1; i <= columnCount; i++) {
                            String columnName = metaData.getColumnName(i);
                            Object value = resultSet.getObject(i);
                            row.put(columnName, value);
                        }
                        results.add(row);
                    }
                }
            }
            
        } catch (SQLException e) {
            throw new FrameworkException("Failed to execute stored procedure: " + procedureName, e);
        }
        
        return results;
    }
    
    /**
     * Sets parameters for PreparedStatement
     * @param statement PreparedStatement
     * @param parameters parameters to set
     * @throws SQLException if parameter setting fails
     */
    private void setParameters(PreparedStatement statement, Object... parameters) throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            statement.setObject(i + 1, parameters[i]);
        }
    }
    
    /**
     * Tests database connection
     * @return true if connection is successful
     */
    public boolean testConnection() {
        try (Connection connection = getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Gets database metadata information
     * @return Map containing database information
     */
    public Map<String, String> getDatabaseInfo() {
        Map<String, String> info = new HashMap<>();
        
        try (Connection connection = getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            info.put("databaseProductName", metaData.getDatabaseProductName());
            info.put("databaseProductVersion", metaData.getDatabaseProductVersion());
            info.put("driverName", metaData.getDriverName());
            info.put("driverVersion", metaData.getDriverVersion());
            info.put("url", metaData.getURL());
            info.put("userName", metaData.getUserName());
            
        } catch (SQLException e) {
            throw new FrameworkException("Failed to get database metadata", e);
        }
        
        return info;
    }
    
    /**
     * Closes the connection pool and releases resources
     */
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
    
    /**
     * Gets connection pool statistics
     * @return Map containing pool statistics
     */
    public Map<String, Object> getPoolStats() {
        Map<String, Object> stats = new HashMap<>();
        
        if (dataSource != null) {
            stats.put("activeConnections", dataSource.getHikariPoolMXBean().getActiveConnections());
            stats.put("idleConnections", dataSource.getHikariPoolMXBean().getIdleConnections());
            stats.put("totalConnections", dataSource.getHikariPoolMXBean().getTotalConnections());
            stats.put("threadsAwaitingConnection", dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection());
        }
        
        return stats;
    }
    
    /**
     * Inner class to hold query with parameters for transactions
     */
    public static class QueryWithParams {
        private final String query;
        private final Object[] parameters;
        
        public QueryWithParams(String query, Object... parameters) {
            this.query = query;
            this.parameters = parameters;
        }
        
        public String getQuery() {
            return query;
        }
        
        public Object[] getParameters() {
            return parameters;
        }
    }
}