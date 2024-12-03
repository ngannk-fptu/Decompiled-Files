/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.stax.events;

import com.sun.xml.fastinfoset.CommonResourceBundle;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

public class StAXFilteredEvent
implements XMLEventReader {
    private XMLEventReader eventReader;
    private EventFilter _filter;

    public StAXFilteredEvent() {
    }

    public StAXFilteredEvent(XMLEventReader reader, EventFilter filter) throws XMLStreamException {
        this.eventReader = reader;
        this._filter = filter;
    }

    public void setEventReader(XMLEventReader reader) {
        this.eventReader = reader;
    }

    public void setFilter(EventFilter filter) {
        this._filter = filter;
    }

    @Override
    public Object next() {
        try {
            return this.nextEvent();
        }
        catch (XMLStreamException e) {
            return null;
        }
    }

    @Override
    public XMLEvent nextEvent() throws XMLStreamException {
        if (this.hasNext()) {
            return this.eventReader.nextEvent();
        }
        return null;
    }

    @Override
    public String getElementText() throws XMLStreamException {
        StringBuffer buffer = new StringBuffer();
        XMLEvent e = this.nextEvent();
        if (!e.isStartElement()) {
            throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.mustBeOnSTART_ELEMENT"));
        }
        while (this.hasNext()) {
            e = this.nextEvent();
            if (e.isStartElement()) {
                throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.getElementTextExpectTextOnly"));
            }
            if (e.isCharacters()) {
                buffer.append(((Characters)e).getData());
            }
            if (!e.isEndElement()) continue;
            return buffer.toString();
        }
        throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.END_ELEMENTnotFound"));
    }

    @Override
    public XMLEvent nextTag() throws XMLStreamException {
        while (this.hasNext()) {
            XMLEvent e = this.nextEvent();
            if (!e.isStartElement() && !e.isEndElement()) continue;
            return e;
        }
        throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.startOrEndNotFound"));
    }

    @Override
    public boolean hasNext() {
        try {
            while (this.eventReader.hasNext()) {
                if (this._filter.accept(this.eventReader.peek())) {
                    return true;
                }
                this.eventReader.nextEvent();
            }
            return false;
        }
        catch (XMLStreamException e) {
            return false;
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public XMLEvent peek() throws XMLStreamException {
        if (this.hasNext()) {
            return this.eventReader.peek();
        }
        return null;
    }

    @Override
    public void close() throws XMLStreamException {
        this.eventReader.close();
    }

    @Override
    public Object getProperty(String name) {
        return this.eventReader.getProperty(name);
    }
}

