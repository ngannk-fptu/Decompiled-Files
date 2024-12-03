/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.helpers;

import java.io.OutputStream;
import java.io.Writer;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;

public abstract class FilterXMLOutputFactory
extends XMLOutputFactory {
    protected XMLOutputFactory source;

    public FilterXMLOutputFactory() {
        this(XMLOutputFactory.newInstance());
    }

    public FilterXMLOutputFactory(XMLOutputFactory source) {
        this.source = source;
    }

    protected abstract XMLEventWriter filter(XMLEventWriter var1);

    protected abstract XMLStreamWriter filter(XMLStreamWriter var1);

    public boolean isPropertySupported(String name) {
        return this.source.isPropertySupported(name);
    }

    public void setProperty(String name, Object value) throws IllegalArgumentException {
        this.source.setProperty(name, value);
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        return this.source.getProperty(name);
    }

    public XMLEventWriter createXMLEventWriter(Result result) throws XMLStreamException {
        return this.filter(this.source.createXMLEventWriter(result));
    }

    public XMLEventWriter createXMLEventWriter(Writer writer) throws XMLStreamException {
        return this.filter(this.source.createXMLEventWriter(writer));
    }

    public XMLEventWriter createXMLEventWriter(OutputStream stream) throws XMLStreamException {
        return this.filter(this.source.createXMLEventWriter(stream));
    }

    public XMLEventWriter createXMLEventWriter(OutputStream stream, String encoding) throws XMLStreamException {
        return this.filter(this.source.createXMLEventWriter(stream, encoding));
    }

    public XMLStreamWriter createXMLStreamWriter(Result result) throws XMLStreamException {
        return this.filter(this.source.createXMLStreamWriter(result));
    }

    public XMLStreamWriter createXMLStreamWriter(Writer writer) throws XMLStreamException {
        return this.filter(this.source.createXMLStreamWriter(writer));
    }

    public XMLStreamWriter createXMLStreamWriter(OutputStream stream) throws XMLStreamException {
        return this.filter(this.source.createXMLStreamWriter(stream));
    }

    public XMLStreamWriter createXMLStreamWriter(OutputStream stream, String encoding) throws XMLStreamException {
        return this.filter(this.source.createXMLStreamWriter(stream, encoding));
    }

    public int hashCode() {
        return FilterXMLOutputFactory.hashCode(this.source);
    }

    protected static int hashCode(Object o) {
        return o == null ? 0 : o.hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof FilterXMLOutputFactory)) {
            return false;
        }
        FilterXMLOutputFactory that = (FilterXMLOutputFactory)o;
        return FilterXMLOutputFactory.equals(this.source, that.source);
    }

    protected static boolean equals(Object x, Object y) {
        return x == null ? y == null : x.equals(y);
    }
}

