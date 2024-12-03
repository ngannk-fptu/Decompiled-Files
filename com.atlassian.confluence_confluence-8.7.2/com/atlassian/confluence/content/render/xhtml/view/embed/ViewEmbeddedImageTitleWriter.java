/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.view.embed;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.view.embed.EmbeddedImageTagWriter;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang3.StringUtils;

public class ViewEmbeddedImageTitleWriter
implements EmbeddedImageTagWriter {
    private final EmbeddedImageTagWriter simpleEmbeddedImageTagWriter;

    public ViewEmbeddedImageTitleWriter(EmbeddedImageTagWriter simpleEmbeddedImageTagWriter) {
        this.simpleEmbeddedImageTagWriter = simpleEmbeddedImageTagWriter;
    }

    @Override
    public void writeEmbeddedImageTag(XMLStreamWriter xmlStreamWriter, Writer underlyingWriter, Attachment attachment, String imageSource, String thumbnailSource, EmbeddedImage embeddedImage, ConversionContext conversionContext) throws IOException, XMLStreamException {
        this.simpleEmbeddedImageTagWriter.writeEmbeddedImageTag(xmlStreamWriter, underlyingWriter, attachment, imageSource, thumbnailSource, embeddedImage, conversionContext);
        if (StringUtils.isBlank((CharSequence)embeddedImage.getAlternativeText())) {
            xmlStreamWriter.writeAttribute("alt", "");
        }
        if (StringUtils.isNotBlank((CharSequence)embeddedImage.getTitle())) {
            xmlStreamWriter.writeAttribute("aria-label", embeddedImage.getTitle());
        }
    }
}

