/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.editor.embed;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.view.embed.EmbeddedImageTagWriter;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.attachments.ImageDetails;
import com.atlassian.confluence.pages.attachments.ImageDetailsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang3.StringUtils;

public class EditorEmbeddedImageTitleWriter
implements EmbeddedImageTagWriter {
    private final EmbeddedImageTagWriter simpleEmbeddedImageTagWriter;
    private final ImageDetailsManager imageDetailsManager;

    public EditorEmbeddedImageTitleWriter(EmbeddedImageTagWriter simpleEmbeddedImageTagWriter, ImageDetailsManager imageDetailsManager) {
        this.simpleEmbeddedImageTagWriter = simpleEmbeddedImageTagWriter;
        this.imageDetailsManager = imageDetailsManager;
    }

    @Override
    public void writeEmbeddedImageTag(XMLStreamWriter xmlStreamWriter, Writer underlyingWriter, Attachment attachment, String imageSource, String thumbnailSource, EmbeddedImage embeddedImage, ConversionContext conversionContext) throws IOException, XMLStreamException {
        ImageDetails imageDetails;
        Space space;
        this.simpleEmbeddedImageTagWriter.writeEmbeddedImageTag(xmlStreamWriter, underlyingWriter, attachment, imageSource, thumbnailSource, embeddedImage, conversionContext);
        StringBuilder location = new StringBuilder();
        ContentEntityObject content = attachment.getContainer();
        if (content instanceof Spaced && (space = ((Spaced)((Object)content)).getSpace()) != null && StringUtils.isNotBlank((CharSequence)space.getName())) {
            location.append(space.getName()).append(" > ");
        }
        if (content instanceof BlogPost) {
            location.append(((BlogPost)content).getDatePath()).append(" > ");
        }
        if (content != null) {
            location.append(content.getTitle()).append(" > ");
        }
        location.append(attachment.getFileName());
        String locationString = location.toString();
        xmlStreamWriter.writeAttribute("title", this.getTitleAttributeValue(locationString, embeddedImage.getTitle()));
        if (StringUtils.isNotBlank((CharSequence)locationString)) {
            xmlStreamWriter.writeAttribute("data-location", locationString);
        }
        if (StringUtils.isNotBlank((CharSequence)embeddedImage.getTitle())) {
            xmlStreamWriter.writeAttribute("data-element-title", embeddedImage.getTitle());
        }
        if ((imageDetails = this.imageDetailsManager.getImageDetails(attachment)) != null) {
            xmlStreamWriter.writeAttribute("data-image-height", String.valueOf(imageDetails.getHeight()));
            xmlStreamWriter.writeAttribute("data-image-width", String.valueOf(imageDetails.getWidth()));
        }
    }

    private String getTitleAttributeValue(String location, String title) {
        if (StringUtils.isNotBlank((CharSequence)location) && StringUtils.isNotBlank((CharSequence)title)) {
            return location + " (" + title + ")";
        }
        if (StringUtils.isNotBlank((CharSequence)location)) {
            return location;
        }
        if (StringUtils.isNotBlank((CharSequence)title)) {
            return "(" + title + ")";
        }
        return "";
    }
}

