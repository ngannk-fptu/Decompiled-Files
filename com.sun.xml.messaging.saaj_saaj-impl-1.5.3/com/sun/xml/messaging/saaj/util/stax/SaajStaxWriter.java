/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.SOAPElement
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPMessage
 */
package com.sun.xml.messaging.saaj.util.stax;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

public class SaajStaxWriter
implements XMLStreamWriter {
    protected SOAPMessage soap;
    protected String envURI;
    protected SOAPElement currentElement;
    protected DeferredElement deferredElement;
    protected static final String Envelope = "Envelope";
    protected static final String Header = "Header";
    protected static final String Body = "Body";
    protected static final String xmlns = "xmlns";

    public SaajStaxWriter(SOAPMessage msg, String uri) throws SOAPException {
        this.soap = msg;
        this.envURI = uri;
        this.deferredElement = new DeferredElement();
    }

    public SOAPMessage getSOAPMessage() {
        return this.soap;
    }

    protected SOAPElement getEnvelope() throws SOAPException {
        return this.soap.getSOAPPart().getEnvelope();
    }

    @Override
    public void writeStartElement(String localName) throws XMLStreamException {
        this.currentElement = this.deferredElement.flushTo(this.currentElement);
        this.deferredElement.setLocalName(localName);
    }

    @Override
    public void writeStartElement(String ns, String ln) throws XMLStreamException {
        this.writeStartElement(null, ln, ns);
    }

    @Override
    public void writeStartElement(String prefix, String ln, String ns) throws XMLStreamException {
        this.currentElement = this.deferredElement.flushTo(this.currentElement);
        if (this.envURI.equals(ns)) {
            try {
                if (Envelope.equals(ln)) {
                    this.currentElement = this.getEnvelope();
                    this.fixPrefix(prefix);
                    return;
                }
                if (Header.equals(ln)) {
                    this.currentElement = this.soap.getSOAPHeader();
                    this.fixPrefix(prefix);
                    return;
                }
                if (Body.equals(ln)) {
                    this.currentElement = this.soap.getSOAPBody();
                    this.fixPrefix(prefix);
                    return;
                }
            }
            catch (SOAPException e) {
                throw new XMLStreamException(e);
            }
        }
        this.deferredElement.setLocalName(ln);
        this.deferredElement.setNamespaceUri(ns);
        this.deferredElement.setPrefix(prefix);
    }

    private void fixPrefix(String prfx) throws XMLStreamException {
        this.fixPrefix(prfx, this.currentElement);
    }

    private void fixPrefix(String prfx, SOAPElement element) throws XMLStreamException {
        String oldPrfx = element.getPrefix();
        if (prfx != null && !prfx.equals(oldPrfx)) {
            element.setPrefix(prfx);
        }
    }

    @Override
    public void writeEmptyElement(String uri, String ln) throws XMLStreamException {
        this.writeStartElement(null, ln, uri);
    }

    @Override
    public void writeEmptyElement(String prefix, String ln, String uri) throws XMLStreamException {
        this.writeStartElement(prefix, ln, uri);
    }

    @Override
    public void writeEmptyElement(String ln) throws XMLStreamException {
        this.writeStartElement(null, ln, null);
    }

    @Override
    public void writeEndElement() throws XMLStreamException {
        this.currentElement = this.deferredElement.flushTo(this.currentElement);
        if (this.currentElement != null) {
            this.currentElement = this.currentElement.getParentElement();
        }
    }

    @Override
    public void writeEndDocument() throws XMLStreamException {
        this.currentElement = this.deferredElement.flushTo(this.currentElement);
    }

    @Override
    public void close() throws XMLStreamException {
    }

    @Override
    public void flush() throws XMLStreamException {
    }

    @Override
    public void writeAttribute(String ln, String val) throws XMLStreamException {
        this.writeAttribute(null, null, ln, val);
    }

    @Override
    public void writeAttribute(String prefix, String ns, String ln, String value) throws XMLStreamException {
        if (ns == null && prefix == null && xmlns.equals(ln)) {
            this.writeNamespace("", value);
        } else if (this.deferredElement.isInitialized()) {
            this.deferredElement.addAttribute(prefix, ns, ln, value);
        } else {
            SaajStaxWriter.addAttibuteToElement(this.currentElement, prefix, ns, ln, value);
        }
    }

    @Override
    public void writeAttribute(String ns, String ln, String val) throws XMLStreamException {
        this.writeAttribute(null, ns, ln, val);
    }

    @Override
    public void writeNamespace(String prefix, String uri) throws XMLStreamException {
        String thePrefix;
        String string = thePrefix = prefix == null || xmlns.equals(prefix) ? "" : prefix;
        if (this.deferredElement.isInitialized()) {
            this.deferredElement.addNamespaceDeclaration(thePrefix, uri);
        } else {
            try {
                this.currentElement.addNamespaceDeclaration(thePrefix, uri);
            }
            catch (SOAPException e) {
                throw new XMLStreamException(e);
            }
        }
    }

    @Override
    public void writeDefaultNamespace(String uri) throws XMLStreamException {
        this.writeNamespace("", uri);
    }

    @Override
    public void writeComment(String data) throws XMLStreamException {
        this.currentElement = this.deferredElement.flushTo(this.currentElement);
        Comment c = this.soap.getSOAPPart().createComment(data);
        this.currentElement.appendChild((Node)c);
    }

    @Override
    public void writeProcessingInstruction(String target) throws XMLStreamException {
        this.currentElement = this.deferredElement.flushTo(this.currentElement);
        ProcessingInstruction n = this.soap.getSOAPPart().createProcessingInstruction(target, "");
        this.currentElement.appendChild((Node)n);
    }

    @Override
    public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
        this.currentElement = this.deferredElement.flushTo(this.currentElement);
        ProcessingInstruction n = this.soap.getSOAPPart().createProcessingInstruction(target, data);
        this.currentElement.appendChild((Node)n);
    }

    @Override
    public void writeCData(String data) throws XMLStreamException {
        this.currentElement = this.deferredElement.flushTo(this.currentElement);
        CDATASection n = this.soap.getSOAPPart().createCDATASection(data);
        this.currentElement.appendChild((Node)n);
    }

    @Override
    public void writeDTD(String dtd) throws XMLStreamException {
        this.currentElement = this.deferredElement.flushTo(this.currentElement);
    }

    @Override
    public void writeEntityRef(String name) throws XMLStreamException {
        this.currentElement = this.deferredElement.flushTo(this.currentElement);
        EntityReference n = this.soap.getSOAPPart().createEntityReference(name);
        this.currentElement.appendChild((Node)n);
    }

    @Override
    public void writeStartDocument() throws XMLStreamException {
    }

    @Override
    public void writeStartDocument(String version) throws XMLStreamException {
        if (version != null) {
            this.soap.getSOAPPart().setXmlVersion(version);
        }
    }

    @Override
    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        if (version != null) {
            this.soap.getSOAPPart().setXmlVersion(version);
        }
        if (encoding != null) {
            try {
                this.soap.setProperty("javax.xml.soap.character-set-encoding", (Object)encoding);
            }
            catch (SOAPException e) {
                throw new XMLStreamException(e);
            }
        }
    }

    @Override
    public void writeCharacters(String text) throws XMLStreamException {
        this.currentElement = this.deferredElement.flushTo(this.currentElement);
        try {
            this.currentElement.addTextNode(text);
        }
        catch (SOAPException e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
        this.currentElement = this.deferredElement.flushTo(this.currentElement);
        char[] chr = start == 0 && len == text.length ? text : Arrays.copyOfRange(text, start, start + len);
        try {
            this.currentElement.addTextNode(new String(chr));
        }
        catch (SOAPException e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    public String getPrefix(String uri) throws XMLStreamException {
        return this.currentElement.lookupPrefix(uri);
    }

    @Override
    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        if (!this.deferredElement.isInitialized()) {
            throw new XMLStreamException("Namespace not associated with any element");
        }
        this.deferredElement.addNamespaceDeclaration(prefix, uri);
    }

    @Override
    public void setDefaultNamespace(String uri) throws XMLStreamException {
        this.setPrefix("", uri);
    }

    @Override
    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        if ("javax.xml.stream.isRepairingNamespaces".equals(name)) {
            return Boolean.FALSE;
        }
        return null;
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return new NamespaceContext(){

            @Override
            public String getNamespaceURI(String prefix) {
                return SaajStaxWriter.this.currentElement.getNamespaceURI(prefix);
            }

            @Override
            public String getPrefix(String namespaceURI) {
                return SaajStaxWriter.this.currentElement.lookupPrefix(namespaceURI);
            }

            public Iterator getPrefixes(final String namespaceURI) {
                return new Iterator<String>(){
                    String prefix;
                    {
                        this.prefix = this.getPrefix(namespaceURI);
                    }

                    @Override
                    public boolean hasNext() {
                        return this.prefix != null;
                    }

                    @Override
                    public String next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        String next = this.prefix;
                        this.prefix = null;
                        return next;
                    }

                    @Override
                    public void remove() {
                    }
                };
            }
        };
    }

    static void addAttibuteToElement(SOAPElement element, String prefix, String ns, String ln, String value) throws XMLStreamException {
        try {
            if (ns == null) {
                element.setAttributeNS("", ln, value);
            } else {
                QName name = prefix == null ? new QName(ns, ln) : new QName(ns, ln, prefix);
                element.addAttribute(name, value);
            }
        }
        catch (SOAPException e) {
            throw new XMLStreamException(e);
        }
    }

    static class AttributeDeclaration {
        final String prefix;
        final String namespaceUri;
        final String localName;
        final String value;

        AttributeDeclaration(String prefix, String namespaceUri, String localName, String value) {
            this.prefix = prefix;
            this.namespaceUri = namespaceUri;
            this.localName = localName;
            this.value = value;
        }
    }

    static class NamespaceDeclaration {
        final String prefix;
        final String namespaceUri;

        NamespaceDeclaration(String prefix, String namespaceUri) {
            this.prefix = prefix;
            this.namespaceUri = namespaceUri;
        }
    }

    static class DeferredElement {
        private String prefix;
        private String localName;
        private String namespaceUri;
        private final List<NamespaceDeclaration> namespaceDeclarations = new LinkedList<NamespaceDeclaration>();
        private final List<AttributeDeclaration> attributeDeclarations = new LinkedList<AttributeDeclaration>();

        DeferredElement() {
            this.reset();
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public void setLocalName(String localName) {
            if (localName == null) {
                throw new IllegalArgumentException("localName can not be null");
            }
            this.localName = localName;
        }

        public void setNamespaceUri(String namespaceUri) {
            this.namespaceUri = namespaceUri;
        }

        public void addNamespaceDeclaration(String prefix, String namespaceUri) {
            if (null == this.namespaceUri && null != namespaceUri && prefix.equals(DeferredElement.emptyIfNull(this.prefix))) {
                this.namespaceUri = namespaceUri;
            }
            this.namespaceDeclarations.add(new NamespaceDeclaration(prefix, namespaceUri));
        }

        public void addAttribute(String prefix, String ns, String ln, String value) {
            if (ns == null && prefix == null && SaajStaxWriter.xmlns.equals(ln)) {
                this.addNamespaceDeclaration(prefix, value);
            } else {
                this.attributeDeclarations.add(new AttributeDeclaration(prefix, ns, ln, value));
            }
        }

        public SOAPElement flushTo(SOAPElement target) throws XMLStreamException {
            try {
                if (this.localName != null) {
                    SOAPElement newElement = this.namespaceUri == null ? target.addChildElement(this.localName) : (this.prefix == null ? target.addChildElement(new QName(this.namespaceUri, this.localName)) : target.addChildElement(this.localName, this.prefix, this.namespaceUri));
                    for (NamespaceDeclaration namespace : this.namespaceDeclarations) {
                        newElement.addNamespaceDeclaration(namespace.prefix, namespace.namespaceUri);
                    }
                    for (AttributeDeclaration attribute : this.attributeDeclarations) {
                        SaajStaxWriter.addAttibuteToElement(newElement, attribute.prefix, attribute.namespaceUri, attribute.localName, attribute.value);
                    }
                    this.reset();
                    return newElement;
                }
                return target;
            }
            catch (SOAPException e) {
                throw new XMLStreamException(e);
            }
        }

        public boolean isInitialized() {
            return this.localName != null;
        }

        private void reset() {
            this.localName = null;
            this.prefix = null;
            this.namespaceUri = null;
            this.namespaceDeclarations.clear();
            this.attributeDeclarations.clear();
        }

        private static String emptyIfNull(String s) {
            return s == null ? "" : s;
        }
    }
}

