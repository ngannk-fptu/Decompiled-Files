/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import groovy.util.Node;
import groovy.xml.FactorySupport;
import groovy.xml.QName;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

public class XmlParser
implements ContentHandler {
    private StringBuilder bodyText = new StringBuilder();
    private List<Node> stack = new ArrayList<Node>();
    private Locator locator;
    private XMLReader reader;
    private Node parent;
    private boolean trimWhitespace = false;
    private boolean keepIgnorableWhitespace = false;
    private boolean namespaceAware;

    public XmlParser() throws ParserConfigurationException, SAXException {
        this(false, true);
    }

    public XmlParser(boolean validating, boolean namespaceAware) throws ParserConfigurationException, SAXException {
        this(validating, namespaceAware, false);
    }

    public XmlParser(boolean validating, boolean namespaceAware, boolean allowDocTypeDeclaration) throws ParserConfigurationException, SAXException {
        SAXParserFactory factory = FactorySupport.createSaxParserFactory();
        factory.setNamespaceAware(namespaceAware);
        this.namespaceAware = namespaceAware;
        factory.setValidating(validating);
        XmlParser.setQuietly(factory, "http://javax.xml.XMLConstants/feature/secure-processing", true);
        XmlParser.setQuietly(factory, "http://apache.org/xml/features/disallow-doctype-decl", !allowDocTypeDeclaration);
        this.reader = factory.newSAXParser().getXMLReader();
    }

    public XmlParser(XMLReader reader) {
        this.reader = reader;
    }

    public XmlParser(SAXParser parser) throws SAXException {
        this.reader = parser.getXMLReader();
    }

    private static void setQuietly(SAXParserFactory factory, String feature, boolean value) {
        try {
            factory.setFeature(feature, value);
        }
        catch (ParserConfigurationException parserConfigurationException) {
        }
        catch (SAXNotRecognizedException sAXNotRecognizedException) {
        }
        catch (SAXNotSupportedException sAXNotSupportedException) {
            // empty catch block
        }
    }

    public boolean isTrimWhitespace() {
        return this.trimWhitespace;
    }

    public void setTrimWhitespace(boolean trimWhitespace) {
        this.trimWhitespace = trimWhitespace;
    }

    public boolean isKeepIgnorableWhitespace() {
        return this.keepIgnorableWhitespace;
    }

    public void setKeepIgnorableWhitespace(boolean keepIgnorableWhitespace) {
        this.keepIgnorableWhitespace = keepIgnorableWhitespace;
    }

    public Node parse(File file) throws IOException, SAXException {
        InputSource input = new InputSource(new FileInputStream(file));
        input.setSystemId("file://" + file.getAbsolutePath());
        this.getXMLReader().parse(input);
        return this.parent;
    }

    public Node parse(InputSource input) throws IOException, SAXException {
        this.getXMLReader().parse(input);
        return this.parent;
    }

    public Node parse(InputStream input) throws IOException, SAXException {
        InputSource is = new InputSource(input);
        this.getXMLReader().parse(is);
        return this.parent;
    }

    public Node parse(Reader in) throws IOException, SAXException {
        InputSource is = new InputSource(in);
        this.getXMLReader().parse(is);
        return this.parent;
    }

    public Node parse(String uri) throws IOException, SAXException {
        InputSource is = new InputSource(uri);
        this.getXMLReader().parse(is);
        return this.parent;
    }

    public Node parseText(String text) throws IOException, SAXException {
        return this.parse(new StringReader(text));
    }

    public boolean isNamespaceAware() {
        return this.namespaceAware;
    }

    public void setNamespaceAware(boolean namespaceAware) {
        this.namespaceAware = namespaceAware;
    }

    public DTDHandler getDTDHandler() {
        return this.reader.getDTDHandler();
    }

    public EntityResolver getEntityResolver() {
        return this.reader.getEntityResolver();
    }

    public ErrorHandler getErrorHandler() {
        return this.reader.getErrorHandler();
    }

    public boolean getFeature(String uri) throws SAXNotRecognizedException, SAXNotSupportedException {
        return this.reader.getFeature(uri);
    }

    public Object getProperty(String uri) throws SAXNotRecognizedException, SAXNotSupportedException {
        return this.reader.getProperty(uri);
    }

    public void setDTDHandler(DTDHandler dtdHandler) {
        this.reader.setDTDHandler(dtdHandler);
    }

    public void setEntityResolver(EntityResolver entityResolver) {
        this.reader.setEntityResolver(entityResolver);
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.reader.setErrorHandler(errorHandler);
    }

    public void setFeature(String uri, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        this.reader.setFeature(uri, value);
    }

    public void setProperty(String uri, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        this.reader.setProperty(uri, value);
    }

    @Override
    public void startDocument() throws SAXException {
        this.parent = null;
    }

    @Override
    public void endDocument() throws SAXException {
        this.stack.clear();
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes list) throws SAXException {
        this.addTextToNode();
        Object nodeName = this.getElementName(namespaceURI, localName, qName);
        int size = list.getLength();
        LinkedHashMap<Object, String> attributes = new LinkedHashMap<Object, String>(size);
        for (int i = 0; i < size; ++i) {
            Object attributeName = this.getElementName(list.getURI(i), list.getLocalName(i), list.getQName(i));
            String value = list.getValue(i);
            attributes.put(attributeName, value);
        }
        this.parent = this.createNode(this.parent, nodeName, attributes);
        this.stack.add(this.parent);
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        this.addTextToNode();
        if (!this.stack.isEmpty()) {
            this.stack.remove(this.stack.size() - 1);
            if (!this.stack.isEmpty()) {
                this.parent = this.stack.get(this.stack.size() - 1);
            }
        }
    }

    @Override
    public void characters(char[] buffer, int start, int length) throws SAXException {
        this.bodyText.append(buffer, start, length);
    }

    @Override
    public void startPrefixMapping(String prefix, String namespaceURI) throws SAXException {
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
    }

    @Override
    public void ignorableWhitespace(char[] buffer, int start, int len) throws SAXException {
        if (this.keepIgnorableWhitespace) {
            this.characters(buffer, start, len);
        }
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
    }

    public Locator getDocumentLocator() {
        return this.locator;
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
    }

    protected XMLReader getXMLReader() {
        this.reader.setContentHandler(this);
        return this.reader;
    }

    protected void addTextToNode() {
        if (this.parent == null) {
            return;
        }
        String text = this.bodyText.toString();
        if (!this.trimWhitespace && this.keepIgnorableWhitespace) {
            this.parent.children().add(text);
        } else if (!this.trimWhitespace && text.trim().length() > 0) {
            this.parent.children().add(text);
        } else if (text.trim().length() > 0) {
            this.parent.children().add(text.trim());
        }
        this.bodyText = new StringBuilder();
    }

    protected Node createNode(Node parent, Object name, Map attributes) {
        return new Node(parent, name, attributes);
    }

    protected Object getElementName(String namespaceURI, String localName, String qName) {
        int index;
        String name = localName;
        String prefix = "";
        if (name == null || name.length() < 1) {
            name = qName;
        }
        if (namespaceURI == null || namespaceURI.length() <= 0) {
            return name;
        }
        if (qName != null && qName.length() > 0 && this.namespaceAware && (index = qName.lastIndexOf(":")) > 0) {
            prefix = qName.substring(0, index);
        }
        return new QName(namespaceURI, name, prefix);
    }
}

