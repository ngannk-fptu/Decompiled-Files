/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils;

import java.util.NoSuchElementException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.XMLEvent;

public abstract class BaseXMLEventReader
implements XMLEventReader {
    protected boolean closed;

    public synchronized String getElementText() throws XMLStreamException {
        XMLEvent event;
        if (this.closed) {
            throw new XMLStreamException("Stream has been closed");
        }
        StringBuffer buffer = new StringBuffer();
        while ((event = this.nextEvent()).isCharacters()) {
            if (event.getEventType() == 6) continue;
            buffer.append(event.asCharacters().getData());
        }
        if (!event.isEndElement()) {
            throw new XMLStreamException("Non-text event encountered in getElementText(): " + event);
        }
        return buffer.toString();
    }

    public XMLEvent nextTag() throws XMLStreamException {
        XMLEvent event;
        if (this.closed) {
            throw new XMLStreamException("Stream has been closed");
        }
        do {
            if (this.hasNext()) {
                event = this.nextEvent();
                if (event.isStartElement() || event.isEndElement()) {
                    return event;
                }
                if (event.isCharacters()) {
                    if (event.asCharacters().isWhiteSpace()) continue;
                    throw new XMLStreamException("Non-ignorable space encountered");
                }
                if (event instanceof Comment) continue;
                throw new XMLStreamException("Non-ignorable event encountered: " + event);
            }
            throw new XMLStreamException("Ran out of events in nextTag()");
        } while (!event.isStartElement() && !event.isEndElement());
        return event;
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        throw new IllegalArgumentException("Property not supported: " + name);
    }

    public synchronized void close() throws XMLStreamException {
        if (!this.closed) {
            this.closed = true;
        }
    }

    public Object next() {
        try {
            return this.nextEvent();
        }
        catch (XMLStreamException e) {
            NoSuchElementException ex = new NoSuchElementException("Error getting next event");
            ex.initCause(e);
            throw ex;
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}

