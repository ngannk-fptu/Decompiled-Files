/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class ForwardingXmlEventReader
implements XMLEventReader {
    protected XMLEventReader delegate;

    public ForwardingXmlEventReader(XMLEventReader delegate) {
        this.delegate = delegate;
    }

    @Override
    public XMLEvent nextEvent() throws XMLStreamException {
        return this.delegate.nextEvent();
    }

    @Override
    public boolean hasNext() {
        return this.delegate.hasNext();
    }

    @Override
    public XMLEvent peek() throws XMLStreamException {
        return this.delegate.peek();
    }

    @Override
    public String getElementText() throws XMLStreamException {
        return this.delegate.getElementText();
    }

    @Override
    public XMLEvent nextTag() throws XMLStreamException {
        return this.delegate.nextTag();
    }

    @Override
    public Object getProperty(String s) throws IllegalArgumentException {
        return this.delegate.getProperty(s);
    }

    @Override
    public void close() throws XMLStreamException {
        this.delegate.close();
    }

    @Override
    public Object next() {
        return this.delegate.next();
    }

    @Override
    public void remove() {
        this.delegate.remove();
    }
}

