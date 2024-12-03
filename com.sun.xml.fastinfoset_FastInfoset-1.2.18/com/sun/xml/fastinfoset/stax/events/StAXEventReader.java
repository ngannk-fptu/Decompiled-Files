/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.stax.events;

import com.sun.xml.fastinfoset.CommonResourceBundle;
import com.sun.xml.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.fastinfoset.stax.events.StAXEventAllocatorBase;
import java.util.NoSuchElementException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;

public class StAXEventReader
implements XMLEventReader {
    protected XMLStreamReader _streamReader;
    protected XMLEventAllocator _eventAllocator;
    private XMLEvent _currentEvent;
    private XMLEvent[] events = new XMLEvent[3];
    private int size = 3;
    private int currentIndex = 0;
    private boolean hasEvent = false;

    public StAXEventReader(XMLStreamReader reader) throws XMLStreamException {
        this._streamReader = reader;
        this._eventAllocator = (XMLEventAllocator)reader.getProperty("javax.xml.stream.allocator");
        if (this._eventAllocator == null) {
            this._eventAllocator = new StAXEventAllocatorBase();
        }
        if (!this._streamReader.hasNext()) {
            throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.noElement"));
        }
        this._streamReader.next();
        this.events[0] = this._currentEvent = this._eventAllocator.allocate(this._streamReader);
        this.hasEvent = true;
    }

    @Override
    public boolean hasNext() {
        return this.hasEvent;
    }

    @Override
    public XMLEvent nextEvent() throws XMLStreamException {
        XMLEvent event = null;
        XMLEvent nextEvent = null;
        if (this.hasEvent) {
            event = this.events[this.currentIndex];
            this.events[this.currentIndex] = null;
            if (this._streamReader.hasNext()) {
                this._streamReader.next();
                nextEvent = this._eventAllocator.allocate(this._streamReader);
                if (++this.currentIndex == this.size) {
                    this.currentIndex = 0;
                }
                this.events[this.currentIndex] = nextEvent;
                this.hasEvent = true;
            } else {
                this._currentEvent = null;
                this.hasEvent = false;
            }
            return event;
        }
        throw new NoSuchElementException();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws XMLStreamException {
        this._streamReader.close();
    }

    @Override
    public String getElementText() throws XMLStreamException {
        if (!this.hasEvent) {
            throw new NoSuchElementException();
        }
        if (!this._currentEvent.isStartElement()) {
            StAXDocumentParser parser = (StAXDocumentParser)this._streamReader;
            return parser.getElementText(true);
        }
        return this._streamReader.getElementText();
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        return this._streamReader.getProperty(name);
    }

    @Override
    public XMLEvent nextTag() throws XMLStreamException {
        if (!this.hasEvent) {
            throw new NoSuchElementException();
        }
        StAXDocumentParser parser = (StAXDocumentParser)this._streamReader;
        parser.nextTag(true);
        return this._eventAllocator.allocate(this._streamReader);
    }

    @Override
    public Object next() {
        try {
            return this.nextEvent();
        }
        catch (XMLStreamException streamException) {
            return null;
        }
    }

    @Override
    public XMLEvent peek() throws XMLStreamException {
        if (!this.hasEvent) {
            throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.noElement"));
        }
        this._currentEvent = this.events[this.currentIndex];
        return this._currentEvent;
    }

    public void setAllocator(XMLEventAllocator allocator) {
        if (allocator == null) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.nullXMLEventAllocator"));
        }
        this._eventAllocator = allocator;
    }
}

