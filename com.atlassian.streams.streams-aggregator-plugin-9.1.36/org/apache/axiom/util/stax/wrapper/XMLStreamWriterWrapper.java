/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.wrapper;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class XMLStreamWriterWrapper
implements XMLStreamWriter {
    private final XMLStreamWriter parent;

    public XMLStreamWriterWrapper(XMLStreamWriter parent) {
        this.parent = parent;
    }

    public void close() throws XMLStreamException {
        this.parent.close();
    }

    public void flush() throws XMLStreamException {
        this.parent.flush();
    }

    public NamespaceContext getNamespaceContext() {
        return this.parent.getNamespaceContext();
    }

    public String getPrefix(String uri) throws XMLStreamException {
        return this.parent.getPrefix(uri);
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        return this.parent.getProperty(name);
    }

    public void setDefaultNamespace(String uri) throws XMLStreamException {
        this.parent.setDefaultNamespace(uri);
    }

    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        this.parent.setNamespaceContext(context);
    }

    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        this.parent.setPrefix(prefix, uri);
    }

    public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
        this.parent.writeAttribute(prefix, namespaceURI, localName, value);
    }

    public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
        this.parent.writeAttribute(namespaceURI, localName, value);
    }

    public void writeAttribute(String localName, String value) throws XMLStreamException {
        this.parent.writeAttribute(localName, value);
    }

    public void writeCData(String data) throws XMLStreamException {
        this.parent.writeCData(data);
    }

    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
        this.parent.writeCharacters(text, start, len);
    }

    public void writeCharacters(String text) throws XMLStreamException {
        this.parent.writeCharacters(text);
    }

    public void writeComment(String data) throws XMLStreamException {
        this.parent.writeComment(data);
    }

    public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
        this.parent.writeDefaultNamespace(namespaceURI);
    }

    public void writeDTD(String dtd) throws XMLStreamException {
        this.parent.writeDTD(dtd);
    }

    public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        this.parent.writeEmptyElement(prefix, localName, namespaceURI);
    }

    public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
        this.parent.writeEmptyElement(namespaceURI, localName);
    }

    public void writeEmptyElement(String localName) throws XMLStreamException {
        this.parent.writeEmptyElement(localName);
    }

    public void writeEndDocument() throws XMLStreamException {
        this.parent.writeEndDocument();
    }

    public void writeEndElement() throws XMLStreamException {
        this.parent.writeEndElement();
    }

    public void writeEntityRef(String name) throws XMLStreamException {
        this.parent.writeEntityRef(name);
    }

    public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
        this.parent.writeNamespace(prefix, namespaceURI);
    }

    public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
        this.parent.writeProcessingInstruction(target, data);
    }

    public void writeProcessingInstruction(String target) throws XMLStreamException {
        this.parent.writeProcessingInstruction(target);
    }

    public void writeStartDocument() throws XMLStreamException {
        this.parent.writeStartDocument();
    }

    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        this.parent.writeStartDocument(encoding, version);
    }

    public void writeStartDocument(String version) throws XMLStreamException {
        this.parent.writeStartDocument(version);
    }

    public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        this.parent.writeStartElement(prefix, localName, namespaceURI);
    }

    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
        this.parent.writeStartElement(namespaceURI, localName);
    }

    public void writeStartElement(String localName) throws XMLStreamException {
        this.parent.writeStartElement(localName);
    }
}

