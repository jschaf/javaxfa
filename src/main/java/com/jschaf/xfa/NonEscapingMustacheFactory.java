package com.jschaf.xfa;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheException;

import java.io.IOException;
import java.io.Writer;

/**
 */

public class NonEscapingMustacheFactory extends DefaultMustacheFactory {
    @Override
    public void encode(String value, Writer writer) {
        try {
            int e = value.length();
            for (int i = 0; i < e; ++i) {
                char c = value.charAt(i);
                writer.write(c);
            }

        } catch (IOException var5) {
            throw new MustacheException("Failed to encode value: " + value);
        }
    }
}
