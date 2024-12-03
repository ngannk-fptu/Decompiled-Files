/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class XMLDocumentHelper {
    private final Document document;
    private final Map<Node, Node> elementMapping;
    private final String sourcePublicID;
    private final String sourceSystemID;

    XMLDocumentHelper(Document doc, Map<Node, Node> elemMap, String pubID, String sysID) {
        this.document = doc;
        this.elementMapping = elemMap;
        this.sourcePublicID = pubID;
        this.sourceSystemID = sysID;
    }

    public static XMLDocumentHelper forNewDocument(String rootElementName) throws ConfigurationException {
        Document doc = XMLDocumentHelper.createDocumentBuilder(XMLDocumentHelper.createDocumentBuilderFactory()).newDocument();
        Element rootElem = doc.createElement(rootElementName);
        doc.appendChild(rootElem);
        return new XMLDocumentHelper(doc, XMLDocumentHelper.emptyElementMapping(), null, null);
    }

    public static XMLDocumentHelper forSourceDocument(Document srcDoc) throws ConfigurationException {
        String sysID;
        String pubID;
        if (srcDoc.getDoctype() != null) {
            pubID = srcDoc.getDoctype().getPublicId();
            sysID = srcDoc.getDoctype().getSystemId();
        } else {
            pubID = null;
            sysID = null;
        }
        return new XMLDocumentHelper(XMLDocumentHelper.copyDocument(srcDoc), XMLDocumentHelper.emptyElementMapping(), pubID, sysID);
    }

    public Document getDocument() {
        return this.document;
    }

    public Map<Node, Node> getElementMapping() {
        return this.elementMapping;
    }

    public String getSourcePublicID() {
        return this.sourcePublicID;
    }

    public String getSourceSystemID() {
        return this.sourceSystemID;
    }

    public static Transformer createTransformer() throws ConfigurationException {
        return XMLDocumentHelper.createTransformer(XMLDocumentHelper.createTransformerFactory());
    }

    public static void transform(Transformer transformer, Source source, Result result) throws ConfigurationException {
        try {
            transformer.transform(source, result);
        }
        catch (TransformerException tex) {
            throw new ConfigurationException(tex);
        }
    }

    public XMLDocumentHelper createCopy() throws ConfigurationException {
        Document docCopy = XMLDocumentHelper.copyDocument(this.getDocument());
        return new XMLDocumentHelper(docCopy, XMLDocumentHelper.createElementMapping(this.getDocument(), docCopy), this.getSourcePublicID(), this.getSourceSystemID());
    }

    static TransformerFactory createTransformerFactory() {
        return TransformerFactory.newInstance();
    }

    static Transformer createTransformer(TransformerFactory factory) throws ConfigurationException {
        try {
            return factory.newTransformer();
        }
        catch (TransformerConfigurationException tex) {
            throw new ConfigurationException(tex);
        }
    }

    static DocumentBuilder createDocumentBuilder(DocumentBuilderFactory factory) throws ConfigurationException {
        try {
            return factory.newDocumentBuilder();
        }
        catch (ParserConfigurationException pcex) {
            throw new ConfigurationException(pcex);
        }
    }

    private static Document copyDocument(Document doc) throws ConfigurationException {
        Transformer transformer = XMLDocumentHelper.createTransformer();
        DOMSource source = new DOMSource(doc);
        DOMResult result = new DOMResult();
        XMLDocumentHelper.transform(transformer, source, result);
        return (Document)result.getNode();
    }

    private static DocumentBuilderFactory createDocumentBuilderFactory() {
        return DocumentBuilderFactory.newInstance();
    }

    private static Map<Node, Node> emptyElementMapping() {
        return Collections.emptyMap();
    }

    private static Map<Node, Node> createElementMapping(Document doc1, Document doc2) {
        HashMap<Node, Node> mapping = new HashMap<Node, Node>();
        XMLDocumentHelper.createElementMappingForNodes(doc1.getDocumentElement(), doc2.getDocumentElement(), mapping);
        return mapping;
    }

    private static void createElementMappingForNodes(Node n1, Node n2, Map<Node, Node> mapping) {
        mapping.put(n1, n2);
        NodeList childNodes1 = n1.getChildNodes();
        NodeList childNodes2 = n2.getChildNodes();
        int count = Math.min(childNodes1.getLength(), childNodes2.getLength());
        for (int i = 0; i < count; ++i) {
            XMLDocumentHelper.createElementMappingForNodes(childNodes1.item(i), childNodes2.item(i), mapping);
        }
    }
}

