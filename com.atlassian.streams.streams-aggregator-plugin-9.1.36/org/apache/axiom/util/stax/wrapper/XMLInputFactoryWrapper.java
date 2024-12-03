/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.wrapper;

import java.io.InputStream;
import java.io.Reader;
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

public class XMLInputFactoryWrapper
extends XMLInputFactory {
    private final XMLInputFactory parent;

    public XMLInputFactoryWrapper(XMLInputFactory parent) {
        this.parent = parent;
    }

    public XMLEventReader createFilteredReader(XMLEventReader reader, EventFilter filter) throws XMLStreamException {
        return this.parent.createFilteredReader(reader, filter);
    }

    public XMLStreamReader createFilteredReader(XMLStreamReader reader, StreamFilter filter) throws XMLStreamException {
        return this.parent.createFilteredReader(reader, filter);
    }

    public XMLEventReader createXMLEventReader(InputStream stream, String encoding) throws XMLStreamException {
        return this.parent.createXMLEventReader(stream, encoding);
    }

    public XMLEventReader createXMLEventReader(InputStream stream) throws XMLStreamException {
        return this.parent.createXMLEventReader(stream);
    }

    public XMLEventReader createXMLEventReader(Reader reader) throws XMLStreamException {
        return this.parent.createXMLEventReader(reader);
    }

    public XMLEventReader createXMLEventReader(Source source) throws XMLStreamException {
        return this.parent.createXMLEventReader(source);
    }

    public XMLEventReader createXMLEventReader(String systemId, InputStream stream) throws XMLStreamException {
        return this.parent.createXMLEventReader(systemId, stream);
    }

    public XMLEventReader createXMLEventReader(String systemId, Reader reader) throws XMLStreamException {
        return this.parent.createXMLEventReader(systemId, reader);
    }

    public XMLEventReader createXMLEventReader(XMLStreamReader reader) throws XMLStreamException {
        return this.parent.createXMLEventReader(reader);
    }

    public XMLStreamReader createXMLStreamReader(InputStream stream, String encoding) throws XMLStreamException {
        return this.parent.createXMLStreamReader(stream, encoding);
    }

    public XMLStreamReader createXMLStreamReader(InputStream stream) throws XMLStreamException {
        return this.parent.createXMLStreamReader(stream);
    }

    public XMLStreamReader createXMLStreamReader(Reader reader) throws XMLStreamException {
        return this.parent.createXMLStreamReader(reader);
    }

    public XMLStreamReader createXMLStreamReader(Source source) throws XMLStreamException {
        return this.parent.createXMLStreamReader(source);
    }

    public XMLStreamReader createXMLStreamReader(String systemId, InputStream stream) throws XMLStreamException {
        return this.parent.createXMLStreamReader(systemId, stream);
    }

    public XMLStreamReader createXMLStreamReader(String systemId, Reader reader) throws XMLStreamException {
        return this.parent.createXMLStreamReader(systemId, reader);
    }

    public XMLEventAllocator getEventAllocator() {
        return this.parent.getEventAllocator();
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        return this.parent.getProperty(name);
    }

    public XMLReporter getXMLReporter() {
        return this.parent.getXMLReporter();
    }

    public XMLResolver getXMLResolver() {
        return this.parent.getXMLResolver();
    }

    public boolean isPropertySupported(String name) {
        return this.parent.isPropertySupported(name);
    }

    public void setEventAllocator(XMLEventAllocator allocator) {
        this.parent.setEventAllocator(allocator);
    }

    public void setProperty(String name, Object value) throws IllegalArgumentException {
        this.parent.setProperty(name, value);
    }

    public void setXMLReporter(XMLReporter reporter) {
        this.parent.setXMLReporter(reporter);
    }

    public void setXMLResolver(XMLResolver resolver) {
        this.parent.setXMLResolver(resolver);
    }
}

