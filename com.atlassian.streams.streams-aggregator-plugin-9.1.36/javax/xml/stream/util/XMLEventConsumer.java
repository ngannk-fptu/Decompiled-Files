/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.stream.util;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public interface XMLEventConsumer {
    public void add(XMLEvent var1) throws XMLStreamException;
}

