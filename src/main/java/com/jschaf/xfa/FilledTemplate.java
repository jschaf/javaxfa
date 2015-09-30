package com.jschaf.xfa;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

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

        final Function<String, String> bang = (k -> k + "!!!");

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

    public static String convertXmlToString(Document doc) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(domSource, result);
            return writer.toString();

        } catch (TransformerException ex){
            ex.printStackTrace();
            return null;
        }
    }

    public Map<String, String> getMap() {
        return filledTemplate;
    }

    public Map<String, String> getMapIncludeContext() {
        return ImmutableMap.<String, String>builder().putAll(filledTemplate).putAll(context).build();
    }

    public Document toXmlDocument() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc;
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        }
        doc = builder.newDocument();
        doc.setXmlStandalone(true);

        Element form1 = doc.createElement("form1");
        doc.appendChild(form1);
        Element root = doc.createElement("Page1");
        form1.appendChild(root);
        getMap().forEach((k, v) -> {
            Element element = doc.createElement(k);
            element.setTextContent(v);
            root.appendChild(element);
        });
        return doc;
    }

    public String toXmlString() {
        return FilledTemplate.convertXmlToString(toXmlDocument());
    }

    public Optional<String> fileNameFormat() {
        return Optional.ofNullable(getMapIncludeContext().get("File Name Format"));
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass()) {
            return false;
        }
        FilledTemplate other = (FilledTemplate) obj;
        return filledTemplate.equals(other.filledTemplate) && context.equals(other.context);
    }
}
