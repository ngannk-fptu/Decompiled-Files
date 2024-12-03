/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util.xml;

import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.hibernate.internal.util.xml.BaseXMLEventReader;

public abstract class FilteringXMLEventReader
extends BaseXMLEventReader {
    private final Deque<QName> prunedElements = new LinkedList<QName>();
    private XMLEvent peekedEvent;

    public FilteringXMLEventReader(XMLEventReader reader) {
        super(reader);
    }

    @Override
    protected final XMLEvent internalNextEvent() throws XMLStreamException {
        return this.internalNext(false);
    }

    @Override
    public boolean hasNext() {
        try {
            return this.peekedEvent != null || super.hasNext() && this.peek() != null;
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        catch (NoSuchElementException e) {
            return false;
        }
    }

    @Override
    public final XMLEvent peek() throws XMLStreamException {
        if (this.peekedEvent != null) {
            return this.peekedEvent;
        }
        this.peekedEvent = this.internalNext(true);
        return this.peekedEvent;
    }

    protected final XMLEvent internalNext(boolean peek) throws XMLStreamException {
        XMLEvent event = null;
        if (this.peekedEvent != null) {
            event = this.peekedEvent;
            this.peekedEvent = null;
            return event;
        }
        do {
            event = super.getParent().nextEvent();
            if (!this.prunedElements.isEmpty()) {
                EndElement endElement;
                QName endElementName;
                QName startElementName;
                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    this.prunedElements.push(startElement.getName());
                } else if (event.isEndElement() && !(startElementName = this.prunedElements.pop()).equals(endElementName = (endElement = event.asEndElement()).getName())) {
                    throw new IllegalArgumentException("Malformed XMLEvent stream. Expected end element for " + startElementName + " but found end element for " + endElementName);
                }
                event = null;
                continue;
            }
            XMLEvent filteredEvent = this.filterEvent(event, peek);
            if (filteredEvent == null && event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                QName name = startElement.getName();
                this.prunedElements.push(name);
            }
            event = filteredEvent;
        } while (event == null);
        return event;
    }

    protected abstract XMLEvent filterEvent(XMLEvent var1, boolean var2);
}

