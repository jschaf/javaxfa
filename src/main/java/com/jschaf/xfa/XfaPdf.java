package com.jschaf.xfa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.XfaForm;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.HashMap;

/**
 * Actually do things with the PDF.
 */
public class XfaPdf {

    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectMapper xmlMapper = new XmlMapper();
    public PdfReader pdf = null;

    public String path = "";

    private XfaPdf initXfaPdf(InputStream inputStream) {
        try {
            pdf = new PdfReader(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mapper.registerModule(new JavaTimeModule());
        xmlMapper.registerModule(new JavaTimeModule());
        return this;
    }

    public XfaPdf(InputStream inputStream) {
        initXfaPdf(inputStream);
    }

    public XfaPdf(String path) {

        try {
            FileInputStream file = new FileInputStream(path);
            initXfaPdf(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Document getXfaDocument() {
        AcroFields form = pdf.getAcroFields();
        return form.getXfa().getDomDocument();
    }

    public void fillPdfWithXfa(InputStream xmlStream, OutputStream outputStream) {
        PdfReader.unethicalreading = true;
        PdfStamper stamper = null;
        try {
            stamper = new PdfStamper(this.pdf, outputStream);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return;
        }
        AcroFields form = stamper.getAcroFields();
        XfaForm xfa = form.getXfa();
        try {
            xfa.fillXfaForm(xmlStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            stamper.close();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    public void fillPdfWithXfa(String xml, OutputStream output) {
        InputStream is = new ByteArrayInputStream(xml.getBytes());
        this.fillPdfWithXfa(is, output);
    }

    public void printXfaInfo() throws ParserConfigurationException, SAXException, IOException {

        XfaForm xfaForm = new XfaForm(pdf);

        System.out.println("Is XFA Present?");
        System.out.println(xfaForm.isXfaPresent());

        if (xfaForm.isXfaPresent() && pdf.getAcroFields().getFields().keySet().size() == 0) {
            System.out.println("  XFA Form is dynamic");
        } else {
            System.out.println("  XFA Form is static");
        }

        System.out.println("\n\n* XFA Datasets Node");
        System.out.println(DatasetGen.convertNodeToString(xfaForm.getDatasetsNode()));


//        System.out.println("XFA DOM Document");
//        try (PrintWriter out = new PrintWriter("C:/Users/joe/da1059.xml")) {
//            out.write(DatasetGen.convertDocumentToString(xfaForm.getDomDocument()));
//        }

        System.out.println("\n\n* XFA TemplateExcel SOM");
        XfaForm.Xml2SomTemplate templateSom = xfaForm.getTemplateSom();
        for (String name : templateSom.getOrder()) {
            System.out.println("    name: " + name);
            XfaForm.Stack2<String> nameStack = XfaForm.Xml2Som.splitParts(name);
            System.out.print("       ");
            while (!nameStack.empty()) {
                String namePart = nameStack.pop();
                System.out.print("  {" + namePart + "}");
            }
            System.out.println();
        }

        System.out.println("\n\n* XFA TemplateExcel Name to Nodes");
        HashMap<String, Node> templateName2Node = templateSom.getName2Node();
        templateName2Node.forEach((k, v) -> {
            System.out.println("    key" + k);
            NamedNodeMap attributes = v.getAttributes();
            System.out.println("       " + attributes.getNamedItem("name").getNodeValue());
//            for (int i = 0; i < attributes.getLength(); i++) {
//                System.out.println("      " + attributes.item(i));
//            }
        });

        System.out.println("\n\n* XFA Datasets SOM");
        XfaForm.Xml2SomDatasets datasetsSom = xfaForm.getDatasetsSom();
        for (String name : datasetsSom.getOrder()) {
            System.out.println("    name: " + name);
        }

        System.out.println("\n\n* XFA Datasets Name to Nodes");
        HashMap<String, Node> name2Node = datasetsSom.getName2Node();
        name2Node.forEach((k, v) -> System.out.println("    key" + k + " value: " + v));
    }


}
