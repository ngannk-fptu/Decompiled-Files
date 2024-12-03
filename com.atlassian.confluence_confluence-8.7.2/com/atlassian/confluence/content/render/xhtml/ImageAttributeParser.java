/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.model.resource.DefaultEmbeddedImage;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import org.apache.commons.lang3.StringUtils;

public abstract class ImageAttributeParser {
    protected DefaultEmbeddedImage embeddedImage;

    public ImageAttributeParser(DefaultEmbeddedImage embededImage) {
        this.embeddedImage = embededImage;
    }

    public void readImageAttributes(StartElement imageElement) {
        this.readImageAttributes(null, null, imageElement);
    }

    public void readImageAttributes(String namespace, String prefix, StartElement imageElement) {
        Attribute attr = imageElement.getAttributeByName(this.getQName(namespace, prefix, "alt"));
        if (attr != null && StringUtils.isNotBlank((CharSequence)attr.getValue())) {
            this.embeddedImage.setAlternativeText(attr.getValue());
        }
        if ((attr = imageElement.getAttributeByName(this.getQName(namespace, prefix, "height"))) != null && StringUtils.isNotBlank((CharSequence)attr.getValue())) {
            this.embeddedImage.setHeight(attr.getValue());
        }
        if ((attr = imageElement.getAttributeByName(this.getQName(namespace, prefix, "width"))) != null && StringUtils.isNotBlank((CharSequence)attr.getValue())) {
            this.embeddedImage.setWidth(attr.getValue());
        }
        if ((attr = imageElement.getAttributeByName(this.getQName(namespace, prefix, "style"))) != null && StringUtils.isNotBlank((CharSequence)attr.getValue())) {
            this.embeddedImage.setStyle(attr.getValue());
        }
        if ((attr = imageElement.getAttributeByName(this.getQName(namespace, prefix, "title"))) != null && StringUtils.isNotBlank((CharSequence)attr.getValue())) {
            this.embeddedImage.setTitle(attr.getValue());
        }
        if ((attr = imageElement.getAttributeByName(this.getQName(namespace, prefix, "hspace"))) != null && StringUtils.isNotBlank((CharSequence)attr.getValue())) {
            this.embeddedImage.setHspace(attr.getValue());
        }
        if ((attr = imageElement.getAttributeByName(this.getQName(namespace, prefix, "vspace"))) != null && StringUtils.isNotBlank((CharSequence)attr.getValue())) {
            this.embeddedImage.setVspace(attr.getValue());
        }
    }

    public DefaultEmbeddedImage getEmbededImage() {
        return this.embeddedImage;
    }

    protected abstract QName getQName(String var1, String var2, String var3);
}

