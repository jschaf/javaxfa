package com.jschaf.xfa;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Template {

    final Map<String, String> rawTemplate;

    public Template(Map<String, String> rawTemplate) {
        this.rawTemplate = rawTemplate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public FilledTemplate render(Map<String, String> context) {
        return new FilledTemplate(this, context);
    }

    public FilledTemplate render() {
        return new FilledTemplate(this, new HashMap<>());
    }

    public static class Builder {
        private final Map<String, String> template = new HashMap<>();

        public Builder withEntry(String key, String value) {
            template.put(key, value);
            return this;
        }

        public Template build() {
            return new Template(template);
        }
    }

}
