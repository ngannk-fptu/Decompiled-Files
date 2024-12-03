/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.stax.factory;

import com.sun.xml.fastinfoset.CommonResourceBundle;
import com.sun.xml.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.fastinfoset.stax.StAXManager;
import com.sun.xml.fastinfoset.stax.events.StAXEventReader;
import com.sun.xml.fastinfoset.stax.events.StAXFilteredEvent;
import com.sun.xml.fastinfoset.stax.util.StAXFilteredParser;
import com.sun.xml.fastinfoset.tools.XML_SAX_FI;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

public class StAXInputFactory
extends XMLInputFactory {
    private StAXManager _manager = new StAXManager(1);

    public static XMLInputFactory newInstance() {
        return XMLInputFactory.newInstance();
    }

    @Override
    public XMLStreamReader createXMLStreamReader(Reader xmlfile) throws XMLStreamException {
        return this.getXMLStreamReader(xmlfile);
    }

    @Override
    public XMLStreamReader createXMLStreamReader(InputStream s) throws XMLStreamException {
        return new StAXDocumentParser(s, this._manager);
    }

    @Override
    public XMLStreamReader createXMLStreamReader(String systemId, Reader xmlfile) throws XMLStreamException {
        return this.getXMLStreamReader(xmlfile);
    }

    @Override
    public XMLStreamReader createXMLStreamReader(Source source) throws XMLStreamException {
        return null;
    }

    @Override
    public XMLStreamReader createXMLStreamReader(String systemId, InputStream inputstream) throws XMLStreamException {
        return this.createXMLStreamReader(inputstream);
    }

    @Override
    public XMLStreamReader createXMLStreamReader(InputStream inputstream, String encoding) throws XMLStreamException {
        return this.createXMLStreamReader(inputstream);
    }

    XMLStreamReader getXMLStreamReader(String systemId, InputStream inputstream, String encoding) throws XMLStreamException {
        return this.createXMLStreamReader(inputstream);
    }

    XMLStreamReader getXMLStreamReader(Reader xmlfile) throws XMLStreamException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedStream = new BufferedOutputStream(byteStream);
        StAXDocumentParser sr = null;
        try {
            XML_SAX_FI convertor = new XML_SAX_FI();
            convertor.convert(xmlfile, bufferedStream);
            ByteArrayInputStream byteInputStream = new ByteArrayInputStream(byteStream.toByteArray());
            BufferedInputStream document = new BufferedInputStream(byteInputStream);
            sr = new StAXDocumentParser();
            sr.setInputStream(document);
            sr.setManager(this._manager);
            return sr;
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public XMLEventReader createXMLEventReader(InputStream inputstream) throws XMLStreamException {
        return new StAXEventReader(this.createXMLStreamReader(inputstream));
    }

    @Override
    public XMLEventReader createXMLEventReader(Reader reader) throws XMLStreamException {
        return new StAXEventReader(this.createXMLStreamReader(reader));
    }

    @Override
    public XMLEventReader createXMLEventReader(Source source) throws XMLStreamException {
        return new StAXEventReader(this.createXMLStreamReader(source));
    }

    @Override
    public XMLEventReader createXMLEventReader(String systemId, InputStream inputstream) throws XMLStreamException {
        return new StAXEventReader(this.createXMLStreamReader(systemId, inputstream));
    }

    @Override
    public XMLEventReader createXMLEventReader(InputStream stream, String encoding) throws XMLStreamException {
        return new StAXEventReader(this.createXMLStreamReader(stream, encoding));
    }

    @Override
    public XMLEventReader createXMLEventReader(String systemId, Reader reader) throws XMLStreamException {
        return new StAXEventReader(this.createXMLStreamReader(systemId, reader));
    }

    @Override
    public XMLEventReader createXMLEventReader(XMLStreamReader streamReader) throws XMLStreamException {
        return new StAXEventReader(streamReader);
    }

    @Override
    public XMLEventAllocator getEventAllocator() {
        return (XMLEventAllocator)this.getProperty("javax.xml.stream.allocator");
    }

    @Override
    public XMLReporter getXMLReporter() {
        return (XMLReporter)this._manager.getProperty("javax.xml.stream.reporter");
    }

    @Override
    public XMLResolver getXMLResolver() {
        Object object = this._manager.getProperty("javax.xml.stream.resolver");
        return (XMLResolver)object;
    }

    @Override
    public void setXMLReporter(XMLReporter xmlreporter) {
        this._manager.setProperty("javax.xml.stream.reporter", xmlreporter);
    }

    @Override
    public void setXMLResolver(XMLResolver xmlresolver) {
        this._manager.setProperty("javax.xml.stream.resolver", xmlresolver);
    }

    @Override
    public XMLEventReader createFilteredReader(XMLEventReader reader, EventFilter filter) throws XMLStreamException {
        return new StAXFilteredEvent(reader, filter);
    }

    @Override
    public XMLStreamReader createFilteredReader(XMLStreamReader reader, StreamFilter filter) throws XMLStreamException {
        if (reader != null && filter != null) {
            return new StAXFilteredParser(reader, filter);
        }
        return null;
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.nullPropertyName"));
        }
        if (this._manager.containsProperty(name)) {
            return this._manager.getProperty(name);
        }
        throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.propertyNotSupported", new Object[]{name}));
    }

    @Override
    public boolean isPropertySupported(String name) {
        if (name == null) {
            return false;
        }
        return this._manager.containsProperty(name);
    }

    @Override
    public void setEventAllocator(XMLEventAllocator allocator) {
        this._manager.setProperty("javax.xml.stream.allocator", allocator);
    }

    @Override
    public void setProperty(String name, Object value) throws IllegalArgumentException {
        this._manager.setProperty(name, value);
    }
}

