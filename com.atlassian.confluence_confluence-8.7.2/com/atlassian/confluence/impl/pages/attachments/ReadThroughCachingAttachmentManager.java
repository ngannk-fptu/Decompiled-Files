/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.pages.attachments;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.impl.pages.attachments.ReadThroughAttachmentDownloadPathCache;
import com.atlassian.confluence.internal.pages.AttachmentManagerInternal;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentDataExistsException;
import com.atlassian.confluence.pages.AttachmentDataStorageType;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.AttachmentStatisticsDTO;
import com.atlassian.confluence.pages.DelegatingAttachmentManager;
import com.atlassian.confluence.pages.SavableAttachment;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDao;
import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.AttachmentDeleteOptions;
import com.atlassian.confluence.web.rangerequest.RangeRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ReadThroughCachingAttachmentManager
implements AttachmentManagerInternal,
DelegatingAttachmentManager {
    private final AttachmentManagerInternal delegate;
    private final ReadThroughAttachmentDownloadPathCache pathCache;

    public ReadThroughCachingAttachmentManager(AttachmentManagerInternal delegate, ReadThroughAttachmentDownloadPathCache pathCache) {
        this.delegate = delegate;
        this.pathCache = pathCache;
    }

    @Override
    public void deepAttachmentDelete(AttachmentDeleteOptions attachmentDeleteOptions) {
        this.delegate.deepAttachmentDelete(attachmentDeleteOptions);
    }

    @Override
    public Attachment getAttachment(long id) {
        return this.delegate.getAttachment(id);
    }

    @Override
    public List<Attachment> getAttachments(List<Long> ids) {
        return this.delegate.getAttachments(ids);
    }

    @Override
    public List<Attachment> getAllVersionsOfAttachments(ContentEntityObject content) {
        return this.delegate.getAllVersionsOfAttachments(content);
    }

    @Override
    public List<Attachment> getLatestVersionsOfAttachments(ContentEntityObject content) {
        return this.delegate.getLatestVersionsOfAttachments(content);
    }

    @Override
    public List<Attachment> getLatestVersionsOfAttachmentsForMultipleCeos(Iterable<? extends ContentEntityObject> contentEntityObjects) {
        return this.delegate.getLatestVersionsOfAttachmentsForMultipleCeos(contentEntityObjects);
    }

    @Override
    public List<Attachment> getLatestVersionsOfAttachmentsWithAnyStatus(ContentEntityObject content) {
        return this.delegate.getLatestVersionsOfAttachmentsWithAnyStatus(content);
    }

    @Override
    public List<Attachment> getLatestVersionsOfAttachmentsWithAnyStatusForContainers(Iterable<? extends ContentEntityObject> contentEntityObjects) {
        return this.delegate.getLatestVersionsOfAttachmentsWithAnyStatusForContainers(contentEntityObjects);
    }

    @Override
    public PageResponse<Attachment> getFilteredAttachments(ContentEntityObject content, LimitedRequest pageRequest, Predicate<? super Attachment> filterPredicate) {
        return this.delegate.getFilteredAttachments(content, pageRequest, filterPredicate);
    }

    @Override
    public int countLatestVersionsOfAttachments(ContentEntityObject content) {
        return this.delegate.countLatestVersionsOfAttachments(content);
    }

    @Override
    public int countLatestVersionsOfAttachmentsWithAnyStatus(ContentEntityObject content) {
        return this.delegate.countLatestVersionsOfAttachmentsWithAnyStatus(content);
    }

    @Override
    public int countLatestVersionsOfAttachmentsOnPageSince(ContentEntityObject content, Date since) {
        return this.delegate.countLatestVersionsOfAttachmentsOnPageSince(content, since);
    }

    @Override
    public Optional<AttachmentStatisticsDTO> getAttachmentStatistics() {
        return this.delegate.getAttachmentStatistics();
    }

    @Override
    public InputStream getAttachmentData(Attachment attachment) {
        return this.delegate.getAttachmentData(attachment);
    }

    @Override
    public InputStream getAttachmentData(Attachment attachment, Optional<RangeRequest> range) {
        return this.delegate.getAttachmentData(attachment, range);
    }

    @Override
    public void removeAttachmentWithoutNotifications(Attachment attachment) {
        this.invalidateCache(attachment);
        this.delegate.removeAttachmentWithoutNotifications(attachment);
    }

    @Override
    public void removeAttachmentFromServer(Attachment attachment) {
        this.invalidateCache(attachment);
        this.delegate.removeAttachmentFromServer(attachment);
    }

    @Override
    public void removeAttachmentVersionFromServer(Attachment attachment) {
        this.invalidateCache(attachment);
        this.delegate.removeAttachmentVersionFromServer(attachment);
    }

    @Override
    public void removeAttachmentVersionFromServerWithoutNotifications(Attachment attachment) {
        this.invalidateCache(attachment);
        this.delegate.removeAttachmentVersionFromServerWithoutNotifications(attachment);
    }

    @Override
    public void removeAttachments(List<? extends Attachment> attachments) {
        for (Attachment attachment : attachments) {
            this.invalidateCache(attachment);
        }
        this.delegate.removeAttachments(attachments);
    }

    @Override
    public void moveAttachment(Attachment latestVersion, String fileName, ContentEntityObject newContent) {
        this.invalidateCache(latestVersion);
        this.delegate.moveAttachment(latestVersion, fileName, newContent);
        ReadThroughAttachmentDownloadPathCache.AttachmentDownloadPathCacheKey newKey = this.pathCache.toKey(newContent, fileName);
        this.pathCache.remove(newKey);
    }

    @Override
    public void moveAttachment(Attachment latestVersion, ContentEntityObject newContainer) {
        this.invalidateCache(latestVersion);
        this.delegate.moveAttachment(latestVersion, newContainer);
        this.invalidateCache(latestVersion);
    }

    @Override
    public void copyAttachments(ContentEntityObject sourceContent, ContentEntityObject destinationContent) throws IOException {
        this.delegate.copyAttachments(sourceContent, destinationContent, DefaultSaveContext.REFACTORING);
    }

    @Override
    public void copyAttachments(ContentEntityObject sourceContent, ContentEntityObject destinationContent, SaveContext saveContext) throws IOException {
        this.delegate.copyAttachments(sourceContent, destinationContent, saveContext);
        for (Attachment attachment : destinationContent.getAttachments()) {
            this.invalidateCache(attachment);
        }
    }

    @Override
    public void copyAttachment(Attachment attachment, ContentEntityObject destinationContent) throws IOException {
        this.delegate.copyAttachment(attachment, destinationContent);
        this.invalidateCache(attachment);
    }

    private void invalidateCache(Attachment attachment) {
        ReadThroughAttachmentDownloadPathCache.AttachmentDownloadPathCacheKey key = this.pathCache.toKey(attachment);
        this.pathCache.remove(key);
    }

    @Override
    public Attachment getAttachment(ContentEntityObject content, String attachmentFileName, int version) {
        return this.delegate.getAttachment(content, attachmentFileName, version);
    }

    @Override
    public Attachment getAttachment(ContentEntityObject content, String attachmentFileName) {
        return this.delegate.getAttachment(content, attachmentFileName);
    }

    @Override
    public String getAttachmentDownloadPath(ContentEntityObject content, String attachmentFileName) {
        ReadThroughAttachmentDownloadPathCache.AttachmentDownloadPathCacheKey key = this.pathCache.toKey(content, attachmentFileName);
        return this.pathCache.getAttachmentDownloadPath(key, () -> this.delegate.getAttachmentDownloadPath(content, attachmentFileName)).orElse(null);
    }

    @Override
    public void saveAttachment(Attachment attachment, @Nullable Attachment previousVersion, InputStream attachmentData, SaveContext saveContext) throws IOException {
        this.delegate.saveAttachment(attachment, previousVersion, attachmentData, saveContext);
        this.invalidateCache(attachment);
    }

    @Override
    public void saveAttachment(Attachment attachment, Attachment previousVersion, InputStream attachmentData) throws IOException {
        this.delegate.saveAttachment(attachment, previousVersion, attachmentData);
        this.invalidateCache(attachment);
    }

    @Override
    public void saveAttachments(List<SavableAttachment> savableAttachments, SaveContext saveContext) throws IOException {
        this.delegate.saveAttachments(savableAttachments, saveContext);
        for (SavableAttachment attachment : savableAttachments) {
            this.invalidateCache(attachment.getAttachment());
        }
    }

    @Override
    public void saveAttachments(List<SavableAttachment> savableAttachments) throws IOException {
        this.delegate.saveAttachments(savableAttachments);
        for (SavableAttachment attachment : savableAttachments) {
            this.invalidateCache(attachment.getAttachment());
        }
    }

    @Override
    public void setAttachmentData(Attachment attachment, InputStream attachmentData) throws AttachmentDataExistsException {
        this.delegate.setAttachmentData(attachment, attachmentData);
    }

    @Override
    public AttachmentDao.AttachmentMigrator getMigrator(AttachmentManager destination) {
        return this.delegate.getMigrator(destination);
    }

    @Override
    public AttachmentDao.AttachmentCopier getCopier(AttachmentManager destination) {
        return this.delegate.getCopier(destination);
    }

    @Override
    public AttachmentDao getAttachmentDao() {
        return this.delegate.getAttachmentDao();
    }

    @Override
    public List<Attachment> getAllVersions(Attachment attachment) {
        return this.delegate.getAllVersions(attachment);
    }

    @Override
    public List<Attachment> getPreviousVersions(Attachment attachment) {
        return this.delegate.getPreviousVersions(attachment);
    }

    @Override
    public List<Attachment> getLastAddedVersionsOf(Attachment attachment) {
        return this.delegate.getLastAddedVersionsOf(attachment);
    }

    @Override
    public AttachmentDataStorageType getBackingStorageType() {
        return this.delegate.getBackingStorageType();
    }

    @Override
    public void trash(Attachment attachment) {
        this.delegate.trash(attachment);
    }

    @Override
    public void restore(Attachment attachment) {
        this.delegate.restore(attachment);
    }

    @Override
    public Optional<Attachment> findAttachmentForDownloadPath(String downloadPath) {
        return this.delegate.findAttachmentForDownloadPath(downloadPath);
    }

    @Override
    public AttachmentManager getAttachmentManager() {
        if (this.delegate instanceof DelegatingAttachmentManager) {
            return ((DelegatingAttachmentManager)((Object)this.delegate)).getAttachmentManager();
        }
        return this.delegate;
    }

    @Override
    public Map<Long, Long> getRemappedAttachmentIds() {
        return this.delegate.getRemappedAttachmentIds();
    }
}

