/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.storage.embed;

import com.atlassian.confluence.content.render.xhtml.ImageAttributeParser;
import com.atlassian.confluence.content.render.xhtml.model.resource.DefaultEmbeddedImage;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import org.apache.commons.lang3.StringUtils;

public class StorageImageAttributeParser
extends ImageAttributeParser {
    public StorageImageAttributeParser(DefaultEmbeddedImage embededImage) {
        super(embededImage);
    }

    @Override
    public void readImageAttributes(StartElement imageElement) {
        super.readImageAttributes("http://atlassian.com/content", "ac", imageElement);
        Attribute attr = imageElement.getAttributeByName(new QName("http://atlassian.com/content", "src", "ac"));
        if (attr != null && StringUtils.isNotBlank((CharSequence)attr.getValue())) {
            this.embeddedImage.setSource(attr.getValue());
        }
        if ((attr = imageElement.getAttributeByName(new QName("http://atlassian.com/content", "thumbnail", "ac"))) != null && StringUtils.isNotBlank((CharSequence)attr.getValue())) {
            this.embeddedImage.setThumbnail(Boolean.valueOf(attr.getValue()));
        }
        if ((attr = imageElement.getAttributeByName(new QName("http://atlassian.com/content", "border", "ac"))) != null && StringUtils.isNotBlank((CharSequence)attr.getValue())) {
            this.embeddedImage.setBorder(Boolean.valueOf(attr.getValue()));
        }
        if ((attr = imageElement.getAttributeByName(new QName("http://atlassian.com/content", "align", "ac"))) != null && StringUtils.isNotBlank((CharSequence)attr.getValue())) {
            this.embeddedImage.setAlignment(attr.getValue());
        }
        if ((attr = imageElement.getAttributeByName(new QName("http://atlassian.com/content", "class", "ac"))) != null && StringUtils.isNotBlank((CharSequence)attr.getValue())) {
            this.embeddedImage.setHtmlClass(attr.getValue());
        }
        if ((attr = imageElement.getAttributeByName(new QName("http://atlassian.com/content", "queryparams", "ac"))) != null && StringUtils.isNotBlank((CharSequence)attr.getValue())) {
            this.embeddedImage.setExtraQueryParameters(attr.getValue());
        }
    }

    @Override
    protected QName getQName(String nameSpace, String prefix, String localName) {
        return new QName(nameSpace, localName, prefix);
    }
}

