/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang3.StringUtils;

public class ImageAttributeWriter {
    private final XMLStreamWriter writer;
    protected boolean ignoreWidthHeight = false;

    public ImageAttributeWriter(XMLStreamWriter writer) {
        this.writer = writer;
    }

    public void writeAttributes(EmbeddedImage embeddedImage) throws XMLStreamException {
        this.writeAttributes(null, null, embeddedImage);
    }

    public void writeAttributes(String prefix, String namespace, EmbeddedImage embeddedImage) throws XMLStreamException {
        this.writeAttribute(prefix, namespace, "alt", embeddedImage.getAlternativeText());
        if (!this.ignoreWidthHeight) {
            this.writeAttribute(prefix, namespace, "height", embeddedImage.getHeight());
            this.writeAttribute(prefix, namespace, "width", embeddedImage.getWidth());
        }
        this.writeAttribute(prefix, namespace, "src", embeddedImage.getSource());
        this.writeAttribute(prefix, namespace, "hspace", embeddedImage.getHspace());
        this.writeAttribute(prefix, namespace, "vspace", embeddedImage.getVspace());
    }

    protected void writeAttribute(String name, String value) throws XMLStreamException {
        if (StringUtils.isNotBlank((CharSequence)value)) {
            this.writer.writeAttribute(name, value);
        }
    }

    public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
        if (StringUtils.isBlank((CharSequence)prefix) || StringUtils.isBlank((CharSequence)namespaceURI)) {
            this.writeAttribute(localName, value);
        } else if (StringUtils.isNotBlank((CharSequence)value)) {
            this.writer.writeAttribute(prefix, namespaceURI, localName, value);
        }
    }
}

