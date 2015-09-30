package com.jschaf.xfa;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.google.common.collect.ImmutableMap;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Template {

    protected final Map<String, Mustache> compiledTemplate;

    public Template(Map<String, String> rawTemplate) {
        this.compiledTemplate = compileTemplate(rawTemplate);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Map<String, String> template = new HashMap<>();

        public Builder withEntry(String key, String value) {
            template.put(key, value);
            return this;
        }

        public Template build() {
            return new Template(template);
        }
    }

    public FilledTemplate execute(Map<String, String> context) {
        return new FilledTemplate(this, context);
    }

    public FilledTemplate execute() {
        return new FilledTemplate(this, new HashMap<>());
    }

    protected static ImmutableMap<String, Mustache> compileTemplate(Map<String, String> template) {
        DefaultMustacheFactory factory = new DefaultMustacheFactory();
        ImmutableMap.Builder<String, Mustache> mustacheBuilder = ImmutableMap.builder();
        template.forEach((k, v) -> mustacheBuilder.put(k, factory.compile(new StringReader(v), k)));
        return mustacheBuilder.build();
    }

}
