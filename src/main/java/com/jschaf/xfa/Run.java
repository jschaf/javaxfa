package com.jschaf.xfa;

import com.itextpdf.text.DocumentException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 */
public class Run {

    public static void main(String[] args) {
        AerExcel aerExcel = new AerExcel("C:/Users/joe/aerTest/AERData.xlsx");
        TemplateExcel templateExcel = new TemplateExcel(aerExcel);
        List<String> xmlStrings = templateExcel.getXmlStrings();
//        xmlStrings.forEach(System.out::println);

        File dir = new File("C:/Users/joe/aerTest/pdf_output");
        dir.mkdirs();
        for (File file : dir.listFiles()) {
            file.delete();
        }

        xmlStrings.forEach(xml -> {
            XfaPdf xfaPdf = new XfaPdf("C:/Users/joe/aerTest/da1059.pdf");
            try {
                // create a pdf output stream in dir
                // parse the xml
                int i = ThreadLocalRandom.current().nextInt();
                xfaPdf.fillPdfWithXfa(xml, new FileOutputStream("C:/Users/joe/aerTest/pdf_output/" + i + ".pdf"));
            } catch (IOException | DocumentException e) {
                e.printStackTrace();
            }

            // set the xfa of the pdf to the xml
            // close the pdf
        });


    }
}
