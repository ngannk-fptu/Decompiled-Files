/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterCallback;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;

public interface XmlStreamWriterTemplate {
    public void execute(Writer var1, XmlStreamWriterCallback var2) throws XMLStreamException, IOException;
}

