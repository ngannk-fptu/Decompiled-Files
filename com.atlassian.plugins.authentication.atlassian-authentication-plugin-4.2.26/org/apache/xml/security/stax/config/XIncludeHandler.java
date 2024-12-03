/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.stax.config;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import org.apache.xml.security.utils.ClassLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class XIncludeHandler
extends DefaultHandler {
    private static final transient Logger LOG = LoggerFactory.getLogger(XIncludeHandler.class);
    private static final String xIncludeNS = "http://www.w3.org/2001/XInclude";
    private static final String xIncludeLN = "include";
    private final ContentHandler contentHandler;
    private URL systemId;
    private boolean skipEvents = false;
    private final Map<URI, Document> uriDocMap;

    public XIncludeHandler(ContentHandler contentHandler) {
        this(contentHandler, new HashMap<URI, Document>());
    }

    private XIncludeHandler(ContentHandler contentHandler, Map<URI, Document> uriDocMap) {
        this.contentHandler = contentHandler;
        this.uriDocMap = uriDocMap;
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        if (locator.getSystemId() == null && this.systemId == null) {
            throw new UnsupportedOperationException("Please specify a correct systemId to the sax.parse() method!");
        }
        try {
            if (locator.getSystemId() != null) {
                this.systemId = new URL(locator.getSystemId());
            }
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
        this.contentHandler.setDocumentLocator(locator);
    }

    @Override
    public void startDocument() throws SAXException {
        if (!this.skipEvents) {
            this.contentHandler.startDocument();
        }
    }

    @Override
    public void endDocument() throws SAXException {
        if (!this.skipEvents) {
            this.contentHandler.endDocument();
        }
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        if (!this.skipEvents) {
            this.contentHandler.startPrefixMapping(prefix, uri);
        }
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        if (!this.skipEvents) {
            this.contentHandler.endPrefixMapping(prefix);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (xIncludeNS.equals(uri) && xIncludeLN.equals(localName)) {
            String href = atts.getValue("href");
            if (href == null) {
                throw new SAXException("XInclude href attribute is missing");
            }
            String parse = atts.getValue("parse");
            if (parse != null && !"xml".equals(parse)) {
                throw new UnsupportedOperationException("Only parse=\"xml\" is currently supported");
            }
            String xpointer = atts.getValue("xpointer");
            URL url = ClassLoaderUtils.getResource(href, XIncludeHandler.class);
            if (url == null) {
                throw new SAXException("XML file not found: " + href);
            }
            Document document = null;
            try {
                document = this.uriDocMap.get(url.toURI());
            }
            catch (URISyntaxException ex) {
                throw new SAXException(ex);
            }
            if (document == null) {
                DOMResult domResult = new DOMResult();
                try {
                    XMLReader xmlReader = XMLReaderFactory.createXMLReader();
                    SAXTransformerFactory saxTransformerFactory = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
                    saxTransformerFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
                    try {
                        saxTransformerFactory.setAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD", "");
                        saxTransformerFactory.setAttribute("http://javax.xml.XMLConstants/property/accessExternalStylesheet", "");
                    }
                    catch (IllegalArgumentException illegalArgumentException) {
                        // empty catch block
                    }
                    TransformerHandler transformerHandler = saxTransformerFactory.newTransformerHandler();
                    transformerHandler.setResult(domResult);
                    xmlReader.setContentHandler(new XIncludeHandler(transformerHandler, this.uriDocMap));
                    xmlReader.parse(url.toExternalForm());
                }
                catch (TransformerConfigurationException e) {
                    throw new SAXException(e);
                }
                catch (IOException e) {
                    throw new SAXException(e);
                }
                document = (Document)domResult.getNode();
                document.setDocumentURI(url.toExternalForm());
                try {
                    this.uriDocMap.put(url.toURI(), document);
                }
                catch (URISyntaxException e) {
                    throw new SAXException(e);
                }
            }
            SAXResult saxResult = new SAXResult(this);
            this.skipEvents = true;
            try {
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                transformerFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
                try {
                    transformerFactory.setAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD", "");
                    transformerFactory.setAttribute("http://javax.xml.XMLConstants/property/accessExternalStylesheet", "");
                }
                catch (IllegalArgumentException saxTransformerFactory) {
                    // empty catch block
                }
                Transformer transformer = transformerFactory.newTransformer();
                if (xpointer == null) {
                    transformer.transform(new DOMSource(document, document.getDocumentURI()), saxResult);
                }
                NodeList nodeList = this.evaluateXPointer(xpointer, document);
                int length = nodeList.getLength();
                for (int i = 0; i < length; ++i) {
                    Node node = nodeList.item(i);
                    transformer.transform(new DOMSource(node, document.getDocumentURI()), saxResult);
                }
            }
            catch (TransformerConfigurationException e) {
                throw new SAXException(e);
            }
            catch (TransformerException e) {
                throw new SAXException(e);
            }
            finally {
                this.skipEvents = false;
            }
        } else {
            this.contentHandler.startElement(uri, localName, qName, atts);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (!xIncludeNS.equals(uri) || !xIncludeLN.equals(localName)) {
            this.contentHandler.endElement(uri, localName, qName);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        this.contentHandler.characters(ch, start, length);
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        this.contentHandler.ignorableWhitespace(ch, start, length);
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        this.contentHandler.processingInstruction(target, data);
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        this.contentHandler.skippedEntity(name);
    }

    @Override
    public void warning(SAXParseException e) throws SAXException {
        LOG.warn(e.getMessage(), (Throwable)e);
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        LOG.error(e.getMessage(), (Throwable)e);
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        LOG.error(e.getMessage(), (Throwable)e);
    }

    private NodeList evaluateXPointer(String xpointer, Node node) throws SAXException {
        String xPointerSchemeString = "xpointer(";
        String xmlnsSchemeString = "xmlns(";
        int xPointerSchemeIndex = xpointer.indexOf("xpointer(");
        if (xPointerSchemeIndex < 0) {
            throw new SAXException("Only xpointer scheme is supported ATM");
        }
        int xPointerSchemeEndIndex = this.findBalancedEndIndex(xpointer, xPointerSchemeIndex += "xpointer(".length(), '(', ')');
        XPathFactory xPathFactory = XPathFactory.newInstance();
        try {
            xPathFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
        }
        catch (XPathFactoryConfigurationException ex) {
            throw new SAXException(ex);
        }
        XPath xPath = xPathFactory.newXPath();
        int xmlnsSchemeIndex = xpointer.indexOf("xmlns(");
        if (xmlnsSchemeIndex >= 0) {
            int xmlnsSchemeEndIndex = this.findBalancedEndIndex(xpointer, xmlnsSchemeIndex += "xmlns(".length(), '(', ')');
            String namespaceScheme = xpointer.substring(xmlnsSchemeIndex, xmlnsSchemeEndIndex);
            final String[] namespaceSplit = namespaceScheme.split("=");
            xPath.setNamespaceContext(new NamespaceContext(){

                @Override
                public String getNamespaceURI(String prefix) {
                    if (prefix.equals(namespaceSplit[0])) {
                        return namespaceSplit[1];
                    }
                    return null;
                }

                @Override
                public String getPrefix(String namespaceURI) {
                    if (namespaceURI.equals(namespaceSplit[1])) {
                        return namespaceSplit[0];
                    }
                    return null;
                }

                @Override
                public Iterator<String> getPrefixes(String namespaceURI) {
                    return null;
                }
            });
        }
        try {
            return (NodeList)xPath.evaluate(xpointer.substring(xPointerSchemeIndex, xPointerSchemeEndIndex), node, XPathConstants.NODESET);
        }
        catch (XPathExpressionException e) {
            throw new SAXException(e);
        }
    }

    private int findBalancedEndIndex(String string, int startIndex, char opening, char ending) {
        int endIndex = -1;
        int openPar = 1;
        int length = string.length();
        for (int i = startIndex; i < length; ++i) {
            char curChar = string.charAt(i);
            if (curChar == opening) {
                ++openPar;
            } else if (curChar == ending) {
                --openPar;
            }
            if (openPar != 0) continue;
            endIndex = i;
            break;
        }
        return endIndex;
    }
}

