/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XmlProperties
extends Properties {
    static final String DTD_SYSTEM_ID = "http://www.mchange.com/dtd/xml-properties.dtd";
    static final String DTD_RSRC_PATH = "dtd/xml-properties.dtd";
    DocumentBuilder docBuilder;
    Transformer identityTransformer;

    public XmlProperties() throws ParserConfigurationException, TransformerConfigurationException {
        EntityResolver entityResolver = new EntityResolver(){

            @Override
            public InputSource resolveEntity(String string, String string2) {
                if (XmlProperties.DTD_SYSTEM_ID.equals(string2)) {
                    InputStream inputStream = XmlProperties.class.getResourceAsStream(XmlProperties.DTD_RSRC_PATH);
                    return new InputSource(inputStream);
                }
                return null;
            }
        };
        ErrorHandler errorHandler = new ErrorHandler(){

            @Override
            public void warning(SAXParseException sAXParseException) throws SAXException {
                System.err.println("[Warning] " + sAXParseException.toString());
            }

            @Override
            public void error(SAXParseException sAXParseException) throws SAXException {
                System.err.println("[Error] " + sAXParseException.toString());
            }

            @Override
            public void fatalError(SAXParseException sAXParseException) throws SAXException {
                System.err.println("[Fatal Error] " + sAXParseException.toString());
            }
        };
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setValidating(true);
        documentBuilderFactory.setCoalescing(true);
        documentBuilderFactory.setIgnoringComments(true);
        this.docBuilder = documentBuilderFactory.newDocumentBuilder();
        this.docBuilder.setEntityResolver(entityResolver);
        this.docBuilder.setErrorHandler(errorHandler);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        this.identityTransformer = transformerFactory.newTransformer();
        this.identityTransformer.setOutputProperty("indent", "yes");
        this.identityTransformer.setOutputProperty("doctype-system", DTD_SYSTEM_ID);
    }

    public synchronized void loadXml(InputStream inputStream) throws IOException, SAXException {
        Document document = this.docBuilder.parse(inputStream);
        NodeList nodeList = document.getElementsByTagName("property");
        int n = nodeList.getLength();
        for (int i = 0; i < n; ++i) {
            this.extractProperty(nodeList.item(i));
        }
    }

    private void extractProperty(Node node) {
        String string;
        Element element = (Element)node;
        String string2 = element.getAttribute("name");
        boolean bl = Boolean.valueOf(element.getAttribute("trim"));
        NodeList nodeList = element.getChildNodes();
        int n = nodeList.getLength();
        assert (n >= 0 && n <= 1) : "Bad number of children of property element: " + n;
        String string3 = string = n == 0 ? "" : ((Text)nodeList.item(0)).getNodeValue();
        if (bl) {
            string = string.trim();
        }
        this.put(string2, string);
    }

    public synchronized void saveXml(OutputStream outputStream) throws IOException, TransformerException {
        this.storeXml(outputStream, null);
    }

    public synchronized void storeXml(OutputStream outputStream, String string) throws IOException, TransformerException {
        Node node;
        Document document = this.docBuilder.newDocument();
        if (string != null) {
            node = document.createComment(string);
            document.appendChild(node);
        }
        node = document.createElement("xml-properties");
        Iterator<Object> iterator = this.keySet().iterator();
        while (iterator.hasNext()) {
            Element element = document.createElement("property");
            String string2 = (String)iterator.next();
            String string3 = (String)this.get(string2);
            element.setAttribute("name", string2);
            Text text = document.createTextNode(string3);
            element.appendChild(text);
            node.appendChild(element);
        }
        document.appendChild(node);
        this.identityTransformer.transform(new DOMSource(document), new StreamResult(outputStream));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void main(String[] stringArray) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(stringArray[0]));
            outputStream = new BufferedOutputStream(new FileOutputStream(stringArray[1]));
            XmlProperties xmlProperties = new XmlProperties();
            xmlProperties.loadXml(inputStream);
            xmlProperties.list(System.out);
            xmlProperties.storeXml(outputStream, "This is the resaved test document.");
            outputStream.flush();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}

