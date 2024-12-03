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
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import org.apache.axiom.util.stax.wrapper.XMLInputFactoryWrapper;

public class WrappingXMLInputFactory
extends XMLInputFactoryWrapper {
    public WrappingXMLInputFactory(XMLInputFactory parent) {
        super(parent);
    }

    protected XMLEventReader wrap(XMLEventReader reader) {
        return reader;
    }

    protected XMLStreamReader wrap(XMLStreamReader reader) {
        return reader;
    }

    public XMLEventReader createFilteredReader(XMLEventReader reader, EventFilter filter) throws XMLStreamException {
        return this.wrap(super.createFilteredReader(reader, filter));
    }

    public XMLStreamReader createFilteredReader(XMLStreamReader reader, StreamFilter filter) throws XMLStreamException {
        return this.wrap(super.createFilteredReader(reader, filter));
    }

    public XMLEventReader createXMLEventReader(InputStream stream, String encoding) throws XMLStreamException {
        return this.wrap(super.createXMLEventReader(stream, encoding));
    }

    public XMLEventReader createXMLEventReader(InputStream stream) throws XMLStreamException {
        return this.wrap(super.createXMLEventReader(stream));
    }

    public XMLEventReader createXMLEventReader(Reader reader) throws XMLStreamException {
        return this.wrap(super.createXMLEventReader(reader));
    }

    public XMLEventReader createXMLEventReader(Source source) throws XMLStreamException {
        return this.wrap(super.createXMLEventReader(source));
    }

    public XMLEventReader createXMLEventReader(String systemId, InputStream stream) throws XMLStreamException {
        return this.wrap(super.createXMLEventReader(systemId, stream));
    }

    public XMLEventReader createXMLEventReader(String systemId, Reader reader) throws XMLStreamException {
        return this.wrap(super.createXMLEventReader(systemId, reader));
    }

    public XMLEventReader createXMLEventReader(XMLStreamReader reader) throws XMLStreamException {
        return this.wrap(super.createXMLEventReader(reader));
    }

    public XMLStreamReader createXMLStreamReader(InputStream stream, String encoding) throws XMLStreamException {
        return this.wrap(super.createXMLStreamReader(stream, encoding));
    }

    public XMLStreamReader createXMLStreamReader(InputStream stream) throws XMLStreamException {
        return this.wrap(super.createXMLStreamReader(stream));
    }

    public XMLStreamReader createXMLStreamReader(Reader reader) throws XMLStreamException {
        return this.wrap(super.createXMLStreamReader(reader));
    }

    public XMLStreamReader createXMLStreamReader(Source source) throws XMLStreamException {
        return this.wrap(super.createXMLStreamReader(source));
    }

    public XMLStreamReader createXMLStreamReader(String systemId, InputStream stream) throws XMLStreamException {
        return this.wrap(super.createXMLStreamReader(systemId, stream));
    }

    public XMLStreamReader createXMLStreamReader(String systemId, Reader reader) throws XMLStreamException {
        return this.wrap(super.createXMLStreamReader(systemId, reader));
    }
}

