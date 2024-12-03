/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import javanet.staxutils.XMLStreamEventWriter;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;

public abstract class BaseXMLOutputFactory
extends XMLOutputFactory {
    public XMLEventWriter createXMLEventWriter(OutputStream stream, String encoding) throws XMLStreamException {
        try {
            return this.createXMLEventWriter(new OutputStreamWriter(stream, encoding));
        }
        catch (UnsupportedEncodingException e) {
            throw new XMLStreamException(e);
        }
    }

    public XMLEventWriter createXMLEventWriter(OutputStream stream) throws XMLStreamException {
        return this.createXMLEventWriter(new OutputStreamWriter(stream));
    }

    public XMLStreamWriter createXMLStreamWriter(OutputStream stream, String encoding) throws XMLStreamException {
        try {
            return this.createXMLStreamWriter(new OutputStreamWriter(stream, encoding));
        }
        catch (UnsupportedEncodingException e) {
            throw new XMLStreamException(e);
        }
    }

    public XMLStreamWriter createXMLStreamWriter(OutputStream stream) throws XMLStreamException {
        return this.createXMLStreamWriter(new OutputStreamWriter(stream));
    }

    public XMLEventWriter createXMLEventWriter(Result result) throws XMLStreamException {
        return this.createXMLEventWriter(this.createXMLStreamWriter(result));
    }

    public XMLEventWriter createXMLEventWriter(XMLStreamWriter writer) {
        return new XMLStreamEventWriter(writer);
    }

    public XMLEventWriter createXMLEventWriter(Writer stream) throws XMLStreamException {
        return this.createXMLEventWriter(this.createXMLStreamWriter(stream));
    }

    public XMLStreamWriter createXMLStreamWriter(Result result) throws XMLStreamException {
        throw new UnsupportedOperationException("TrAX result not supported");
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        throw new IllegalArgumentException(name + " property isn't supported");
    }

    public boolean isPropertySupported(String name) {
        return false;
    }

    public void setProperty(String name, Object value) throws IllegalArgumentException {
        throw new IllegalArgumentException(name + " property isn't supported");
    }
}

