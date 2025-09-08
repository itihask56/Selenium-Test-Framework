package com.framework.utils;

import com.framework.exceptions.FrameworkException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.testng.annotations.DataProvider;

import java.io.*;
import java.util.*;

/**
 * ExcelUtils provides utilities for reading and writing Excel files
 * Supports both .xlsx and .xls formats with TestNG DataProvider integration
 */
public class ExcelUtils {
    
    private Workbook workbook;
    private Sheet sheet;
    private String filePath;
    
    /**
     * Constructor with file path
     * @param filePath path to Excel file
     */
    public ExcelUtils(String filePath) {
        this.filePath = filePath;
        loadWorkbook();
    }
    
    /**
     * Constructor with file path and sheet name
     * @param filePath path to Excel file
     * @param sheetName name of the sheet to work with
     */
    public ExcelUtils(String filePath, String sheetName) {
        this.filePath = filePath;
        loadWorkbook();
        setSheet(sheetName);
    }
    
    /**
     * Constructor with file path and sheet index
     * @param filePath path to Excel file
     * @param sheetIndex index of the sheet to work with
     */
    public ExcelUtils(String filePath, int sheetIndex) {
        this.filePath = filePath;
        loadWorkbook();
        setSheet(sheetIndex);
    }
    
    /**
     * Loads the workbook from file path
     */
    private void loadWorkbook() {
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            
            if (filePath.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fileInputStream);
            } else if (filePath.endsWith(".xls")) {
                workbook = new HSSFWorkbook(fileInputStream);
            } else {
                throw new FrameworkException("Unsupported file format. Only .xlsx and .xls are supported.");
            }
            
            fileInputStream.close();
        } catch (IOException e) {
            throw new FrameworkException("Failed to load Excel file: " + filePath, e);
        }
    }
    
    /**
     * Sets the active sheet by name
     * @param sheetName name of the sheet
     */
    public void setSheet(String sheetName) {
        sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            throw new FrameworkException("Sheet '" + sheetName + "' not found in workbook");
        }
    }
    
    /**
     * Sets the active sheet by index
     * @param sheetIndex index of the sheet (0-based)
     */
    public void setSheet(int sheetIndex) {
        try {
            sheet = workbook.getSheetAt(sheetIndex);
        } catch (IllegalArgumentException e) {
            throw new FrameworkException("Sheet at index " + sheetIndex + " not found in workbook", e);
        }
    }
    
    /**
     * Gets the number of rows in the current sheet
     * @return number of rows
     */
    public int getRowCount() {
        if (sheet == null) {
            throw new FrameworkException("No sheet is currently selected");
        }
        return sheet.getLastRowNum() + 1;
    }
    
    /**
     * Gets the number of columns in a specific row
     * @param rowIndex row index (0-based)
     * @return number of columns
     */
    public int getColumnCount(int rowIndex) {
        if (sheet == null) {
            throw new FrameworkException("No sheet is currently selected");
        }
        Row row = sheet.getRow(rowIndex);
        return row != null ? row.getLastCellNum() : 0;
    }
    
    /**
     * Reads cell value as String
     * @param rowIndex row index (0-based)
     * @param columnIndex column index (0-based)
     * @return cell value as String
     */
    public String getCellData(int rowIndex, int columnIndex) {
        if (sheet == null) {
            throw new FrameworkException("No sheet is currently selected");
        }
        
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            return "";
        }
        
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            return "";
        }
        
        return getCellValueAsString(cell);
    }
    
    /**
     * Reads cell value as String using column name
     * @param rowIndex row index (0-based)
     * @param columnName column name (header)
     * @return cell value as String
     */
    public String getCellData(int rowIndex, String columnName) {
        int columnIndex = getColumnIndex(columnName);
        return getCellData(rowIndex, columnIndex);
    }
    
    /**
     * Sets cell value
     * @param rowIndex row index (0-based)
     * @param columnIndex column index (0-based)
     * @param value value to set
     */
    public void setCellData(int rowIndex, int columnIndex, String value) {
        if (sheet == null) {
            throw new FrameworkException("No sheet is currently selected");
        }
        
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            cell = row.createCell(columnIndex);
        }
        
        cell.setCellValue(value);
    }
    
    /**
     * Sets cell value using column name
     * @param rowIndex row index (0-based)
     * @param columnName column name (header)
     * @param value value to set
     */
    public void setCellData(int rowIndex, String columnName, String value) {
        int columnIndex = getColumnIndex(columnName);
        setCellData(rowIndex, columnIndex, value);
    }
    
    /**
     * Gets column index by column name (header)
     * @param columnName column name
     * @return column index (0-based)
     */
    public int getColumnIndex(String columnName) {
        if (sheet == null) {
            throw new FrameworkException("No sheet is currently selected");
        }
        
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new FrameworkException("Header row not found");
        }
        
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null && columnName.equals(getCellValueAsString(cell))) {
                return i;
            }
        }
        
        throw new FrameworkException("Column '" + columnName + "' not found in header row");
    }
    
    /**
     * Gets all column names (headers)
     * @return list of column names
     */
    public List<String> getColumnNames() {
        if (sheet == null) {
            throw new FrameworkException("No sheet is currently selected");
        }
        
        List<String> columnNames = new ArrayList<>();
        Row headerRow = sheet.getRow(0);
        
        if (headerRow != null) {
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                columnNames.add(cell != null ? getCellValueAsString(cell) : "");
            }
        }
        
        return columnNames;
    }
    
    /**
     * Reads all data from the sheet as a 2D array
     * @return 2D array of data
     */
    public String[][] getAllData() {
        if (sheet == null) {
            throw new FrameworkException("No sheet is currently selected");
        }
        
        int rowCount = getRowCount();
        if (rowCount == 0) {
            return new String[0][0];
        }
        
        int columnCount = getColumnCount(0);
        String[][] data = new String[rowCount][columnCount];
        
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                data[i][j] = getCellData(i, j);
            }
        }
        
        return data;
    }
    
    /**
     * Reads data excluding header row
     * @return 2D array of data without headers
     */
    public String[][] getDataWithoutHeaders() {
        if (sheet == null) {
            throw new FrameworkException("No sheet is currently selected");
        }
        
        int rowCount = getRowCount();
        if (rowCount <= 1) {
            return new String[0][0];
        }
        
        int columnCount = getColumnCount(0);
        String[][] data = new String[rowCount - 1][columnCount];
        
        for (int i = 1; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                data[i - 1][j] = getCellData(i, j);
            }
        }
        
        return data;
    }
    
    /**
     * Reads data as List of Maps (column name -> value)
     * @return List of Maps representing rows
     */
    public List<Map<String, String>> getDataAsMapList() {
        if (sheet == null) {
            throw new FrameworkException("No sheet is currently selected");
        }
        
        List<Map<String, String>> dataList = new ArrayList<>();
        List<String> columnNames = getColumnNames();
        int rowCount = getRowCount();
        
        for (int i = 1; i < rowCount; i++) { // Skip header row
            Map<String, String> rowData = new HashMap<>();
            for (int j = 0; j < columnNames.size(); j++) {
                String columnName = columnNames.get(j);
                String cellValue = getCellData(i, j);
                rowData.put(columnName, cellValue);
            }
            dataList.add(rowData);
        }
        
        return dataList;
    }
    
    /**
     * Saves the workbook to file
     */
    public void save() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            workbook.write(fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
            throw new FrameworkException("Failed to save Excel file: " + filePath, e);
        }
    }
    
    /**
     * Saves the workbook to a new file
     * @param newFilePath path for the new file
     */
    public void saveAs(String newFilePath) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(newFilePath);
            workbook.write(fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
            throw new FrameworkException("Failed to save Excel file: " + newFilePath, e);
        }
    }
    
    /**
     * Closes the workbook and releases resources
     */
    public void close() {
        try {
            if (workbook != null) {
                workbook.close();
            }
        } catch (IOException e) {
            throw new FrameworkException("Failed to close Excel workbook", e);
        }
    }
    
    /**
     * Creates a new sheet in the workbook
     * @param sheetName name of the new sheet
     */
    public void createSheet(String sheetName) {
        if (workbook.getSheet(sheetName) != null) {
            throw new FrameworkException("Sheet '" + sheetName + "' already exists");
        }
        sheet = workbook.createSheet(sheetName);
    }
    
    /**
     * Deletes a sheet from the workbook
     * @param sheetName name of the sheet to delete
     */
    public void deleteSheet(String sheetName) {
        int sheetIndex = workbook.getSheetIndex(sheetName);
        if (sheetIndex == -1) {
            throw new FrameworkException("Sheet '" + sheetName + "' not found");
        }
        workbook.removeSheetAt(sheetIndex);
    }
    
    /**
     * Gets all sheet names in the workbook
     * @return list of sheet names
     */
    public List<String> getSheetNames() {
        List<String> sheetNames = new ArrayList<>();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            sheetNames.add(workbook.getSheetName(i));
        }
        return sheetNames;
    }
    
    /**
     * Converts cell value to String regardless of cell type
     * @param cell the cell to read
     * @return cell value as String
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // Format numeric values to avoid scientific notation
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == Math.floor(numericValue)) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return String.valueOf(cell.getNumericCellValue());
                } catch (Exception e) {
                    return cell.getStringCellValue();
                }
            case BLANK:
                return "";
            default:
                return "";
        }
    }
    
    /**
     * Static method to create TestNG DataProvider from Excel file
     * @param filePath path to Excel file
     * @param sheetName name of the sheet
     * @return Object[][] for TestNG DataProvider
     */
    public static Object[][] getTestData(String filePath, String sheetName) {
        ExcelUtils excelUtils = new ExcelUtils(filePath, sheetName);
        String[][] data = excelUtils.getDataWithoutHeaders();
        excelUtils.close();
        
        // Convert String[][] to Object[][]
        Object[][] testData = new Object[data.length][];
        for (int i = 0; i < data.length; i++) {
            testData[i] = data[i];
        }
        
        return testData;
    }
    
    /**
     * Static method to create TestNG DataProvider from Excel file with Map data
     * @param filePath path to Excel file
     * @param sheetName name of the sheet
     * @return Object[][] containing Maps for TestNG DataProvider
     */
    public static Object[][] getTestDataAsMap(String filePath, String sheetName) {
        ExcelUtils excelUtils = new ExcelUtils(filePath, sheetName);
        List<Map<String, String>> dataList = excelUtils.getDataAsMapList();
        excelUtils.close();
        
        Object[][] testData = new Object[dataList.size()][];
        for (int i = 0; i < dataList.size(); i++) {
            testData[i] = new Object[]{dataList.get(i)};
        }
        
        return testData;
    }
    
    /**
     * Static method to check if Excel file exists
     * @param filePath path to Excel file
     * @return true if file exists, false otherwise
     */
    public static boolean isFileExists(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }
    
    /**
     * Static method to read CSV file data for TestNG DataProvider
     * @param filePath path to CSV file
     * @return Object[][] for TestNG DataProvider
     */
    public static Object[][] getCSVData(String filePath) {
        List<String[]> dataList = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header row
                }
                
                // Split by comma, handling quoted values
                String[] values = parseCSVLine(line);
                dataList.add(values);
            }
            
        } catch (IOException e) {
            throw new FrameworkException("Failed to read CSV file: " + filePath, e);
        }
        
        // Convert List<String[]> to Object[][]
        Object[][] testData = new Object[dataList.size()][];
        for (int i = 0; i < dataList.size(); i++) {
            testData[i] = dataList.get(i);
        }
        
        return testData;
    }
    
    /**
     * Parses a CSV line handling quoted values and commas within quotes
     * @param line CSV line to parse
     * @return array of values
     */
    private static String[] parseCSVLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder currentValue = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(currentValue.toString().trim());
                currentValue = new StringBuilder();
            } else {
                currentValue.append(c);
            }
        }
        
        // Add the last value
        values.add(currentValue.toString().trim());
        
        return values.toArray(new String[0]);
    }
    
    /**
     * Static method to create a new Excel file
     * @param filePath path for the new Excel file
     * @param sheetName name of the first sheet
     */
    public static void createExcelFile(String filePath, String sheetName) {
        try {
            Workbook workbook;
            if (filePath.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook();
            } else if (filePath.endsWith(".xls")) {
                workbook = new HSSFWorkbook();
            } else {
                throw new FrameworkException("Unsupported file format. Only .xlsx and .xls are supported.");
            }
            
            workbook.createSheet(sheetName);
            
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            workbook.write(fileOutputStream);
            fileOutputStream.close();
            workbook.close();
            
        } catch (IOException e) {
            throw new FrameworkException("Failed to create Excel file: " + filePath, e);
        }
    }
}