/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.util.xml;

import com.sun.xml.ws.api.streaming.XMLStreamWriterFactory;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class XMLStreamWriterFilter
implements XMLStreamWriter,
XMLStreamWriterFactory.RecycleAware {
    protected XMLStreamWriter writer;

    public XMLStreamWriterFilter(XMLStreamWriter writer) {
        this.writer = writer;
    }

    @Override
    public void close() throws XMLStreamException {
        this.writer.close();
    }

    @Override
    public void flush() throws XMLStreamException {
        this.writer.flush();
    }

    @Override
    public void writeEndDocument() throws XMLStreamException {
        this.writer.writeEndDocument();
    }

    @Override
    public void writeEndElement() throws XMLStreamException {
        this.writer.writeEndElement();
    }

    @Override
    public void writeStartDocument() throws XMLStreamException {
        this.writer.writeStartDocument();
    }

    private boolean isUnusualChar(char c) {
        return c <= '\u001f' && c != '\t' && c != '\n' && c != '\r';
    }

    private String transformWhiteSpaces(String text) {
        char[] cstr = text.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char c : cstr) {
            if (this.isUnusualChar(c)) {
                sb.append("&#").append(Integer.toString(c)).append(";");
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private void transformWhiteSpaces(char[] text, int start, int len) {
        for (int i = 0; i < len && i + start < text.length; ++i) {
            if (!this.isUnusualChar(text[i])) continue;
            text[i] = 32;
        }
    }

    @Override
    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
        this.transformWhiteSpaces(text, start, len);
        this.writer.writeCharacters(text, start, len);
    }

    @Override
    public void setDefaultNamespace(String uri) throws XMLStreamException {
        this.writer.setDefaultNamespace(uri);
    }

    @Override
    public void writeCData(String data) throws XMLStreamException {
        this.writer.writeCData(data);
    }

    @Override
    public void writeCharacters(String text) throws XMLStreamException {
        text = this.transformWhiteSpaces(text);
        this.writer.writeCharacters(text);
    }

    @Override
    public void writeComment(String data) throws XMLStreamException {
        this.writer.writeComment(data);
    }

    @Override
    public void writeDTD(String dtd) throws XMLStreamException {
        this.writer.writeDTD(dtd);
    }

    @Override
    public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
        this.writer.writeDefaultNamespace(namespaceURI);
    }

    @Override
    public void writeEmptyElement(String localName) throws XMLStreamException {
        this.writer.writeEmptyElement(localName);
    }

    @Override
    public void writeEntityRef(String name) throws XMLStreamException {
        this.writer.writeEntityRef(name);
    }

    @Override
    public void writeProcessingInstruction(String target) throws XMLStreamException {
        this.writer.writeProcessingInstruction(target);
    }

    @Override
    public void writeStartDocument(String version) throws XMLStreamException {
        this.writer.writeStartDocument(version);
    }

    @Override
    public void writeStartElement(String localName) throws XMLStreamException {
        this.writer.writeStartElement(localName);
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return this.writer.getNamespaceContext();
    }

    @Override
    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        this.writer.setNamespaceContext(context);
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        return this.writer.getProperty(name);
    }

    @Override
    public String getPrefix(String uri) throws XMLStreamException {
        return this.writer.getPrefix(uri);
    }

    @Override
    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        this.writer.setPrefix(prefix, uri);
    }

    @Override
    public void writeAttribute(String localName, String value) throws XMLStreamException {
        this.writer.writeAttribute(localName, value);
    }

    @Override
    public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
        this.writer.writeEmptyElement(namespaceURI, localName);
    }

    @Override
    public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
        this.writer.writeNamespace(prefix, namespaceURI);
    }

    @Override
    public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
        this.writer.writeProcessingInstruction(target, data);
    }

    @Override
    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        this.writer.writeStartDocument(encoding, version);
    }

    @Override
    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
        this.writer.writeStartElement(namespaceURI, localName);
    }

    @Override
    public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
        this.writer.writeAttribute(namespaceURI, localName, value);
    }

    @Override
    public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        this.writer.writeEmptyElement(prefix, localName, namespaceURI);
    }

    @Override
    public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        this.writer.writeStartElement(prefix, localName, namespaceURI);
    }

    @Override
    public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
        this.writer.writeAttribute(prefix, namespaceURI, localName, value);
    }

    @Override
    public void onRecycled() {
        XMLStreamWriterFactory.recycle(this.writer);
        this.writer = null;
    }
}

