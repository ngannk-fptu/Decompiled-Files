/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.wrapper;

import java.io.OutputStream;
import java.io.Writer;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;

public class XMLOutputFactoryWrapper
extends XMLOutputFactory {
    private final XMLOutputFactory parent;

    public XMLOutputFactoryWrapper(XMLOutputFactory parent) {
        this.parent = parent;
    }

    public XMLEventWriter createXMLEventWriter(OutputStream stream, String encoding) throws XMLStreamException {
        return this.parent.createXMLEventWriter(stream, encoding);
    }

    public XMLEventWriter createXMLEventWriter(OutputStream stream) throws XMLStreamException {
        return this.parent.createXMLEventWriter(stream);
    }

    public XMLEventWriter createXMLEventWriter(Result result) throws XMLStreamException {
        return this.parent.createXMLEventWriter(result);
    }

    public XMLEventWriter createXMLEventWriter(Writer stream) throws XMLStreamException {
        return this.parent.createXMLEventWriter(stream);
    }

    public XMLStreamWriter createXMLStreamWriter(OutputStream stream, String encoding) throws XMLStreamException {
        return this.parent.createXMLStreamWriter(stream, encoding);
    }

    public XMLStreamWriter createXMLStreamWriter(OutputStream stream) throws XMLStreamException {
        return this.parent.createXMLStreamWriter(stream);
    }

    public XMLStreamWriter createXMLStreamWriter(Result result) throws XMLStreamException {
        return this.parent.createXMLStreamWriter(result);
    }

    public XMLStreamWriter createXMLStreamWriter(Writer stream) throws XMLStreamException {
        return this.parent.createXMLStreamWriter(stream);
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        return this.parent.getProperty(name);
    }

    public boolean isPropertySupported(String name) {
        return this.parent.isPropertySupported(name);
    }

    public void setProperty(String name, Object value) throws IllegalArgumentException {
        this.parent.setProperty(name, value);
    }
}

