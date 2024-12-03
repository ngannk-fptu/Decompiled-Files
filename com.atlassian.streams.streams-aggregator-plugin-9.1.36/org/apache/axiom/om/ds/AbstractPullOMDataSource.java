/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.ds;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.ds.AbstractOMDataSource;
import org.apache.axiom.om.impl.serialize.StreamingOMSerializer;

public abstract class AbstractPullOMDataSource
extends AbstractOMDataSource {
    public final boolean isDestructiveWrite() {
        return this.isDestructiveRead();
    }

    public final void serialize(XMLStreamWriter xmlWriter) throws XMLStreamException {
        StreamingOMSerializer serializer = new StreamingOMSerializer();
        serializer.serialize(this.getReader(), xmlWriter);
    }
}

