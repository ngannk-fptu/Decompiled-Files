/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.util;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.util.XMLStreamWriterFilter;

public abstract class XMLStreamWriterFilterBase
implements XMLStreamWriterFilter {
    XMLStreamWriter delegate = null;

    public void setDelegate(XMLStreamWriter writer) {
        this.delegate = writer;
    }

    public XMLStreamWriter getDelegate() {
        return this.delegate;
    }

    public void close() throws XMLStreamException {
        this.delegate.close();
    }

    public void flush() throws XMLStreamException {
        this.delegate.flush();
    }

    public NamespaceContext getNamespaceContext() {
        return this.delegate.getNamespaceContext();
    }

    public String getPrefix(String uri) throws XMLStreamException {
        return this.delegate.getPrefix(uri);
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        return this.delegate.getProperty(name);
    }

    public void setDefaultNamespace(String uri) throws XMLStreamException {
        this.delegate.setDefaultNamespace(uri);
    }

    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        this.delegate.setNamespaceContext(context);
    }

    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        this.delegate.setPrefix(prefix, uri);
    }

    public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
        this.delegate.writeAttribute(prefix, namespaceURI, localName, this.xmlData(value));
    }

    public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
        this.delegate.writeAttribute(namespaceURI, localName, this.xmlData(value));
    }

    public void writeAttribute(String localName, String value) throws XMLStreamException {
        this.delegate.writeAttribute(localName, this.xmlData(value));
    }

    public void writeCData(String data) throws XMLStreamException {
        this.delegate.writeCData(this.xmlData(data));
    }

    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
        String value = new String(text, start, len);
        this.writeCharacters(value);
    }

    public void writeCharacters(String text) throws XMLStreamException {
        this.delegate.writeCharacters(this.xmlData(text));
    }

    public void writeComment(String data) throws XMLStreamException {
        this.delegate.writeComment(data);
    }

    public void writeDTD(String dtd) throws XMLStreamException {
        this.delegate.writeDTD(dtd);
    }

    public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
        this.delegate.writeDefaultNamespace(namespaceURI);
    }

    public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        this.delegate.writeEmptyElement(prefix, localName, namespaceURI);
    }

    public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
        this.delegate.writeEmptyElement(namespaceURI, localName);
    }

    public void writeEmptyElement(String localName) throws XMLStreamException {
        this.delegate.writeEmptyElement(localName);
    }

    public void writeEndDocument() throws XMLStreamException {
        this.delegate.writeEndDocument();
    }

    public void writeEndElement() throws XMLStreamException {
        this.delegate.writeEndElement();
    }

    public void writeEntityRef(String name) throws XMLStreamException {
        this.delegate.writeEntityRef(name);
    }

    public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
        this.delegate.writeNamespace(prefix, namespaceURI);
    }

    public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
        this.delegate.writeProcessingInstruction(target, data);
    }

    public void writeProcessingInstruction(String target) throws XMLStreamException {
        this.delegate.writeProcessingInstruction(target);
    }

    public void writeStartDocument() throws XMLStreamException {
        this.delegate.writeStartDocument();
    }

    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        this.delegate.writeStartDocument(encoding, version);
    }

    public void writeStartDocument(String version) throws XMLStreamException {
        this.delegate.writeStartDocument(version);
    }

    public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        this.delegate.writeStartElement(prefix, localName, namespaceURI);
    }

    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
        this.delegate.writeStartElement(namespaceURI, localName);
    }

    public void writeStartElement(String localName) throws XMLStreamException {
        this.delegate.writeStartElement(localName);
    }

    protected abstract String xmlData(String var1);
}

