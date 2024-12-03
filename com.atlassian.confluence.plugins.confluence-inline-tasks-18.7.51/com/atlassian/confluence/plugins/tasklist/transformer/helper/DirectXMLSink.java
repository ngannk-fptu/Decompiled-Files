/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.tasklist.transformer.helper;

import com.atlassian.confluence.plugins.tasklist.transformer.helper.XMLSink;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class DirectXMLSink
implements XMLSink {
    private final XMLEventWriter delegate;

    public DirectXMLSink(XMLEventWriter delegate) {
        this.delegate = delegate;
    }

    @Override
    public void add(XMLEvent event) throws XMLStreamException {
        this.delegate.add(event);
    }
}

