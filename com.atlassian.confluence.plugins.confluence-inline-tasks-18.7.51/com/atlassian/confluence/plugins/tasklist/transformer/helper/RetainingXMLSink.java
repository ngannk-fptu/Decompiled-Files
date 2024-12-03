/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.plugins.tasklist.transformer.helper;

import com.atlassian.confluence.plugins.tasklist.transformer.helper.XMLSink;
import com.google.common.collect.Lists;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class RetainingXMLSink
implements XMLSink {
    private final XMLSink delegate;
    private final List<XMLEvent> retainedEvents = Lists.newArrayList();

    public RetainingXMLSink(XMLSink delegate) {
        this.delegate = delegate;
    }

    @Override
    public void add(XMLEvent event) {
        this.retainedEvents.add(event);
    }

    public void flush() throws XMLStreamException {
        for (XMLEvent event : this.retainedEvents) {
            this.delegate.add(event);
        }
        this.retainedEvents.clear();
    }

    public List<XMLEvent> getRetainedEvents() {
        return Lists.newArrayList(this.retainedEvents);
    }

    public void rewriteEvent(int index, XMLEvent event) {
        this.retainedEvents.set(index, event);
    }

    public String toString() {
        return "RetainingXMLSink [retainedEvents=" + this.retainedEvents + ", delegate=" + this.delegate + "]";
    }
}

