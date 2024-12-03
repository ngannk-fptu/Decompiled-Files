/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public interface XmlStreamWriterCallback {
    public void withStreamWriter(XMLStreamWriter var1, Writer var2) throws XMLStreamException, IOException;
}

