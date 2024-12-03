/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl;

import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;

public class XMLSecurityEventReader
implements XMLEventReader {
    private final Iterator<XMLSecEvent> xmlSecEventIterator;
    private XMLEvent xmlSecEvent;

    public XMLSecurityEventReader(Deque<XMLSecEvent> xmlSecEvents, int fromIndex) {
        this.xmlSecEventIterator = xmlSecEvents.descendingIterator();
        int curIdx = 0;
        while (curIdx++ < fromIndex) {
            this.xmlSecEventIterator.next();
        }
    }

    @Override
    public XMLEvent nextEvent() throws XMLStreamException {
        XMLEvent currentXMLEvent;
        if (this.xmlSecEvent != null) {
            XMLEvent currentXMLEvent2 = this.xmlSecEvent;
            this.xmlSecEvent = null;
            return currentXMLEvent2;
        }
        try {
            currentXMLEvent = this.xmlSecEventIterator.next();
        }
        catch (NoSuchElementException e) {
            throw new XMLStreamException(e);
        }
        return currentXMLEvent;
    }

    @Override
    public boolean hasNext() {
        if (this.xmlSecEvent != null) {
            return true;
        }
        return this.xmlSecEventIterator.hasNext();
    }

    @Override
    public XMLEvent peek() throws XMLStreamException {
        if (this.xmlSecEvent != null) {
            return this.xmlSecEvent;
        }
        try {
            this.xmlSecEvent = this.xmlSecEventIterator.next();
            return this.xmlSecEvent;
        }
        catch (NoSuchElementException e) {
            return null;
        }
    }

    @Override
    public String getElementText() throws XMLStreamException {
        throw new XMLStreamException(new UnsupportedOperationException());
    }

    @Override
    public XMLEvent nextTag() throws XMLStreamException {
        throw new XMLStreamException(new UnsupportedOperationException());
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        throw new IllegalArgumentException(new UnsupportedOperationException());
    }

    @Override
    public void close() throws XMLStreamException {
    }

    @Override
    public Object next() {
        try {
            return this.nextEvent();
        }
        catch (XMLStreamException e) {
            throw new NoSuchElementException(e.getMessage());
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

