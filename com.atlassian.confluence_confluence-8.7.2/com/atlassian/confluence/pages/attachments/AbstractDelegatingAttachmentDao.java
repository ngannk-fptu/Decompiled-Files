/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.google.common.base.Predicate
 */
package com.atlassian.confluence.pages.attachments;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.internal.pages.persistence.AttachmentDaoInternal;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentDataNotFoundException;
import com.atlassian.confluence.pages.AttachmentDataStorageType;
import com.atlassian.confluence.pages.AttachmentStatisticsDTO;
import com.atlassian.confluence.pages.attachments.DelegatingAttachmentDao;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDao;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataDao;
import com.atlassian.confluence.web.rangerequest.RangeRequest;
import com.google.common.base.Predicate;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Internal
public abstract class AbstractDelegatingAttachmentDao
implements AttachmentDaoInternal,
DelegatingAttachmentDao {
    private final AttachmentDaoInternal delegate;

    protected AbstractDelegatingAttachmentDao(AttachmentDaoInternal delegate) {
        this.delegate = delegate;
    }

    @Override
    public AttachmentDao getDelegate() {
        return this.delegate;
    }

    @Override
    public Attachment getById(long id) {
        return this.delegate.getById(id);
    }

    @Override
    public List<Attachment> getByIds(List<Long> ids) {
        return this.delegate.getByIds(ids);
    }

    @Override
    public Attachment getLatestAttachment(ContentEntityObject content, String fileName) {
        return this.delegate.getLatestAttachment(content, fileName);
    }

    @Override
    public List<Attachment> getLastAddedVersionsOf(Attachment attachment) {
        return this.delegate.getLastAddedVersionsOf(attachment);
    }

    @Override
    public void removeAttachmentFromServer(Attachment attachment) {
        this.delegate.removeAttachmentFromServer(attachment);
    }

    @Override
    public List<Attachment> removeAllVersionsFromServer(Attachment attachment) {
        return this.delegate.removeAllVersionsFromServer(attachment);
    }

    @Override
    public void removeAttachmentVersionFromServer(Attachment attachment) {
        this.delegate.removeAttachmentVersionFromServer(attachment);
    }

    @Override
    public List<Attachment> getLatestVersionsOfAttachments(ContentEntityObject ceo) {
        return this.delegate.getLatestVersionsOfAttachments(ceo);
    }

    @Override
    public List<Attachment> getLatestVersionsOfAttachmentsForMultipleCeos(Iterable<? extends ContentEntityObject> contentEntityObjects) {
        return this.delegate.getLatestVersionsOfAttachmentsForMultipleCeos(contentEntityObjects);
    }

    @Override
    public List<Attachment> getLatestVersionsOfAttachmentsWithAnyStatusForContainers(Iterable<? extends ContentEntityObject> contentEntityObjects) {
        return this.delegate.getLatestVersionsOfAttachmentsWithAnyStatusForContainers(contentEntityObjects);
    }

    @Override
    public List<Attachment> getLatestVersionsOfAttachmentsWithAnyStatus(ContentEntityObject ceo) {
        return this.delegate.getLatestVersionsOfAttachmentsWithAnyStatus(ceo);
    }

    @Override
    @Deprecated
    public PageResponse<Attachment> getLatestVersionsOfAttachments(ContentEntityObject ceo, LimitedRequest pageRequest, Predicate<? super Attachment> predicate) {
        return this.delegate.getLatestVersionsOfAttachments(ceo, pageRequest, predicate);
    }

    @Override
    public int countLatestVersionsOfAttachments(ContentEntityObject content) {
        return this.delegate.countLatestVersionsOfAttachments(content);
    }

    @Override
    public int countLatestVersionsOfAttachmentsOnPageSince(ContentEntityObject content, Date since) {
        return this.delegate.countLatestVersionsOfAttachmentsOnPageSince(content, since);
    }

    @Override
    public int countLatestVersionsOfAttachmentsWithAnyStatus(ContentEntityObject content) {
        return this.delegate.countLatestVersionsOfAttachmentsWithAnyStatus(content);
    }

    @Override
    public Optional<AttachmentStatisticsDTO> getAttachmentStatistics() {
        return this.delegate.getAttachmentStatistics();
    }

    @Override
    public List<Attachment> findAll() {
        return this.delegate.findAll();
    }

    @Override
    public Iterator<Attachment> findLatestVersionsIterator() {
        return this.delegate.findLatestVersionsIterator();
    }

    @Override
    public InputStream getAttachmentData(Attachment attachment) throws AttachmentDataNotFoundException {
        return this.delegate.getAttachmentData(attachment);
    }

    @Override
    public InputStream getAttachmentData(Attachment attachment, Optional<RangeRequest> range) throws AttachmentDataNotFoundException {
        return this.delegate.getAttachmentData(attachment, range);
    }

    @Override
    public void saveNewAttachment(Attachment attachment, InputStream attachmentData) {
        this.delegate.saveNewAttachment(attachment, attachmentData);
    }

    @Override
    public void saveNewAttachmentVersion(Attachment attachment, Attachment previousVersion, InputStream attachmentData) {
        this.delegate.saveNewAttachmentVersion(attachment, previousVersion, attachmentData);
    }

    @Override
    public boolean isAttachmentPresent(Attachment attachment) {
        return this.delegate.isAttachmentPresent(attachment);
    }

    @Override
    public void moveAttachment(Attachment attachment, Attachment oldAttachment, ContentEntityObject newContent) {
        this.delegate.moveAttachment(attachment, oldAttachment, newContent);
    }

    @Override
    public void replaceAttachmentData(Attachment attachment, InputStream attachmentData) {
        this.delegate.replaceAttachmentData(attachment, attachmentData);
    }

    @Override
    public AttachmentDao.AttachmentMigrator getMigrator(AttachmentDao destinationDao) {
        return this.delegate.getMigrator(destinationDao);
    }

    @Override
    public AttachmentDao.AttachmentCopier getCopier(AttachmentDao destinationDao) {
        return this.delegate.getCopier(destinationDao);
    }

    @Override
    public void prepareForMigrationTo() {
        this.delegate.prepareForMigrationTo();
    }

    @Override
    public void afterMigrationFrom() {
        this.delegate.afterMigrationFrom();
    }

    @Override
    public List<Attachment> findAllVersions(Attachment attachment) {
        return this.delegate.findAllVersions(attachment);
    }

    @Override
    public Attachment getAttachment(ContentEntityObject content, String fileName, int version) {
        return this.delegate.getAttachment(content, fileName, version);
    }

    @Override
    public AttachmentDataStorageType getBackingStorageType() {
        return this.delegate.getBackingStorageType();
    }

    @Override
    public void updateAttachment(Attachment attachment) {
        this.delegate.updateAttachment(attachment);
    }

    @Override
    public Map<Long, Long> getRemappedAttachmentIds() {
        return this.delegate.getRemappedAttachmentIds();
    }

    @Override
    public AttachmentDataDao getDataDao() {
        return this.delegate.getDataDao();
    }
}

