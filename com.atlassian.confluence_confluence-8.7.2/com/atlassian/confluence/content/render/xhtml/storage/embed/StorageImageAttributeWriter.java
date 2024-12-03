/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.embed;

import com.atlassian.confluence.content.render.xhtml.ImageAttributeWriter;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class StorageImageAttributeWriter
extends ImageAttributeWriter {
    public static final String NAMESPACE = "ac";

    public StorageImageAttributeWriter(XMLStreamWriter writer) {
        super(writer);
    }

    @Override
    public void writeAttributes(EmbeddedImage embeddedImage) throws XMLStreamException {
        this.writeAttribute(NAMESPACE, "http://atlassian.com/content", "align", embeddedImage.getAlignment());
        if (embeddedImage.isBorder()) {
            this.writeAttribute(NAMESPACE, "http://atlassian.com/content", "border", "true");
        }
        this.writeAttribute(NAMESPACE, "http://atlassian.com/content", "class", embeddedImage.getHtmlClass());
        this.writeAttribute(NAMESPACE, "http://atlassian.com/content", "title", embeddedImage.getTitle());
        this.writeAttribute(NAMESPACE, "http://atlassian.com/content", "style", embeddedImage.getStyle());
        this.writeAttribute(NAMESPACE, "http://atlassian.com/content", "queryparams", embeddedImage.getExtraQueryParameters());
        if (embeddedImage.isThumbnail()) {
            this.writeAttribute(NAMESPACE, "http://atlassian.com/content", "thumbnail", "true");
        }
        super.writeAttributes(NAMESPACE, "http://atlassian.com/content", embeddedImage);
    }
}

