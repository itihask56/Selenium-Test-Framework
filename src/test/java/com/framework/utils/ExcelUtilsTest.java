package com.framework.utils;

import com.framework.exceptions.FrameworkException;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Unit tests for ExcelUtils class
 */
public class ExcelUtilsTest {
    
    private String testFilePath;
    private String testFilePathXls;
    private ExcelUtils excelUtils;
    
    @BeforeMethod
    public void setUp() throws IOException {
        // Create temporary test files
        Path tempDir = Files.createTempDirectory("excel-test");
        testFilePath = tempDir.resolve("test.xlsx").toString();
        testFilePathXls = tempDir.resolve("test.xls").toString();
        
        // Create test Excel file with sample data
        createTestExcelFile();
    }
    
    @AfterMethod
    public void tearDown() {
        if (excelUtils != null) {
            excelUtils.close();
        }
        
        // Clean up test files
        deleteFile(testFilePath);
        deleteFile(testFilePathXls);
    }
    
    private void createTestExcelFile() {
        ExcelUtils.createExcelFile(testFilePath, "TestSheet");
        excelUtils = new ExcelUtils(testFilePath, "TestSheet");
        
        // Add headers
        excelUtils.setCellData(0, 0, "Name");
        excelUtils.setCellData(0, 1, "Age");
        excelUtils.setCellData(0, 2, "Email");
        
        // Add test data
        excelUtils.setCellData(1, 0, "John Doe");
        excelUtils.setCellData(1, 1, "30");
        excelUtils.setCellData(1, 2, "john@example.com");
        
        excelUtils.setCellData(2, 0, "Jane Smith");
        excelUtils.setCellData(2, 1, "25");
        excelUtils.setCellData(2, 2, "jane@example.com");
        
        excelUtils.save();
        excelUtils.close();
    }
    
    private void deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }
    
    @Test
    public void testConstructorWithFilePath() {
        excelUtils = new ExcelUtils(testFilePath);
        Assert.assertNotNull(excelUtils);
    }
    
    @Test
    public void testConstructorWithFilePathAndSheetName() {
        excelUtils = new ExcelUtils(testFilePath, "TestSheet");
        Assert.assertNotNull(excelUtils);
    }
    
    @Test
    public void testConstructorWithFilePathAndSheetIndex() {
        excelUtils = new ExcelUtils(testFilePath, 0);
        Assert.assertNotNull(excelUtils);
    }
    
    @Test(expectedExceptions = FrameworkException.class)
    public void testConstructorWithInvalidFilePath() {
        excelUtils = new ExcelUtils("invalid/path/file.xlsx");
    }
    
    @Test(expectedExceptions = FrameworkException.class)
    public void testConstructorWithUnsupportedFileFormat() {
        excelUtils = new ExcelUtils("test.txt");
    }
    
    @Test
    public void testSetSheetByName() {
        excelUtils = new ExcelUtils(testFilePath);
        excelUtils.setSheet("TestSheet");
        // Should not throw exception
    }
    
    @Test(expectedExceptions = FrameworkException.class)
    public void testSetSheetByInvalidName() {
        excelUtils = new ExcelUtils(testFilePath);
        excelUtils.setSheet("InvalidSheet");
    }
    
    @Test
    public void testSetSheetByIndex() {
        excelUtils = new ExcelUtils(testFilePath);
        excelUtils.setSheet(0);
        // Should not throw exception
    }
    
    @Test(expectedExceptions = FrameworkException.class)
    public void testSetSheetByInvalidIndex() {
        excelUtils = new ExcelUtils(testFilePath);
        excelUtils.setSheet(10);
    }
    
    @Test
    public void testGetRowCount() {
        excelUtils = new ExcelUtils(testFilePath, "TestSheet");
        int rowCount = excelUtils.getRowCount();
        Assert.assertEquals(rowCount, 3); // Header + 2 data rows
    }
    
    @Test
    public void testGetColumnCount() {
        excelUtils = new ExcelUtils(testFilePath, "TestSheet");
        int columnCount = excelUtils.getColumnCount(0);
        Assert.assertEquals(columnCount, 3); // Name, Age, Email
    }
    
    @Test
    public void testGetCellData() {
        excelUtils = new ExcelUtils(testFilePath, "TestSheet");
        String cellData = excelUtils.getCellData(1, 0);
        Assert.assertEquals(cellData, "John Doe");
    }
    
    @Test
    public void testGetCellDataByColumnName() {
        excelUtils = new ExcelUtils(testFilePath, "TestSheet");
        String cellData = excelUtils.getCellData(1, "Name");
        Assert.assertEquals(cellData, "John Doe");
    }
    
    @Test
    public void testSetCellData() {
        excelUtils = new ExcelUtils(testFilePath, "TestSheet");
        excelUtils.setCellData(3, 0, "Bob Johnson");
        String cellData = excelUtils.getCellData(3, 0);
        Assert.assertEquals(cellData, "Bob Johnson");
    }
    
    @Test
    public void testSetCellDataByColumnName() {
        excelUtils = new ExcelUtils(testFilePath, "TestSheet");
        excelUtils.setCellData(3, "Name", "Bob Johnson");
        String cellData = excelUtils.getCellData(3, "Name");
        Assert.assertEquals(cellData, "Bob Johnson");
    }
    
    @Test
    public void testGetColumnIndex() {
        excelUtils = new ExcelUtils(testFilePath, "TestSheet");
        int columnIndex = excelUtils.getColumnIndex("Age");
        Assert.assertEquals(columnIndex, 1);
    }
    
    @Test(expectedExceptions = FrameworkException.class)
    public void testGetColumnIndexInvalidColumn() {
        excelUtils = new ExcelUtils(testFilePath, "TestSheet");
        excelUtils.getColumnIndex("InvalidColumn");
    }
    
    @Test
    public void testGetColumnNames() {
        excelUtils = new ExcelUtils(testFilePath, "TestSheet");
        List<String> columnNames = excelUtils.getColumnNames();
        Assert.assertEquals(columnNames.size(), 3);
        Assert.assertEquals(columnNames.get(0), "Name");
        Assert.assertEquals(columnNames.get(1), "Age");
        Assert.assertEquals(columnNames.get(2), "Email");
    }
    
    @Test
    public void testGetAllData() {
        excelUtils = new ExcelUtils(testFilePath, "TestSheet");
        String[][] allData = excelUtils.getAllData();
        Assert.assertEquals(allData.length, 3); // Header + 2 data rows
        Assert.assertEquals(allData[0][0], "Name"); // Header
        Assert.assertEquals(allData[1][0], "John Doe"); // First data row
    }
    
    @Test
    public void testGetDataWithoutHeaders() {
        excelUtils = new ExcelUtils(testFilePath, "TestSheet");
        String[][] data = excelUtils.getDataWithoutHeaders();
        Assert.assertEquals(data.length, 2); // Only data rows
        Assert.assertEquals(data[0][0], "John Doe"); // First data row
        Assert.assertEquals(data[1][0], "Jane Smith"); // Second data row
    }
    
    @Test
    public void testGetDataAsMapList() {
        excelUtils = new ExcelUtils(testFilePath, "TestSheet");
        List<Map<String, String>> dataList = excelUtils.getDataAsMapList();
        Assert.assertEquals(dataList.size(), 2);
        
        Map<String, String> firstRow = dataList.get(0);
        Assert.assertEquals(firstRow.get("Name"), "John Doe");
        Assert.assertEquals(firstRow.get("Age"), "30");
        Assert.assertEquals(firstRow.get("Email"), "john@example.com");
    }
    
    @Test
    public void testSave() {
        excelUtils = new ExcelUtils(testFilePath, "TestSheet");
        excelUtils.setCellData(3, 0, "Test Save");
        excelUtils.save();
        
        // Verify by reading again
        excelUtils.close();
        excelUtils = new ExcelUtils(testFilePath, "TestSheet");
        String cellData = excelUtils.getCellData(3, 0);
        Assert.assertEquals(cellData, "Test Save");
    }
    
    @Test
    public void testSaveAs() throws IOException {
        Path tempDir = Files.createTempDirectory("excel-test");
        String newFilePath = tempDir.resolve("test_copy.xlsx").toString();
        
        try {
            excelUtils = new ExcelUtils(testFilePath, "TestSheet");
            excelUtils.saveAs(newFilePath);
            
            // Verify new file exists and has same data
            Assert.assertTrue(ExcelUtils.isFileExists(newFilePath));
            
            ExcelUtils newExcelUtils = new ExcelUtils(newFilePath, "TestSheet");
            String cellData = newExcelUtils.getCellData(1, 0);
            Assert.assertEquals(cellData, "John Doe");
            newExcelUtils.close();
            
        } finally {
            deleteFile(newFilePath);
        }
    }
    
    @Test
    public void testCreateSheet() {
        excelUtils = new ExcelUtils(testFilePath);
        excelUtils.createSheet("NewSheet");
        
        List<String> sheetNames = excelUtils.getSheetNames();
        Assert.assertTrue(sheetNames.contains("NewSheet"));
    }
    
    @Test(expectedExceptions = FrameworkException.class)
    public void testCreateSheetAlreadyExists() {
        excelUtils = new ExcelUtils(testFilePath);
        excelUtils.createSheet("TestSheet"); // Already exists
    }
    
    @Test
    public void testDeleteSheet() {
        excelUtils = new ExcelUtils(testFilePath);
        excelUtils.createSheet("ToDelete");
        excelUtils.deleteSheet("ToDelete");
        
        List<String> sheetNames = excelUtils.getSheetNames();
        Assert.assertFalse(sheetNames.contains("ToDelete"));
    }
    
    @Test(expectedExceptions = FrameworkException.class)
    public void testDeleteSheetNotExists() {
        excelUtils = new ExcelUtils(testFilePath);
        excelUtils.deleteSheet("NonExistentSheet");
    }
    
    @Test
    public void testGetSheetNames() {
        excelUtils = new ExcelUtils(testFilePath);
        List<String> sheetNames = excelUtils.getSheetNames();
        Assert.assertTrue(sheetNames.contains("TestSheet"));
    }
    
    @Test
    public void testGetTestDataStatic() {
        Object[][] testData = ExcelUtils.getTestData(testFilePath, "TestSheet");
        Assert.assertEquals(testData.length, 2); // 2 data rows
        Assert.assertEquals(testData[0][0], "John Doe");
        Assert.assertEquals(testData[1][0], "Jane Smith");
    }
    
    @Test
    public void testGetTestDataAsMapStatic() {
        Object[][] testData = ExcelUtils.getTestDataAsMap(testFilePath, "TestSheet");
        Assert.assertEquals(testData.length, 2);
        
        @SuppressWarnings("unchecked")
        Map<String, String> firstRow = (Map<String, String>) testData[0][0];
        Assert.assertEquals(firstRow.get("Name"), "John Doe");
    }
    
    @Test
    public void testIsFileExists() {
        Assert.assertTrue(ExcelUtils.isFileExists(testFilePath));
        Assert.assertFalse(ExcelUtils.isFileExists("non-existent-file.xlsx"));
    }
    
    @Test
    public void testCreateExcelFileStatic() throws IOException {
        Path tempDir = Files.createTempDirectory("excel-test");
        String newFilePath = tempDir.resolve("new_file.xlsx").toString();
        
        try {
            ExcelUtils.createExcelFile(newFilePath, "Sheet1");
            Assert.assertTrue(ExcelUtils.isFileExists(newFilePath));
            
            // Verify sheet was created
            ExcelUtils newExcelUtils = new ExcelUtils(newFilePath);
            List<String> sheetNames = newExcelUtils.getSheetNames();
            Assert.assertTrue(sheetNames.contains("Sheet1"));
            newExcelUtils.close();
            
        } finally {
            deleteFile(newFilePath);
        }
    }
    
    @Test
    public void testCreateExcelFileXlsFormat() throws IOException {
        Path tempDir = Files.createTempDirectory("excel-test");
        String newFilePath = tempDir.resolve("new_file.xls").toString();
        
        try {
            ExcelUtils.createExcelFile(newFilePath, "Sheet1");
            Assert.assertTrue(ExcelUtils.isFileExists(newFilePath));
            
        } finally {
            deleteFile(newFilePath);
        }
    }
    
    @Test(expectedExceptions = FrameworkException.class)
    public void testCreateExcelFileUnsupportedFormat() {
        ExcelUtils.createExcelFile("test.txt", "Sheet1");
    }
    
    @Test
    public void testGetCellDataEmptyCell() {
        excelUtils = new ExcelUtils(testFilePath, "TestSheet");
        String cellData = excelUtils.getCellData(10, 10); // Non-existent cell
        Assert.assertEquals(cellData, "");
    }
    
    @Test
    public void testNumericCellValue() {
        excelUtils = new ExcelUtils(testFilePath, "TestSheet");
        String ageValue = excelUtils.getCellData(1, 1); // Age column
        Assert.assertEquals(ageValue, "30");
    }
}