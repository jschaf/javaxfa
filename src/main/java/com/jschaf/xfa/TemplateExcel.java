package com.jschaf.xfa;

import com.google.common.collect.Table;

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

    protected static List<FilledTemplate> fillVariableTemplates(
            Template template,
            Table<Integer, String, String> table) {
        return table
                .rowMap().entrySet().stream()
                .map(entry -> template.render(entry.getValue()))
                .collect(Collectors.toList());
    }

    protected static List<FilledTemplate> fillTranslationTemplates(List<FilledTemplate> variables,
                                                                   Template template) {
        return variables.stream()
                .map(filledVars -> template.render(filledVars.getMapIncludeContext()))
                .collect(Collectors.toList());
    }

    protected static List<String> convertEntriesToXml(List<FilledTemplate> entries) {
        return entries.stream().map(FilledTemplate::toXmlString).collect(Collectors.toList());
    }

    public List<FilledTemplate> getVariables() {
        return variableTemplates;
    }

    public List<FilledTemplate> getTranslations() {
        return translations;
    }

    public List<String> getXmlStrings() {
        return convertEntriesToXml(translations);
    }

}
