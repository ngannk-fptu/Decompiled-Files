/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.xml.XMLEntityResolver
 *  com.atlassian.confluence.xml.XhtmlEntityResolver
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.highlight.xml;

import com.atlassian.confluence.xml.XMLEntityResolver;
import com.atlassian.confluence.xml.XhtmlEntityResolver;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Component
public class XMLParserHelper {
    private static final String WRAPPING_ELEMENT_START = "<root>";
    private static final String WRAPPING_ELEMENT_END = "</root>";
    private final DocumentBuilder documentBuilder;
    private final XMLEntityResolver xmlEntityResolver = new XhtmlEntityResolver();

    public XMLParserHelper() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            this.documentBuilder = factory.newDocumentBuilder();
            this.documentBuilder.setEntityResolver((EntityResolver)this.xmlEntityResolver);
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static String documentToString(Document document) {
        try {
            StringWriter writer = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("omit-xml-declaration", "yes");
            transformer.setOutputProperty("encoding", "UTF-16");
            StreamResult output = new StreamResult(writer);
            DOMSource input = new DOMSource(document);
            transformer.transform(input, output);
            String xml = writer.toString();
            if (xml.startsWith(WRAPPING_ELEMENT_START)) {
                xml = xml.substring(WRAPPING_ELEMENT_START.length(), xml.length() - WRAPPING_ELEMENT_END.length());
            }
            return xml;
        }
        catch (TransformerException e) {
            throw new RuntimeException("Unexpected error", e);
        }
    }

    public DocumentFragment parseDocumentFragment(Document targetDocument, String xml) throws SAXException {
        try {
            InputSource xmlSource = new InputSource(new StringReader(this.addEntityDTD(xml)));
            Document document = this.documentBuilder.parse(xmlSource);
            DocumentFragment fragment = targetDocument.createDocumentFragment();
            NodeList childNodes = document.getDocumentElement().getChildNodes();
            for (int i = 0; i < childNodes.getLength(); ++i) {
                Node child = childNodes.item(i);
                fragment.appendChild(targetDocument.adoptNode(child));
            }
            return fragment;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Document parseDocument(String xml) throws SAXException {
        try {
            InputSource xmlSource = new InputSource(new StringReader(this.addEntityDTD(xml)));
            return this.documentBuilder.parse(xmlSource);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String addEntityDTD(String xml) {
        return "<!DOCTYPE xml [ " + this.xmlEntityResolver.createDTD() + "]>\n<root>" + xml + WRAPPING_ELEMENT_END;
    }
}

