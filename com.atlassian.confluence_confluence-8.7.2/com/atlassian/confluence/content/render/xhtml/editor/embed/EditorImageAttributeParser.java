/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.editor.embed;

import com.atlassian.confluence.content.render.xhtml.ImageAttributeParser;
import com.atlassian.confluence.content.render.xhtml.model.resource.DefaultEmbeddedImage;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import java.util.ArrayList;
import java.util.Arrays;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EditorImageAttributeParser
extends ImageAttributeParser {
    private static final Logger logger = LoggerFactory.getLogger(EditorImageAttributeParser.class);

    public EditorImageAttributeParser(DefaultEmbeddedImage embededImage) {
        super(embededImage);
    }

    @Override
    public void readImageAttributes(StartElement imageElement) {
        Attribute titleAttr;
        Attribute queryParams;
        super.readImageAttributes(imageElement);
        Attribute attr = imageElement.getAttributeByName(new QName("class"));
        String cssClass = attr != null ? attr.getValue() : "";
        Object[] cssClasses = cssClass.split(" ");
        ArrayList<String> preservedClasses = new ArrayList<String>();
        for (String string : cssClasses) {
            if ("confluence-content-image-border".equals(string)) {
                this.embeddedImage.setBorder(true);
                continue;
            }
            if ("confluence-thumbnail".equals(string)) {
                this.embeddedImage.setThumbnail(true);
                continue;
            }
            if (string.startsWith("image-") && string.length() > "image-".length()) {
                this.embeddedImage.setAlignment(string.substring("image-".length()));
                continue;
            }
            if ("confluence-embedded-image".equals(string) || "confluence-external-resource".equals(string) || !StringUtils.isNotBlank((CharSequence)string)) continue;
            preservedClasses.add(string);
        }
        if (!preservedClasses.isEmpty()) {
            this.embeddedImage.setHtmlClass(StringUtils.join(preservedClasses, (String)" "));
        }
        if ((queryParams = imageElement.getAttributeByName(new QName("confluence-query-params"))) != null) {
            this.embeddedImage.setExtraQueryParameters(queryParams.getValue());
        }
        if ((titleAttr = imageElement.getAttributeByName(new QName("data-element-title"))) != null && StringUtils.isNotBlank((CharSequence)titleAttr.getValue())) {
            this.embeddedImage.setTitle(titleAttr.getValue());
        } else if (this.embeddedImage.getResourceIdentifier() instanceof AttachmentResourceIdentifier) {
            this.embeddedImage.setTitle(null);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Image with title: {}", (Object)(titleAttr == null ? "unknown" : titleAttr.getValue()));
            logger.debug("Image query params: {}", (Object)(queryParams == null ? "unknown" : queryParams.getValue()));
            logger.debug("Image attribute: {}", (Object)Arrays.toString(cssClasses));
        }
    }

    @Override
    protected QName getQName(String nameSpace, String prefix, String localName) {
        return new QName(localName);
    }
}

