/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Properties;
import org.apache.xml.serializer.ToSAXHandler;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public final class ToHTMLSAXHandler
extends ToSAXHandler {
    private boolean m_dtdHandled = false;
    protected boolean m_escapeSetting = true;

    @Override
    public Properties getOutputFormat() {
        return null;
    }

    @Override
    public OutputStream getOutputStream() {
        return null;
    }

    @Override
    public Writer getWriter() {
        return null;
    }

    public void indent(int n) throws SAXException {
    }

    @Override
    public void serialize(Node node) throws IOException {
    }

    @Override
    public boolean setEscaping(boolean escape) throws SAXException {
        boolean oldEscapeSetting = this.m_escapeSetting;
        this.m_escapeSetting = escape;
        if (escape) {
            this.processingInstruction("javax.xml.transform.enable-output-escaping", "");
        } else {
            this.processingInstruction("javax.xml.transform.disable-output-escaping", "");
        }
        return oldEscapeSetting;
    }

    @Override
    public void setIndent(boolean indent) {
    }

    @Override
    public void setOutputFormat(Properties format) {
    }

    @Override
    public void setOutputStream(OutputStream output) {
    }

    @Override
    public void setWriter(Writer writer) {
    }

    @Override
    public void attributeDecl(String eName, String aName, String type, String valueDefault, String value) throws SAXException {
    }

    @Override
    public void elementDecl(String name, String model) throws SAXException {
    }

    @Override
    public void externalEntityDecl(String arg0, String arg1, String arg2) throws SAXException {
    }

    @Override
    public void internalEntityDecl(String name, String value) throws SAXException {
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        this.flushPending();
        this.m_saxHandler.endElement(uri, localName, qName);
        if (this.m_tracer != null) {
            super.fireEndElem(qName);
        }
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        this.flushPending();
        this.m_saxHandler.processingInstruction(target, data);
        if (this.m_tracer != null) {
            super.fireEscapingEvent(target, data);
        }
    }

    @Override
    public void setDocumentLocator(Locator arg0) {
    }

    @Override
    public void skippedEntity(String arg0) throws SAXException {
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        this.flushPending();
        super.startElement(namespaceURI, localName, qName, atts);
        this.m_saxHandler.startElement(namespaceURI, localName, qName, atts);
        this.m_elemContext.m_startTagOpen = false;
    }

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        this.flushPending();
        if (this.m_lexHandler != null) {
            this.m_lexHandler.comment(ch, start, length);
        }
        if (this.m_tracer != null) {
            super.fireCommentEvent(ch, start, length);
        }
    }

    @Override
    public void endCDATA() throws SAXException {
    }

    @Override
    public void endDTD() throws SAXException {
    }

    @Override
    public void startCDATA() throws SAXException {
    }

    @Override
    public void startEntity(String arg0) throws SAXException {
    }

    @Override
    public void endDocument() throws SAXException {
        this.flushPending();
        this.m_saxHandler.endDocument();
        if (this.m_tracer != null) {
            super.fireEndDoc();
        }
    }

    @Override
    protected void closeStartTag() throws SAXException {
        this.m_elemContext.m_startTagOpen = false;
        this.m_saxHandler.startElement("", this.m_elemContext.m_elementName, this.m_elemContext.m_elementName, this.m_attributes);
        this.m_attributes.clear();
    }

    @Override
    public void close() {
    }

    @Override
    public void characters(String chars) throws SAXException {
        int length = chars.length();
        if (length > this.m_charsBuff.length) {
            this.m_charsBuff = new char[length * 2 + 1];
        }
        chars.getChars(0, length, this.m_charsBuff, 0);
        this.characters(this.m_charsBuff, 0, length);
    }

    public ToHTMLSAXHandler(ContentHandler handler, String encoding) {
        super(handler, encoding);
    }

    public ToHTMLSAXHandler(ContentHandler handler, LexicalHandler lex, String encoding) {
        super(handler, lex, encoding);
    }

    @Override
    public void startElement(String elementNamespaceURI, String elementLocalName, String elementName) throws SAXException {
        super.startElement(elementNamespaceURI, elementLocalName, elementName);
        this.flushPending();
        if (!this.m_dtdHandled) {
            String doctypeSystem = this.getDoctypeSystem();
            String doctypePublic = this.getDoctypePublic();
            if ((doctypeSystem != null || doctypePublic != null) && this.m_lexHandler != null) {
                this.m_lexHandler.startDTD(elementName, doctypePublic, doctypeSystem);
            }
            this.m_dtdHandled = true;
        }
        this.m_elemContext = this.m_elemContext.push(elementNamespaceURI, elementLocalName, elementName);
    }

    @Override
    public void startElement(String elementName) throws SAXException {
        this.startElement(null, null, elementName);
    }

    @Override
    public void endElement(String elementName) throws SAXException {
        this.flushPending();
        this.m_saxHandler.endElement("", elementName, elementName);
        if (this.m_tracer != null) {
            super.fireEndElem(elementName);
        }
    }

    @Override
    public void characters(char[] ch, int off, int len) throws SAXException {
        this.flushPending();
        this.m_saxHandler.characters(ch, off, len);
        if (this.m_tracer != null) {
            super.fireCharEvent(ch, off, len);
        }
    }

    @Override
    public void flushPending() throws SAXException {
        if (this.m_needToCallStartDocument) {
            this.startDocumentInternal();
            this.m_needToCallStartDocument = false;
        }
        if (this.m_elemContext.m_startTagOpen) {
            this.closeStartTag();
            this.m_elemContext.m_startTagOpen = false;
        }
    }

    @Override
    public boolean startPrefixMapping(String prefix, String uri, boolean shouldFlush) throws SAXException {
        if (shouldFlush) {
            this.flushPending();
        }
        this.m_saxHandler.startPrefixMapping(prefix, uri);
        return false;
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        this.startPrefixMapping(prefix, uri, true);
    }

    @Override
    public void namespaceAfterStartElement(String prefix, String uri) throws SAXException {
        String prefix1;
        if (this.m_elemContext.m_elementURI == null && (prefix1 = ToHTMLSAXHandler.getPrefixPart(this.m_elemContext.m_elementName)) == null && "".equals(prefix)) {
            this.m_elemContext.m_elementURI = uri;
        }
        this.startPrefixMapping(prefix, uri, false);
    }

    @Override
    public boolean reset() {
        boolean wasReset = false;
        if (super.reset()) {
            this.resetToHTMLSAXHandler();
            wasReset = true;
        }
        return wasReset;
    }

    private void resetToHTMLSAXHandler() {
        this.m_dtdHandled = false;
        this.m_escapeSetting = true;
    }
}

