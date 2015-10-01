package com.jschaf.xfa;

import com.google.common.collect.Table;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
class TemplateExcel {


    private final Table<Integer, String, String> dataTable;
    private final Template variables;
    private final Template translation;

    private final List<FilledTemplate> variableTemplates;
    private final List<FilledTemplate> translations;

    private TemplateExcel(Table<Integer, String, String> dataTable,
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

    private static List<FilledTemplate> fillVariableTemplates(
            Template template,
            Table<Integer, String, String> table) {
        return table
                .rowMap().entrySet().stream()
                .map(entry -> template.render(entry.getValue()))
                .collect(Collectors.toList());
    }

    private static List<FilledTemplate> fillTranslationTemplates(List<FilledTemplate> variables,
                                                                 Template template) {
        return variables.stream()
                .map(filledVars -> template.render(filledVars.getMapIncludeContext()))
                .collect(Collectors.toList());
    }

    private static List<String> convertEntriesToXml(List<FilledTemplate> entries) {
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
