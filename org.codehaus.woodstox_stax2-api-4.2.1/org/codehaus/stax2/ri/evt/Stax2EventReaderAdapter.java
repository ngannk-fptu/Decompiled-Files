/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri.evt;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.codehaus.stax2.XMLEventReader2;

public class Stax2EventReaderAdapter
implements XMLEventReader2 {
    protected final XMLEventReader mReader;

    protected Stax2EventReaderAdapter(XMLEventReader er) {
        this.mReader = er;
    }

    public static XMLEventReader2 wrapIfNecessary(XMLEventReader er) {
        if (er instanceof XMLEventReader2) {
            return (XMLEventReader2)er;
        }
        return new Stax2EventReaderAdapter(er);
    }

    @Override
    public void close() throws XMLStreamException {
        this.mReader.close();
    }

    @Override
    public String getElementText() throws XMLStreamException {
        return this.mReader.getElementText();
    }

    @Override
    public Object getProperty(String name) {
        return this.mReader.getProperty(name);
    }

    @Override
    public boolean hasNext() {
        return this.mReader.hasNext();
    }

    @Override
    public XMLEvent nextEvent() throws XMLStreamException {
        return this.mReader.nextEvent();
    }

    @Override
    public Object next() {
        return this.mReader.next();
    }

    @Override
    public XMLEvent nextTag() throws XMLStreamException {
        return this.mReader.nextTag();
    }

    @Override
    public XMLEvent peek() throws XMLStreamException {
        return this.mReader.peek();
    }

    @Override
    public void remove() {
        this.mReader.remove();
    }

    @Override
    public boolean hasNextEvent() throws XMLStreamException {
        return this.peek() != null;
    }

    @Override
    public boolean isPropertySupported(String name) {
        try {
            this.mReader.getProperty(name);
        }
        catch (IllegalArgumentException iae) {
            return false;
        }
        return true;
    }

    @Override
    public boolean setProperty(String name, Object value) {
        return false;
    }
}

