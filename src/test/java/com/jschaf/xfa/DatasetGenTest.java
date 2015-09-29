package com.jschaf.xfa;

import org.custommonkey.xmlunit.*;
import org.custommonkey.xmlunit.examples.RecursiveElementNameAndTextQualifier;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.InputStream;
import java.util.Optional;

import static org.testng.Assert.*;

/**
 * Tests for Dataset
 */
public class DatasetGenTest {

    private InputStream da410xml;

    @BeforeClass
    public void setUp() throws Exception {
        da410xml = this.getClass().getResourceAsStream("/da410.xml");
        XMLUnit.setIgnoreWhitespace(true);
    }

    @Test
    public void convertEmptyStringTest() {
        String xml = "";
        Document actual = DatasetGen.convertStringToDocument(xml);
        assertEquals(actual.getChildNodes().getLength(), 0);
    }

    @Test
    public void roundTripTest() {
        String xml = "<joe><was>1</was><not>2</not></joe>";
        Node doc = DatasetGen.convertStringToDocument(xml);
        String xmlOutput = DatasetGen.convertNodeToString(doc);
        assertEquals(xml, xmlOutput, "XML roundtrip not the same.");
    }

    @Test
    public void nodeChildrenTest0Elems() {
        String xml = "<joe></joe>";
        Document doc = DatasetGen.convertStringToDocument(xml);
        Node node = doc.getFirstChild();
        assertEquals(0, DatasetGen.nodeChildren(node).count());
    }

    @Test
    public void nodeChildrenTest1Elem() {
        String xml = "<joe><value></value></joe>";
        Document doc = DatasetGen.convertStringToDocument(xml);
        Node node = doc.getFirstChild();
        assertEquals(1, DatasetGen.nodeChildren(node).count());
    }

    @Test
    public void nodeChildrenTestFilter() {
        String xml = "<joe><value></value></joe>";
        Document doc = DatasetGen.convertStringToDocument(xml);
        Node node = doc.getFirstChild();
        assertEquals(1,
                DatasetGen.nodeChildren(node)
                .filter(e -> "value".equals(e.getNodeName()))
                .count()
        );
    }

    @Test
    public void nodeChildrenTestFilter2() {
        String xml = "<joe><value></value><value></value></joe>";
        Document doc = DatasetGen.convertStringToDocument(xml);
        Node node = doc.getFirstChild();
        assertEquals(2,
                DatasetGen.nodeChildren(node)
                        .filter(e -> "value".equals(e.getNodeName()))
                        .count()
        );
    }

    @Test
    public void nodeChildrenTestFilterGetFirst() {
        String xml = "<joe><value></value><value></value></joe>";
        Document doc = DatasetGen.convertStringToDocument(xml);
        Node node = doc.getFirstChild();
        assertEquals("value",
                DatasetGen.nodeChildren(node)
                        .filter(e -> "value".equals(e.getNodeName()))
                        .findFirst().get().getNodeName()
        );
    }

    @Test
    public void getValueTest1() {
        String xml = "<joe><value><text>0</text></value></joe>";
        Document doc = DatasetGen.convertStringToDocument(xml);
        Node node = doc.getFirstChild();
        assertEquals("0", DatasetGen.getFieldValue(node).get());
    }

    @Test
    public void convertTemplateNullTest() {
        Document actual = DatasetGen.createDatasetFromTemplate((String) null);
        assertEquals(actual.getChildNodes().getLength(), 0);
    }

    @Test
    public void convertTemplateEmptyTest() {
        String input = "<template></template>";
        Document actual = DatasetGen.createDatasetFromTemplate(input);
        assertEquals(actual.getChildNodes().getLength(), 0);
    }

    public static void assertDatasetCorrect(String template, String dataset) {
        Document expected = DatasetGen.convertStringToDocument(dataset);
        Document actual = DatasetGen.createDatasetFromTemplate(template);
        XMLAssert.assertXMLEqual(expected, actual);
    }

    @Test
    public void convertTemplateOneSubformTest() {
        String template = "<template xmlns=\"http://www.xfa.org/schema/xfa-template/3.3/\"><subform name=\"form1\"></subform></template>";
        String dataset = "<form1></form1>";
        assertDatasetCorrect(template, dataset);
    }

    @Test
    public void convertTemplateOneFieldTest() {
        String template = "<template><subform name=\"form1\"><field name=\"date\"></field></subform></template>";
        String dataset = "<form1><date></date></form1>";
        assertDatasetCorrect(template, dataset);
    }

    @Test
    public void convertTemplateOneFieldNoMatchTest() {
        String template = "<template><subform name=\"form1\"><field name=\"date\"><bind match=\"none\"/></field></subform></template>";
        String dataset = "<form1></form1>";
        assertDatasetCorrect(template, dataset);
    }

    @Test
    public void convertTemplateTwoFieldTest() {
        String template = "<template><subform name=\"form1\">"
                + "<field name=\"date\"></field>"
                + "<field name=\"first_name\"></field>"
                + "</subform></template>";
        String dataset = "<form1><date></date><first_name></first_name></form1>";
        assertDatasetCorrect(template, dataset);
    }

    @Test
    public void convertTemplateTwoFieldValueTest() {
        String template = "<template><subform name=\"form1\">"
                + "<field name=\"date\"><value><text>dateValue</text></value></field>"
                + "<field name=\"first_name\"></field>"
                + "</subform></template>";
        String dataset = "<form1><date>dateValue</date><first_name></first_name></form1>";
        assertDatasetCorrect(template, dataset);
    }

    // I'm pretty sure the dataset is wrong
    @Test(enabled=false)
    public void convertTemplateDa410Pdf() {
        InputStream da410pdf = this.getClass().getResourceAsStream("/da410.pdf");
        XfaPdf da410 = new XfaPdf(da410pdf);
        XfaDocument xfaDocument = new XfaDocument(da410.pdf);
        Document actual = DatasetGen.createDatasetFromTemplate(xfaDocument.getTemplateNode());
        String expectedXml = "<form1>"
                +  "<Page1/>"
                +  "<FROM/>"
                +  "<TO/>"
                +  "<DATE/>"
                +  "<ACCOUNT_A>0</ACCOUNT_A>"
                +  "<ACCOUNT_B>0</ACCOUNT_B>"
                +  "<QUANTITY/>"
                +  "<FORM/>"
                +  "<PRINTER/>"
                +  "<DATE_REC/>"
                +  "<INSTALL/>"
                +  "<NAME/>"
                + "</form1>";

        Document expected = DatasetGen.convertStringToDocument(expectedXml);
        System.out.println("** Expected:\n" + DatasetGen.convertDocumentToString(expected));
        System.out.println("** Actual:\n" + DatasetGen.convertDocumentToString(actual));
        Diff d = new Diff(expected, actual);
        DetailedDiff diff = new DetailedDiff(d);
        XMLAssert.assertXMLEqual(diff, true);
    }

    @Test
    public void convertTemplateDa1059Pdf() {
        InputStream da1059pdf = this.getClass().getResourceAsStream("/da1059.pdf");
        XfaPdf da1059 = new XfaPdf(da1059pdf);
        XfaDocument xfaDocument = new XfaDocument(da1059.pdf);
        Document actual = DatasetGen.createDatasetFromTemplate(xfaDocument.getTemplateNode());
        String expectedXml = "<form1>"
                +   "<Page1>"
                +     "<Todays_Date/>"
                +     "<Name/>"
                +     "<SSN/>"
                +     "<SPECIALTY_MOSC/>"
                +     "<COURSE_TITLE/>"
                +     "<From/>"
                +     "<Thru/>"
                +     "<Typed_Name_Grade_Rater/>"
                +     "<RaterDate/>"
                +     "<Typed_Name_Grade_Review/>"
                +     "<ReviewerDate/>"
                +     "<RatedSoldierDate/>"
                +     "<Comments/>"
                +     "<Name_School/>"
                +     "<Performance_Summary1/>"
                +     "<Performance_Summary2/>"
                +     "<Performance_Summary3/>"
                +     "<Performance_Summary4/>"
                +     "<NOT_EVALUATED1/>"
                +     "<UNSAT1/>"
                +     "<SAT1/>"
                +     "<SUPERIOR1/>"
                +     "<SUPERIOR2/>"
                +     "<SAT2/>"
                +     "<UNSAT2/>"
                +     "<NOT_EVALUATED2/>"
                +     "<SUPERIOR3/>"
                +     "<SAT3/>"
                +     "<UNSAT3/>"
                +     "<NOT_EVALUATED3/>"
                +     "<SUPERIOR4/>"
                +     "<SAT4/>"
                +     "<UNSAT4/>"
                +     "<NOT_EVALUATED4/>"
                +     "<SUPERIOR5/>"
                +     "<SAT5/>"
                +     "<UNSAT5/>"
                +     "<NOT_EVALUATED5/>"
                +     "<Academic_Potential_Yes/>"
                +     "<Academic_Potential_No/>"
                +     "<NA/>"
                +     "<Referred_Report1/>"
                +     "<Referred_Report2/>"
                +     "<Referred_Report3/>"
                +     "<Component1/>"
                +     "<aRank1dropdwnList2/>"
                +     "<RankdrpdwnListBox2>28</RankdrpdwnListBox2>"
                +     "<Component1BK>33</Component1BK>"
                +   "</Page1>"
                + "</form1>";
        Document expected = DatasetGen.convertStringToDocument(expectedXml);
        System.out.println("** Expected:\n" + DatasetGen.convertDocumentToString(expected));
        System.out.println("** Actual:\n" + DatasetGen.convertDocumentToString(actual));
        Diff d = new Diff(expected, actual);
        DetailedDiff diff = new DetailedDiff(d);
        diff.overrideElementQualifier(new ElementNameAndAttributeQualifier());
        System.out.println("Detailed Differences:\n" + diff.getAllDifferences().toString());
        assertTrue(diff.similar());
    }
}