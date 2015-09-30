package com.jschaf.xfa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 */
public class Main {

    public static void main(String[] args) {
        AerExcel aerExcel = new AerExcel("C:/Users/joe/aerTest/AERData.xlsx");
        TemplateExcel templateExcel = new TemplateExcel(aerExcel);

        File dir = new File("C:/Users/joe/aerTest/pdf_output");
        dir.mkdirs();
        for (File file : dir.listFiles()) {
            file.delete();
        }

        templateExcel.getTranslations().forEach(filledTemplate -> {
            XfaPdf xfaPdf = new XfaPdf("C:/Users/joe/aerTest/da1059.pdf");
            int i = ThreadLocalRandom.current().nextInt(1, 1000000);
            String defaultFileName = "default" + i + ".pdf";
            String fileName = filledTemplate.fileNameFormat().orElse(defaultFileName);
            FileOutputStream output;
            try {
                output = new FileOutputStream("C:/Users/joe/aerTest/pdf_output/" + fileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            }

            xfaPdf.fillPdfWithXfa(filledTemplate.toXmlString(), output);
        });
    }
}
