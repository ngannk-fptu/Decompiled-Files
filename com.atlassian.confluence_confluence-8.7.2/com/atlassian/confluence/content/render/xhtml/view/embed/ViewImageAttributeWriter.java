/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.view.embed;

import com.atlassian.confluence.content.render.xhtml.ImageAttributeWriter;
import com.atlassian.confluence.content.render.xhtml.migration.UrlResourceIdentifier;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang3.StringUtils;

public class ViewImageAttributeWriter
extends ImageAttributeWriter {
    public ViewImageAttributeWriter(XMLStreamWriter writer) {
        super(writer);
    }

    public ViewImageAttributeWriter(XMLStreamWriter writer, boolean ignoreWidthHeight) {
        this(writer);
        this.ignoreWidthHeight = ignoreWidthHeight;
    }

    @Override
    public void writeAttributes(EmbeddedImage embeddedImage) throws XMLStreamException {
        String alignment;
        StringBuilder cssClass = new StringBuilder("confluence-embedded-image");
        if (StringUtils.isNotBlank((CharSequence)embeddedImage.getHtmlClass())) {
            cssClass.append(" ").append(embeddedImage.getHtmlClass());
        }
        if (embeddedImage.isThumbnail()) {
            cssClass.append(" confluence-thumbnail");
        }
        if (embeddedImage.getResourceIdentifier() instanceof UrlResourceIdentifier) {
            cssClass.append(" ").append("confluence-external-resource");
        }
        if (embeddedImage.isBorder()) {
            cssClass.append(" ").append("confluence-content-image-border");
        }
        if (StringUtils.isNotBlank((CharSequence)(alignment = embeddedImage.getAlignment())) && !"none".equals(alignment)) {
            cssClass.append(" ").append("image-").append(alignment);
        }
        this.writeAttribute("class", cssClass.toString());
        this.writeAttribute("draggable", "false");
        super.writeAttributes(embeddedImage);
    }
}

