/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.OMInformationItem;

public interface OMSerializable
extends OMInformationItem {
    public boolean isComplete();

    public void build();

    public void close(boolean var1);

    public void serialize(XMLStreamWriter var1) throws XMLStreamException;

    public void serializeAndConsume(XMLStreamWriter var1) throws XMLStreamException;

    public void serialize(XMLStreamWriter var1, boolean var2) throws XMLStreamException;
}

