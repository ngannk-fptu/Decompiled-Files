/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.util.stax.AbstractXMLStreamWriter;

class NamespaceContextCorrectingXMLStreamWriterWrapper
extends AbstractXMLStreamWriter {
    private final XMLStreamWriter parent;

    public NamespaceContextCorrectingXMLStreamWriterWrapper(XMLStreamWriter parent) {
        this.parent = parent;
    }

    protected void doWriteAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
        this.parent.writeAttribute(prefix, namespaceURI, localName, value);
    }

    protected void doWriteAttribute(String localName, String value) throws XMLStreamException {
        this.parent.writeAttribute(localName, value);
    }

    protected void doWriteCData(String data) throws XMLStreamException {
        this.parent.writeCData(data);
    }

    protected void doWriteCharacters(char[] text, int start, int len) throws XMLStreamException {
        this.parent.writeCharacters(text, start, len);
    }

    protected void doWriteCharacters(String text) throws XMLStreamException {
        this.parent.writeCharacters(text);
    }

    protected void doWriteComment(String data) throws XMLStreamException {
        this.parent.writeComment(data);
    }

    protected void doWriteDefaultNamespace(String namespaceURI) throws XMLStreamException {
        this.parent.writeDefaultNamespace(namespaceURI);
    }

    protected void doWriteDTD(String dtd) throws XMLStreamException {
        this.parent.writeDTD(dtd);
    }

    protected void doWriteEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        this.parent.writeEmptyElement(prefix, localName, namespaceURI);
    }

    protected void doWriteEmptyElement(String localName) throws XMLStreamException {
        this.parent.writeEmptyElement(localName);
    }

    protected void doWriteEndDocument() throws XMLStreamException {
        this.parent.writeEndDocument();
    }

    protected void doWriteEndElement() throws XMLStreamException {
        this.parent.writeEndElement();
    }

    protected void doWriteEntityRef(String name) throws XMLStreamException {
        this.parent.writeEntityRef(name);
    }

    protected void doWriteNamespace(String prefix, String namespaceURI) throws XMLStreamException {
        this.parent.writeNamespace(prefix, namespaceURI);
    }

    protected void doWriteProcessingInstruction(String target, String data) throws XMLStreamException {
        this.parent.writeProcessingInstruction(target, data);
    }

    protected void doWriteProcessingInstruction(String target) throws XMLStreamException {
        this.parent.writeProcessingInstruction(target);
    }

    protected void doWriteStartDocument() throws XMLStreamException {
        this.parent.writeStartDocument();
    }

    protected void doWriteStartDocument(String encoding, String version) throws XMLStreamException {
        this.parent.writeStartDocument(encoding, version);
    }

    protected void doWriteStartDocument(String version) throws XMLStreamException {
        this.parent.writeStartDocument(version);
    }

    protected void doWriteStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        this.parent.writeStartElement(prefix, localName, namespaceURI);
    }

    protected void doWriteStartElement(String localName) throws XMLStreamException {
        this.parent.writeStartElement(localName);
    }

    public void close() throws XMLStreamException {
        this.parent.close();
    }

    public void flush() throws XMLStreamException {
        this.parent.flush();
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        return this.parent.getProperty(name);
    }
}

