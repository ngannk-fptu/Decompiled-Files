/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.stream;

import java.util.Iterator;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public interface XMLEventReader
extends Iterator {
    public XMLEvent nextEvent() throws XMLStreamException;

    public boolean hasNext();

    public XMLEvent peek() throws XMLStreamException;

    public String getElementText() throws XMLStreamException;

    public XMLEvent nextTag() throws XMLStreamException;

    public Object getProperty(String var1) throws IllegalArgumentException;

    public void close() throws XMLStreamException;
}

