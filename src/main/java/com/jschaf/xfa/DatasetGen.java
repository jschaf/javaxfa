package com.jschaf.xfa;

import com.sun.istack.internal.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Generates the XFA Dataset from the template.
 */
public class DatasetGen {

    public static Document newEmptyDocument() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document doc = null;
        try {
            builder = factory.newDocumentBuilder();
            doc = builder.newDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return doc;
    }


    public static String convertNodeToString(Node node) {
        TransformerFactory tf = TransformerFactory.newInstance();
        try {
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(node), new StreamResult(writer));
            return writer.getBuffer().toString();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String convertDocumentToString(Document document) {
        return convertNodeToString((Node) document);
    }

    public static Document convertStringToDocument(String xml) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document doc = null;
        try {

            builder = factory.newDocumentBuilder();
            if ("".equals(xml)) {
                doc = builder.newDocument();
            } else {
                doc = builder.parse(new InputSource(new StringReader(xml)));
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return doc;
    }


    public static Iterable<Node> iterable(final NodeList nodes) {
       return new Iterable<Node>() {
           @Override
           public Iterator<Node> iterator() {
               return new Iterator<Node>() {
                   int index = 0;

                   @Override
                   public boolean hasNext() {
                       return index < nodes.getLength();
                   }

                   @Override
                   public Node next() {
                       if (hasNext()) {
                           return nodes.item(index++);
                       } else {
                           throw new NoSuchElementException();
                       }
                   }

               };
           }
       };
    }

    public static Stream<Node> nodeChildren(Node node) {
        return StreamSupport.stream(iterable(node.getChildNodes()).spliterator(), false);
    }

    public static Optional<String> getFieldValue(@NotNull Node field) {
        Optional<Node> value = nodeChildren(field)
                .filter(e -> "value".equals(e.getNodeName()))
                .findFirst();

        Optional<Node> text = value.flatMap(
                v -> nodeChildren(v)
                        .filter(e -> "text".equals(e.getNodeName()))
                        .findFirst());

        return text.map(Node::getFirstChild).map(Node::getNodeValue);
    }

    public static Optional<Element> handleField(Document doc, Node field) {
        Node name = field.getAttributes().getNamedItem("name");
        String nodeName = "#field";
        if (name != null) {
            nodeName = name.getNodeValue();
        }

        if (getBindValue(field).map(e -> e.equals("none")).orElse(false)) {
            return Optional.empty();
        }
        Element element = doc.createElement(nodeName);
        getFieldValue(field).ifPresent(element::setTextContent);
        return Optional.of(element);
    }

    public static Optional<String> getBindValue(Node next) {
        return nodeChildren(next)
                .filter(e -> e.getNodeName().equals("bind"))
                .findFirst()
                .map(bind -> ((Element) bind).getAttribute("match"));
    }

    public static Optional<Element> handleSubform(Document doc, Node subform) {
        Node name = subform.getAttributes().getNamedItem("name");
        String nodeName = "#subform";
        if (name != null) {
            nodeName = name.getNodeValue();
        }
        Element element = doc.createElement(nodeName);

        nodeChildren(subform)
                .forEach(child -> {
                    String childName = child.getNodeName();
                    if (childName.equals("subform")) {
                        handleSubform(doc, child).ifPresent(element::appendChild);
                    } else if (childName.equals("field")) {
                        handleField(doc, child).ifPresent(element::appendChild);
                    }
                });

        return Optional.of(element);
    }

    public static Document createDatasetFromTemplate(Node template) {
        Document doc = newEmptyDocument();
        if (template == null) {
            return doc;
        }

        assert "template".equals(template.getNodeName()) : "The root node must be a template.";
        Node nextNode = template.getFirstChild();
        while (nextNode != null) {
            if (nextNode.getNodeType() == Node.ELEMENT_NODE) {
                String s = nextNode.getNodeName();
                if ("subform".equals(s)) {
                    handleSubform(doc, nextNode).ifPresent(doc::appendChild);
                }
            }
            nextNode = nextNode.getNextSibling();
        }
        return doc;
    }

    public static Document createDatasetFromTemplate(Document xml) {
        return createDatasetFromTemplate(xml.getFirstChild());
    }

    public static Document createDatasetFromTemplate(String xml) {
        if (xml == null) {
            return newEmptyDocument();
        }
        return createDatasetFromTemplate(DatasetGen.convertStringToDocument(xml));
    }
}
