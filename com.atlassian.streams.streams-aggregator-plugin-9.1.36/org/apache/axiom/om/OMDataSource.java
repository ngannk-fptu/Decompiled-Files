/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om;

import java.io.OutputStream;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.OMOutputFormat;

public interface OMDataSource {
    public void serialize(OutputStream var1, OMOutputFormat var2) throws XMLStreamException;

    public void serialize(Writer var1, OMOutputFormat var2) throws XMLStreamException;

    public void serialize(XMLStreamWriter var1) throws XMLStreamException;

    public XMLStreamReader getReader() throws XMLStreamException;
}

