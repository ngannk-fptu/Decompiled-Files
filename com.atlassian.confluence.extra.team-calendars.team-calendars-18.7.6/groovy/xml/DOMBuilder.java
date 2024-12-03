/*
 * Decompiled with CFR 0.152.
 */
package groovy.xml;

import groovy.util.BuilderSupport;
import groovy.xml.FactorySupport;
import groovy.xml.QName;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DOMBuilder
extends BuilderSupport {
    Document document;
    DocumentBuilder documentBuilder;

    public static DOMBuilder newInstance() throws ParserConfigurationException {
        return DOMBuilder.newInstance(false, true);
    }

    public static DOMBuilder newInstance(boolean validating, boolean namespaceAware) throws ParserConfigurationException {
        DocumentBuilderFactory factory = FactorySupport.createDocumentBuilderFactory();
        factory.setNamespaceAware(namespaceAware);
        factory.setValidating(validating);
        return new DOMBuilder(factory.newDocumentBuilder());
    }

    public static Document parse(Reader reader) throws SAXException, IOException, ParserConfigurationException {
        return DOMBuilder.parse(reader, false, true);
    }

    public static Document parse(Reader reader, boolean validating, boolean namespaceAware) throws SAXException, IOException, ParserConfigurationException {
        return DOMBuilder.parse(reader, validating, namespaceAware, false);
    }

    public static Document parse(Reader reader, boolean validating, boolean namespaceAware, boolean allowDocTypeDeclaration) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory factory = FactorySupport.createDocumentBuilderFactory();
        factory.setNamespaceAware(namespaceAware);
        factory.setValidating(validating);
        DOMBuilder.setQuietly(factory, "http://javax.xml.XMLConstants/feature/secure-processing", true);
        DOMBuilder.setQuietly(factory, "http://apache.org/xml/features/disallow-doctype-decl", !allowDocTypeDeclaration);
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        return documentBuilder.parse(new InputSource(reader));
    }

    private static void setQuietly(DocumentBuilderFactory factory, String feature, boolean value) {
        try {
            factory.setFeature(feature, value);
        }
        catch (ParserConfigurationException parserConfigurationException) {
            // empty catch block
        }
    }

    public Document parseText(String text) throws SAXException, IOException, ParserConfigurationException {
        return DOMBuilder.parse(new StringReader(text));
    }

    public DOMBuilder(Document document) {
        this.document = document;
    }

    public DOMBuilder(DocumentBuilder documentBuilder) {
        this.documentBuilder = documentBuilder;
    }

    @Override
    protected void setParent(Object parent, Object child) {
        Node current = (Node)parent;
        Node node = (Node)child;
        current.appendChild(node);
    }

    @Override
    protected Object createNode(Object name) {
        if (this.document == null) {
            this.document = this.createDocument();
        }
        if (name instanceof QName) {
            QName qname = (QName)name;
            return this.document.createElementNS(qname.getNamespaceURI(), qname.getQualifiedName());
        }
        return this.document.createElement(name.toString());
    }

    protected Document createDocument() {
        if (this.documentBuilder == null) {
            throw new IllegalArgumentException("No Document or DOMImplementation available so cannot create Document");
        }
        return this.documentBuilder.newDocument();
    }

    @Override
    protected Object createNode(Object name, Object value) {
        Element element = (Element)this.createNode(name);
        element.appendChild(this.document.createTextNode(value.toString()));
        return element;
    }

    @Override
    protected Object createNode(Object name, Map attributes, Object value) {
        Element element = (Element)this.createNode(name, attributes);
        element.appendChild(this.document.createTextNode(value.toString()));
        return element;
    }

    @Override
    protected Object createNode(Object name, Map attributes) {
        Element element = (Element)this.createNode(name);
        for (Map.Entry entry : attributes.entrySet()) {
            String attrName = entry.getKey().toString();
            Object value = entry.getValue();
            if ("xmlns".equals(attrName)) {
                if (value instanceof Map) {
                    this.appendNamespaceAttributes(element, (Map)value);
                    continue;
                }
                if (value instanceof String) {
                    DOMBuilder.setStringNS(element, "", value);
                    continue;
                }
                throw new IllegalArgumentException("The value of the xmlns attribute must be a Map of QNames to String URIs");
            }
            if (attrName.startsWith("xmlns:") && value instanceof String) {
                DOMBuilder.setStringNS(element, attrName.substring(6), value);
                continue;
            }
            String valueText = value != null ? value.toString() : "";
            element.setAttribute(attrName, valueText);
        }
        return element;
    }

    protected void appendNamespaceAttributes(Element element, Map<Object, Object> attributes) {
        for (Map.Entry<Object, Object> entry : attributes.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (value == null) {
                throw new IllegalArgumentException("The value of key: " + key + " cannot be null");
            }
            if (key instanceof String) {
                DOMBuilder.setStringNS(element, key, value);
                continue;
            }
            if (key instanceof QName) {
                QName qname = (QName)key;
                element.setAttributeNS(qname.getNamespaceURI(), qname.getQualifiedName(), value.toString());
                continue;
            }
            throw new IllegalArgumentException("The key: " + key + " should be an instanceof of " + QName.class);
        }
    }

    private static void setStringNS(Element element, Object key, Object value) {
        String prefix = (String)key;
        element.setAttributeNS("http://www.w3.org/2000/xmlns/", "".equals(prefix) ? "xmlns" : "xmlns:" + prefix, value.toString());
    }
}

