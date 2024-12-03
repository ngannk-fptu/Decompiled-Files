/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.internal.stax;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.hibernate.boot.jaxb.internal.stax.BaseXMLEventReader;

public class BufferedXMLEventReader
extends BaseXMLEventReader {
    private final LinkedList<XMLEvent> eventBuffer = new LinkedList();
    private int eventLimit;
    private ListIterator<XMLEvent> bufferReader;

    public BufferedXMLEventReader(XMLEventReader reader) {
        super(reader);
    }

    public BufferedXMLEventReader(XMLEventReader reader, int eventLimit) {
        super(reader);
        this.eventLimit = eventLimit;
    }

    public List<XMLEvent> getBuffer() {
        return new ArrayList<XMLEvent>(this.eventBuffer);
    }

    @Override
    protected XMLEvent internalNextEvent() throws XMLStreamException {
        if (this.bufferReader != null) {
            XMLEvent event = this.bufferReader.next();
            if (!this.bufferReader.hasNext()) {
                this.bufferReader = null;
            }
            return event;
        }
        XMLEvent event = this.getParent().nextEvent();
        if (this.eventLimit != 0) {
            this.eventBuffer.offer(event);
            if (this.eventLimit > 0 && this.eventBuffer.size() > this.eventLimit) {
                this.eventBuffer.poll();
            }
        }
        return event;
    }

    @Override
    public boolean hasNext() {
        return this.bufferReader != null || super.hasNext();
    }

    @Override
    public XMLEvent peek() throws XMLStreamException {
        if (this.bufferReader != null) {
            XMLEvent event = this.bufferReader.next();
            this.bufferReader.previous();
            return event;
        }
        return super.peek();
    }

    public void mark() {
        this.mark(-1);
    }

    public void mark(int eventLimit) {
        this.eventLimit = eventLimit;
        if (this.eventLimit == 0) {
            this.eventBuffer.clear();
            this.bufferReader = null;
        } else if (this.eventLimit > 0) {
            int iteratorIndex = 0;
            if (this.bufferReader != null) {
                int nextIndex = this.bufferReader.nextIndex();
                iteratorIndex = Math.max(0, nextIndex - (this.eventBuffer.size() - this.eventLimit));
            }
            while (this.eventBuffer.size() > this.eventLimit) {
                this.eventBuffer.poll();
            }
            if (this.bufferReader != null) {
                this.bufferReader = this.eventBuffer.listIterator(iteratorIndex);
            }
        }
    }

    public void reset() {
        this.bufferReader = this.eventBuffer.isEmpty() ? null : this.eventBuffer.listIterator();
    }

    @Override
    public void close() throws XMLStreamException {
        this.mark(0);
        super.close();
    }

    public int bufferSize() {
        return this.eventBuffer.size();
    }

    @Override
    public void remove() {
        if (this.bufferReader != null && this.bufferReader.hasNext()) {
            throw new IllegalStateException("Cannot remove a buffered element");
        }
        super.remove();
    }
}

