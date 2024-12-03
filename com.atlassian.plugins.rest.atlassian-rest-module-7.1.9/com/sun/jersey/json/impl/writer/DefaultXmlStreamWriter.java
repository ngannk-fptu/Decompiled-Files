/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.json.impl.writer;

import com.sun.jersey.json.impl.reader.JsonNamespaceContext;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public abstract class DefaultXmlStreamWriter
implements XMLStreamWriter {
    private NamespaceContext namespaceContext = null;

    @Override
    public NamespaceContext getNamespaceContext() {
        if (this.namespaceContext == null) {
            this.namespaceContext = new JsonNamespaceContext();
        }
        return this.namespaceContext;
    }

    @Override
    public String getPrefix(String uri) throws XMLStreamException {
        return this.getNamespaceContext().getPrefix(uri);
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        return null;
    }

    @Override
    public void setDefaultNamespace(String uri) throws XMLStreamException {
    }

    @Override
    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        this.namespaceContext = context;
    }

    @Override
    public void setPrefix(String prefix, String uri) throws XMLStreamException {
    }

    @Override
    public void writeCData(String data) throws XMLStreamException {
        this.writeCharacters(data);
    }

    @Override
    public void writeComment(String data) throws XMLStreamException {
    }

    @Override
    public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
    }

    @Override
    public void writeDTD(String dtd) throws XMLStreamException {
    }

    @Override
    public void writeEntityRef(String name) throws XMLStreamException {
    }

    @Override
    public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
    }

    @Override
    public void writeProcessingInstruction(String target) throws XMLStreamException {
    }

    @Override
    public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
    }

    @Override
    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
    }

    @Override
    public void writeStartDocument(String version) throws XMLStreamException {
    }

    @Override
    public void writeStartDocument() throws XMLStreamException {
    }
}

