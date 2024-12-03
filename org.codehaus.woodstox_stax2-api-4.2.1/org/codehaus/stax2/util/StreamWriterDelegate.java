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

    public StreamWriterDelegate(XMLStreamWriter parentWriter) {
        this.mDelegate = parentWriter;
    }

    public void setParent(XMLStreamWriter parentWriter) {
        this.mDelegate = parentWriter;
    }

    public XMLStreamWriter getParent() {
        return this.mDelegate;
    }

    @Override
    public void close() throws XMLStreamException {
        this.mDelegate.close();
    }

    @Override
    public void flush() throws XMLStreamException {
        this.mDelegate.flush();
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return this.mDelegate.getNamespaceContext();
    }

    @Override
    public String getPrefix(String ns) throws XMLStreamException {
        return this.mDelegate.getPrefix(ns);
    }

    @Override
    public Object getProperty(String pname) throws IllegalArgumentException {
        return this.mDelegate.getProperty(pname);
    }

    @Override
    public void setDefaultNamespace(String ns) throws XMLStreamException {
        this.mDelegate.setDefaultNamespace(ns);
    }

    @Override
    public void setNamespaceContext(NamespaceContext nc) throws XMLStreamException {
        this.mDelegate.setNamespaceContext(nc);
    }

    @Override
    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        this.mDelegate.setPrefix(prefix, uri);
    }

    @Override
    public void writeAttribute(String arg0, String arg1) throws XMLStreamException {
        this.mDelegate.writeAttribute(arg0, arg1);
    }

    @Override
    public void writeAttribute(String arg0, String arg1, String arg2) throws XMLStreamException {
        this.mDelegate.writeAttribute(arg0, arg1, arg2);
    }

    @Override
    public void writeAttribute(String arg0, String arg1, String arg2, String arg3) throws XMLStreamException {
        this.mDelegate.writeAttribute(arg0, arg1, arg2, arg3);
    }

    @Override
    public void writeCData(String arg0) throws XMLStreamException {
        this.mDelegate.writeCData(arg0);
    }

    @Override
    public void writeCharacters(String arg0) throws XMLStreamException {
        this.mDelegate.writeCharacters(arg0);
    }

    @Override
    public void writeCharacters(char[] arg0, int arg1, int arg2) throws XMLStreamException {
        this.mDelegate.writeCharacters(arg0, arg1, arg2);
    }

    @Override
    public void writeComment(String arg0) throws XMLStreamException {
        this.mDelegate.writeComment(arg0);
    }

    @Override
    public void writeDTD(String arg0) throws XMLStreamException {
        this.mDelegate.writeDTD(arg0);
    }

    @Override
    public void writeDefaultNamespace(String arg0) throws XMLStreamException {
        this.mDelegate.writeDefaultNamespace(arg0);
    }

    @Override
    public void writeEmptyElement(String arg0) throws XMLStreamException {
        this.mDelegate.writeEmptyElement(arg0);
    }

    @Override
    public void writeEmptyElement(String arg0, String arg1) throws XMLStreamException {
        this.mDelegate.writeEmptyElement(arg0, arg1);
    }

    @Override
    public void writeEmptyElement(String arg0, String arg1, String arg2) throws XMLStreamException {
        this.mDelegate.writeEmptyElement(arg0, arg1, arg2);
    }

    @Override
    public void writeEndDocument() throws XMLStreamException {
        this.mDelegate.writeEndDocument();
    }

    @Override
    public void writeEndElement() throws XMLStreamException {
        this.mDelegate.writeEndElement();
    }

    @Override
    public void writeEntityRef(String arg0) throws XMLStreamException {
        this.mDelegate.writeEntityRef(arg0);
    }

    @Override
    public void writeNamespace(String arg0, String arg1) throws XMLStreamException {
        this.mDelegate.writeNamespace(arg0, arg1);
    }

    @Override
    public void writeProcessingInstruction(String arg0) throws XMLStreamException {
        this.mDelegate.writeProcessingInstruction(arg0);
    }

    @Override
    public void writeProcessingInstruction(String arg0, String arg1) throws XMLStreamException {
        this.mDelegate.writeProcessingInstruction(arg0, arg1);
    }

    @Override
    public void writeStartDocument() throws XMLStreamException {
        this.mDelegate.writeStartDocument();
    }

    @Override
    public void writeStartDocument(String arg0) throws XMLStreamException {
        this.mDelegate.writeStartDocument(arg0);
    }

    @Override
    public void writeStartDocument(String arg0, String arg1) throws XMLStreamException {
        this.mDelegate.writeStartDocument(arg0, arg1);
    }

    @Override
    public void writeStartElement(String arg0) throws XMLStreamException {
        this.mDelegate.writeStartElement(arg0);
    }

    @Override
    public void writeStartElement(String arg0, String arg1) throws XMLStreamException {
        this.mDelegate.writeStartElement(arg0, arg1);
    }

    @Override
    public void writeStartElement(String arg0, String arg1, String arg2) throws XMLStreamException {
        this.mDelegate.writeStartElement(arg0, arg1, arg2);
    }
}

