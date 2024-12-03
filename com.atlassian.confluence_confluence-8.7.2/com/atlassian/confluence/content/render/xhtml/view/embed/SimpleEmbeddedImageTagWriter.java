/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.view.embed;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxStreamMarshaller;
import com.atlassian.confluence.content.render.xhtml.view.embed.AttachedImageRenderHelper;
import com.atlassian.confluence.content.render.xhtml.view.embed.AttachedImageUnresolvedCommentCountAggregator;
import com.atlassian.confluence.content.render.xhtml.view.embed.EmbeddedImageTagWriter;
import com.atlassian.confluence.content.render.xhtml.view.embed.ViewImageAttributeWriter;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.attachments.ImageDetails;
import com.atlassian.confluence.pages.attachments.ImageDetailsManager;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang3.StringUtils;

public class SimpleEmbeddedImageTagWriter
implements EmbeddedImageTagWriter {
    private static final String MOBILE_VIEW_RENDERER = "mobile-view-renderer";
    private static final String IMAGE_PLACEHOLDER_PATH = "/images/icons/attachments/image_placeholder.png";
    private final StaxStreamMarshaller<Attachment> attachmentStaxStreamMarshaller;
    private final AttachedImageRenderHelper attachedImageRenderHelper;
    private final ImageDetailsManager imageDetailsManager;
    private final WebResourceUrlProvider webResourceUrlProvider;

    public SimpleEmbeddedImageTagWriter(StaxStreamMarshaller<Attachment> attachmentStaxStreamMarshaller, AttachedImageRenderHelper attachedImageRenderHelper) {
        this(attachmentStaxStreamMarshaller, attachedImageRenderHelper, null, null);
    }

    public SimpleEmbeddedImageTagWriter(StaxStreamMarshaller<Attachment> attachmentStaxStreamMarshaller, AttachedImageRenderHelper attachedImageRenderHelper, ImageDetailsManager imageDetailsManager, WebResourceUrlProvider webResourceUrlProvider) {
        this.attachmentStaxStreamMarshaller = attachmentStaxStreamMarshaller;
        this.attachedImageRenderHelper = attachedImageRenderHelper;
        this.imageDetailsManager = imageDetailsManager;
        this.webResourceUrlProvider = webResourceUrlProvider;
    }

    @Override
    public void writeEmbeddedImageTag(XMLStreamWriter xmlStreamWriter, Writer underlyingWriter, Attachment attachment, String imageSource, String thumbnailSource, EmbeddedImage embeddedImage, ConversionContext conversionContext) throws IOException, XMLStreamException {
        xmlStreamWriter.writeStartElement("img");
        new ViewImageAttributeWriter(xmlStreamWriter).writeAttributes(embeddedImage);
        String queryParams = embeddedImage.getExtraQueryParameters();
        if (StringUtils.isNotBlank((CharSequence)queryParams)) {
            xmlStreamWriter.writeAttribute("confluence-query-params", queryParams);
        }
        if (this.isMobileViewRenderer(conversionContext)) {
            xmlStreamWriter.writeAttribute("src", this.webResourceUrlProvider.getStaticResourcePrefix(UrlMode.RELATIVE) + IMAGE_PLACEHOLDER_PATH);
            xmlStreamWriter.writeAttribute("data-thumbnail-image-src", thumbnailSource);
            xmlStreamWriter.writeAttribute("data-image-src", imageSource);
            ImageDetails imageDetails = this.imageDetailsManager.getImageDetails(attachment);
            if (imageDetails != null) {
                xmlStreamWriter.writeAttribute("data-image-width", String.valueOf(imageDetails.getWidth()));
                xmlStreamWriter.writeAttribute("data-image-height", String.valueOf(imageDetails.getHeight()));
            }
        } else {
            xmlStreamWriter.writeAttribute("src", thumbnailSource);
            xmlStreamWriter.writeAttribute("data-image-src", imageSource);
            AttachedImageUnresolvedCommentCountAggregator aggregator = this.attachedImageRenderHelper.getUnresolvedCommentCountAggregatorFrom(conversionContext);
            xmlStreamWriter.writeAttribute("data-unresolved-comment-count", String.valueOf(aggregator.getUnresolvedCommentCount(attachment.getId())));
            if (this.attachmentStaxStreamMarshaller != null) {
                this.attachmentStaxStreamMarshaller.marshal(attachment, xmlStreamWriter, conversionContext);
            }
        }
    }

    private boolean isMobileViewRenderer(ConversionContext conversionContext) {
        Object isMobileViewRendererObject = conversionContext.getProperty(MOBILE_VIEW_RENDERER);
        return isMobileViewRendererObject != null && Boolean.valueOf(isMobileViewRendererObject.toString()) != false;
    }
}

