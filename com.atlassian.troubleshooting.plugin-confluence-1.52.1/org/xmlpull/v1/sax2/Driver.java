/*
 * Decompiled with CFR 0.152.
 */
package org.xmlpull.v1.sax2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
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
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class Driver
implements Locator,
XMLReader,
Attributes {
    protected static final String DECLARATION_HANDLER_PROPERTY = "http://xml.org/sax/properties/declaration-handler";
    protected static final String LEXICAL_HANDLER_PROPERTY = "http://xml.org/sax/properties/lexical-handler";
    protected static final String NAMESPACES_FEATURE = "http://xml.org/sax/features/namespaces";
    protected static final String NAMESPACE_PREFIXES_FEATURE = "http://xml.org/sax/features/namespace-prefixes";
    protected static final String VALIDATION_FEATURE = "http://xml.org/sax/features/validation";
    protected static final String APACHE_SCHEMA_VALIDATION_FEATURE = "http://apache.org/xml/features/validation/schema";
    protected static final String APACHE_DYNAMIC_VALIDATION_FEATURE = "http://apache.org/xml/features/validation/dynamic";
    protected ContentHandler contentHandler = new DefaultHandler();
    protected ErrorHandler errorHandler = new DefaultHandler();
    protected String systemId;
    protected XmlPullParser pp;

    public Driver() throws XmlPullParserException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        this.pp = factory.newPullParser();
    }

    public Driver(XmlPullParser pp) throws XmlPullParserException {
        this.pp = pp;
    }

    public int getLength() {
        return this.pp.getAttributeCount();
    }

    public String getURI(int index) {
        return this.pp.getAttributeNamespace(index);
    }

    public String getLocalName(int index) {
        return this.pp.getAttributeName(index);
    }

    public String getQName(int index) {
        String prefix = this.pp.getAttributePrefix(index);
        if (prefix != null) {
            return prefix + ':' + this.pp.getAttributeName(index);
        }
        return this.pp.getAttributeName(index);
    }

    public String getType(int index) {
        return this.pp.getAttributeType(index);
    }

    public String getValue(int index) {
        return this.pp.getAttributeValue(index);
    }

    public int getIndex(String uri, String localName) {
        for (int i = 0; i < this.pp.getAttributeCount(); ++i) {
            if (!this.pp.getAttributeNamespace(i).equals(uri) || !this.pp.getAttributeName(i).equals(localName)) continue;
            return i;
        }
        return -1;
    }

    public int getIndex(String qName) {
        for (int i = 0; i < this.pp.getAttributeCount(); ++i) {
            if (!this.pp.getAttributeName(i).equals(qName)) continue;
            return i;
        }
        return -1;
    }

    public String getType(String uri, String localName) {
        for (int i = 0; i < this.pp.getAttributeCount(); ++i) {
            if (!this.pp.getAttributeNamespace(i).equals(uri) || !this.pp.getAttributeName(i).equals(localName)) continue;
            return this.pp.getAttributeType(i);
        }
        return null;
    }

    public String getType(String qName) {
        for (int i = 0; i < this.pp.getAttributeCount(); ++i) {
            if (!this.pp.getAttributeName(i).equals(qName)) continue;
            return this.pp.getAttributeType(i);
        }
        return null;
    }

    public String getValue(String uri, String localName) {
        return this.pp.getAttributeValue(uri, localName);
    }

    public String getValue(String qName) {
        return this.pp.getAttributeValue(null, qName);
    }

    public String getPublicId() {
        return null;
    }

    public String getSystemId() {
        return this.systemId;
    }

    public int getLineNumber() {
        return this.pp.getLineNumber();
    }

    public int getColumnNumber() {
        return this.pp.getColumnNumber();
    }

    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (NAMESPACES_FEATURE.equals(name)) {
            return this.pp.getFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces");
        }
        if (NAMESPACE_PREFIXES_FEATURE.equals(name)) {
            return this.pp.getFeature("http://xmlpull.org/v1/doc/features.html#report-namespace-prefixes");
        }
        if (VALIDATION_FEATURE.equals(name)) {
            return this.pp.getFeature("http://xmlpull.org/v1/doc/features.html#validation");
        }
        return this.pp.getFeature(name);
    }

    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            if (NAMESPACES_FEATURE.equals(name)) {
                this.pp.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", value);
            } else if (NAMESPACE_PREFIXES_FEATURE.equals(name)) {
                if (this.pp.getFeature("http://xmlpull.org/v1/doc/features.html#report-namespace-prefixes") != value) {
                    this.pp.setFeature("http://xmlpull.org/v1/doc/features.html#report-namespace-prefixes", value);
                }
            } else if (VALIDATION_FEATURE.equals(name)) {
                this.pp.setFeature("http://xmlpull.org/v1/doc/features.html#validation", value);
            } else {
                this.pp.setFeature(name, value);
            }
        }
        catch (XmlPullParserException ex) {
            throw new SAXNotSupportedException("problem with setting feature " + name + ": " + ex);
        }
    }

    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (DECLARATION_HANDLER_PROPERTY.equals(name)) {
            return null;
        }
        if (LEXICAL_HANDLER_PROPERTY.equals(name)) {
            return null;
        }
        return this.pp.getProperty(name);
    }

    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (DECLARATION_HANDLER_PROPERTY.equals(name)) {
            throw new SAXNotSupportedException("not supported setting property " + name);
        }
        if (LEXICAL_HANDLER_PROPERTY.equals(name)) {
            throw new SAXNotSupportedException("not supported setting property " + name);
        }
        try {
            this.pp.setProperty(name, value);
        }
        catch (XmlPullParserException ex) {
            throw new SAXNotSupportedException("not supported set property " + name + ": " + ex);
        }
    }

    public void setEntityResolver(EntityResolver resolver) {
    }

    public EntityResolver getEntityResolver() {
        return null;
    }

    public void setDTDHandler(DTDHandler handler) {
    }

    public DTDHandler getDTDHandler() {
        return null;
    }

    public void setContentHandler(ContentHandler handler) {
        this.contentHandler = handler;
    }

    public ContentHandler getContentHandler() {
        return this.contentHandler;
    }

    public void setErrorHandler(ErrorHandler handler) {
        this.errorHandler = handler;
    }

    public ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    public void parse(InputSource source) throws SAXException, IOException {
        block12: {
            this.systemId = source.getSystemId();
            this.contentHandler.setDocumentLocator(this);
            Reader reader = source.getCharacterStream();
            try {
                if (reader == null) {
                    InputStream stream = source.getByteStream();
                    String encoding = source.getEncoding();
                    if (stream == null) {
                        this.systemId = source.getSystemId();
                        if (this.systemId == null) {
                            SAXParseException saxException = new SAXParseException("null source systemId", this);
                            this.errorHandler.fatalError(saxException);
                            return;
                        }
                        try {
                            URL url = new URL(this.systemId);
                            stream = url.openStream();
                        }
                        catch (MalformedURLException nue) {
                            try {
                                stream = new FileInputStream(this.systemId);
                            }
                            catch (FileNotFoundException fnfe) {
                                SAXParseException saxException = new SAXParseException("could not open file with systemId " + this.systemId, this, fnfe);
                                this.errorHandler.fatalError(saxException);
                                return;
                            }
                        }
                    }
                    this.pp.setInput(stream, encoding);
                    break block12;
                }
                this.pp.setInput(reader);
            }
            catch (XmlPullParserException ex) {
                SAXParseException saxException = new SAXParseException("parsing initialization error: " + ex, this, ex);
                this.errorHandler.fatalError(saxException);
                return;
            }
        }
        try {
            this.contentHandler.startDocument();
            this.pp.next();
            if (this.pp.getEventType() != 2) {
                SAXParseException saxException = new SAXParseException("expected start tag not" + this.pp.getPositionDescription(), this);
                this.errorHandler.fatalError(saxException);
                return;
            }
        }
        catch (XmlPullParserException ex) {
            SAXParseException saxException = new SAXParseException("parsing initialization error: " + ex, this, ex);
            this.errorHandler.fatalError(saxException);
            return;
        }
        this.parseSubTree(this.pp);
        this.contentHandler.endDocument();
    }

    public void parse(String systemId) throws SAXException, IOException {
        this.parse(new InputSource(systemId));
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void parseSubTree(XmlPullParser pp) throws SAXException, IOException {
        this.pp = pp;
        boolean namespaceAware = pp.getFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces");
        try {
            if (pp.getEventType() != 2) {
                throw new SAXException("start tag must be read before skiping subtree" + pp.getPositionDescription());
            }
            int[] holderForStartAndLength = new int[2];
            StringBuffer rawName = new StringBuffer(16);
            String prefix = null;
            String name = null;
            int level = pp.getDepth() - 1;
            int type = 2;
            do {
                switch (type) {
                    case 2: {
                        if (namespaceAware) {
                            int depth = pp.getDepth() - 1;
                            int countPrev = level > depth ? pp.getNamespaceCount(depth) : 0;
                            int count = pp.getNamespaceCount(depth + 1);
                            for (int i = countPrev; i < count; ++i) {
                                this.contentHandler.startPrefixMapping(pp.getNamespacePrefix(i), pp.getNamespaceUri(i));
                            }
                            name = pp.getName();
                            prefix = pp.getPrefix();
                            if (prefix != null) {
                                rawName.setLength(0);
                                rawName.append(prefix);
                                rawName.append(':');
                                rawName.append(name);
                            }
                            this.startElement(pp.getNamespace(), name, prefix != null ? rawName.toString() : name);
                            break;
                        }
                        this.startElement(pp.getNamespace(), pp.getName(), pp.getName());
                        break;
                    }
                    case 4: {
                        char[] chars = pp.getTextCharacters(holderForStartAndLength);
                        this.contentHandler.characters(chars, holderForStartAndLength[0], holderForStartAndLength[1]);
                        break;
                    }
                    case 3: {
                        if (namespaceAware) {
                            name = pp.getName();
                            prefix = pp.getPrefix();
                            if (prefix != null) {
                                rawName.setLength(0);
                                rawName.append(prefix);
                                rawName.append(':');
                                rawName.append(name);
                            }
                            this.contentHandler.endElement(pp.getNamespace(), name, prefix != null ? rawName.toString() : name);
                            int depth = pp.getDepth();
                            int countPrev = level > depth ? pp.getNamespaceCount(pp.getDepth()) : 0;
                            int count = pp.getNamespaceCount(pp.getDepth() - 1);
                            for (int i = count - 1; i >= countPrev; --i) {
                                this.contentHandler.endPrefixMapping(pp.getNamespacePrefix(i));
                            }
                            break;
                        } else {
                            this.contentHandler.endElement(pp.getNamespace(), pp.getName(), pp.getName());
                            break;
                        }
                    }
                    case 1: {
                        return;
                    }
                }
                type = pp.next();
            } while (pp.getDepth() > level);
            return;
        }
        catch (XmlPullParserException ex) {
            SAXParseException saxException = new SAXParseException("parsing error: " + ex, this, ex);
            ex.printStackTrace();
            this.errorHandler.fatalError(saxException);
        }
    }

    protected void startElement(String namespace, String localName, String qName) throws SAXException {
        this.contentHandler.startElement(namespace, localName, qName, this);
    }
}

