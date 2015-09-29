package com.jschaf.xfa;

import com.itextpdf.text.pdf.PdfReader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Represents the XFA portion of a PDF.
 */
public class XfaDocument {


    public final Document xfaDom;
    private Node templateNode;

    public XfaDocument(PdfReader pdf) {
        xfaDom = pdf.getAcroFields().getXfa().getDomDocument();
    }

    public XfaDocument(Document doc) {
        xfaDom = doc;
    }

    public Document getXfaTemplate() {
        XPath xPath = XPathFactory.newInstance().newXPath();
        try {
            Node node = (Node) xPath.evaluate("/xdp:xdp/tempate", xfaDom, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return null;
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

    public Document getXfaData() {
        return null;
    }

}
