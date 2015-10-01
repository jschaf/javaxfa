package com.jschaf.xfa;

import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 *
 */
public class FilledTemplateTest {

    private final ImmutableMap<String, String> defaultContext = ImmutableMap.of(
            "last_name", "smith",
            "first_name", "john",
            "title", "Mr.");

    @Test
    public void testFillTemplate() throws Exception {
        FilledTemplate actual = Template.builder()
                .withEntry("a", "{{title}}")
                .build()
                .render(defaultContext);

        ImmutableMap<String, String> expected = ImmutableMap.<String, String>builder()
                .putAll(defaultContext)
                .put("a", "Mr.")
                .build();

        assertEquals(actual.getMapIncludeContext(), expected);
    }

    @Test
    public void testFillTemplate2() throws Exception {
        FilledTemplate actual = Template.builder()
                .withEntry("a", "{{last_name}}, {{first_name}}")
                .build()
                .render(defaultContext);

        ImmutableMap<String, String> expected = ImmutableMap.<String, String>builder()
                .putAll(defaultContext)
                .put("a", "smith, john")
                .build();

        assertEquals(actual.getMapIncludeContext(), expected);
    }

    @Test
    public void testFillTemplateRecursive() throws Exception {
        FilledTemplate actual = Template.builder()
                .withEntry("a", "{{last_name}}")
                .withEntry("b", "{{a}}, {{first_name}}")
                .build()
                .render(defaultContext);

        ImmutableMap<String, String> expected = ImmutableMap.<String, String>builder()
                .putAll(defaultContext)
                .put("a", "smith")
                .put("b", "smith, john")
                .build();

        assertEquals(actual.getMapIncludeContext(), expected);
    }

    @Test
    public void testFunctions() throws Exception {
        FilledTemplate actual = Template.builder().withEntry("a", "{{'joe'|capitalize}}").build().render();

        ImmutableMap<String, String> expected = ImmutableMap.of("a", "Joe");

        assertEquals(actual.getMapIncludeContext(), expected);
    }

}