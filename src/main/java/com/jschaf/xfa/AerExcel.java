package com.jschaf.xfa;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFName;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * Define interface between AER Excel file and the AER hierarchy.
 */
public class AerExcel {

    public XSSFWorkbook workbook;

    public ImmutableTable<Integer, String, String> getDataTable() {
        if (dataTable == null) {
            parseDataTable();
        }
        return dataTable;
    }

    public ImmutableMap<String, String> getVariables() {
        if (variables == null) {
            parseVariables();
        }
        return variables;
    }

    public ImmutableMap<String, String> getTranslation() {
        if (translation == null) {
            parseTranslation();
        }
        return translation;
    }

    protected ImmutableTable<Integer, String, String> dataTable;
    protected ImmutableMap<String, String> variables;
    protected ImmutableMap<String, String> translation;


    public AerExcel(String path) {
        try {
            workbook = new XSSFWorkbook(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public AerExcel(InputStream excelFile) {
        try {
            workbook = new XSSFWorkbook(excelFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Optional<CellReference[]> getTableCells(String name) {

        XSSFName name1 = workbook.getName(name);
        if (name1 == null) {
            System.out.println("NAME NOT FOUND");
        } else {
            System.out.println("the name is " + name1.getNameName());

        }

        for (int i = 0; i < 6; i++) {
            XSSFName nameAt = workbook.getNameAt(i);
            if (nameAt == null) {
                break;
            }
            System.out.println(nameAt.getNameName());
        }

        int namedCellIdx = workbook.getNameIndex(name);
        if (namedCellIdx == -1) {
            System.out.println("IT DOESN'T EXIST");
            return Optional.empty();
        }
        Name namedCell = workbook.getNameAt(namedCellIdx);
        AreaReference areaReference = new AreaReference(namedCell.getRefersToFormula(), SpreadsheetVersion.EXCEL2007);
        System.out.println(areaReference.formatAsString());
        return Optional.of(areaReference.getAllReferencedCells());
    }

    public void printCellReference(CellReference cellRef) {

        Sheet sheet = workbook.getSheet(cellRef.getSheetName());
        Row row = sheet.getRow(cellRef.getRow());
        Cell cell = row.getCell(cellRef.getCol());

        System.out.print(cellRef.formatAsString());
        System.out.print(" - ");

        printCell(cell);
    }

    public void printCell(Cell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                System.out.print(cell.getRichStringCellValue().getString());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    System.out.println(cell.getDateCellValue());
                } else {
                    System.out.print(cell.getNumericCellValue());
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                System.out.print(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA:
                System.out.print(cell.getCellFormula());
                break;
            default:
        }
    }

    private ImmutableMap<Integer, String> parseDataHeaderRow() {
        Sheet sheet = workbook.getSheet("Data");
        return parseHeaderRow(sheet.getRow(2));
    }

    private ImmutableMap<Integer, String> parseHeaderRow(Row row) {
        ImmutableMap.Builder<Integer, String> headerMap = ImmutableMap.builder();
        for (Cell cell : row) {
            headerMap.put(cell.getColumnIndex(), cell.getStringCellValue());
        }
        return headerMap.build();
    }


    protected ImmutableTable<Integer, String, String> parseDataTable() {
        ImmutableTable.Builder<Integer, String, String> table = ImmutableTable.builder();
        Sheet sheet = workbook.getSheet("Data");
        DataFormatter formatter = new DataFormatter();
        XSSFFormulaEvaluator formulator = new XSSFFormulaEvaluator(workbook);
        ImmutableMap<Integer, String> headerMap = parseDataHeaderRow();
        for (Row row : sheet) {
            // Skip Header
            if (row.getRowNum() < 3) {
                continue;
            }

            for (Cell cell : row) {
                String cellValue = formatter.formatCellValue(cell, formulator);
                table.put(cell.getRowIndex(), headerMap.get(cell.getColumnIndex()), cellValue);
            }
        }
        dataTable = table.build();
        return dataTable;
    }

    public void printDataTable() {
        dataTable.cellSet().forEach(c -> {
            System.out.println(c.getRowKey() + " " + c.getColumnKey() + " " + c.getValue());
        });
    }

    private ImmutableMap<String, String> parseTable(String sheetName) {
        ImmutableMap.Builder<String, String> variables = new ImmutableMap.Builder<>();
        Sheet sheet = workbook.getSheet(sheetName);

        DataFormatter formatter = new DataFormatter();
        XSSFFormulaEvaluator formulator = new XSSFFormulaEvaluator(workbook);

        String key = "";
        String value = "";
        for (Row row : sheet) {
            // Skip Header
            if (row.getRowNum() < 3) { continue; }

            for (Cell cell : row) {

                // TODO: Handle empty keys
                switch (cell.getColumnIndex()) {
                    case 0:
                        key = formatter.formatCellValue(cell, formulator);
                        break;
                    case 1:
                        value = formatter.formatCellValue(cell, formulator);
                        variables.put(key, value);
                        key = "";
                        value = "";
                        break;
                    default:
                }
            }
        }
        return variables.build();
    }

    public void parseVariables() {
        variables = parseTable("Variables");
    }

    public void parseTranslation() {
        translation = parseTable("Form Translation");
    }

    public Optional<String> getSingleNamedStringCell(String name) {
//        Optional<CellReference[]> cellReferences = getTableCells(name);
//        cellReferences.map(cells -> Stream.of(cells).forEach(cellReference ->
//        {
//            XSSFSheet sheet = workbook.getSheet(cellReference.getSheetName());
//            Row row = sheet.getRow(cellReference.getRow());
//            Cell cell = row.getCell(cellReference.getCol());
//            switch (cell.getCellType()) {
//                case Cell.CELL_TYPE_STRING:
//                    return Optional.of(cell.getStringCellValue());
//            }
//        })
        return Optional.empty();
    }
}
