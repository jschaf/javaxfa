package com.jschaf.xfa;

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
public class TemplateTest {


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
    public void testFillTemplate() throws Exception {
        HashMap<String, Mustache> mustache = makeCompiledMustache("a", "{{table1_col}}", "b", "{{table2_col}}");
        System.out.print("mustache: ");
        printMap(makeCompiledMustache());
        Map<String, String> colMap = ImmutableMap.of("table1_col", "table1_col_result",
                "table2_col", "table2_col_result");
        Map<String, String> expected = ImmutableMap.of("table1_col", "table1_col_result",
                "table2_col", "table2_col_result",
                "a", "table1_col_result",
                "b", "table2_col_result");
        Map<String, String> actual = Template.fillTemplate(mustache, colMap, true);
        printMap("expect ", expected);
        printMap("actual ", actual);
        assertEquals(actual, expected);
    }

    @Test
    public void testFillTemplateWithSpaces() throws Exception {
        HashMap<String, Mustache> mustache = makeCompiledMustache("a", "{{last name}}, {{first name}}");
        ImmutableMap<String, String> colMap = ImmutableMap.of("last name", "smith", "first name", "john");
        ImmutableMap<String, String> expected = ImmutableMap.of("last name", "smith",
                "first name",
                "john", "a",
                "smith, john");
        Map<String, String> actual = Template.fillTemplate(mustache, colMap, true);
        printMap("expect ", expected);
        printMap("actual ", actual);
        assertEquals(actual, expected);
    }

    @Test
    public void testFillTemplateRecursive() throws Exception {
        HashMap<String, Mustache> mustache = makeCompiledMustache("a", "{{last name}}", "b", "{{a}}, {{first name}}");
        ImmutableMap<String, String> colMap = ImmutableMap.of("last name", "smith", "first name", "john");
        ImmutableMap<String, String> expected = ImmutableMap.of("last name", "smith",
                "first name", "john",
                "a", "smith",
                "b", "smith, john");
        Map<String, String> actual = Template.fillTemplate(mustache, colMap, true);
        printMap("expect ", expected);
        printMap("actual ", actual);
        assertEquals(actual, expected);
    }

    @Test
    public void testExcelFormEntries() throws Exception {

        InputStream aerData = this.getClass().getResourceAsStream("/template.xlsx");
        Template template = new Template(new AerExcel(aerData));
        List<Map<String, String>> actual = template.getFormEntries();

        ImmutableList<ImmutableMap<String, String>> expected = ImmutableList.of(
                ImmutableMap.of("TranslateBlam", "Boom Value - SMITH", "Translated_Name", "SMITH, JOHN"),
                ImmutableMap.of("TranslateBlam", "Boom Value - HENRY", "Translated_Name", "HENRY, FORD")
        );
        System.out.println("Actual Form Entries");
        actual.forEach(TemplateTest::printMap);

        System.out.println("Expected Form Entries");
        expected.forEach(TemplateTest::printMap);


        System.out.println("XML");
        System.out.println(Template.convertEntryToXml(actual.get(0)));

        assertEquals(actual, expected);
    }

    @Test
    public void testExcelVariables() throws Exception {

        InputStream aerData = this.getClass().getResourceAsStream("/template.xlsx");
        AerExcel excel = new AerExcel(aerData);

        Template template = new Template(excel);
        template.getFormEntries();
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

        List<Map<String, String>> actual = template.getVariables();
        assertEquals(actual, expected);
    }
}