package com.jschaf.xfa;

import autovalue.shaded.com.google.common.common.collect.Maps;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

/**
 *
 */
public class TemplateExcelTest {

    private final ImmutableMap<String, String> defaultContext = ImmutableMap.of(
            "Last Name", "SMITH",
            "First Name", "JOHN");

    private MustacheFactory mustacheFactory;

    @BeforeClass
    public void setUp() {
        mustacheFactory = new DefaultMustacheFactory();
    }

    public Mustache createMustache(String s) {
        return mustacheFactory.compile(new StringReader(s), "name");
    }

    public HashMap<String, Mustache> makeCompiledMustache(String... args) {
        if (args.length % 2 == 1) {
            throw new IllegalArgumentException("need an even number of arguments");
        }
        HashMap<String, Mustache> map = new HashMap<>();
        for (int i = 0; i < args.length; i=i+2) {
            map.put(args[i], createMustache(args[i+1]));
        }
        return map;
    }

    public static <K, V> void printMap(String name, Map<K, V> map) {
        System.out.print(name);
        printMap(map);
    }
    public static <K, V> void printMap(Map<K, V> map) {
        Joiner.MapJoiner mapJoiner = Joiner.on(", ").withKeyValueSeparator("=");
        System.out.println(mapJoiner.join(map));
    }
    @Test
    public void testExcelFormEntries() throws Exception {

        InputStream aerData = this.getClass().getResourceAsStream("/template.xlsx");
        TemplateExcel templateExcel = new TemplateExcel(new AerExcel(aerData));
        List<FilledTemplate> actual = templateExcel.getTranslations();

        List<FilledTemplate> expected = ImmutableList.of(
                Template.builder()
                        .withEntry("TranslateBlam", "Boom Value - SMITH")
                        .withEntry("Translated_Name", "SMITH, JOHN")
                        .build()
                        .execute(ImmutableMap.<String, String>builder()
                                .putAll(defaultContext)
                                .put("Last Name", "SMITH")
                                .put("First Name", "JOHN").build()),
                Template.builder()
                        .withEntry("TranslateBlam", "Boom Value - HENRY")
                        .withEntry("Translated_Name", "HENRY, FORD")
                        .build()
                        .execute(ImmutableMap.<String, String>builder()
                                .putAll(defaultContext)
                                .put("Last Name", "HENRY")
                                .put("First Name", "FORD").build())
        );

        System.out.println("Actual Form Entries");
        actual.forEach(t -> printMap(t.getMap()));

        System.out.println("Expected Form Entries");
        expected.forEach(t -> printMap(t.getMap()));

        boolean equals = actual.equals(expected);
        assertTrue(equals);

//        assertEquals(actual.get(0).getMap(), expected.get(0).getMap());
    }

    @Test
    public void testExcelVariables() throws Exception {

        InputStream aerData = this.getClass().getResourceAsStream("/template.xlsx");
        AerExcel excel = new AerExcel(aerData);

        TemplateExcel templateExcel = new TemplateExcel(excel);
        List<FilledTemplate> actual = templateExcel.getVariables();
//        templateExcel.getTranslations();
        ImmutableList<ImmutableMap<String, String>> expected = ImmutableList.of(
                ImmutableMap.of("Last Name", "SMITH",
                        "First Name", "JOHN",
                        "Boom", "Boom Value - SMITH",
                        "Right", "Boom Value - SMITH 2"),
                ImmutableMap.of("Last Name", "HENRY",
                        "First Name", "FORD",
                        "Boom", "Boom Value - HENRY",
                        "Right", "Boom Value - HENRY 2")
        );

        assertEquals(actual.get(0), expected.get(0));

        assertEquals(actual, expected);
    }
}