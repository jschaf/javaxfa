package com.jschaf.xfa;

/**
 *
 */
public class Run {

    public void main(String[] args) {
        AerExcel aerExcel = new AerExcel("C:/Users/joe/aerTest/AERData.xlsx");
        Template template = new Template(aerExcel);
        template.getXmlStrings();
    }
}
