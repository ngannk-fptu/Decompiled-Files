/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public interface OMSerializer {
    public void serialize(XMLStreamReader var1, XMLStreamWriter var2) throws XMLStreamException;
}

