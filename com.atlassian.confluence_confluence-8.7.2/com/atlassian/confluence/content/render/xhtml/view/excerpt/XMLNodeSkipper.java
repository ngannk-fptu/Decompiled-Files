/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Throwables
 */
package com.atlassian.confluence.content.render.xhtml.view.excerpt;

import com.google.common.base.Throwables;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class XMLNodeSkipper {
    private final Iterator<XMLEvent> events;
    private int count;
    private boolean startEventConsumed;

    public static void skipCurrentNodeTree(final XMLEventReader reader) {
        XMLNodeSkipper.skipCurrentNode(new Iterator<XMLEvent>(){

            @Override
            public boolean hasNext() {
                return reader.hasNext();
            }

            @Override
            public XMLEvent next() {
                try {
                    return reader.nextEvent();
                }
                catch (XMLStreamException e) {
                    throw Throwables.propagate((Throwable)e);
                }
            }
        });
    }

    public static void skipCurrentNode(Iterator<XMLEvent> xmlEvents) {
        new XMLNodeSkipper(xmlEvents).skipCurrentNode();
    }

    public XMLNodeSkipper(Iterator<XMLEvent> events) {
        this.events = events;
    }

    private XMLEvent nextEvent() {
        if (!this.hasNext()) {
            throw new NoSuchElementException("No such element exists - reached end of event stream or iterator.");
        }
        XMLEvent nextEvent = this.events.next();
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

    private boolean hasNext() {
        return !this.startEventConsumed || this.count != 0 && this.events.hasNext();
    }

    public Iterator<XMLEvent> skipCurrentNode() {
        while (this.hasNext()) {
            this.nextEvent();
        }
        return this.events;
    }
}

