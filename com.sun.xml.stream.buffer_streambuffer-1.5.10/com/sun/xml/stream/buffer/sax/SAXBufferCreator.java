/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.stream.buffer.sax;

import com.sun.xml.stream.buffer.AbstractCreator;
import com.sun.xml.stream.buffer.MutableXMLStreamBuffer;
import java.io.IOException;
import java.io.InputStream;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

public class SAXBufferCreator
extends AbstractCreator
implements EntityResolver,
DTDHandler,
ContentHandler,
ErrorHandler,
LexicalHandler {
    protected String[] _namespaceAttributes = new String[32];
    protected int _namespaceAttributesPtr;
    private int depth = 0;

    public SAXBufferCreator() {
    }

    public SAXBufferCreator(MutableXMLStreamBuffer buffer) {
        this();
        this.setBuffer(buffer);
    }

    public MutableXMLStreamBuffer create(XMLReader reader, InputStream in) throws IOException, SAXException {
        return this.create(reader, in, null);
    }

    public MutableXMLStreamBuffer create(XMLReader reader, InputStream in, String systemId) throws IOException, SAXException {
        if (this._buffer == null) {
            this.createBuffer();
        }
        this._buffer.setSystemId(systemId);
        reader.setContentHandler(this);
        reader.setProperty("http://xml.org/sax/properties/lexical-handler", this);
        try {
            this.setHasInternedStrings(reader.getFeature("http://xml.org/sax/features/string-interning"));
        }
        catch (SAXException sAXException) {
            // empty catch block
        }
        if (systemId != null) {
            InputSource s = new InputSource(systemId);
            s.setByteStream(in);
            reader.parse(s);
        } else {
            reader.parse(new InputSource(in));
        }
        return this.getXMLStreamBuffer();
    }

    public void reset() {
        this._buffer = null;
        this._namespaceAttributesPtr = 0;
        this.depth = 0;
    }

    @Override
    public void startDocument() throws SAXException {
        this.storeStructure(16);
    }

    @Override
    public void endDocument() throws SAXException {
        this.storeStructure(144);
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        this.cacheNamespaceAttribute(prefix, uri);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        this.storeQualifiedName(32, uri, localName, qName);
        if (this._namespaceAttributesPtr > 0) {
            this.storeNamespaceAttributes();
        }
        if (attributes.getLength() > 0) {
            this.storeAttributes(attributes);
        }
        ++this.depth;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        this.storeStructure(144);
        if (--this.depth == 0) {
            this.increaseTreeCount();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        this.storeContentCharacters(80, ch, start, length);
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        this.characters(ch, start, length);
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        this.storeStructure(112);
        this.storeStructureString(target);
        this.storeStructureString(data);
    }

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        this.storeContentCharacters(96, ch, start, length);
    }

    private void cacheNamespaceAttribute(String prefix, String uri) {
        this._namespaceAttributes[this._namespaceAttributesPtr++] = prefix;
        this._namespaceAttributes[this._namespaceAttributesPtr++] = uri;
        if (this._namespaceAttributesPtr == this._namespaceAttributes.length) {
            String[] namespaceAttributes = new String[this._namespaceAttributesPtr * 2];
            System.arraycopy(this._namespaceAttributes, 0, namespaceAttributes, 0, this._namespaceAttributesPtr);
            this._namespaceAttributes = namespaceAttributes;
        }
    }

    private void storeNamespaceAttributes() {
        for (int i = 0; i < this._namespaceAttributesPtr; i += 2) {
            int item = 64;
            if (this._namespaceAttributes[i].length() > 0) {
                item |= 1;
                this.storeStructureString(this._namespaceAttributes[i]);
            }
            if (this._namespaceAttributes[i + 1].length() > 0) {
                item |= 2;
                this.storeStructureString(this._namespaceAttributes[i + 1]);
            }
            this.storeStructure(item);
        }
        this._namespaceAttributesPtr = 0;
    }

    private void storeAttributes(Attributes attributes) {
        for (int i = 0; i < attributes.getLength(); ++i) {
            if (attributes.getQName(i).startsWith("xmlns")) continue;
            this.storeQualifiedName(48, attributes.getURI(i), attributes.getLocalName(i), attributes.getQName(i));
            this.storeStructureString(attributes.getType(i));
            this.storeContentString(attributes.getValue(i));
        }
    }

    private void storeQualifiedName(int item, String uri, String localName, String qName) {
        if (uri.length() > 0) {
            item |= 2;
            this.storeStructureString(uri);
        }
        this.storeStructureString(localName);
        if (qName.indexOf(58) >= 0) {
            item |= 4;
            this.storeStructureString(qName);
        }
        this.storeStructure(item);
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
        return null;
    }

    @Override
    public void notationDecl(String name, String publicId, String systemId) throws SAXException {
    }

    @Override
    public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
    }

    @Override
    public void setDocumentLocator(Locator locator) {
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
    }

    @Override
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
    }

    @Override
    public void endDTD() throws SAXException {
    }

    @Override
    public void startEntity(String name) throws SAXException {
    }

    @Override
    public void endEntity(String name) throws SAXException {
    }

    @Override
    public void startCDATA() throws SAXException {
    }

    @Override
    public void endCDATA() throws SAXException {
    }

    @Override
    public void warning(SAXParseException e) throws SAXException {
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        throw e;
    }
}

