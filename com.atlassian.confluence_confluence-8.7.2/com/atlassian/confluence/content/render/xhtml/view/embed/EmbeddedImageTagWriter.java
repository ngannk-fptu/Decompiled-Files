/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.view.embed;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public interface EmbeddedImageTagWriter {
    public void writeEmbeddedImageTag(XMLStreamWriter var1, Writer var2, Attachment var3, String var4, String var5, EmbeddedImage var6, ConversionContext var7) throws IOException, XMLStreamException;
}

