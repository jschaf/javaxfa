package com.jschaf.xfa;

import com.itextpdf.text.pdf.PdfReader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Represents the XFA portion of a PDF.
 */
class XfaDocument {


    private final Document xfaDom;

    public XfaDocument(PdfReader pdf) {
        xfaDom = pdf.getAcroFields().getXfa().getDomDocument();
    }

    public Node getTemplateNode() {
        Node search = xfaDom.getFirstChild().getFirstChild();
        while (search != null) {
            if ("template".equals(search.getNodeName())) {
                return search;
            }
            search = search.getNextSibling();
        }
        return null;
    }
}
