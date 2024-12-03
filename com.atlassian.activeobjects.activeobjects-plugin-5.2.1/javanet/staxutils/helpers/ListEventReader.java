/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.helpers;

import java.util.List;
import java.util.NoSuchElementException;
import javanet.staxutils.BaseXMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class ListEventReader
extends BaseXMLEventReader {
    private int nextEvent = 0;
    private List events;

    public ListEventReader(List events) {
        this.events = events;
    }

    public XMLEvent nextEvent() throws XMLStreamException {
        if (this.hasNext()) {
            XMLEvent event = (XMLEvent)this.events.get(this.nextEvent);
            ++this.nextEvent;
            return event;
        }
        throw new NoSuchElementException("End of stream reached");
    }

    public boolean hasNext() {
        return this.nextEvent < this.events.size();
    }

    public XMLEvent peek() throws XMLStreamException {
        if (this.hasNext()) {
            return (XMLEvent)this.events.get(this.nextEvent);
        }
        return null;
    }
}

