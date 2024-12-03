/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri.evt;

import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.codehaus.stax2.XMLEventReader2;

public class Stax2FilteredEventReader
implements XMLEventReader2,
XMLStreamConstants {
    final XMLEventReader2 mReader;
    final EventFilter mFilter;

    public Stax2FilteredEventReader(XMLEventReader2 xMLEventReader2, EventFilter eventFilter) {
        this.mReader = xMLEventReader2;
        this.mFilter = eventFilter;
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
        try {
            return this.peek() != null;
        }
        catch (XMLStreamException xMLStreamException) {
            throw new RuntimeException(xMLStreamException);
        }
    }

    public XMLEvent nextEvent() throws XMLStreamException {
        XMLEvent xMLEvent;
        while ((xMLEvent = this.mReader.nextEvent()) != null && !this.mFilter.accept(xMLEvent)) {
        }
        return xMLEvent;
    }

    public Object next() {
        try {
            return this.nextEvent();
        }
        catch (XMLStreamException xMLStreamException) {
            throw new RuntimeException(xMLStreamException);
        }
    }

    public XMLEvent nextTag() throws XMLStreamException {
        XMLEvent xMLEvent;
        while ((xMLEvent = this.mReader.nextTag()) != null && !this.mFilter.accept(xMLEvent)) {
        }
        return xMLEvent;
    }

    public XMLEvent peek() throws XMLStreamException {
        XMLEvent xMLEvent;
        while ((xMLEvent = this.mReader.peek()) != null && !this.mFilter.accept(xMLEvent)) {
            this.mReader.nextEvent();
        }
        return xMLEvent;
    }

    public void remove() {
        this.mReader.remove();
    }

    public boolean hasNextEvent() throws XMLStreamException {
        return this.peek() != null;
    }

    public boolean isPropertySupported(String string) {
        return this.mReader.isPropertySupported(string);
    }

    public boolean setProperty(String string, Object object) {
        return this.mReader.setProperty(string, object);
    }
}

