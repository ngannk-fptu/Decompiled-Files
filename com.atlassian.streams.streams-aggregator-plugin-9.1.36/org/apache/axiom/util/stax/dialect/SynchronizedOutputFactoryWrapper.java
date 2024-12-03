/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import java.io.OutputStream;
import java.io.Writer;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import org.apache.axiom.util.stax.wrapper.XMLOutputFactoryWrapper;

class SynchronizedOutputFactoryWrapper
extends XMLOutputFactoryWrapper {
    public SynchronizedOutputFactoryWrapper(XMLOutputFactory parent) {
        super(parent);
    }

    public synchronized XMLEventWriter createXMLEventWriter(OutputStream stream, String encoding) throws XMLStreamException {
        return super.createXMLEventWriter(stream, encoding);
    }

    public synchronized XMLEventWriter createXMLEventWriter(OutputStream stream) throws XMLStreamException {
        return super.createXMLEventWriter(stream);
    }

    public synchronized XMLEventWriter createXMLEventWriter(Result result) throws XMLStreamException {
        return super.createXMLEventWriter(result);
    }

    public synchronized XMLEventWriter createXMLEventWriter(Writer stream) throws XMLStreamException {
        return super.createXMLEventWriter(stream);
    }

    public synchronized XMLStreamWriter createXMLStreamWriter(OutputStream stream, String encoding) throws XMLStreamException {
        return super.createXMLStreamWriter(stream, encoding);
    }

    public synchronized XMLStreamWriter createXMLStreamWriter(OutputStream stream) throws XMLStreamException {
        return super.createXMLStreamWriter(stream);
    }

    public synchronized XMLStreamWriter createXMLStreamWriter(Result result) throws XMLStreamException {
        return super.createXMLStreamWriter(result);
    }

    public synchronized XMLStreamWriter createXMLStreamWriter(Writer stream) throws XMLStreamException {
        return super.createXMLStreamWriter(stream);
    }
}

