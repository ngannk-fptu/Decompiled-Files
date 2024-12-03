/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.thumbnail.ThumbnailInfo
 *  com.atlassian.confluence.pages.thumbnail.ThumbnailManager
 *  com.atlassian.confluence.pages.thumbnail.ThumbnailRenderException
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.util.actions.ContentTypesDisplayMapper
 *  com.atlassian.core.util.thumbnail.Thumbnail
 *  com.atlassian.plugins.rest.common.Link
 *  com.atlassian.plugins.rest.common.expand.entity.ListWrapperCallback
 *  com.atlassian.plugins.rest.common.expand.parameter.Indexes
 *  com.atlassian.user.User
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.rest.manager;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.thumbnail.ThumbnailInfo;
import com.atlassian.confluence.pages.thumbnail.ThumbnailManager;
import com.atlassian.confluence.pages.thumbnail.ThumbnailRenderException;
import com.atlassian.confluence.plugins.rest.entities.AttachmentEntity;
import com.atlassian.confluence.plugins.rest.entities.AttachmentEntityList;
import com.atlassian.confluence.plugins.rest.entities.builders.DefaultContentEntityBuilder;
import com.atlassian.confluence.plugins.rest.manager.DateEntityFactory;
import com.atlassian.confluence.plugins.rest.manager.RequestContext;
import com.atlassian.confluence.plugins.rest.manager.RequestContextThreadLocal;
import com.atlassian.confluence.plugins.rest.manager.RestAttachmentManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.actions.ContentTypesDisplayMapper;
import com.atlassian.core.util.thumbnail.Thumbnail;
import com.atlassian.plugins.rest.common.Link;
import com.atlassian.plugins.rest.common.expand.entity.ListWrapperCallback;
import com.atlassian.plugins.rest.common.expand.parameter.Indexes;
import com.atlassian.user.User;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultRestAttachmentManager
implements RestAttachmentManager {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultRestAttachmentManager.class);
    private final AttachmentManager attachmentManager;
    private final PermissionManager permissionManager;
    private final SettingsManager settingsManager;
    private final ThumbnailManager thumbnailManager;
    private final DateEntityFactory dateEntityFactory;

    public DefaultRestAttachmentManager(AttachmentManager attachmentManager, PermissionManager permissionManager, SettingsManager settingsManager, ThumbnailManager thumbnailManager, DateEntityFactory dateEntityFactory) {
        this.attachmentManager = attachmentManager;
        this.permissionManager = permissionManager;
        this.settingsManager = settingsManager;
        this.thumbnailManager = thumbnailManager;
        this.dateEntityFactory = dateEntityFactory;
    }

    @Override
    public AttachmentEntity convertToAttachmentEntity(Attachment attachment) {
        RequestContext requestContext = RequestContextThreadLocal.get();
        User user = requestContext.getUser();
        if (!this.permissionManager.hasPermission(user, Permission.VIEW, (Object)attachment)) {
            return null;
        }
        AttachmentEntity attachmentEntity = new AttachmentEntity();
        attachmentEntity.setFileName(attachment.getFileName());
        attachmentEntity.setFileSize(attachment.getFileSize());
        attachmentEntity.setNiceFileSize(attachment.getNiceFileSize());
        attachmentEntity.setComment(attachment.getVersionComment());
        attachmentEntity.setContentType(attachment.getMediaType());
        attachmentEntity.setVersion(attachment.getVersion());
        attachmentEntity.addLink(Link.self((URI)RequestContextThreadLocal.get().getUriBuilder("attachment").build(new Object[]{attachment.getId()})));
        attachmentEntity.addLink(Link.link((URI)DefaultRestAttachmentManager.baseUri(attachment.getDownloadPath(), this.settingsManager), (String)"download", (String)attachment.getMediaType()));
        attachmentEntity.setNiceType(attachment.getNiceType());
        attachmentEntity.setIconClass(ContentTypesDisplayMapper.getIconForAttachment((String)attachment.getMediaType(), (String)attachment.getFileName()));
        attachmentEntity.setId(String.valueOf(attachment.getId()));
        ContentEntityObject content = Objects.requireNonNull(attachment.getContainer());
        attachmentEntity.setOwnerId(content.getIdAsString());
        attachmentEntity.setParentTitle(content.getTitle());
        attachmentEntity.setParentContentType(content.getType());
        if (content instanceof BlogPost) {
            attachmentEntity.setParentDatePath(((BlogPost)content).getDatePath());
        }
        attachmentEntity.setLastModifiedDate(this.dateEntityFactory.buildDateEntity(attachment.getLastModificationDate()));
        attachmentEntity.setCreatedDate(this.dateEntityFactory.buildDateEntity(attachment.getCreationDate()));
        try {
            attachmentEntity.setWikiLink(attachment.getLinkWikiMarkup());
        }
        catch (IllegalStateException e) {
            LOG.debug("IllegalStateException caught. Ignoring wiki link field for attachments that are unlinkable.", (Throwable)e);
        }
        attachmentEntity.setSpace(DefaultContentEntityBuilder.createSpaceEntity(attachment.getSpace()));
        this.setThumbnailInfo(attachment, attachmentEntity);
        try {
            attachmentEntity.addLink(Link.link((URI)new URI(this.settingsManager.getGlobalSettings().getBaseUrl() + attachment.getUrlPath()), (String)"alternate", (String)"text/html"));
        }
        catch (URISyntaxException e) {
            LOG.debug("URISyntaxException caught. Not setting the alternate link.", (Throwable)e);
        }
        return attachmentEntity;
    }

    private void setThumbnailInfo(Attachment attachment, AttachmentEntity attachmentEntity) {
        if (this.thumbnailManager.isThumbnailable(attachment)) {
            try {
                Thumbnail thumbnail = this.thumbnailManager.getThumbnail(attachment);
                attachmentEntity.setThumbnailWidth(thumbnail.getWidth());
                attachmentEntity.setThumbnailHeight(thumbnail.getHeight());
                String attachmentPath = attachment.getDownloadPathWithoutVersion();
                Link link = Link.link((URI)DefaultRestAttachmentManager.baseUri(ThumbnailInfo.createThumbnailUrlPathFromAttachmentUrl((String)attachmentPath), this.settingsManager), (String)"thumbnail");
                attachmentEntity.setThumbnailLink(link);
            }
            catch (IllegalArgumentException e) {
                LOG.warn("Thumbnail not set, illegal arguments passed in for {}", (Object)attachment);
                LOG.debug("IllegalArgumentException caught.", (Throwable)e);
            }
            catch (ThumbnailRenderException e) {
                LOG.warn("Thumbnail not set, cannot render thumbnail for {}", (Object)attachment);
                LOG.debug("ThumbnailRenderException caught.", (Throwable)e);
            }
        } else {
            LOG.debug("Attachment {} is not thumbnailable. Media type: {}", (Object)attachment.getDisplayTitle(), (Object)attachment.getMediaType());
        }
    }

    private static URI baseUri(String uri, SettingsManager settingsManager) {
        try {
            return new URI(settingsManager.getGlobalSettings().getBaseUrl() + uri);
        }
        catch (URISyntaxException e) {
            LOG.debug("URISyntaxException caught. Not including invalid link {}", (Object)uri);
            return null;
        }
    }

    @Override
    public AttachmentEntity getAttachmentEntity(Long attachmentId) {
        return this.convertToAttachmentEntity(this.attachmentManager.getAttachmentDao().getById(attachmentId.longValue()));
    }

    @Override
    public AttachmentEntityList createAttachmentEntityListForContent(ContentEntityObject object) {
        List attachments = this.attachmentManager.getLatestVersionsOfAttachments(object);
        return new AttachmentEntityList(attachments.size(), this.createAttachmentWrapperCallback(attachments));
    }

    private ListWrapperCallback<AttachmentEntity> createAttachmentWrapperCallback(final List<Attachment> attachments) {
        return new ListWrapperCallback<AttachmentEntity>(){

            public List<AttachmentEntity> getItems(Indexes indexes) {
                int size = attachments.size();
                if (size == 0) {
                    return Collections.emptyList();
                }
                int startIndex = Math.max(0, indexes.getMinIndex(size));
                int endIndex = Math.max(0, indexes.getMaxIndex(size)) + 1;
                return this.createAttachmentEntityList(attachments.subList(startIndex, endIndex));
            }

            private List<AttachmentEntity> createAttachmentEntityList(List<Attachment> attachments2) {
                ArrayList<AttachmentEntity> result = new ArrayList<AttachmentEntity>();
                for (Attachment attachment : attachments2) {
                    result.add(DefaultRestAttachmentManager.this.convertToAttachmentEntity(attachment));
                }
                return result;
            }
        };
    }
}

