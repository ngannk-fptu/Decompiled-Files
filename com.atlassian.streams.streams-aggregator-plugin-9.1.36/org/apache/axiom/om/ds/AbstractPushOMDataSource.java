/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.ds;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.ds.AbstractOMDataSource;
import org.apache.axiom.om.util.StAXUtils;

public abstract class AbstractPushOMDataSource
extends AbstractOMDataSource {
    public final boolean isDestructiveRead() {
        return this.isDestructiveWrite();
    }

    public final XMLStreamReader getReader() throws XMLStreamException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        this.serialize(bos, new OMOutputFormat());
        return StAXUtils.createXMLStreamReader(new ByteArrayInputStream(bos.toByteArray()));
    }
}

