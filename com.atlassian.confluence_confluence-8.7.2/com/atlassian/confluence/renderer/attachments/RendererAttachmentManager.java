/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.attachments.RendererAttachment
 *  com.atlassian.renderer.attachments.RendererAttachmentManager
 *  com.atlassian.renderer.embedded.EmbeddedImage
 *  com.atlassian.renderer.embedded.EmbeddedResource
 *  com.atlassian.renderer.util.UrlUtil
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.renderer.attachments;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.thumbnail.CannotGenerateThumbnailException;
import com.atlassian.confluence.pages.thumbnail.ThumbnailInfo;
import com.atlassian.confluence.pages.thumbnail.ThumbnailManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.embedded.EmbeddedResourceUtils;
import com.atlassian.confluence.renderer.embedded.ImagePathHelper;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.ConfluenceRenderUtils;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.attachments.RendererAttachment;
import com.atlassian.renderer.embedded.EmbeddedImage;
import com.atlassian.renderer.embedded.EmbeddedResource;
import com.atlassian.renderer.util.UrlUtil;
import com.atlassian.user.User;
import java.sql.Timestamp;

public class RendererAttachmentManager
implements com.atlassian.renderer.attachments.RendererAttachmentManager,
ImagePathHelper {
    private PermissionManager permissionManager;
    private ThumbnailManager thumbnailManager;

    public RendererAttachment getAttachment(RenderContext renderContext, EmbeddedResource embeddedResource) {
        Attachment confAttachment = EmbeddedResourceUtils.resolveAttachment((PageContext)renderContext, embeddedResource);
        if (confAttachment == null || !this.isPermittedToViewAttachment(confAttachment)) {
            return null;
        }
        return this.convertToRendererAttachment(confAttachment, renderContext, embeddedResource);
    }

    public RendererAttachment getThumbnail(RendererAttachment rendererAttachment, RenderContext renderContext, EmbeddedImage embeddedImage) {
        ThumbnailInfo thumbnailInfo;
        String fileName = UrlUtil.escapeSpecialCharacters((String)embeddedImage.getFilename());
        Attachment attachment = EmbeddedResourceUtils.resolveAttachment((PageContext)renderContext, (EmbeddedResource)embeddedImage);
        if (attachment == null) {
            if (!renderContext.isRenderingForWysiwyg()) {
                throw new RuntimeException("Attachment " + fileName + " was not found");
            }
            return null;
        }
        try {
            thumbnailInfo = this.thumbnailManager.getThumbnailInfo(attachment);
        }
        catch (CannotGenerateThumbnailException e) {
            thumbnailInfo = null;
        }
        if (thumbnailInfo == null || !this.thumbnailManager.isThumbnailable(thumbnailInfo)) {
            throw new RuntimeException("Could not generate thumbnail: Image file format not supported");
        }
        return this.convertToThumbnailRendererAttachment(attachment, thumbnailInfo, renderContext);
    }

    private RendererAttachment convertToThumbnailRendererAttachment(Attachment attachment, ThumbnailInfo thumbnailInfo, RenderContext renderContext) {
        long id = attachment.getId();
        String filename = attachment.getFileName();
        String contentType = attachment.getMediaType();
        String creatorName = attachment.getCreatorName();
        String comment = attachment.getVersionComment();
        Timestamp creationTime = new Timestamp(attachment.getCreationDate().getTime());
        String urlPath = thumbnailInfo.getThumbnailUrlPath();
        String popupPrefix = thumbnailInfo.getPopupLinkPrefix();
        String popupSuffix = thumbnailInfo.getPopupLinkSuffix();
        if (renderContext.isRenderingForWysiwyg()) {
            popupPrefix = null;
            popupSuffix = null;
        }
        return new RendererAttachment(id, filename, contentType, creatorName, comment, urlPath, popupPrefix, popupSuffix, creationTime);
    }

    public boolean systemSupportsThumbnailing() {
        return ThumbnailInfo.systemSupportsThumbnailing();
    }

    private RendererAttachment convertToRendererAttachment(Attachment attachment, RenderContext context, EmbeddedResource resource) {
        return new RendererAttachment(attachment.getId(), attachment.getFileName(), attachment.getMediaType(), attachment.getCreatorName(), attachment.getVersionComment(), this.getRendererAttachmentUrl(attachment, context, resource), null, null, new Timestamp(attachment.getCreationDate().getTime()));
    }

    private String getRendererAttachmentUrl(Attachment attachment, RenderContext context, EmbeddedResource resource) {
        if (context.getAttachmentsPath() != null) {
            return context.getAttachmentsPath() + "/" + resource.getFilename();
        }
        if (context.getOutputType().equals("html_export")) {
            return this.getImagePath(attachment, false) + "/" + resource.getFilename();
        }
        return context.getSiteRoot() + attachment.getDownloadPath();
    }

    @Override
    public String getImagePath(Attachment attachment, boolean isThumbnail) {
        return ConfluenceRenderUtils.getAttachmentRemotePath(attachment);
    }

    protected boolean isPermittedToViewAttachment(Attachment attachment) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        return this.permissionManager.hasPermission((User)user, Permission.VIEW, attachment);
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void setThumbnailManager(ThumbnailManager thumbnailManager) {
        this.thumbnailManager = thumbnailManager;
    }
}

