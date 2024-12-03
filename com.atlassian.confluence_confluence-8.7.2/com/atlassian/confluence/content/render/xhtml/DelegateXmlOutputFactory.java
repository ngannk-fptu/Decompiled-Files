/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;

public class DelegateXmlOutputFactory
extends XMLOutputFactory
implements XmlOutputFactory {
    private final XMLOutputFactory staxXmlOutputFactory;

    public DelegateXmlOutputFactory(XMLOutputFactory staxXmlOutputFactory) {
        this.staxXmlOutputFactory = staxXmlOutputFactory;
    }

    @Override
    public XMLStreamWriter createXMLStreamWriter(Writer stream) throws XMLStreamException {
        return this.staxXmlOutputFactory.createXMLStreamWriter(stream);
    }

    @Override
    public XMLStreamWriter createXMLStreamWriter(OutputStream stream) throws XMLStreamException {
        return this.staxXmlOutputFactory.createXMLStreamWriter(stream);
    }

    @Override
    public XMLStreamWriter createXMLStreamWriter(OutputStream stream, String encoding) throws XMLStreamException {
        return this.staxXmlOutputFactory.createXMLStreamWriter(stream, encoding);
    }

    @Override
    public XMLStreamWriter createXMLStreamWriter(Result result) throws XMLStreamException {
        return this.staxXmlOutputFactory.createXMLStreamWriter(result);
    }

    @Override
    public XMLEventWriter createXMLEventWriter(Result result) throws XMLStreamException {
        return this.staxXmlOutputFactory.createXMLEventWriter(result);
    }

    @Override
    public XMLEventWriter createXMLEventWriter(OutputStream stream) throws XMLStreamException {
        return this.staxXmlOutputFactory.createXMLEventWriter(stream);
    }

    @Override
    public XMLEventWriter createXMLEventWriter(OutputStream stream, String encoding) throws XMLStreamException {
        return this.staxXmlOutputFactory.createXMLEventWriter(stream, encoding);
    }

    @Override
    public XMLEventWriter createXMLEventWriter(Writer stream) throws XMLStreamException {
        return this.staxXmlOutputFactory.createXMLEventWriter(stream);
    }

    @Override
    public void setProperty(String name, Object value) throws IllegalArgumentException {
        this.staxXmlOutputFactory.setProperty(name, value);
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        return this.staxXmlOutputFactory.getProperty(name);
    }

    @Override
    public boolean isPropertySupported(String name) {
        return this.staxXmlOutputFactory.isPropertySupported(name);
    }
}

