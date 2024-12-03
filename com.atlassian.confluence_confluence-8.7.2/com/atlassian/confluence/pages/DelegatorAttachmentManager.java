/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 */
package com.atlassian.confluence.pages;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SaveContext;
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
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.web.rangerequest.RangeRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class DelegatorAttachmentManager
implements AttachmentManagerInternal,
DelegatingAttachmentManager {
    private SettingsManager settingsManager;
    private AttachmentManagerInternal defaultAttachmentManager;
    private AttachmentManagerInternal databaseAttachmentManager;

    @Override
    public AttachmentManagerInternal getAttachmentManager() {
        String currentSetting = this.settingsManager.getGlobalSettings().getAttachmentDataStore();
        if ("database.based.attachments.storage".equals(currentSetting)) {
            return this.databaseAttachmentManager;
        }
        return this.defaultAttachmentManager;
    }

    @Override
    public void deepAttachmentDelete(AttachmentDeleteOptions attachmentDeleteOptions) {
        this.getAttachmentManager().deepAttachmentDelete(attachmentDeleteOptions);
    }

    @Override
    public Attachment getAttachment(long id) {
        return this.getAttachmentManager().getAttachment(id);
    }

    @Override
    public List<Attachment> getAttachments(List<Long> ids) {
        return this.getAttachmentManager().getAttachments(ids);
    }

    @Override
    public List<Attachment> getAllVersionsOfAttachments(ContentEntityObject content) {
        return this.getAttachmentManager().getAllVersionsOfAttachments(content);
    }

    @Override
    public List<Attachment> getLatestVersionsOfAttachments(ContentEntityObject content) {
        return this.getAttachmentManager().getLatestVersionsOfAttachments(content);
    }

    @Override
    public List<Attachment> getLatestVersionsOfAttachmentsForMultipleCeos(Iterable<? extends ContentEntityObject> contentEntityObjects) {
        return this.getAttachmentManager().getLatestVersionsOfAttachmentsForMultipleCeos(contentEntityObjects);
    }

    @Override
    public List<Attachment> getLatestVersionsOfAttachmentsWithAnyStatus(ContentEntityObject content) {
        return this.getAttachmentManager().getLatestVersionsOfAttachmentsWithAnyStatus(content);
    }

    @Override
    public List<Attachment> getLatestVersionsOfAttachmentsWithAnyStatusForContainers(Iterable<? extends ContentEntityObject> contentEntityObjects) {
        return this.getAttachmentManager().getLatestVersionsOfAttachmentsWithAnyStatusForContainers(contentEntityObjects);
    }

    @Override
    public PageResponse<Attachment> getFilteredAttachments(ContentEntityObject content, LimitedRequest pageRequest, Predicate<? super Attachment> filterPredicate) {
        return this.getAttachmentManagerInternal().getFilteredAttachments(content, pageRequest, filterPredicate);
    }

    private AttachmentManagerInternal getAttachmentManagerInternal() {
        return this.getAttachmentManager();
    }

    @Override
    public int countLatestVersionsOfAttachments(ContentEntityObject content) {
        return this.getAttachmentManager().countLatestVersionsOfAttachments(content);
    }

    @Override
    public int countLatestVersionsOfAttachmentsWithAnyStatus(ContentEntityObject content) {
        return this.getAttachmentManager().countLatestVersionsOfAttachmentsWithAnyStatus(content);
    }

    @Override
    public int countLatestVersionsOfAttachmentsOnPageSince(ContentEntityObject content, Date since) {
        return this.getAttachmentManager().countLatestVersionsOfAttachmentsOnPageSince(content, since);
    }

    @Override
    public Optional<AttachmentStatisticsDTO> getAttachmentStatistics() {
        return this.getAttachmentManager().getAttachmentStatistics();
    }

    @Override
    public Attachment getAttachment(ContentEntityObject content, String attachmentFileName, int version) {
        return this.getAttachmentManager().getAttachment(content, attachmentFileName, version);
    }

    @Override
    public Attachment getAttachment(ContentEntityObject content, String attachmentFileName) {
        return this.getAttachmentManager().getAttachment(content, attachmentFileName);
    }

    @Override
    public String getAttachmentDownloadPath(ContentEntityObject content, String attachmentFileName) {
        return this.getAttachmentManager().getAttachmentDownloadPath(content, attachmentFileName);
    }

    @Override
    public InputStream getAttachmentData(Attachment attachment) {
        return this.getAttachmentManager().getAttachmentData(attachment);
    }

    @Override
    public InputStream getAttachmentData(Attachment attachment, Optional<RangeRequest> range) {
        return this.getAttachmentManager().getAttachmentData(attachment, range);
    }

    @Override
    public void removeAttachmentWithoutNotifications(Attachment attachment) {
        this.getAttachmentManager().removeAttachmentWithoutNotifications(attachment);
    }

    @Override
    public void removeAttachmentFromServer(Attachment attachment) {
        this.getAttachmentManager().removeAttachmentFromServer(attachment);
    }

    @Override
    public void removeAttachmentVersionFromServer(Attachment attachment) {
        this.getAttachmentManager().removeAttachmentVersionFromServer(attachment);
    }

    @Override
    public void removeAttachmentVersionFromServerWithoutNotifications(Attachment attachment) {
        this.getAttachmentManager().removeAttachmentVersionFromServerWithoutNotifications(attachment);
    }

    @Override
    public void saveAttachment(Attachment attachment, Attachment previousVersion, InputStream attachmentData, SaveContext saveContext) throws IOException {
        this.getAttachmentManager().saveAttachment(attachment, previousVersion, attachmentData, saveContext);
    }

    @Override
    public void saveAttachment(Attachment attachment, Attachment previousVersion, InputStream attachmentData) throws IOException {
        this.getAttachmentManager().saveAttachment(attachment, previousVersion, attachmentData);
    }

    @Override
    public void saveAttachments(List<SavableAttachment> savableAttachments, SaveContext saveContext) throws IOException {
        this.getAttachmentManager().saveAttachments(savableAttachments, saveContext);
    }

    @Override
    public void saveAttachments(List<SavableAttachment> savableAttachments) throws IOException {
        this.getAttachmentManager().saveAttachments(savableAttachments);
    }

    @Override
    public void setAttachmentData(Attachment attachment, InputStream attachmentData) throws AttachmentDataExistsException {
        this.getAttachmentManager().setAttachmentData(attachment, attachmentData);
    }

    @Override
    public void moveAttachment(Attachment attachment, String newFileName, ContentEntityObject newName) {
        this.getAttachmentManager().moveAttachment(attachment, newFileName, newName);
    }

    @Override
    public void moveAttachment(Attachment attachment, ContentEntityObject newContainer) {
        this.getAttachmentManager().moveAttachment(attachment, newContainer);
    }

    @Override
    public void copyAttachments(ContentEntityObject sourceContent, ContentEntityObject destinationContent, SaveContext saveContext) throws IOException {
        this.getAttachmentManager().copyAttachments(sourceContent, destinationContent, saveContext);
    }

    @Override
    public void copyAttachments(ContentEntityObject sourceContent, ContentEntityObject destinationContent) throws IOException {
        this.getAttachmentManager().copyAttachments(sourceContent, destinationContent);
    }

    @Override
    public void copyAttachment(Attachment attachment, ContentEntityObject destinationContent) throws IOException {
        this.getAttachmentManager().copyAttachment(attachment, destinationContent);
    }

    @Override
    public List<Attachment> getAllVersions(Attachment attachment) {
        return this.getAttachmentManager().getAllVersions(attachment);
    }

    @Override
    public List<Attachment> getPreviousVersions(Attachment attachment) {
        return this.getAttachmentManager().getPreviousVersions(attachment);
    }

    @Override
    public List<Attachment> getLastAddedVersionsOf(Attachment attachment) {
        return this.getAttachmentManager().getLastAddedVersionsOf(attachment);
    }

    @Override
    public void removeAttachments(List<? extends Attachment> attachments) {
        this.getAttachmentManager().removeAttachments(attachments);
    }

    @Override
    public AttachmentDao.AttachmentMigrator getMigrator(AttachmentManager destination) {
        return this.getAttachmentManager().getMigrator(destination);
    }

    @Override
    public AttachmentDao.AttachmentCopier getCopier(AttachmentManager destination) {
        return this.getAttachmentManager().getCopier(destination);
    }

    @Override
    public AttachmentDao getAttachmentDao() {
        return this.getAttachmentManager().getAttachmentDao();
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public void setDefaultAttachmentManager(AttachmentManagerInternal defaultAttachmentManager) {
        this.defaultAttachmentManager = defaultAttachmentManager;
    }

    public void setDatabaseAttachmentManager(AttachmentManagerInternal databaseAttachmentManager) {
        this.databaseAttachmentManager = databaseAttachmentManager;
    }

    @Override
    public AttachmentDataStorageType getBackingStorageType() {
        return this.getAttachmentManager().getBackingStorageType();
    }

    @Override
    public Optional<Attachment> findAttachmentForDownloadPath(String downloadPath) {
        return this.getAttachmentManager().findAttachmentForDownloadPath(downloadPath);
    }

    @Override
    public Map<Long, Long> getRemappedAttachmentIds() {
        return this.getAttachmentManager().getRemappedAttachmentIds();
    }

    @Override
    public void trash(Attachment attachment) {
        this.getAttachmentManager().trash(attachment);
    }

    @Override
    public void restore(Attachment attachment) {
        this.getAttachmentManager().restore(attachment);
    }
}

