/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.tasklist.transformer.helper;

import com.atlassian.confluence.plugins.tasklist.transformer.helper.XMLSink;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class DelegateXMLSink
implements XMLSink {
    private final XMLSink delegate;

    public DelegateXMLSink(XMLSink delegate) {
        this.delegate = delegate;
    }

    @Override
    public void add(XMLEvent event) throws XMLStreamException {
        this.delegate.add(event);
    }
}

