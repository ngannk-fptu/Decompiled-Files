/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.stax.factory;

import com.sun.xml.fastinfoset.CommonResourceBundle;
import com.sun.xml.fastinfoset.stax.StAXDocumentSerializer;
import com.sun.xml.fastinfoset.stax.StAXManager;
import com.sun.xml.fastinfoset.stax.events.StAXEventWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

public class StAXOutputFactory
extends XMLOutputFactory {
    private StAXManager _manager = new StAXManager(2);

    @Override
    public XMLEventWriter createXMLEventWriter(Result result) throws XMLStreamException {
        return new StAXEventWriter(this.createXMLStreamWriter(result));
    }

    @Override
    public XMLEventWriter createXMLEventWriter(Writer writer) throws XMLStreamException {
        return new StAXEventWriter(this.createXMLStreamWriter(writer));
    }

    @Override
    public XMLEventWriter createXMLEventWriter(OutputStream outputStream) throws XMLStreamException {
        return new StAXEventWriter(this.createXMLStreamWriter(outputStream));
    }

    @Override
    public XMLEventWriter createXMLEventWriter(OutputStream outputStream, String encoding) throws XMLStreamException {
        return new StAXEventWriter(this.createXMLStreamWriter(outputStream, encoding));
    }

    @Override
    public XMLStreamWriter createXMLStreamWriter(Result result) throws XMLStreamException {
        if (result instanceof StreamResult) {
            StreamResult streamResult = (StreamResult)result;
            if (streamResult.getWriter() != null) {
                return this.createXMLStreamWriter(streamResult.getWriter());
            }
            if (streamResult.getOutputStream() != null) {
                return this.createXMLStreamWriter(streamResult.getOutputStream());
            }
            if (streamResult.getSystemId() != null) {
                FileWriter writer = null;
                boolean isError = true;
                try {
                    writer = new FileWriter(new File(streamResult.getSystemId()));
                    XMLStreamWriter streamWriter = this.createXMLStreamWriter(writer);
                    isError = false;
                    XMLStreamWriter xMLStreamWriter = streamWriter;
                    return xMLStreamWriter;
                }
                catch (IOException ie) {
                    throw new XMLStreamException(ie);
                }
                finally {
                    if (isError && writer != null) {
                        try {
                            writer.close();
                        }
                        catch (IOException iOException) {}
                    }
                }
            }
        } else {
            FileWriter writer = null;
            boolean isError = true;
            try {
                writer = new FileWriter(new File(result.getSystemId()));
                XMLStreamWriter streamWriter = this.createXMLStreamWriter(writer);
                isError = false;
                XMLStreamWriter xMLStreamWriter = streamWriter;
                return xMLStreamWriter;
            }
            catch (IOException ie) {
                throw new XMLStreamException(ie);
            }
            finally {
                if (isError && writer != null) {
                    try {
                        writer.close();
                    }
                    catch (IOException iOException) {}
                }
            }
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public XMLStreamWriter createXMLStreamWriter(Writer writer) throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    @Override
    public XMLStreamWriter createXMLStreamWriter(OutputStream outputStream) throws XMLStreamException {
        return new StAXDocumentSerializer(outputStream, new StAXManager(this._manager));
    }

    @Override
    public XMLStreamWriter createXMLStreamWriter(OutputStream outputStream, String encoding) throws XMLStreamException {
        StAXDocumentSerializer serializer = new StAXDocumentSerializer(outputStream, new StAXManager(this._manager));
        serializer.setEncoding(encoding);
        return serializer;
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.propertyNotSupported", new Object[]{null}));
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
    public void setProperty(String name, Object value) throws IllegalArgumentException {
        this._manager.setProperty(name, value);
    }
}

