/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.util;

import javax.xml.stream.XMLStreamWriter;

public interface XMLStreamWriterFilter
extends XMLStreamWriter {
    public void setDelegate(XMLStreamWriter var1);

    public XMLStreamWriter getDelegate();
}

