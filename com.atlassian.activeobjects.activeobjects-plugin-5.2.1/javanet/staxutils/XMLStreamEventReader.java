/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils;

import javanet.staxutils.events.EventAllocator;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;

public class XMLStreamEventReader
implements XMLEventReader {
    private XMLStreamReader reader;
    private XMLEventAllocator allocator;
    private XMLEvent nextEvent;
    private boolean closed;

    public XMLStreamEventReader(XMLStreamReader reader) {
        this.reader = reader;
        this.allocator = new EventAllocator();
    }

    public XMLStreamEventReader(XMLStreamReader reader, XMLEventAllocator allocator) {
        this.reader = reader;
        this.allocator = allocator == null ? new EventAllocator() : allocator;
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        throw new IllegalArgumentException("Unknown property: " + name);
    }

    public synchronized boolean hasNext() {
        if (this.closed) {
            return false;
        }
        try {
            return this.reader.hasNext();
        }
        catch (XMLStreamException e) {
            return false;
        }
    }

    public synchronized XMLEvent nextTag() throws XMLStreamException {
        if (this.closed) {
            throw new XMLStreamException("Stream has been closed");
        }
        this.nextEvent = null;
        this.reader.nextTag();
        return this.nextEvent();
    }

    public synchronized String getElementText() throws XMLStreamException {
        if (this.closed) {
            throw new XMLStreamException("Stream has been closed");
        }
        this.nextEvent = null;
        return this.reader.getElementText();
    }

    public synchronized XMLEvent nextEvent() throws XMLStreamException {
        XMLEvent event;
        if (this.closed) {
            throw new XMLStreamException("Stream has been closed");
        }
        if (this.nextEvent != null) {
            event = this.nextEvent;
            this.nextEvent = null;
        } else {
            event = this.allocateEvent();
            this.reader.next();
        }
        return event;
    }

    public synchronized XMLEvent peek() throws XMLStreamException {
        if (this.closed) {
            throw new XMLStreamException("Stream has been closed");
        }
        if (this.nextEvent == null) {
            this.nextEvent = this.allocateEvent();
            this.reader.next();
        }
        return this.nextEvent;
    }

    public Object next() {
        try {
            return this.nextEvent();
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public synchronized void close() throws XMLStreamException {
        if (!this.closed) {
            this.reader.close();
            this.closed = true;
            this.nextEvent = null;
            this.reader = null;
            this.allocator = null;
        }
    }

    protected XMLEvent allocateEvent() throws XMLStreamException {
        return this.allocator.allocate(this.reader);
    }
}

