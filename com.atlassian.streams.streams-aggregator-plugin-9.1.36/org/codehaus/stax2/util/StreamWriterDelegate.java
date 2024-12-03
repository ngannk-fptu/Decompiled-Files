/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.util;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class StreamWriterDelegate
implements XMLStreamWriter {
    protected XMLStreamWriter mDelegate;

    public StreamWriterDelegate(XMLStreamWriter xMLStreamWriter) {
        this.mDelegate = xMLStreamWriter;
    }

    public void setParent(XMLStreamWriter xMLStreamWriter) {
        this.mDelegate = xMLStreamWriter;
    }

    public XMLStreamWriter getParent() {
        return this.mDelegate;
    }

    public void close() throws XMLStreamException {
        this.mDelegate.close();
    }

    public void flush() throws XMLStreamException {
        this.mDelegate.flush();
    }

    public NamespaceContext getNamespaceContext() {
        return this.mDelegate.getNamespaceContext();
    }

    public String getPrefix(String string) throws XMLStreamException {
        return this.mDelegate.getPrefix(string);
    }

    public Object getProperty(String string) throws IllegalArgumentException {
        return this.mDelegate.getProperty(string);
    }

    public void setDefaultNamespace(String string) throws XMLStreamException {
        this.mDelegate.setDefaultNamespace(string);
    }

    public void setNamespaceContext(NamespaceContext namespaceContext) throws XMLStreamException {
        this.mDelegate.setNamespaceContext(namespaceContext);
    }

    public void setPrefix(String string, String string2) throws XMLStreamException {
        this.mDelegate.setPrefix(string, string2);
    }

    public void writeAttribute(String string, String string2) throws XMLStreamException {
        this.mDelegate.writeAttribute(string, string2);
    }

    public void writeAttribute(String string, String string2, String string3) throws XMLStreamException {
        this.mDelegate.writeAttribute(string, string2, string3);
    }

    public void writeAttribute(String string, String string2, String string3, String string4) throws XMLStreamException {
        this.mDelegate.writeAttribute(string, string2, string3, string4);
    }

    public void writeCData(String string) throws XMLStreamException {
        this.mDelegate.writeCData(string);
    }

    public void writeCharacters(String string) throws XMLStreamException {
        this.mDelegate.writeCharacters(string);
    }

    public void writeCharacters(char[] cArray, int n, int n2) throws XMLStreamException {
        this.mDelegate.writeCharacters(cArray, n, n2);
    }

    public void writeComment(String string) throws XMLStreamException {
        this.mDelegate.writeComment(string);
    }

    public void writeDTD(String string) throws XMLStreamException {
        this.mDelegate.writeDTD(string);
    }

    public void writeDefaultNamespace(String string) throws XMLStreamException {
        this.mDelegate.writeDefaultNamespace(string);
    }

    public void writeEmptyElement(String string) throws XMLStreamException {
        this.mDelegate.writeEmptyElement(string);
    }

    public void writeEmptyElement(String string, String string2) throws XMLStreamException {
        this.mDelegate.writeEmptyElement(string, string2);
    }

    public void writeEmptyElement(String string, String string2, String string3) throws XMLStreamException {
        this.mDelegate.writeEmptyElement(string, string2, string3);
    }

    public void writeEndDocument() throws XMLStreamException {
        this.mDelegate.writeEndDocument();
    }

    public void writeEndElement() throws XMLStreamException {
        this.mDelegate.writeEndElement();
    }

    public void writeEntityRef(String string) throws XMLStreamException {
        this.mDelegate.writeEntityRef(string);
    }

    public void writeNamespace(String string, String string2) throws XMLStreamException {
        this.mDelegate.writeNamespace(string, string2);
    }

    public void writeProcessingInstruction(String string) throws XMLStreamException {
        this.mDelegate.writeProcessingInstruction(string);
    }

    public void writeProcessingInstruction(String string, String string2) throws XMLStreamException {
        this.mDelegate.writeProcessingInstruction(string, string2);
    }

    public void writeStartDocument() throws XMLStreamException {
        this.mDelegate.writeStartDocument();
    }

    public void writeStartDocument(String string) throws XMLStreamException {
        this.mDelegate.writeStartDocument(string);
    }

    public void writeStartDocument(String string, String string2) throws XMLStreamException {
        this.mDelegate.writeStartDocument(string, string2);
    }

    public void writeStartElement(String string) throws XMLStreamException {
        this.mDelegate.writeStartElement(string);
    }

    public void writeStartElement(String string, String string2) throws XMLStreamException {
        this.mDelegate.writeStartElement(string, string2);
    }

    public void writeStartElement(String string, String string2, String string3) throws XMLStreamException {
        this.mDelegate.writeStartElement(string, string2, string3);
    }
}

