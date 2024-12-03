/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Maybe
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.pages;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentDataExistsException;
import com.atlassian.confluence.pages.AttachmentDataStorageType;
import com.atlassian.confluence.pages.AttachmentStatisticsDTO;
import com.atlassian.confluence.pages.SavableAttachment;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDao;
import com.atlassian.confluence.pages.persistence.dao.bulk.attachment.delete.AttachmentDeleteOptions;
import com.atlassian.confluence.util.FugueConversionUtil;
import com.atlassian.confluence.web.rangerequest.RangeRequest;
import com.atlassian.fugue.Maybe;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface AttachmentManager {
    public void deepAttachmentDelete(AttachmentDeleteOptions var1);

    @Deprecated
    @Transactional(readOnly=true)
    public Attachment getAttachment(long var1);

    @Deprecated
    @Transactional(readOnly=true)
    public List<Attachment> getAttachments(List<Long> var1);

    @Transactional(readOnly=true)
    public List<Attachment> getAllVersionsOfAttachments(ContentEntityObject var1);

    @Transactional(readOnly=true)
    public List<Attachment> getLatestVersionsOfAttachments(ContentEntityObject var1);

    @Transactional(readOnly=true)
    public List<Attachment> getLatestVersionsOfAttachmentsForMultipleCeos(Iterable<? extends ContentEntityObject> var1);

    @Transactional(readOnly=true)
    public List<Attachment> getLatestVersionsOfAttachmentsWithAnyStatus(ContentEntityObject var1);

    public int countLatestVersionsOfAttachments(ContentEntityObject var1);

    public int countLatestVersionsOfAttachmentsWithAnyStatus(ContentEntityObject var1);

    public int countLatestVersionsOfAttachmentsOnPageSince(ContentEntityObject var1, Date var2);

    @Transactional(readOnly=true)
    public Optional<AttachmentStatisticsDTO> getAttachmentStatistics();

    @Transactional(readOnly=true, noRollbackFor={IllegalArgumentException.class})
    public InputStream getAttachmentData(Attachment var1);

    @Transactional(readOnly=true, noRollbackFor={IllegalArgumentException.class})
    public InputStream getAttachmentData(Attachment var1, Optional<RangeRequest> var2);

    public void removeAttachmentFromServer(Attachment var1);

    public void removeAttachmentWithoutNotifications(Attachment var1);

    public void removeAttachmentVersionFromServer(Attachment var1);

    public void removeAttachmentVersionFromServerWithoutNotifications(Attachment var1);

    public void removeAttachments(List<? extends Attachment> var1);

    public void moveAttachment(Attachment var1, String var2, ContentEntityObject var3);

    public void copyAttachments(ContentEntityObject var1, ContentEntityObject var2, SaveContext var3) throws IOException;

    public void copyAttachments(ContentEntityObject var1, ContentEntityObject var2) throws IOException;

    public void copyAttachment(Attachment var1, ContentEntityObject var2) throws IOException;

    @Deprecated
    @Transactional(readOnly=true)
    public Attachment getAttachment(ContentEntityObject var1, String var2, int var3);

    @Deprecated
    @Transactional(readOnly=true)
    public Attachment getAttachment(ContentEntityObject var1, String var2);

    @Transactional(readOnly=true)
    public String getAttachmentDownloadPath(ContentEntityObject var1, String var2);

    public void saveAttachment(Attachment var1, @Nullable Attachment var2, InputStream var3, SaveContext var4) throws IOException;

    public void saveAttachment(Attachment var1, @Nullable Attachment var2, InputStream var3) throws IOException;

    public void saveAttachments(List<SavableAttachment> var1, SaveContext var2) throws IOException;

    public void saveAttachments(List<SavableAttachment> var1) throws IOException;

    public void setAttachmentData(Attachment var1, InputStream var2) throws AttachmentDataExistsException;

    @Transactional(readOnly=true)
    public AttachmentDao.AttachmentMigrator getMigrator(AttachmentManager var1);

    @Transactional(readOnly=true)
    public AttachmentDao.AttachmentCopier getCopier(AttachmentManager var1);

    @Deprecated
    @Transactional(readOnly=true)
    public AttachmentDao getAttachmentDao();

    @Transactional(readOnly=true)
    public List<Attachment> getAllVersions(Attachment var1);

    @Transactional(readOnly=true)
    public List<Attachment> getPreviousVersions(Attachment var1);

    @Transactional(readOnly=true)
    public List<Attachment> getLastAddedVersionsOf(Attachment var1);

    @Transactional(readOnly=true)
    public AttachmentDataStorageType getBackingStorageType();

    @Deprecated
    @Transactional(readOnly=true)
    default public Maybe<Attachment> getAttachmentForDownloadPath(String downloadPath) {
        return FugueConversionUtil.toComOption(this.findAttachmentForDownloadPath(downloadPath));
    }

    public Optional<Attachment> findAttachmentForDownloadPath(String var1);

    @Transactional(readOnly=true)
    public Map<Long, Long> getRemappedAttachmentIds();

    public void trash(Attachment var1);

    public void restore(Attachment var1);
}

