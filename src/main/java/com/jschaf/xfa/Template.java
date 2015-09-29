package com.jschaf.xfa;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.sun.istack.internal.NotNull;
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
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
public class Template {


    protected Table<Integer, String, String> dataTable;
    protected Map<String, String> variables;
    protected Map<String, String> translation;

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

    public List<Map<String, String>> getVariables() {
        return variableTemplates;
    }

    public List<Map<String, String>> getFormEntries() {
        return formEntries;
    }

    private List<Map<String, String>> variableTemplates;
    private List<Map<String, String>> formEntries;

    public Template(Table<Integer, String, String> dataTable,
                    Map<String, String> variables,
                    Map<String, String> translation) {
        this.dataTable = dataTable;
        this.variables = variables;
        this.translation = translation;

        fillVariables();
        fillFormEntries();
    }

    public Template(AerExcel excel) {
        this(excel.getDataTable(), excel.getVariables(), excel.getTranslation());
    }

    protected static Map<String, String> fillTemplate(
            Map<String, Mustache> template,
            Map<String, String> context,
            boolean includeContext){
        HashMap<String, String> filledTemplate = Maps.newHashMap();

        ArrayList<Map<String, String>> scopes = new ArrayList<>();
        scopes.add(context);
        scopes.add(filledTemplate);
        template.forEach((k, mustache) -> {
            StringWriter writer = new StringWriter();
            mustache.execute(writer, scopes.toArray());
            filledTemplate.put(k, writer.toString());
        });
        if (includeContext) {
            filledTemplate.putAll(context);
        }
        return filledTemplate;
    }

    protected static ImmutableMap<String, Mustache> compileTemplate(@NotNull Map<String, String> template) {
        DefaultMustacheFactory factory = new DefaultMustacheFactory();
        ImmutableMap.Builder<String, Mustache> mustacheBuilder = ImmutableMap.builder();
        template.forEach((k, v) -> mustacheBuilder.put(k, factory.compile(new StringReader(v), k)));
        return mustacheBuilder.build();
    }

    public ImmutableList<Map<String, String>> fillTemplates(Map<String, String> template,
                                                            Table<Integer, String, String> table,
                                                            boolean includeContext) {
        ImmutableMap<String, Mustache> variableTemplates = compileTemplate(template);
        ImmutableList.Builder<Map<String, String>> contextList = ImmutableList.builder();
        table.rowMap().forEach((row, colMap) ->
                contextList.add(fillTemplate(variableTemplates, colMap, includeContext)));
        return contextList.build();
    }

    public static List<Map<String, String>> makeFormEntries(List<Map<String, String>> rows, Map<String, String> template) {
        return rows.stream()
                .map(rowContext -> fillTemplate(compileTemplate(template), rowContext, false))
                .collect(Collectors.toList());
    }

    public void fillVariables() {
        variableTemplates = fillTemplates(variables, dataTable, true);
    }

    public void fillFormEntries() {
        formEntries = makeFormEntries(variableTemplates, translation);
    }

    protected static String convertEntryToXml(Map<String, String> entry) {
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

        Element root = doc.createElement("form1");
        doc.appendChild(root);
        entry.forEach((k, v) -> {
            Element element = doc.createElement(k);
            element.setTextContent(v);
            root.appendChild(element);
        });
        return convertXmlToString(doc);
    }

    protected static List<String> convertEntriesToXml(List<Map<String, String>> entries) {
        return entries.stream().map(Template::convertEntryToXml).collect(Collectors.toList());
    }

    public List<String> getXmlStrings() {
        convertEntriesToXml(formEntries).forEach(System.out::println);
        return convertEntriesToXml(formEntries);
    }

}
