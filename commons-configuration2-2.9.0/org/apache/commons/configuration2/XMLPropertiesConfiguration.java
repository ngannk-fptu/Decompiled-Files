/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.text.StringEscapeUtils
 */
package org.apache.commons.configuration2;

import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileLocator;
import org.apache.commons.configuration2.io.FileLocatorAware;
import org.apache.commons.text.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class XMLPropertiesConfiguration
extends BaseConfiguration
implements FileBasedConfiguration,
FileLocatorAware {
    public static final String DEFAULT_ENCODING = "UTF-8";
    private static final String MALFORMED_XML_EXCEPTION = "Malformed XML";
    private FileLocator locator;
    private String header;

    public XMLPropertiesConfiguration() {
    }

    public XMLPropertiesConfiguration(Element element) throws ConfigurationException {
        this.load(element);
    }

    public String getHeader() {
        return this.header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    @Override
    public void read(Reader in) throws ConfigurationException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(false);
        factory.setValidating(true);
        try {
            SAXParser parser = factory.newSAXParser();
            XMLReader xmlReader = parser.getXMLReader();
            xmlReader.setEntityResolver((publicId, systemId) -> new InputSource(this.getClass().getClassLoader().getResourceAsStream("properties.dtd")));
            xmlReader.setContentHandler(new XMLPropertiesHandler());
            xmlReader.parse(new InputSource(in));
        }
        catch (Exception e) {
            throw new ConfigurationException("Unable to parse the configuration file", e);
        }
    }

    public void load(Element element) throws ConfigurationException {
        if (!element.getNodeName().equals("properties")) {
            throw new ConfigurationException(MALFORMED_XML_EXCEPTION);
        }
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            Node item = childNodes.item(i);
            if (!(item instanceof Element)) continue;
            if (item.getNodeName().equals("comment")) {
                this.setHeader(item.getTextContent());
                continue;
            }
            if (item.getNodeName().equals("entry")) {
                String key = ((Element)item).getAttribute("key");
                this.addProperty(key, item.getTextContent());
                continue;
            }
            throw new ConfigurationException(MALFORMED_XML_EXCEPTION);
        }
    }

    @Override
    public void write(Writer out) throws ConfigurationException {
        String encoding;
        PrintWriter writer = new PrintWriter(out);
        String string = encoding = this.locator != null ? this.locator.getEncoding() : null;
        if (encoding == null) {
            encoding = DEFAULT_ENCODING;
        }
        writer.println("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");
        writer.println("<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">");
        writer.println("<properties>");
        if (this.getHeader() != null) {
            writer.println("  <comment>" + StringEscapeUtils.escapeXml10((String)this.getHeader()) + "</comment>");
        }
        Iterator<String> keys = this.getKeys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = this.getProperty(key);
            if (value instanceof List) {
                this.writeProperty(writer, key, (List)value);
                continue;
            }
            this.writeProperty(writer, key, value);
        }
        writer.println("</properties>");
        writer.flush();
    }

    private void writeProperty(PrintWriter out, String key, Object value) {
        String k = StringEscapeUtils.escapeXml10((String)key);
        if (value != null) {
            String v = this.escapeValue(value);
            out.println("  <entry key=\"" + k + "\">" + v + "</entry>");
        } else {
            out.println("  <entry key=\"" + k + "\"/>");
        }
    }

    private void writeProperty(PrintWriter out, String key, List<?> values) {
        values.forEach(value -> this.writeProperty(out, key, value));
    }

    public void save(Document document, Node parent) {
        Element properties = document.createElement("properties");
        parent.appendChild(properties);
        if (this.getHeader() != null) {
            Element comment = document.createElement("comment");
            properties.appendChild(comment);
            comment.setTextContent(StringEscapeUtils.escapeXml10((String)this.getHeader()));
        }
        Iterator<String> keys = this.getKeys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = this.getProperty(key);
            if (value instanceof List) {
                this.writeProperty(document, (Node)properties, key, (List)value);
                continue;
            }
            this.writeProperty(document, (Node)properties, key, value);
        }
    }

    @Override
    public void initFileLocator(FileLocator locator) {
        this.locator = locator;
    }

    private void writeProperty(Document document, Node properties, String key, Object value) {
        Element entry = document.createElement("entry");
        properties.appendChild(entry);
        String k = StringEscapeUtils.escapeXml10((String)key);
        entry.setAttribute("key", k);
        if (value != null) {
            String v = this.escapeValue(value);
            entry.setTextContent(v);
        }
    }

    private void writeProperty(Document document, Node properties, String key, List<?> values) {
        values.forEach(value -> this.writeProperty(document, properties, key, value));
    }

    private String escapeValue(Object value) {
        String v = StringEscapeUtils.escapeXml10((String)String.valueOf(value));
        return String.valueOf(this.getListDelimiterHandler().escape(v, ListDelimiterHandler.NOOP_TRANSFORMER));
    }

    private class XMLPropertiesHandler
    extends DefaultHandler {
        private String key;
        private StringBuilder value = new StringBuilder();
        private boolean inCommentElement;
        private boolean inEntryElement;

        private XMLPropertiesHandler() {
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attrs) {
            if ("comment".equals(qName)) {
                this.inCommentElement = true;
            }
            if ("entry".equals(qName)) {
                this.key = attrs.getValue("key");
                this.inEntryElement = true;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            if (this.inCommentElement) {
                XMLPropertiesConfiguration.this.setHeader(this.value.toString());
                this.inCommentElement = false;
            }
            if (this.inEntryElement) {
                XMLPropertiesConfiguration.this.addProperty(this.key, this.value.toString());
                this.inEntryElement = false;
            }
            this.value = new StringBuilder();
        }

        @Override
        public void characters(char[] chars, int start, int length) {
            this.value.append(chars, start, length);
        }
    }
}

