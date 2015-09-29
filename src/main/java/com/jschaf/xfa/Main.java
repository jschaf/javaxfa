package com.jschaf.xfa;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.internal.Lists;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.XfaForm;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.List;
import java.util.Set;


public class Main {

    @Parameter
    public List<String> parameters = Lists.newArrayList();

    // @Parameter(names = {"-i", "--input"}, description = "input PDF")
    // public inputPdf


    public static final String RESOURCE = "C:/Users/joe/A1059.pdf";
    public static final String RESOURCE_FILLED = "C:/Users/joe/A1059-filled.pdf";
    public static final String XMLDEST = "C:/Users/joe/A1059xfa.xml";

    public static void readFieldNames(String src, String dest) throws IOException {
        PrintWriter out = new PrintWriter(dest);

        PdfReader reader = new PdfReader(src);
        AcroFields form = reader.getAcroFields();
        XfaForm xfa = form.getXfa();
        System.out.println(xfa.isXfaPresent() ? "XFA Form" : "Acrobat Form");

        Set<String> fields = form.getFields().keySet();
        for (String key : fields) {
            System.out.println(key);
        }

        Document doc = xfa.getDomDocument();
        String docString = getDocumentString(doc);
        out.println(docString);

        out.flush();
        out.close();
        reader.close();
        System.out.println("Done Reading Field Names");
    }

    public static String getDocumentString(Document doc) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();

        } catch (TransformerException ex){
            ex.printStackTrace();
            return null;
        }
    }

    public static void manipulatePdf(String src, String xml, String dest) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(src);
        PdfReader.unethicalreading = true;
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
        AcroFields form = stamper.getAcroFields();
        XfaForm xfa = form.getXfa();
        xfa.fillXfaForm(new FileInputStream(xml));
        stamper.close();
        reader.close();
    }

    public static void main(String[] args) {
        AerExcel aerExcel = new AerExcel("C:/Users/joe/aerTest/AERData.xlsx");
        Template template = new Template(aerExcel);
        template.getXmlStrings();
//        try {
//            readFieldNames(RESOURCE, XMLDEST);
//
//            manipulatePdf(RESOURCE, "C:/Users/joe/a1059-data.xml", "C:/Users/joe/a1059-with-xfa-data.pdf");
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }
    }

}
