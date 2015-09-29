package com.jschaf.xfa;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.XfaForm;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import java.io.*;
import java.time.LocalDate;

import static org.testng.Assert.*;

/**
 * Test for XfaPdf
 */
public class XfaPdfTest {

    private InputStream da1059Pdf;
    private InputStream da410Pdf;

    @BeforeMethod
    public void setUp() {
         da1059Pdf = this.getClass().getResourceAsStream("/da1059.pdf");
         da410Pdf = this.getClass().getResourceAsStream("/da410.pdf");
    }

    @Test
    public void documentTitle() throws IOException {
        XfaPdf doc = new XfaPdf(da1059Pdf);
        assertEquals("DA Form 1059, MAR 2014", doc.pdf.getInfo().get("Title"));
    }

    @Test
    public void getXfaDocumentTest() throws IOException {
        XfaPdf doc = new XfaPdf(da410Pdf);
        System.out.println(DatasetGen.convertDocumentToString(doc.getXfaDocument()));
    }

    @Test
    public void getXfaTest() throws IOException, ParserConfigurationException, SAXException, TransformerException, XPathExpressionException {
        XfaPdf doc = new XfaPdf(da410Pdf);
        XfaForm xfaForm = new XfaForm(doc.pdf);

        System.out.println("Is XFA Present?");
        System.out.println(xfaForm.isXfaPresent());

        if (xfaForm != null && xfaForm.isXfaPresent() && doc.pdf.getAcroFields().getFields().keySet().size() == 0) {
            System.out.println("XFA Form is dynamic");
        } else {
            System.out.println("XFA Form is static");
        }

        System.out.println("XFA Template SOM");
        XfaForm.Xml2SomTemplate templateSom = xfaForm.getTemplateSom();
        for (String name : templateSom.getOrder()) {
           System.out.println("    name: " + name);
        }

        System.out.println("XFA Datasets SOM");
        XfaForm.Xml2SomDatasets datasetsSom = xfaForm.getDatasetsSom();
        for (String name : datasetsSom.getOrder()) {
            System.out.println("    name: " + name);
        }
//
//        System.out.println("Acroform fields");
//        AcroFields acroFields = doc.pdf.getAcroFields();
//        Map<String, AcroFields.Item> fields = acroFields.getFields();
//        for (Map.Entry<String, AcroFields.Item> field : fields.entrySet()) {
//            System.out.println("   " + field.getKey() + ": " + field.getValue().toString());
//        }
//
//        System.out.println("XFA Blank");
//        System.out.println(xfaForm.getDomDocument().toString());


//        System.out.println("XFA Field Names");
////        System.out.println(doc.getFieldNames());
//
//        System.out.println("!!!!!\n\n\n\n\n\n\n\n!!!!!\n\n\n\n\n\n\n\n!!!!!!!");
//
//        System.out.println("XFA through Acrofields");
//        System.out.println(XfaPdf.convertXmlToString(acroFields.getXfa().getDomDocument()));

    }

    @Test
    public void datasetSom1059pdf() throws IOException, ParserConfigurationException, SAXException {
        XfaPdf doc = new XfaPdf(da1059Pdf);
        doc.printXfaInfo();
    }
}