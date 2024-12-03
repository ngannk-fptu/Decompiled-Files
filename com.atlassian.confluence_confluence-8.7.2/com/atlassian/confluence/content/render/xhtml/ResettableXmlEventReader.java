/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.ForwardingXmlEventReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class ResettableXmlEventReader
extends ForwardingXmlEventReader {
    private final List<XMLEvent> eventsCache = new ArrayList<XMLEvent>();
    private int pointer = 0;

    public ResettableXmlEventReader(XMLEventReader delegate) {
        super(delegate);
    }

    public ResettableXmlEventReader reset() {
        this.pointer = 0;
        return this;
    }

    @Override
    public XMLEvent nextEvent() throws XMLStreamException {
        if (this.isCurrentEventCached()) {
            return this.eventsCache.get(this.pointer++);
        }
        XMLEvent nextEvent = this.delegate.nextEvent();
        this.eventsCache.add(nextEvent);
        ++this.pointer;
        return nextEvent;
    }

    private boolean isCurrentEventCached() {
        return this.eventsCache.size() > 0 && this.pointer < this.eventsCache.size();
    }

    @Override
    public boolean hasNext() {
        return this.isCurrentEventCached() ? this.pointer < this.eventsCache.size() : this.delegate.hasNext();
    }

    @Override
    public XMLEvent peek() throws XMLStreamException {
        if (this.isCurrentEventCached()) {
            return this.eventsCache.get(this.pointer);
        }
        return this.delegate.peek();
    }

    public int getCurrentEventPosition() {
        return this.pointer;
    }

    public void restoreEventPosition(int position) {
        if (position < 0) {
            throw new IllegalArgumentException("position must be greater than 0");
        }
        this.pointer = position;
    }

    @Override
    public String getElementText() throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    @Override
    public XMLEvent nextTag() throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object next() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ResettableXmlEventReader{pointer=" + this.pointer + ", eventsCache=\"");
        for (XMLEvent event : this.eventsCache) {
            if (event.isStartElement()) {
                sb.append("<").append(event.asStartElement().getName().getLocalPart()).append(" ...>");
                continue;
            }
            if (event.isEndElement()) {
                sb.append("</").append(event.asEndElement().getName().getLocalPart()).append(" ...>");
                continue;
            }
            if (event.isCharacters()) {
                sb.append(event.asCharacters().getData());
                continue;
            }
            sb.append(event.toString());
        }
        sb.append("\"}");
        return sb.toString();
    }
}

