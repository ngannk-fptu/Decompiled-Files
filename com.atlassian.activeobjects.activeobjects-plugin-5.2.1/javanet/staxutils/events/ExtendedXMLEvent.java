/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.events;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;

public interface ExtendedXMLEvent
extends XMLEvent {
    public boolean matches(XMLEvent var1);

    public void writeEvent(XMLStreamWriter var1) throws XMLStreamException;
}

