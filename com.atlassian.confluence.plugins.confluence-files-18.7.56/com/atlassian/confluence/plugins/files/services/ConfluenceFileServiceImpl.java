/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.plugins.conversion.api.ConversionType
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.google.common.base.Function
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.files.services;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.plugins.conversion.api.ConversionType;
import com.atlassian.confluence.plugins.files.api.services.ConfluenceFileService;
import com.atlassian.confluence.plugins.files.entities.ConfluenceFileEntity;
import com.atlassian.confluence.plugins.files.entities.FileContentEntity;
import com.atlassian.confluence.plugins.files.entities.FileVersionSummaryEntity;
import com.atlassian.confluence.plugins.files.manager.ConfluenceFileManager;
import com.atlassian.confluence.plugins.files.manager.FilePermissionHelper;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@ExportAsService(value={ConfluenceFileService.class})
@Component
public class ConfluenceFileServiceImpl
implements ConfluenceFileService {
    private final PermissionManager permissionManager;
    private final ConfluenceFileManager fileManager;
    private final AttachmentManager attachmentManager;
    private final ContentEntityManager contentEntityManager;
    private final FilePermissionHelper filePermissionHelper;
    public final Function<Attachment, ConfluenceFileEntity> attachmentToFileEntity = new Function<Attachment, ConfluenceFileEntity>(){

        public ConfluenceFileEntity apply(@Nonnull Attachment attachment) {
            ConfluenceFileEntity entity = new ConfluenceFileEntity();
            entity.setId(attachment.getId());
            entity.setContainerId(attachment.getContainer() != null ? attachment.getContainer().getId() : 0L);
            entity.setFileName(attachment.getFileName());
            entity.setContentType(attachment.getMediaType());
            entity.setNiceType(attachment.getNiceType());
            entity.setDownloadUrl(attachment.getDownloadPath());
            entity.setVersion(attachment.getVersion());
            entity.setPreviewContents(ConfluenceFileServiceImpl.this.getPreviewMap(attachment));
            ConfluenceFileServiceImpl.this.filePermissionHelper.setupPermission(entity, attachment);
            return entity;
        }
    };

    @Autowired
    public ConfluenceFileServiceImpl(ConfluenceFileManager fileManager, @ComponentImport(value="contentEntityManager") @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, @ComponentImport PermissionManager permissionManager, FilePermissionHelper filePermissionHelper, @ComponentImport AttachmentManager attachmentManager) {
        this.fileManager = fileManager;
        this.contentEntityManager = contentEntityManager;
        this.permissionManager = permissionManager;
        this.attachmentManager = attachmentManager;
        this.filePermissionHelper = filePermissionHelper;
    }

    @Override
    @Nonnull
    public ConfluenceFileEntity getFileById(long attachmentId) {
        return this.getFileById(attachmentId, 0);
    }

    @Override
    @Nonnull
    public ConfluenceFileEntity getFileById(long attachmentId, int attachmentVersion) {
        Attachment attachment = this.getOrThrowWithPermissionChecking(attachmentId, attachmentVersion, Attachment.class);
        return (ConfluenceFileEntity)this.attachmentToFileEntity.apply((Object)attachment);
    }

    @Override
    @Nonnull
    public PageResponse<ConfluenceFileEntity> getFiles(long contentId, @Nonnull PageRequest pageRequest) {
        this.checkExistenceAndPermission(contentId);
        PageResponse<Attachment> response = this.fileManager.getFilesForContent(contentId, pageRequest);
        return PageResponseImpl.from((Iterable)Lists.transform((List)response.getResults(), this.attachmentToFileEntity), (boolean)response.hasMore()).build();
    }

    @Nonnull
    public PageResponseImpl<FileVersionSummaryEntity> getVersionSummaries(long attachmentId, @Nonnull PageRequest pageRequest) {
        this.checkExistenceAndPermission(attachmentId);
        PageResponse<FileVersionSummaryEntity> response = this.fileManager.getVersionSummaries(attachmentId, pageRequest);
        return PageResponseImpl.from((Iterable)response.getResults(), (boolean)response.hasMore()).build();
    }

    @Override
    @Nonnull
    public Map<ConversionType, FileContentEntity> getPreviewMap(@Nonnull Attachment attachment) {
        if (!this.hasViewContentPermission((ContentEntityObject)attachment)) {
            throw new PermissionException("User is not permitted to view file: " + attachment.getId());
        }
        return this.fileManager.getPreviewMap(attachment);
    }

    @Override
    @Nonnull
    public PageResponse<ConfluenceFileEntity> getFilesByIds(@Nonnull List<Long> attachmentIds) {
        List attachments = this.attachmentManager.getAttachments(attachmentIds);
        List permittedAttachments = this.permissionManager.getPermittedEntities((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, attachments);
        return PageResponseImpl.from((Iterable)Lists.transform((List)permittedAttachments, this.attachmentToFileEntity), (boolean)false).build();
    }

    @Override
    @Nonnull
    public PageResponse<ConfluenceFileEntity> getFilesMinusAttachmentId(long contentId, @Nonnull List<Long> attachmentIds, @Nonnull PageRequest request) {
        this.checkExistenceAndPermission(contentId);
        PageResponse<Attachment> response = this.fileManager.getFilesMinusAttachmentId(contentId, attachmentIds, request);
        return PageResponseImpl.from((Iterable)Lists.transform((List)response.getResults(), this.attachmentToFileEntity), (boolean)response.hasMore()).build();
    }

    @Override
    public int getUnresolvedCommentCountByAttachmentId(long attachmentId) {
        return this.getUnresolvedCommentCountByAttachmentId(attachmentId, 0);
    }

    @Override
    public int getUnresolvedCommentCountByAttachmentId(long attachmentId, int attachmentVersion) {
        this.checkExistenceAndPermission(attachmentId, attachmentVersion);
        return this.fileManager.getUnresolvedCommentCountByAttachmentId(attachmentId, attachmentVersion);
    }

    private void checkExistenceAndPermission(long contentId) {
        this.checkExistenceAndPermission(contentId, 0);
    }

    private ContentEntityObject checkExistenceAndPermission(long contentId, int contentVersion) {
        ContentEntityObject ceo = this.contentEntityManager.getById(contentId);
        if (ceo != null && contentVersion > 0) {
            ceo = this.contentEntityManager.getOtherVersion(ceo, contentVersion);
        }
        if (ceo == null) {
            throw new NotFoundException("No content was found with id: " + contentId);
        }
        if (!this.hasViewContentPermission(ceo)) {
            throw new PermissionException("User is not permitted to view content: " + contentId);
        }
        return ceo;
    }

    private <T extends ContentEntityObject> T getOrThrowWithPermissionChecking(long contentId, int contentVersion, Class<T> clazz) {
        try {
            return (T)((ContentEntityObject)clazz.cast(this.checkExistenceAndPermission(contentId, contentVersion)));
        }
        catch (ClassCastException ignored) {
            throw new IllegalArgumentException("The specified ID doesn't point to the right object");
        }
    }

    private boolean hasViewContentPermission(@Nonnull ContentEntityObject content) {
        return this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)content);
    }
}

