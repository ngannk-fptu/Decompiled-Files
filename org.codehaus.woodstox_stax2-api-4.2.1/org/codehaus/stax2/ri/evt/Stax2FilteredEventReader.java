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

    public Stax2FilteredEventReader(XMLEventReader2 r, EventFilter f) {
        this.mReader = r;
        this.mFilter = f;
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
        try {
            return this.peek() != null;
        }
        catch (XMLStreamException sex) {
            throw new RuntimeException(sex);
        }
    }

    @Override
    public XMLEvent nextEvent() throws XMLStreamException {
        XMLEvent evt;
        while ((evt = this.mReader.nextEvent()) != null && !this.mFilter.accept(evt)) {
        }
        return evt;
    }

    @Override
    public Object next() {
        try {
            return this.nextEvent();
        }
        catch (XMLStreamException sex) {
            throw new RuntimeException(sex);
        }
    }

    @Override
    public XMLEvent nextTag() throws XMLStreamException {
        XMLEvent evt;
        while ((evt = this.mReader.nextTag()) != null && !this.mFilter.accept(evt)) {
        }
        return evt;
    }

    @Override
    public XMLEvent peek() throws XMLStreamException {
        XMLEvent evt;
        while ((evt = this.mReader.peek()) != null && !this.mFilter.accept(evt)) {
            this.mReader.nextEvent();
        }
        return evt;
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
        return this.mReader.isPropertySupported(name);
    }

    @Override
    public boolean setProperty(String name, Object value) {
        return this.mReader.setProperty(name, value);
    }
}

