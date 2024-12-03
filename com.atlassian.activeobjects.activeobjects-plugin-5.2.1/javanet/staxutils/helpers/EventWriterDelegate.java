/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.helpers;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public abstract class EventWriterDelegate
implements XMLEventWriter {
    protected final XMLEventWriter out;

    protected EventWriterDelegate(XMLEventWriter out) {
        this.out = out;
    }

    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        this.out.setNamespaceContext(context);
    }

    public NamespaceContext getNamespaceContext() {
        return this.out.getNamespaceContext();
    }

    public void setDefaultNamespace(String uri) throws XMLStreamException {
        this.out.setDefaultNamespace(uri);
    }

    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        this.out.setPrefix(prefix, uri);
    }

    public String getPrefix(String uri) throws XMLStreamException {
        return this.out.getPrefix(uri);
    }

    public void add(XMLEvent event) throws XMLStreamException {
        this.out.add(event);
    }

    public void add(XMLEventReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            this.add(reader.nextEvent());
        }
    }

    public void flush() throws XMLStreamException {
        this.out.flush();
    }

    public void close() throws XMLStreamException {
        this.out.close();
    }
}

