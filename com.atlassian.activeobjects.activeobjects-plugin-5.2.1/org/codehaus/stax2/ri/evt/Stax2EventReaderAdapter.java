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
    final XMLEventReader mReader;

    protected Stax2EventReaderAdapter(XMLEventReader xMLEventReader) {
        this.mReader = xMLEventReader;
    }

    public static XMLEventReader2 wrapIfNecessary(XMLEventReader xMLEventReader) {
        if (xMLEventReader instanceof XMLEventReader2) {
            return (XMLEventReader2)xMLEventReader;
        }
        return new Stax2EventReaderAdapter(xMLEventReader);
    }

    public void close() throws XMLStreamException {
        this.mReader.close();
    }

    public String getElementText() throws XMLStreamException {
        return this.mReader.getElementText();
    }

    public Object getProperty(String string) {
        return this.mReader.getProperty(string);
    }

    public boolean hasNext() {
        return this.mReader.hasNext();
    }

    public XMLEvent nextEvent() throws XMLStreamException {
        return this.mReader.nextEvent();
    }

    public Object next() {
        return this.mReader.next();
    }

    public XMLEvent nextTag() throws XMLStreamException {
        return this.mReader.nextTag();
    }

    public XMLEvent peek() throws XMLStreamException {
        return this.mReader.peek();
    }

    public void remove() {
        this.mReader.remove();
    }

    public boolean hasNextEvent() throws XMLStreamException {
        return this.peek() != null;
    }

    public boolean isPropertySupported(String string) {
        try {
            this.mReader.getProperty(string);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return false;
        }
        return true;
    }

    public boolean setProperty(String string, Object object) {
        return false;
    }
}

