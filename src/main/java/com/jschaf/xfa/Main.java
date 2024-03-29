package com.jschaf.xfa;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 */
class Main {

    /**
     * Compute the absolute file path to the jar file.
     * The framework is based on http://stackoverflow.com/a/12733172/1614775
     * But that gets it right for only one of the four cases.
     *
     * @param aclass A class residing in the required jar.
     * @return A File object for the directory in which the jar file resides.
     * During testing with NetBeans, the result is ./build/classes/,
     * which is the directory containing what will be in the jar.
     */
    private static File getJarDir(Class aclass) {
        URL url;
        String extURL;      //  url.toExternalForm();

        // get an url
        try {
            url = aclass.getProtectionDomain().getCodeSource().getLocation();
            // url is in one of two forms
            //        ./build/classes/   NetBeans test
            //        jardir/JarName.jar  froma jar
        } catch (SecurityException ex) {
            url = aclass.getResource(aclass.getSimpleName() + ".class");
            // url is in one of two forms, both ending "/com/physpics/tools/ui/PropNode.class"
            //          file:/U:/Fred/java/Tools/UI/build/classes
            //          jar:file:/U:/Fred/java/Tools/UI/dist/UI.jar!
        }

        // convert to external form
        extURL = url.toExternalForm();

        // prune for various cases
        if (extURL.endsWith(".jar"))   // from getCodeSource
            extURL = extURL.substring(0, extURL.lastIndexOf("/"));
        else {  // from getResource
            String suffix = "/" + (aclass.getName()).replace(".", "/") + ".class";
            extURL = extURL.replace(suffix, "");
            if (extURL.startsWith("jar:") && extURL.endsWith(".jar!"))
                extURL = extURL.substring(4, extURL.lastIndexOf("/"));
        }

        // convert back to url
        try {
            url = new URL(extURL);
        } catch (MalformedURLException mux) {
            // leave url unchanged; probably does not happen
        }

        // convert url to File
        try {
            return new File(url.toURI());
        } catch (URISyntaxException ex) {
            return new File(url.getPath());
        }
    }

    public static void main(String[] args) {

        File currentDirectory = getJarDir(Main.class);

        File pdfDirectory = new File(currentDirectory, "AerPdfs");
        pdfDirectory.mkdirs();

        File dataPath = new File(currentDirectory, "AERData.xlsx");

        AerExcel aerExcel = new AerExcel(dataPath.getAbsolutePath());
        TemplateExcel templateExcel = new TemplateExcel(aerExcel);


        final File aerPdf = new File(currentDirectory, "da1059.pdf");

        templateExcel.getTranslations().stream().parallel().forEach(filledTemplate -> {
            XfaPdf xfaPdf = new XfaPdf(aerPdf.getAbsolutePath());
            int i = ThreadLocalRandom.current().nextInt(1, 1000000);
            String defaultFileName = "default" + i + ".pdf";
            String fileName = filledTemplate.fileNameFormat().orElse(defaultFileName);
            File filledPdf = new File(pdfDirectory, fileName);
            try (FileOutputStream output = new FileOutputStream(filledPdf.getAbsoluteFile())) {
                xfaPdf.fillPdfWithXfa(filledTemplate.toXmlString(), output);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }
}
