package com.jschaf.xfa;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

/**
 *
 */
public class TemplateExcelTest {

    public static <K, V> void printMap(Map<K, V> map) {
        Joiner.MapJoiner mapJoiner = Joiner.on(", ").withKeyValueSeparator("=");
        System.out.println(mapJoiner.join(map));
    }

    @Test
    public void testExcelTranslations() throws Exception {

        InputStream aerData = this.getClass().getResourceAsStream("/template.xlsx");
        TemplateExcel templateExcel = new TemplateExcel(new AerExcel(aerData));
        List<FilledTemplate> actual = templateExcel.getTranslations();

        List<FilledTemplate> expected = ImmutableList.of(
                Template.builder()
                        .withEntry("TranslateBlam", "Boom Value - SMITH")
                        .withEntry("Translated_Name", "SMITH, JOHN")
                        .build()
                        .execute(ImmutableMap.of(
                                "Boom", "Boom Value - SMITH",
                                "Right", "Boom Value - SMITH 2",
                                "Last Name", "SMITH",
                                "First Name", "JOHN")),
                Template.builder()
                        .withEntry("TranslateBlam", "Boom Value - HENRY")
                        .withEntry("Translated_Name", "HENRY, FORD")
                        .build()
                        .execute(ImmutableMap.of(
                                "Boom", "Boom Value - HENRY",
                                "Right", "Boom Value - HENRY 2",
                                "Last Name", "HENRY",
                                "First Name", "FORD")));
        assertEquals(actual, expected);
    }

    @Test
    public void testExcelVariables() throws Exception {

        InputStream aerData = this.getClass().getResourceAsStream("/template.xlsx");
        AerExcel excel = new AerExcel(aerData);

        TemplateExcel templateExcel = new TemplateExcel(excel);
        List<FilledTemplate> actual = templateExcel.getVariables();
        List<FilledTemplate> expected = ImmutableList.of(
                Template.builder()
                        .withEntry("Boom", "Boom Value - SMITH")
                        .withEntry("Right", "Boom Value - SMITH 2")
                        .build()
                        .execute(ImmutableMap.of(
                                "Last Name", "SMITH",
                                "First Name", "JOHN")),
                Template.builder()
                        .withEntry("Boom", "Boom Value - HENRY")
                        .withEntry("Right", "Boom Value - HENRY 2")
                        .build()
                        .execute(ImmutableMap.of(
                                "Last Name", "HENRY",
                                "First Name", "FORD")));

        assertEquals(actual, expected);
    }
}