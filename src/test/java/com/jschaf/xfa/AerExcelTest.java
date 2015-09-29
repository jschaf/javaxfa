package com.jschaf.xfa;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.InputStream;

import static org.testng.Assert.*;

/**
 *
 */
public class AerExcelTest {

    private AerExcel excel;

    @BeforeClass
    public void setUp() {
        InputStream aerData = this.getClass().getResourceAsStream("/AERSampleData.xlsx");
        excel = new AerExcel(aerData);
    }

    @Test
    public void testParseVariables() throws Exception {
        ImmutableMap<String, String> expected = ImmutableMap.of(
                "Boom", "Boom Value - {{[Last Name]}}",
                "Explosion", "Explosion Value",
                "Right", "1",
                "Left", "4",
                "Date", "2015-09-20");
        excel.parseVariables();
        assertEquals(expected, excel.variables);
    }

    @Test
    public void testParseDataTable() throws Exception {
        excel.parseDataTable();
        ImmutableTable<Integer, String, String> expected = ImmutableTable.<Integer, String, String>builder()
                .put(3, "Last Name", "SMITH")
                .put(3, "First Name", "JOHN")
                .put(3, "Middle Initial", "A.")
                .put(3, "SSN", "123-41-1234")
                .put(3, "Rank", "2LT")
                .put(3, "Branch", "1")
                .put(4, "Last Name", "HENRY")
                .put(4, "First Name", "FORD")
                .put(4, "Middle Initial", "B.")
                .put(4, "SSN", "123-47-8901")
                .put(4, "Rank", "2LT")
                .put(4, "Branch", "2")
                .build();
        assertEquals(expected, excel.dataTable);
    }
}