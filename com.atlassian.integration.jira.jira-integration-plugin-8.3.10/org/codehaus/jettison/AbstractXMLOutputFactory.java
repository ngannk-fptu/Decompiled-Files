/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jettison;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import org.codehaus.jettison.AbstractXMLEventWriter;

public abstract class AbstractXMLOutputFactory
extends XMLOutputFactory {
    @Override
    public XMLEventWriter createXMLEventWriter(OutputStream out, String charset) throws XMLStreamException {
        return new AbstractXMLEventWriter(this.createXMLStreamWriter(out, charset));
    }

    @Override
    public XMLEventWriter createXMLEventWriter(OutputStream out) throws XMLStreamException {
        return new AbstractXMLEventWriter(this.createXMLStreamWriter(out));
    }

    @Override
    public XMLEventWriter createXMLEventWriter(Result result) throws XMLStreamException {
        return new AbstractXMLEventWriter(this.createXMLStreamWriter(result));
    }

    @Override
    public XMLEventWriter createXMLEventWriter(Writer writer) throws XMLStreamException {
        return new AbstractXMLEventWriter(this.createXMLStreamWriter(writer));
    }

    @Override
    public XMLStreamWriter createXMLStreamWriter(OutputStream out, String charset) throws XMLStreamException {
        if (charset == null) {
            charset = "UTF-8";
        }
        try {
            return this.createXMLStreamWriter(new OutputStreamWriter(out, charset));
        }
        catch (UnsupportedEncodingException e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    public XMLStreamWriter createXMLStreamWriter(OutputStream out) throws XMLStreamException {
        return this.createXMLStreamWriter(out, null);
    }

    @Override
    public XMLStreamWriter createXMLStreamWriter(Result result) throws XMLStreamException {
        if (result instanceof StreamResult) {
            StreamResult sr = (StreamResult)result;
            OutputStream out = sr.getOutputStream();
            if (out != null) {
                return this.createXMLStreamWriter(out);
            }
            Writer w = sr.getWriter();
            if (w != null) {
                return this.createXMLStreamWriter(w);
            }
            throw new UnsupportedOperationException("Only those javax.xml.transform.stream.StreamResult instances supported that have an OutputStream or Writer");
        }
        throw new UnsupportedOperationException("Only javax.xml.transform.stream.StreamResult type supported");
    }

    @Override
    public abstract XMLStreamWriter createXMLStreamWriter(Writer var1) throws XMLStreamException;

    @Override
    public Object getProperty(String arg0) throws IllegalArgumentException {
        return null;
    }

    @Override
    public boolean isPropertySupported(String arg0) {
        return false;
    }

    @Override
    public void setProperty(String arg0, Object arg1) throws IllegalArgumentException {
    }
}

