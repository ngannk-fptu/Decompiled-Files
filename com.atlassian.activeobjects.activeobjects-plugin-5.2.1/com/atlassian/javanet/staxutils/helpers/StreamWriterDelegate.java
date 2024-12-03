/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.javanet.staxutils.helpers;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public abstract class StreamWriterDelegate
implements XMLStreamWriter {
    protected XMLStreamWriter out;

    protected StreamWriterDelegate(XMLStreamWriter out) {
        this.out = out;
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        return this.out.getProperty(name);
    }

    public NamespaceContext getNamespaceContext() {
        return this.out.getNamespaceContext();
    }

    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        this.out.setNamespaceContext(context);
    }

    public void setDefaultNamespace(String uri) throws XMLStreamException {
        this.out.setDefaultNamespace(uri);
    }

    public void writeStartDocument() throws XMLStreamException {
        this.out.writeStartDocument();
    }

    public void writeStartDocument(String version) throws XMLStreamException {
        this.out.writeStartDocument(version);
    }

    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        this.out.writeStartDocument(encoding, version);
    }

    public void writeDTD(String dtd) throws XMLStreamException {
        this.out.writeDTD(dtd);
    }

    public void writeProcessingInstruction(String target) throws XMLStreamException {
        this.out.writeProcessingInstruction(target);
    }

    public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
        this.out.writeProcessingInstruction(target, data);
    }

    public void writeComment(String data) throws XMLStreamException {
        this.out.writeComment(data);
    }

    public void writeEmptyElement(String localName) throws XMLStreamException {
        this.out.writeEmptyElement(localName);
    }

    public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
        this.out.writeEmptyElement(namespaceURI, localName);
    }

    public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        this.out.writeEmptyElement(prefix, localName, namespaceURI);
    }

    public void writeStartElement(String localName) throws XMLStreamException {
        this.out.writeStartElement(localName);
    }

    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
        this.out.writeStartElement(namespaceURI, localName);
    }

    public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        this.out.writeStartElement(prefix, localName, namespaceURI);
    }

    public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
        this.out.writeDefaultNamespace(namespaceURI);
    }

    public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
        this.out.writeNamespace(prefix, namespaceURI);
    }

    public String getPrefix(String uri) throws XMLStreamException {
        return this.out.getPrefix(uri);
    }

    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        this.out.setPrefix(prefix, uri);
    }

    public void writeAttribute(String localName, String value) throws XMLStreamException {
        this.out.writeAttribute(localName, value);
    }

    public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
        this.out.writeAttribute(namespaceURI, localName, value);
    }

    public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
        this.out.writeAttribute(prefix, namespaceURI, localName, value);
    }

    public void writeCharacters(String text) throws XMLStreamException {
        this.out.writeCharacters(text);
    }

    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
        this.out.writeCharacters(text, start, len);
    }

    public void writeCData(String data) throws XMLStreamException {
        this.out.writeCData(data);
    }

    public void writeEntityRef(String name) throws XMLStreamException {
        this.out.writeEntityRef(name);
    }

    public void writeEndElement() throws XMLStreamException {
        this.out.writeEndElement();
    }

    public void writeEndDocument() throws XMLStreamException {
        this.out.writeEndDocument();
    }

    public void flush() throws XMLStreamException {
        this.out.flush();
    }

    public void close() throws XMLStreamException {
        this.out.close();
    }
}

