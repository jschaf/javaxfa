package com.jschaf.xfa;

import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 *
 */
public class FilledTemplateTest {

    private final ImmutableMap<String, String> defaultContext = ImmutableMap.of(
            "last name", "smith",
            "first name", "john",
            "title", "Mr.");

    @Test
    public void testFillTemplate() throws Exception {
        FilledTemplate actual = Template.builder()
                .withEntry("a", "{{title}}")
                .build()
                .execute(defaultContext);

        ImmutableMap<String, String> expected = ImmutableMap.<String, String>builder()
                .putAll(defaultContext)
                .put("a", "Mr.")
                .build();

        assertEquals(actual.getMapIncludeContext(), expected);
    }

    @Test
    public void testFillTemplateWithSpaces() throws Exception {
        FilledTemplate actual = Template.builder()
                .withEntry("a", "{{last name}}, {{first name}}")
                .build()
                .execute(defaultContext);

        ImmutableMap<String, String> expected = ImmutableMap.<String, String>builder()
                .putAll(defaultContext)
                .put("a", "smith, john")
                .build();

        assertEquals(actual.getMapIncludeContext(), expected);
    }

    @Test
    public void testFillTemplateRecursive() throws Exception {
        FilledTemplate actual = Template.builder()
                .withEntry("a", "{{last name}}")
                .withEntry("b", "{{a}}, {{first name}}")
                .build()
                .execute(defaultContext);

        ImmutableMap<String, String> expected = ImmutableMap.<String, String>builder()
                .putAll(defaultContext)
                .put("a", "smith")
                .put("b", "smith, john")
                .build();

        assertEquals(actual.getMapIncludeContext(), expected);
    }

}