package com.jschaf.xfa;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class FilledTemplate {

    private final Map<String, String> context;
    private final Map<String, String> filledTemplate;

    public FilledTemplate(Template template, Map<String, String> context) {
        this.context = context;
        filledTemplate = fill(template, context);
    }

    protected static Map<String, String> fill(
            Template template,
            Map<String, String> context){

        HashMap<String, String> filledTemplate = Maps.newHashMap();
        ArrayList<Map<String, String>> scopes = new ArrayList<>();
        scopes.add(context);
        scopes.add(filledTemplate);
        template.compiledTemplate.forEach((k, mustache) -> {
            StringWriter writer = new StringWriter();
            mustache.execute(writer, scopes.toArray());
            filledTemplate.put(k, writer.toString());
        });

        return filledTemplate;
    }

    public Map<String, String> getMap() {
        return filledTemplate;
    }

    public Map<String, String> getMapIncludeContext() {
        return ImmutableMap.<String, String>builder().putAll(filledTemplate).putAll(context).build();
    }

    protected static String toXml(FilledTemplate entry) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.newDocument();
            doc.setXmlStandalone(true);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return "";
        }

        Element form1 = doc.createElement("form1");
        doc.appendChild(form1);
        Element root = doc.createElement("Page1");
        form1.appendChild(root);
        entry.getMap().forEach((k, v) -> {
            Element element = doc.createElement(k);
            element.setTextContent(v);
            root.appendChild(element);
        });
        return TemplateExcel.convertXmlToString(doc);
    }

    public String formatString() {
        return "";
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass()) {
            return false;
        }
        FilledTemplate other = (FilledTemplate) obj;
        return context.equals(other.context) && filledTemplate.equals(other.filledTemplate);
    }
}
