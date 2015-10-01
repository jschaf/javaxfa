package com.jschaf.xfa;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.hubspot.jinjava.Jinjava;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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

    private static Map<String, String> fill(
            Template template,
            Map<String, String> context){

        HashMap<String, String> filledTemplate = Maps.newHashMap();
        Map<String, String> scope = Maps.newHashMap(context);
        scope.putAll(filledTemplate);

        return Maps.transformEntries(template.rawTemplate,
                (k, v) -> {
                    String rendered = new Jinjava().render(v, scope);
                    scope.put(k, rendered);
                    return rendered;
                }
        );
    }

    private static String convertXmlToString(Document doc) {
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

    private Map<String, String> getMap() {
        return filledTemplate;
    }

    public Map<String, String> getMapIncludeContext() {
        return ImmutableMap.<String, String>builder().putAll(filledTemplate).putAll(context).build();
    }

    private Document toXmlDocument() {
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
        return Optional.ofNullable(getMapIncludeContext().get("FileNameFormat"));
    }

    @Override
    public int hashCode() {
        return Objects.hash(filledTemplate, context);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        FilledTemplate other = (FilledTemplate) obj;
        return filledTemplate.equals(other.filledTemplate) && context.equals(other.context);
    }
}
