/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.helpers;

import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventConsumer;

public class ListEventConsumer
implements XMLEventConsumer {
    private List events;

    public ListEventConsumer() {
    }

    public ListEventConsumer(List events) {
        this.events = events;
    }

    public void add(XMLEvent event) throws XMLStreamException {
        if (this.events == null) {
            this.events = new ArrayList();
        }
        this.events.add(event);
    }

    public List getEvents() {
        return this.events;
    }

    public void setEvents(List events) {
        this.events = events;
    }

    public void reset() {
        if (this.events != null) {
            this.events.clear();
        }
    }
}

