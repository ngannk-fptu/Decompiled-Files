/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jettison;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
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
import javax.xml.transform.stream.StreamSource;
import org.codehaus.jettison.json.JSONTokener;

public abstract class AbstractXMLInputFactory
extends XMLInputFactory {
    private static final int INPUT_BUF_SIZE = 1024;
    private int bufSize = 1024;

    protected AbstractXMLInputFactory() {
    }

    protected AbstractXMLInputFactory(int bufSize) {
        this.bufSize = bufSize;
    }

    @Override
    public XMLEventReader createFilteredReader(XMLEventReader arg0, EventFilter arg1) throws XMLStreamException {
        return null;
    }

    @Override
    public XMLStreamReader createFilteredReader(XMLStreamReader arg0, StreamFilter arg1) throws XMLStreamException {
        return null;
    }

    @Override
    public XMLEventReader createXMLEventReader(InputStream arg0, String encoding) throws XMLStreamException {
        return null;
    }

    @Override
    public XMLEventReader createXMLEventReader(InputStream arg0) throws XMLStreamException {
        return null;
    }

    @Override
    public XMLEventReader createXMLEventReader(Reader arg0) throws XMLStreamException {
        return null;
    }

    @Override
    public XMLEventReader createXMLEventReader(Source arg0) throws XMLStreamException {
        return null;
    }

    @Override
    public XMLEventReader createXMLEventReader(String systemId, InputStream arg1) throws XMLStreamException {
        return null;
    }

    @Override
    public XMLEventReader createXMLEventReader(String systemId, Reader arg1) throws XMLStreamException {
        return null;
    }

    @Override
    public XMLEventReader createXMLEventReader(XMLStreamReader arg0) throws XMLStreamException {
        return null;
    }

    @Override
    public XMLStreamReader createXMLStreamReader(InputStream is) throws XMLStreamException {
        return this.createXMLStreamReader(is, null);
    }

    @Override
    public XMLStreamReader createXMLStreamReader(InputStream is, String charset) throws XMLStreamException {
        if (charset == null) {
            charset = "UTF-8";
        }
        try {
            String doc = this.readAll(is, charset);
            return this.createXMLStreamReader(this.createNewJSONTokener(doc));
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    protected JSONTokener createNewJSONTokener(String doc) {
        return new JSONTokener(doc);
    }

    private String readAll(InputStream in, String encoding) throws IOException {
        int count;
        byte[] buffer = new byte[this.bufSize];
        ByteArrayOutputStream bos = null;
        while ((count = in.read(buffer)) >= 0) {
            if (bos == null) {
                int cap = count < 64 ? 64 : (count == this.bufSize ? this.bufSize * 4 : count);
                bos = new ByteArrayOutputStream(cap);
            }
            bos.write(buffer, 0, count);
        }
        return bos == null ? "" : bos.toString(encoding);
    }

    public abstract XMLStreamReader createXMLStreamReader(JSONTokener var1) throws XMLStreamException;

    @Override
    public XMLStreamReader createXMLStreamReader(Reader reader) throws XMLStreamException {
        try {
            return this.createXMLStreamReader(new JSONTokener(this.readAll(reader)));
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    private String readAll(Reader r) throws IOException {
        int count;
        char[] buf = new char[this.bufSize];
        int len = 0;
        do {
            if ((count = r.read(buf, len, buf.length - len)) >= 0) continue;
            return len == 0 ? "" : new String(buf, 0, len);
        } while ((len += count) < buf.length);
        CharArrayWriter wrt = new CharArrayWriter(this.bufSize * 4);
        wrt.write(buf, 0, len);
        while ((len = r.read(buf)) != -1) {
            wrt.write(buf, 0, len);
        }
        return wrt.toString();
    }

    @Override
    public XMLStreamReader createXMLStreamReader(Source src) throws XMLStreamException {
        if (src instanceof StreamSource) {
            StreamSource ss = (StreamSource)src;
            InputStream in = ss.getInputStream();
            String systemId = ss.getSystemId();
            if (in != null) {
                if (systemId != null) {
                    return this.createXMLStreamReader(systemId, in);
                }
                return this.createXMLStreamReader(in);
            }
            Reader r = ss.getReader();
            if (r != null) {
                if (systemId != null) {
                    return this.createXMLStreamReader(systemId, r);
                }
                return this.createXMLStreamReader(r);
            }
            throw new UnsupportedOperationException("Only those javax.xml.transform.stream.StreamSource instances supported that have an InputStream or Reader");
        }
        throw new UnsupportedOperationException("Only javax.xml.transform.stream.StreamSource type supported");
    }

    @Override
    public XMLStreamReader createXMLStreamReader(String systemId, InputStream arg1) throws XMLStreamException {
        return this.createXMLStreamReader(arg1, null);
    }

    @Override
    public XMLStreamReader createXMLStreamReader(String systemId, Reader r) throws XMLStreamException {
        return this.createXMLStreamReader(r);
    }

    @Override
    public XMLEventAllocator getEventAllocator() {
        return null;
    }

    @Override
    public Object getProperty(String arg0) throws IllegalArgumentException {
        throw new IllegalArgumentException();
    }

    @Override
    public XMLReporter getXMLReporter() {
        return null;
    }

    @Override
    public XMLResolver getXMLResolver() {
        return null;
    }

    @Override
    public boolean isPropertySupported(String arg0) {
        return false;
    }

    @Override
    public void setEventAllocator(XMLEventAllocator arg0) {
    }

    @Override
    public void setProperty(String arg0, Object arg1) throws IllegalArgumentException {
        throw new IllegalArgumentException();
    }

    @Override
    public void setXMLReporter(XMLReporter arg0) {
    }

    @Override
    public void setXMLResolver(XMLResolver arg0) {
    }
}

