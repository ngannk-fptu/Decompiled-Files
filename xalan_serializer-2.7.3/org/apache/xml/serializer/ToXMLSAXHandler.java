/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Properties;
import org.apache.xml.serializer.NamespaceMappings;
import org.apache.xml.serializer.ToSAXHandler;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public final class ToXMLSAXHandler
extends ToSAXHandler {
    protected boolean m_escapeSetting = true;

    public ToXMLSAXHandler() {
        this.m_prefixMap = new NamespaceMappings();
        this.initCDATA();
    }

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
    public void setOutputFormat(Properties format) {
    }

    @Override
    public void setOutputStream(OutputStream output) {
    }

    @Override
    public void setWriter(Writer writer) {
    }

    @Override
    public void attributeDecl(String arg0, String arg1, String arg2, String arg3, String arg4) throws SAXException {
    }

    @Override
    public void elementDecl(String arg0, String arg1) throws SAXException {
    }

    @Override
    public void externalEntityDecl(String arg0, String arg1, String arg2) throws SAXException {
    }

    @Override
    public void internalEntityDecl(String arg0, String arg1) throws SAXException {
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
        String localName = ToXMLSAXHandler.getLocalName(this.m_elemContext.m_elementName);
        String uri = this.getNamespaceURI(this.m_elemContext.m_elementName, true);
        if (this.m_needToCallStartDocument) {
            this.startDocumentInternal();
        }
        this.m_saxHandler.startElement(uri, localName, this.m_elemContext.m_elementName, this.m_attributes);
        this.m_attributes.clear();
        if (this.m_state != null) {
            this.m_state.setCurrentNode(null);
        }
    }

    @Override
    public void closeCDATA() throws SAXException {
        if (this.m_lexHandler != null && this.m_cdataTagOpen) {
            this.m_lexHandler.endCDATA();
        }
        this.m_cdataTagOpen = false;
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        this.flushPending();
        if (namespaceURI == null) {
            namespaceURI = this.m_elemContext.m_elementURI != null ? this.m_elemContext.m_elementURI : this.getNamespaceURI(qName, true);
        }
        if (localName == null) {
            localName = this.m_elemContext.m_elementLocalName != null ? this.m_elemContext.m_elementLocalName : ToXMLSAXHandler.getLocalName(qName);
        }
        this.m_saxHandler.endElement(namespaceURI, localName, qName);
        if (this.m_tracer != null) {
            super.fireEndElem(qName);
        }
        this.m_prefixMap.popNamespaces(this.m_elemContext.m_currentElemDepth, this.m_saxHandler);
        this.m_elemContext = this.m_elemContext.m_prev;
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
    }

    @Override
    public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {
        this.m_saxHandler.ignorableWhitespace(arg0, arg1, arg2);
    }

    @Override
    public void setDocumentLocator(Locator arg0) {
        this.m_saxHandler.setDocumentLocator(arg0);
    }

    @Override
    public void skippedEntity(String arg0) throws SAXException {
        this.m_saxHandler.skippedEntity(arg0);
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        this.startPrefixMapping(prefix, uri, true);
    }

    @Override
    public boolean startPrefixMapping(String prefix, String uri, boolean shouldFlush) throws SAXException {
        int pushDepth;
        if (shouldFlush) {
            this.flushPending();
            pushDepth = this.m_elemContext.m_currentElemDepth + 1;
        } else {
            pushDepth = this.m_elemContext.m_currentElemDepth;
        }
        boolean pushed = this.m_prefixMap.pushNamespace(prefix, uri, pushDepth);
        if (pushed) {
            this.m_saxHandler.startPrefixMapping(prefix, uri);
            if (this.getShouldOutputNSAttr()) {
                if ("".equals(prefix)) {
                    String name = "xmlns";
                    this.addAttributeAlways("http://www.w3.org/2000/xmlns/", name, name, "CDATA", uri, false);
                } else if (!"".equals(uri)) {
                    String name = "xmlns:" + prefix;
                    this.addAttributeAlways("http://www.w3.org/2000/xmlns/", prefix, name, "CDATA", uri, false);
                }
            }
        }
        return pushed;
    }

    @Override
    public void comment(char[] arg0, int arg1, int arg2) throws SAXException {
        this.flushPending();
        if (this.m_lexHandler != null) {
            this.m_lexHandler.comment(arg0, arg1, arg2);
        }
        if (this.m_tracer != null) {
            super.fireCommentEvent(arg0, arg1, arg2);
        }
    }

    @Override
    public void endCDATA() throws SAXException {
    }

    @Override
    public void endDTD() throws SAXException {
        if (this.m_lexHandler != null) {
            this.m_lexHandler.endDTD();
        }
    }

    @Override
    public void startEntity(String arg0) throws SAXException {
        if (this.m_lexHandler != null) {
            this.m_lexHandler.startEntity(arg0);
        }
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

    public ToXMLSAXHandler(ContentHandler handler, String encoding) {
        super(handler, encoding);
        this.initCDATA();
        this.m_prefixMap = new NamespaceMappings();
    }

    public ToXMLSAXHandler(ContentHandler handler, LexicalHandler lex, String encoding) {
        super(handler, lex, encoding);
        this.initCDATA();
        this.m_prefixMap = new NamespaceMappings();
    }

    @Override
    public void startElement(String elementNamespaceURI, String elementLocalName, String elementName) throws SAXException {
        this.startElement(elementNamespaceURI, elementLocalName, elementName, null);
    }

    @Override
    public void startElement(String elementName) throws SAXException {
        this.startElement(null, null, elementName, null);
    }

    @Override
    public void characters(char[] ch, int off, int len) throws SAXException {
        if (this.m_needToCallStartDocument) {
            this.startDocumentInternal();
            this.m_needToCallStartDocument = false;
        }
        if (this.m_elemContext.m_startTagOpen) {
            this.closeStartTag();
            this.m_elemContext.m_startTagOpen = false;
        }
        if (this.m_elemContext.m_isCdataSection && !this.m_cdataTagOpen && this.m_lexHandler != null) {
            this.m_lexHandler.startCDATA();
            this.m_cdataTagOpen = true;
        }
        this.m_saxHandler.characters(ch, off, len);
        if (this.m_tracer != null) {
            this.fireCharEvent(ch, off, len);
        }
    }

    @Override
    public void endElement(String elemName) throws SAXException {
        this.endElement(null, null, elemName);
    }

    @Override
    public void namespaceAfterStartElement(String prefix, String uri) throws SAXException {
        this.startPrefixMapping(prefix, uri, false);
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        this.flushPending();
        this.m_saxHandler.processingInstruction(target, data);
        if (this.m_tracer != null) {
            super.fireEscapingEvent(target, data);
        }
    }

    protected boolean popNamespace(String prefix) {
        try {
            if (this.m_prefixMap.popNamespace(prefix)) {
                this.m_saxHandler.endPrefixMapping(prefix);
                return true;
            }
        }
        catch (SAXException sAXException) {
            // empty catch block
        }
        return false;
    }

    @Override
    public void startCDATA() throws SAXException {
        if (!this.m_cdataTagOpen) {
            this.flushPending();
            if (this.m_lexHandler != null) {
                this.m_lexHandler.startCDATA();
                this.m_cdataTagOpen = true;
            }
        }
    }

    @Override
    public void startElement(String namespaceURI, String localName, String name, Attributes atts) throws SAXException {
        this.flushPending();
        super.startElement(namespaceURI, localName, name, atts);
        if (this.m_needToOutputDocTypeDecl) {
            String doctypeSystem = this.getDoctypeSystem();
            if (doctypeSystem != null && this.m_lexHandler != null) {
                String doctypePublic = this.getDoctypePublic();
                if (doctypeSystem != null) {
                    this.m_lexHandler.startDTD(name, doctypePublic, doctypeSystem);
                }
            }
            this.m_needToOutputDocTypeDecl = false;
        }
        this.m_elemContext = this.m_elemContext.push(namespaceURI, localName, name);
        if (namespaceURI != null) {
            this.ensurePrefixIsDeclared(namespaceURI, name);
        }
        if (atts != null) {
            this.addAttributes(atts);
        }
        this.m_elemContext.m_isCdataSection = this.isCdataSection();
    }

    private void ensurePrefixIsDeclared(String ns, String rawName) throws SAXException {
        if (ns != null && ns.length() > 0) {
            String foundURI;
            String prefix;
            int index = rawName.indexOf(":");
            boolean no_prefix = index < 0;
            String string = prefix = no_prefix ? "" : rawName.substring(0, index);
            if (!(null == prefix || null != (foundURI = this.m_prefixMap.lookupNamespace(prefix)) && foundURI.equals(ns))) {
                this.startPrefixMapping(prefix, ns, false);
                if (this.getShouldOutputNSAttr()) {
                    this.addAttributeAlways("http://www.w3.org/2000/xmlns/", no_prefix ? "xmlns" : prefix, no_prefix ? "xmlns" : "xmlns:" + prefix, "CDATA", ns, false);
                }
            }
        }
    }

    @Override
    public void addAttribute(String uri, String localName, String rawName, String type, String value, boolean XSLAttribute) throws SAXException {
        if (this.m_elemContext.m_startTagOpen) {
            this.ensurePrefixIsDeclared(uri, rawName);
            this.addAttributeAlways(uri, localName, rawName, type, value, false);
        }
    }

    @Override
    public boolean reset() {
        boolean wasReset = false;
        if (super.reset()) {
            this.resetToXMLSAXHandler();
            wasReset = true;
        }
        return wasReset;
    }

    private void resetToXMLSAXHandler() {
        this.m_escapeSetting = true;
    }
}

