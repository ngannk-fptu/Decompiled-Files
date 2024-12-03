/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.InputStreamSource
 */
package com.atlassian.confluence.importexport.resource;

import com.atlassian.confluence.content.render.image.ImageDimensions;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.impl.pages.thumbnail.ThumbnailManagerInternal;
import com.atlassian.confluence.importexport.resource.AttachmentDownloadResourceManager;
import com.atlassian.confluence.importexport.resource.DownloadResourceNotFoundException;
import com.atlassian.confluence.importexport.resource.DownloadResourcePrefixEnum;
import com.atlassian.confluence.importexport.resource.DownloadResourceReader;
import com.atlassian.confluence.importexport.resource.ThumbnailDownloadResourceReader;
import com.atlassian.confluence.importexport.resource.UnauthorizedDownloadResourceException;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.thumbnail.ThumbnailManager;
import com.atlassian.confluence.pages.thumbnail.ThumbnailRenderException;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.servlet.download.AttachmentUrlParser;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.springframework.core.io.InputStreamSource;

public class ThumbnailDownloadResourceManager
extends AttachmentDownloadResourceManager {
    private ThumbnailManagerInternal thumbnailManager;

    public ThumbnailDownloadResourceManager(PermissionManager permissionManager, AttachmentManager attachmentManager, ConfluenceUserDao confluenceUserDao, AttachmentUrlParser attachmentUrlParser, ThumbnailManagerInternal thumbnailManager, ContextPathHolder contextPathHolder) {
        super(permissionManager, attachmentManager, confluenceUserDao, attachmentUrlParser, contextPathHolder);
        this.thumbnailManager = thumbnailManager;
    }

    @Deprecated
    public ThumbnailDownloadResourceManager(PermissionManager permissionManager, AttachmentManager attachmentManager, ConfluenceUserDao confluenceUserDao, AttachmentUrlParser attachmentUrlParser, ThumbnailManager thumbnailManager, ContextPathHolder contextPathHolder) {
        super(permissionManager, attachmentManager, confluenceUserDao, attachmentUrlParser, contextPathHolder);
        this.setThumbnailManager(thumbnailManager);
    }

    @Override
    public boolean matches(String resourcePath) {
        return resourcePath.startsWith(this.getContextPathHolder().getContextPath() + DownloadResourcePrefixEnum.THUMBNAIL_DOWNLOAD_RESOURCE_PREFIX.getPrefix());
    }

    @Override
    public DownloadResourceReader getResourceReader(String userName, String resourcePath, Map parameters) throws UnauthorizedDownloadResourceException, DownloadResourceNotFoundException {
        ImageDimensions thumbnailImageDimensions = this.buildThumbnailImageDimensions(parameters);
        Attachment attachment = this.getAttachment(userName, resourcePath, parameters, "thumbnails");
        File thumbnailFile = this.thumbnailManager.getThumbnailPath(attachment, thumbnailImageDimensions).asJavaFile();
        try {
            this.thumbnailManager.getThumbnail(attachment, thumbnailImageDimensions);
            return new ThumbnailDownloadResourceReader(attachment, thumbnailFile, new ThumbnailInputStreamSource(attachment, thumbnailImageDimensions));
        }
        catch (ThumbnailRenderException | IllegalArgumentException te) {
            throw new DownloadResourceNotFoundException("Thumbnail could not be generated for: " + attachment, te);
        }
    }

    @Deprecated
    public void setThumbnailManager(ThumbnailManager thumbnailManager) {
        this.thumbnailManager = (ThumbnailManagerInternal)thumbnailManager;
    }

    private ImageDimensions buildThumbnailImageDimensions(Map parameters) {
        if (parameters == null) {
            return ImageDimensions.EMPTY;
        }
        Integer thumbnailHeight = this.getIntParamValue(parameters, "height");
        Integer thumbnailWidth = this.getIntParamValue(parameters, "width");
        return new ImageDimensions(thumbnailWidth, thumbnailHeight);
    }

    private Integer getIntParamValue(Map parameters, String paramKey) {
        String strValue = null;
        Object value = parameters.get(paramKey);
        if (value instanceof String[]) {
            strValue = ((String[])value)[0];
        } else if (value instanceof String) {
            strValue = (String)value;
        }
        return strValue == null ? -1 : Integer.parseInt(strValue);
    }

    private class ThumbnailInputStreamSource
    implements InputStreamSource {
        private Attachment attachment;
        private ImageDimensions imageDimensions;

        public ThumbnailInputStreamSource(Attachment attachment, ImageDimensions imageDimensions) {
            this.attachment = attachment;
            this.imageDimensions = imageDimensions;
        }

        public InputStream getInputStream() throws IOException {
            return ThumbnailDownloadResourceManager.this.thumbnailManager.getThumbnailData(this.attachment, this.imageDimensions);
        }
    }
}

