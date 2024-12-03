/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.ForwardingXmlEventReader;
import java.util.NoSuchElementException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class XmlFragmentEventReader
extends ForwardingXmlEventReader {
    protected int count = 0;
    protected boolean startEventConsumed = false;

    public XmlFragmentEventReader(XMLEventReader xmlEventReader) throws XMLStreamException {
        super(xmlEventReader);
    }

    @Override
    public XMLEvent nextEvent() throws XMLStreamException {
        if (!this.hasNext()) {
            throw new NoSuchElementException("No such element exists - reached end of event stream or iterator.");
        }
        XMLEvent nextEvent = this.delegate.nextEvent();
        if (this.count == 0) {
            this.startEventConsumed = true;
        }
        if (nextEvent.isStartElement()) {
            ++this.count;
        } else if (nextEvent.isEndElement()) {
            --this.count;
        }
        return nextEvent;
    }

    @Override
    public boolean hasNext() {
        return !this.startEventConsumed || this.count != 0 && this.delegate.hasNext();
    }

    @Override
    public XMLEvent peek() throws XMLStreamException {
        if (this.hasNext()) {
            return this.delegate.peek();
        }
        return null;
    }

    @Override
    public XMLEvent nextTag() throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws XMLStreamException {
        while (this.hasNext()) {
            this.nextEvent();
        }
    }

    @Override
    public Object next() {
        try {
            return this.nextEvent();
        }
        catch (XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
    }
}

