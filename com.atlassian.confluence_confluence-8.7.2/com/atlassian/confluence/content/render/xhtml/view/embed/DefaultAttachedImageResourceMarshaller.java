/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.util.FileTypeUtil
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.view.embed;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType;
import com.atlassian.confluence.content.render.xhtml.StaxStreamMarshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.links.HrefEvaluator;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifierOnlyUriResolver;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.CannotResolveResourceIdentifierException;
import com.atlassian.confluence.content.render.xhtml.view.embed.AttachedImageMarshaller;
import com.atlassian.confluence.content.render.xhtml.view.embed.AttachedImageRenderHelper;
import com.atlassian.confluence.content.render.xhtml.view.embed.EmbeddedImageTagWriter;
import com.atlassian.confluence.content.render.xhtml.view.embed.UnidentifiedAttachmentMarshaller;
import com.atlassian.confluence.content.render.xhtml.view.embed.ViewImageAttributeWriter;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.attachments.ImageDetails;
import com.atlassian.confluence.pages.attachments.ImageDetailsManager;
import com.atlassian.confluence.pages.thumbnail.CannotGenerateThumbnailException;
import com.atlassian.confluence.pages.thumbnail.ThumbnailInfo;
import com.atlassian.confluence.pages.thumbnail.ThumbnailManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import com.atlassian.renderer.util.FileTypeUtil;
import com.atlassian.user.User;
import java.net.URI;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultAttachedImageResourceMarshaller
implements AttachedImageMarshaller {
    private static final Logger logger = LoggerFactory.getLogger(DefaultAttachedImageResourceMarshaller.class);
    private static final String GIF_MIME_TYPE = "image/gif";
    private final AttachmentResourceIdentifierResolver attachmentResourceIdentifierResolver;
    private final StaxStreamMarshaller<Attachment> attachmentStaxStreamMarshaller;
    private final HrefEvaluator hrefEvaluator;
    private final PermissionManager permissionManager;
    private final ThumbnailManager thumbnailManager;
    private final EmbeddedImageTagWriter embeddedImageWriter;
    private final UnidentifiedAttachmentMarshaller unidentifiedAttachmentPlaceholderWriter;
    private final AttachmentResourceIdentifierOnlyUriResolver attachmentUriResolver;
    private final ImageDetailsManager imageDetailsManager;
    private final DarkFeaturesManager darkFeaturesManager;
    private final AttachedImageRenderHelper attachedImageRenderHelper;

    public DefaultAttachedImageResourceMarshaller(AttachmentResourceIdentifierResolver attachmentResourceIdentifierResolver, PermissionManager permissionManager, ThumbnailManager thumbnailManager, HrefEvaluator hrefEvaluator, StaxStreamMarshaller<Attachment> attachmentStaxStreamMarshaller, EmbeddedImageTagWriter embeddedImageWriter, UnidentifiedAttachmentMarshaller unidentifiedAttachmentPlaceholderWriter, AttachmentResourceIdentifierOnlyUriResolver attachmentResourceIdentifierOnlyUriResolver, ImageDetailsManager imageDetailsManager, DarkFeaturesManager darkFeaturesManager, AttachedImageRenderHelper attachedImageRenderHelper) {
        this.attachmentResourceIdentifierResolver = attachmentResourceIdentifierResolver;
        this.attachmentStaxStreamMarshaller = attachmentStaxStreamMarshaller;
        this.hrefEvaluator = hrefEvaluator;
        this.permissionManager = permissionManager;
        this.thumbnailManager = thumbnailManager;
        this.embeddedImageWriter = embeddedImageWriter;
        this.unidentifiedAttachmentPlaceholderWriter = unidentifiedAttachmentPlaceholderWriter;
        this.attachmentUriResolver = attachmentResourceIdentifierOnlyUriResolver;
        this.imageDetailsManager = imageDetailsManager;
        this.darkFeaturesManager = darkFeaturesManager;
        this.attachedImageRenderHelper = attachedImageRenderHelper;
    }

    @Override
    public Streamable marshal(XmlStreamWriterTemplate xmlStreamWriterTemplate, EmbeddedImage embeddedImage, AttachmentResourceIdentifier attachmentResourceIdentifier, ConversionContext conversionContext) throws XhtmlException {
        Attachment attachment;
        if ("email".equals(conversionContext.getOutputType())) {
            try {
                URI attachmentURIForEmail = this.attachmentUriResolver.resolve(attachmentResourceIdentifier, conversionContext);
                return Streamables.from(xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> {
                    xmlStreamWriter.writeStartElement("img");
                    new ViewImageAttributeWriter(xmlStreamWriter).writeAttributes(embeddedImage);
                    xmlStreamWriter.writeAttribute("src", attachmentURIForEmail.toString());
                });
            }
            catch (CannotResolveResourceIdentifierException e) {
                return this.unidentifiedAttachmentPlaceholderWriter.marshalPlaceholder(xmlStreamWriterTemplate, embeddedImage, attachmentResourceIdentifier, conversionContext);
            }
        }
        try {
            attachment = this.attachmentResourceIdentifierResolver.resolve(attachmentResourceIdentifier, conversionContext);
            logger.debug("Getting attachment [{}] from resource identifier [{}]", (Object)attachment, (Object)attachmentResourceIdentifier);
        }
        catch (CannotResolveResourceIdentifierException e) {
            return this.unidentifiedAttachmentPlaceholderWriter.marshalPlaceholder(xmlStreamWriterTemplate, embeddedImage, attachmentResourceIdentifier, conversionContext);
        }
        if (attachment == null || !this.canSeeAttachment(attachment, conversionContext)) {
            return this.unidentifiedAttachmentPlaceholderWriter.marshalPlaceholder(xmlStreamWriterTemplate, embeddedImage, attachmentResourceIdentifier, conversionContext);
        }
        this.attachedImageRenderHelper.getUnresolvedCommentCountAggregatorFrom(conversionContext).addAttachedImageId(attachment.getId());
        String attachmentSource = this.getAttachmentSource(conversionContext, attachment);
        String fullSizedSource = this.getImageSource(attachmentSource, embeddedImage, conversionContext);
        String thumbnailSource = this.getThumbnailSource(attachmentSource, embeddedImage, conversionContext, attachment);
        return Streamables.from(xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> {
            if (!this.isImage(attachment)) {
                xmlStreamWriter.writeStartElement("a");
                xmlStreamWriter.writeAttribute("href", fullSizedSource);
                if (this.attachmentStaxStreamMarshaller != null) {
                    this.attachmentStaxStreamMarshaller.marshal(attachment, xmlStreamWriter, conversionContext);
                }
                xmlStreamWriter.writeCharacters(attachment.getFileName());
                xmlStreamWriter.writeEndElement();
                return;
            }
            this.embeddedImageWriter.writeEmbeddedImageTag(xmlStreamWriter, underlyingWriter, attachment, attachmentSource, thumbnailSource == null ? fullSizedSource : thumbnailSource, embeddedImage, conversionContext);
        });
    }

    private boolean isImage(Attachment attachment) {
        return this.isImageMimeType(attachment.getMediaType()) || this.isImageMimeType(FileTypeUtil.getContentType((String)attachment.getFileName()));
    }

    private boolean isImageMimeType(String contentType) {
        return contentType != null && contentType.startsWith("image");
    }

    private String getAttachmentSource(ConversionContext conversionContext, Attachment attachment) throws XhtmlException {
        return this.hrefEvaluator.createHref(conversionContext, attachment, null);
    }

    private String getImageSource(String attachmentSource, EmbeddedImage embeddedImage, ConversionContext conversionContext) throws XhtmlException {
        return this.appendExtraQueryParams(attachmentSource, embeddedImage, conversionContext);
    }

    private String getThumbnailSource(String attachmentSource, EmbeddedImage embeddedImage, ConversionContext conversionContext, Attachment attachment) throws XhtmlException {
        String imageSource = null;
        ImageDetails imageDetails = this.imageDetailsManager.getImageDetails(attachment);
        boolean isThumbnail = embeddedImage.isThumbnail();
        boolean thumbnailDarkFeatureEnabled = this.darkFeaturesManager.getDarkFeatures().isFeatureEnabled("thumbnail-image");
        if (thumbnailDarkFeatureEnabled && (GIF_MIME_TYPE.equals(imageDetails.getMimeType()) || !isThumbnail && embeddedImage.getWidth() == null && embeddedImage.getHeight() == null)) {
            return null;
        }
        if ((thumbnailDarkFeatureEnabled || isThumbnail) && !ConversionContextOutputType.WORD.value().equals(conversionContext.getOutputType()) && this.useThumbnail(embeddedImage, imageDetails)) {
            imageSource = this.appendExtraQueryParams(this.makeThumbnailImageSource(attachmentSource, attachment), embeddedImage, conversionContext);
        }
        return imageSource;
    }

    String makeThumbnailImageSource(String attachmentSource, Attachment attachment) throws XhtmlException {
        try {
            this.thumbnailManager.getThumbnailInfo(attachment);
        }
        catch (CannotGenerateThumbnailException e) {
            Attachment exAttachment = e.getAttachment();
            ContentEntityObject container = exAttachment.getContainer();
            if (container == null) {
                throw new XhtmlException("Cannot generate thumbnail for the file '" + exAttachment.getFileName() + "' without a container.", e);
            }
            throw new XhtmlException("Cannot generate thumbnail for the file '" + exAttachment.getFileName() + "' attached to the content '" + container.getTitle() + "'.", e);
        }
        return ThumbnailInfo.createThumbnailUrlPathFromAttachmentUrl(attachmentSource);
    }

    private boolean canSeeAttachment(Attachment attachment, ConversionContext conversionContext) {
        if (this.isLocalAttachment(attachment, conversionContext)) {
            return true;
        }
        boolean isConversionForDiffs = ConversionContextOutputType.DIFF.value().equals(conversionContext.getOutputType());
        if (isConversionForDiffs) {
            return false;
        }
        return this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, attachment);
    }

    private boolean isLocalAttachment(Attachment attachment, ConversionContext conversionContext) {
        ContentEntityObject container = attachment.getContainer();
        ContentEntityObject entity = conversionContext.getEntity();
        if (container == null) {
            return true;
        }
        if (entity == null) {
            return false;
        }
        if (container.equals(entity.getLatestVersion())) {
            return true;
        }
        if (entity instanceof Draft) {
            Draft draft = (Draft)entity;
            return StringUtils.equals((CharSequence)draft.getPageId(), (CharSequence)container.getIdAsString());
        }
        return false;
    }

    private String appendExtraQueryParams(String thumbnailSource, EmbeddedImage embeddedImage, ConversionContext conversionContext) {
        if (ConversionContextOutputType.WORD.value().equals(conversionContext.getOutputType())) {
            return thumbnailSource;
        }
        String queryParams = embeddedImage.getExtraQueryParameters();
        if (StringUtils.isNotBlank((CharSequence)queryParams)) {
            thumbnailSource = this.appendExtraQueryParam(thumbnailSource, queryParams);
        }
        if (this.darkFeaturesManager.getDarkFeatures().isFeatureEnabled("thumbnail-image")) {
            if (embeddedImage.getWidth() != null) {
                thumbnailSource = this.appendExtraQueryParam(thumbnailSource, "width=" + embeddedImage.getWidth());
            } else if (embeddedImage.getHeight() != null) {
                thumbnailSource = this.appendExtraQueryParam(thumbnailSource, "height=" + embeddedImage.getHeight());
            }
        }
        return thumbnailSource;
    }

    private String appendExtraQueryParam(String thumbnailSource, String queryParam) {
        return thumbnailSource + (thumbnailSource.indexOf(63) != -1 ? (char)'&' : '?') + queryParam;
    }

    private boolean useThumbnail(EmbeddedImage embeddedImage, ImageDetails imageDetails) {
        if (this.darkFeaturesManager.getDarkFeatures().isFeatureEnabled("thumbnail-image")) {
            if (embeddedImage.getWidth() == null && embeddedImage.getHeight() == null) {
                return true;
            }
            return embeddedImage.getWidth() != null && Integer.parseInt(embeddedImage.getWidth()) < imageDetails.getWidth() || embeddedImage.getHeight() != null && Integer.parseInt(embeddedImage.getHeight()) < imageDetails.getHeight();
        }
        return true;
    }
}

