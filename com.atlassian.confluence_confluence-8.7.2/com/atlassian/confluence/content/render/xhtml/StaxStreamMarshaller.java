/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public interface StaxStreamMarshaller<T> {
    public void marshal(T var1, XMLStreamWriter var2, ConversionContext var3) throws XMLStreamException;
}

