/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.view.link;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.xhtml.api.Link;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public interface CommonLinkAttributesWriter {
    public void writeCommonAttributes(Link var1, XMLStreamWriter var2, ConversionContext var3) throws XMLStreamException;
}

