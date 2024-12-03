/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.evt;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.codehaus.stax2.XMLStreamWriter2;

public interface XMLEvent2
extends XMLEvent {
    public void writeUsing(XMLStreamWriter2 var1) throws XMLStreamException;
}

