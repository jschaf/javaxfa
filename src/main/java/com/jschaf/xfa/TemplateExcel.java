package com.jschaf.xfa;

import com.google.common.collect.Table;
import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
public class TemplateExcel {


    protected final Table<Integer, String, String> dataTable;
    protected final Template variables;
    protected final Template translation;

    private List<FilledTemplate> variableTemplates;
    private List<FilledTemplate> translations;

    public TemplateExcel(Table<Integer, String, String> dataTable,
                         Map<String, String> variables,
                         Map<String, String> translation) {
        this.dataTable = dataTable;
        this.variables = new Template(variables);
        this.translation = new Template(translation);

        variableTemplates = fillVariableTemplates(this.variables, this.dataTable);
        translations = fillTranslationTemplates(variableTemplates, this.translation);
    }

    public TemplateExcel(AerExcel excel) {
        this(excel.getDataTable(), excel.getVariables(), excel.getTranslation());
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

    public List<FilledTemplate> getVariables() {
        return variableTemplates;
    }

    public List<FilledTemplate> getTranslations() {
        return translations;
    }

    protected static List<FilledTemplate> fillVariableTemplates(
            Template template,
            Table<Integer, String, String> table) {
        return table
                .rowMap().entrySet().stream()
                .map(entry -> template.execute(entry.getValue()))
                .collect(Collectors.toList());
    }

    protected static List<FilledTemplate> fillTranslationTemplates(List<FilledTemplate> variables,
                                                                   Template template) {
        return variables.stream()
                .map(filledVars -> template.execute(filledVars.getMapIncludeContext()))
                .collect(Collectors.toList());
    }

    protected static List<String> convertEntriesToXml(List<FilledTemplate> entries) {
        return entries.stream().map(FilledTemplate::toXml).collect(Collectors.toList());
    }

    public List<String> getXmlStrings() {
        return convertEntriesToXml(translations);
    }

}
