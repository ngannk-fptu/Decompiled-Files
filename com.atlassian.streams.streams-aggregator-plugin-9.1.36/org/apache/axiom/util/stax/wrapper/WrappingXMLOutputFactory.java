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
import org.apache.axiom.util.stax.wrapper.XMLOutputFactoryWrapper;

public class WrappingXMLOutputFactory
extends XMLOutputFactoryWrapper {
    public WrappingXMLOutputFactory(XMLOutputFactory parent) {
        super(parent);
    }

    protected XMLEventWriter wrap(XMLEventWriter writer) {
        return writer;
    }

    protected XMLStreamWriter wrap(XMLStreamWriter writer) {
        return writer;
    }

    public XMLEventWriter createXMLEventWriter(OutputStream stream, String encoding) throws XMLStreamException {
        return this.wrap(super.createXMLEventWriter(stream, encoding));
    }

    public XMLEventWriter createXMLEventWriter(OutputStream stream) throws XMLStreamException {
        return this.wrap(super.createXMLEventWriter(stream));
    }

    public XMLEventWriter createXMLEventWriter(Result result) throws XMLStreamException {
        return this.wrap(super.createXMLEventWriter(result));
    }

    public XMLEventWriter createXMLEventWriter(Writer stream) throws XMLStreamException {
        return this.wrap(super.createXMLEventWriter(stream));
    }

    public XMLStreamWriter createXMLStreamWriter(OutputStream stream, String encoding) throws XMLStreamException {
        return this.wrap(super.createXMLStreamWriter(stream, encoding));
    }

    public XMLStreamWriter createXMLStreamWriter(OutputStream stream) throws XMLStreamException {
        return this.wrap(super.createXMLStreamWriter(stream));
    }

    public XMLStreamWriter createXMLStreamWriter(Result result) throws XMLStreamException {
        return this.wrap(super.createXMLStreamWriter(result));
    }

    public XMLStreamWriter createXMLStreamWriter(Writer stream) throws XMLStreamException {
        return this.wrap(super.createXMLStreamWriter(stream));
    }
}

