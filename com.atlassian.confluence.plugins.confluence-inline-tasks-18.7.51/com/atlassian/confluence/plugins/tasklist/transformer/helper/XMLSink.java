/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.tasklist.transformer.helper;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public interface XMLSink {
    public void add(XMLEvent var1) throws XMLStreamException;
}

