/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import javanet.staxutils.XMLStreamEventReader;
import javax.xml.stream.EventFilter;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.transform.Source;

public abstract class BaseXMLInputFactory
extends XMLInputFactory {
    protected XMLEventAllocator eventAllocator;
    protected XMLReporter xmlReporter;
    protected XMLResolver xmlResolver;

    public Object getProperty(String name) throws IllegalArgumentException {
        throw new IllegalArgumentException(name + " property not supported");
    }

    public boolean isPropertySupported(String name) {
        return false;
    }

    public void setProperty(String name, Object value) throws IllegalArgumentException {
        throw new IllegalArgumentException(name + " property not supported");
    }

    public XMLEventAllocator getEventAllocator() {
        return this.eventAllocator;
    }

    public void setEventAllocator(XMLEventAllocator eventAllocator) {
        this.eventAllocator = eventAllocator;
    }

    public XMLReporter getXMLReporter() {
        return this.xmlReporter;
    }

    public void setXMLReporter(XMLReporter xmlReporter) {
        this.xmlReporter = xmlReporter;
    }

    public XMLResolver getXMLResolver() {
        return this.xmlResolver;
    }

    public void setXMLResolver(XMLResolver xmlResolver) {
        this.xmlResolver = xmlResolver;
    }

    public XMLEventReader createXMLEventReader(InputStream stream, String encoding) throws XMLStreamException {
        try {
            if (encoding != null) {
                return this.createXMLEventReader(new InputStreamReader(stream, encoding), encoding);
            }
            return this.createXMLEventReader(new InputStreamReader(stream));
        }
        catch (UnsupportedEncodingException e) {
            throw new XMLStreamException(e);
        }
    }

    public XMLEventReader createXMLEventReader(InputStream stream) throws XMLStreamException {
        return this.createXMLEventReader(new InputStreamReader(stream));
    }

    public XMLEventReader createXMLEventReader(String systemId, InputStream stream) throws XMLStreamException {
        return this.createXMLEventReader(systemId, new InputStreamReader(stream));
    }

    public XMLEventReader createXMLEventReader(XMLStreamReader reader) throws XMLStreamException {
        return new XMLStreamEventReader(reader);
    }

    public XMLStreamReader createXMLStreamReader(InputStream stream, String encoding) throws XMLStreamException {
        try {
            if (encoding != null) {
                return this.createXMLStreamReader(new InputStreamReader(stream, encoding), encoding);
            }
            return this.createXMLStreamReader(new InputStreamReader(stream));
        }
        catch (UnsupportedEncodingException e) {
            throw new XMLStreamException(e);
        }
    }

    public XMLStreamReader createXMLStreamReader(InputStream stream) throws XMLStreamException {
        return this.createXMLStreamReader(new InputStreamReader(stream));
    }

    public XMLStreamReader createXMLStreamReader(String systemId, InputStream stream) throws XMLStreamException {
        return this.createXMLStreamReader(systemId, new InputStreamReader(stream));
    }

    public XMLEventReader createXMLEventReader(Reader reader) throws XMLStreamException {
        return this.createXMLEventReader(this.createXMLStreamReader(reader));
    }

    public XMLEventReader createXMLEventReader(Reader reader, String encoding) throws XMLStreamException {
        return this.createXMLEventReader(this.createXMLStreamReader(reader, encoding));
    }

    public XMLEventReader createXMLEventReader(Source source) throws XMLStreamException {
        return this.createXMLEventReader(this.createXMLStreamReader(source));
    }

    public XMLEventReader createXMLEventReader(String systemId, Reader reader) throws XMLStreamException {
        return this.createXMLEventReader(this.createXMLStreamReader(systemId, reader));
    }

    public XMLEventReader createXMLEventReader(String systemId, Reader reader, String encoding) throws XMLStreamException {
        return this.createXMLEventReader(this.createXMLStreamReader(systemId, reader, encoding));
    }

    public XMLStreamReader createXMLStreamReader(Source source) throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    public XMLStreamReader createXMLStreamReader(Reader reader) throws XMLStreamException {
        return this.createXMLStreamReader(null, reader, null);
    }

    public XMLStreamReader createXMLStreamReader(Reader reader, String encoding) throws XMLStreamException {
        return this.createXMLStreamReader(null, reader, encoding);
    }

    public XMLStreamReader createXMLStreamReader(String systemId, Reader reader) throws XMLStreamException {
        String encoding = null;
        if (reader instanceof InputStreamReader) {
            encoding = ((InputStreamReader)reader).getEncoding();
        }
        return this.createXMLStreamReader(systemId, reader, encoding);
    }

    public XMLEventReader createFilteredReader(XMLEventReader reader, EventFilter filter) throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    public XMLStreamReader createFilteredReader(XMLStreamReader reader, StreamFilter filter) throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    public abstract XMLStreamReader createXMLStreamReader(String var1, Reader var2, String var3) throws XMLStreamException;
}

