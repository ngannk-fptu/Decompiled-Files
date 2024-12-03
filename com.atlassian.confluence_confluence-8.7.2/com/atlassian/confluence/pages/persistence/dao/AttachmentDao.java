/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.core.util.ProgressMeter
 *  com.google.common.base.Predicate
 */
package com.atlassian.confluence.pages.persistence.dao;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentDataNotFoundException;
import com.atlassian.confluence.pages.AttachmentDataStorageType;
import com.atlassian.confluence.pages.AttachmentStatisticsDTO;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataDao;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.web.rangerequest.RangeRequest;
import com.atlassian.core.util.ProgressMeter;
import com.google.common.base.Predicate;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AttachmentDao {
    public List<Attachment> getLatestVersionsOfAttachments(ContentEntityObject var1);

    public List<Attachment> getLatestVersionsOfAttachmentsForMultipleCeos(Iterable<? extends ContentEntityObject> var1);

    public List<Attachment> getLatestVersionsOfAttachmentsWithAnyStatus(ContentEntityObject var1);

    @Deprecated
    public PageResponse<Attachment> getLatestVersionsOfAttachments(ContentEntityObject var1, LimitedRequest var2, Predicate<? super Attachment> var3);

    public int countLatestVersionsOfAttachments(ContentEntityObject var1);

    public int countLatestVersionsOfAttachmentsWithAnyStatus(ContentEntityObject var1);

    public int countLatestVersionsOfAttachmentsOnPageSince(ContentEntityObject var1, Date var2);

    public Optional<AttachmentStatisticsDTO> getAttachmentStatistics();

    public List<Attachment> findAll();

    public Iterator<Attachment> findLatestVersionsIterator();

    public InputStream getAttachmentData(Attachment var1) throws AttachmentDataNotFoundException;

    public InputStream getAttachmentData(Attachment var1, Optional<RangeRequest> var2) throws AttachmentDataNotFoundException;

    public void saveNewAttachment(Attachment var1, InputStream var2);

    public void saveNewAttachmentVersion(Attachment var1, Attachment var2, InputStream var3);

    public boolean isAttachmentPresent(Attachment var1);

    public void moveAttachment(Attachment var1, Attachment var2, ContentEntityObject var3);

    public void removeAttachmentFromServer(Attachment var1);

    public void removeAttachmentVersionFromServer(Attachment var1);

    public void replaceAttachmentData(Attachment var1, InputStream var2);

    public AttachmentMigrator getMigrator(AttachmentDao var1);

    public AttachmentCopier getCopier(AttachmentDao var1);

    public void prepareForMigrationTo();

    public void afterMigrationFrom();

    public List<Attachment> findAllVersions(Attachment var1);

    public Attachment getById(long var1);

    public List<Attachment> getByIds(List<Long> var1);

    public Attachment getAttachment(ContentEntityObject var1, String var2, int var3) throws IllegalArgumentException;

    public Attachment getLatestAttachment(ContentEntityObject var1, String var2) throws IllegalArgumentException;

    public List<Attachment> getLastAddedVersionsOf(Attachment var1);

    public AttachmentDataStorageType getBackingStorageType();

    public void updateAttachment(Attachment var1);

    public Map<Long, Long> getRemappedAttachmentIds();

    public AttachmentDataDao getDataDao();

    public static interface AttachmentCopier {
        public void copy();

        public void setParentContentToExclude(List<? extends ConfluenceEntityObject> var1);

        public void setSpacesToInclude(List<? extends Space> var1);

        public void setProgressMeter(ProgressMeter var1);
    }

    public static interface AttachmentMigrator {
        public void migrate();

        public void setParentContentToExclude(List<? extends ConfluenceEntityObject> var1);

        public void setSpacesToInclude(List<? extends Space> var1);

        public void setProgressMeter(ProgressMeter var1);
    }
}

