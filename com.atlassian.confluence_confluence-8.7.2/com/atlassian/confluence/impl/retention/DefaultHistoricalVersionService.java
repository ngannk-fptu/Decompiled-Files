/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.retention;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.retention.ContentRetentionDao;
import com.atlassian.confluence.impl.retention.HistoricalVersionService;
import com.atlassian.confluence.impl.retention.exception.VersionRemovalException;
import com.atlassian.confluence.impl.retention.rules.ContentType;
import com.atlassian.confluence.impl.retention.rules.DeletedHistoricalVersion;
import com.atlassian.confluence.impl.retention.rules.DeletedHistoricalVersionSummary;
import com.atlassian.confluence.impl.retention.rules.HistoricalVersion;
import com.atlassian.confluence.internal.ContentEntityManagerInternal;
import com.atlassian.confluence.internal.pages.AttachmentManagerInternal;
import com.atlassian.confluence.pages.Attachment;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultHistoricalVersionService
implements HistoricalVersionService {
    private static final Logger log = LoggerFactory.getLogger(DefaultHistoricalVersionService.class);
    private final ContentRetentionDao contentRetentionDao;
    private final ContentEntityManagerInternal contentEntityManagerInternal;
    private final AttachmentManagerInternal attachmentManagerInternal;

    public DefaultHistoricalVersionService(ContentRetentionDao contentRetentionDao, ContentEntityManagerInternal contentEntityManagerInternal, AttachmentManagerInternal attachmentManagerInternal) {
        this.contentRetentionDao = contentRetentionDao;
        this.contentEntityManagerInternal = contentEntityManagerInternal;
        this.attachmentManagerInternal = attachmentManagerInternal;
    }

    @Override
    public List<HistoricalVersion> find(long startOriginalId, int limit, ContentType contentType) {
        log.debug("Finding Historical {} Content for content IDs starting with {}, result limit [{}]", new Object[]{contentType, startOriginalId, limit});
        if (contentType == ContentType.PAGE) {
            return this.contentRetentionDao.findHistoricalPageVersions(startOriginalId, limit);
        }
        return this.contentRetentionDao.findHistoricalAttachmentVersions(startOriginalId, limit);
    }

    @Override
    public DeletedHistoricalVersionSummary delete(List<HistoricalVersion> historicalVersionList) {
        log.debug("Deleting [{}] items", (Object)historicalVersionList.size());
        List<DeletedHistoricalVersion> pageResults = historicalVersionList.stream().filter(historicalContent -> ContentType.PAGE == historicalContent.getContentType()).map(this::deletePageVersion).collect(Collectors.toList());
        List<DeletedHistoricalVersion> attachmentResults = historicalVersionList.stream().filter(historicalContent -> ContentType.ATTACHMENT == historicalContent.getContentType()).map(this::deleteAttachmentVersion).collect(Collectors.toList());
        long maxId = historicalVersionList.stream().filter(Objects::nonNull).mapToLong(HistoricalVersion::getOriginalId).max().orElse(0L);
        return this.createSummary(pageResults, attachmentResults, maxId);
    }

    private DeletedHistoricalVersionSummary createSummary(List<DeletedHistoricalVersion> pageResults, List<DeletedHistoricalVersion> attachmentResults, long maxId) {
        return new DeletedHistoricalVersionSummary.Builder().pageVersionsRemoved(pageResults.stream().filter(deletedHistoricalVersion -> deletedHistoricalVersion.getContentType().equals((Object)ContentType.PAGE)).count()).attachmentVersionsRemoved(attachmentResults.stream().filter(deletedHistoricalVersion -> deletedHistoricalVersion.getContentType().equals((Object)ContentType.ATTACHMENT)).count()).attachmentSizeRemoved(attachmentResults.stream().filter(Objects::nonNull).mapToLong(DeletedHistoricalVersion::getAttachmentSize).sum()).lastIdProcessed(maxId).build();
    }

    private DeletedHistoricalVersion deletePageVersion(HistoricalVersion historicalContent) {
        try {
            ContentEntityObject oldVersion = this.contentEntityManagerInternal.getById(historicalContent.getId());
            if (oldVersion != null) {
                this.contentEntityManagerInternal.removeHistoricalVersion(oldVersion);
            }
            return new DeletedHistoricalVersion.Builder().contentType(ContentType.PAGE).build();
        }
        catch (Exception ex) {
            log.error("Error deleting Historical page Version [{}]", (Object)historicalContent, (Object)ex);
            throw new VersionRemovalException(historicalContent.getOriginalId());
        }
    }

    private DeletedHistoricalVersion deleteAttachmentVersion(HistoricalVersion historicalContent) {
        try {
            Attachment attachment = this.attachmentManagerInternal.getAttachment(historicalContent.getId());
            if (attachment != null) {
                this.attachmentManagerInternal.removeAttachmentVersionFromServerWithoutNotifications(attachment);
                long filesize = attachment.getProperties().getLongProperty("FILESIZE", 0L);
                return new DeletedHistoricalVersion.Builder().contentType(ContentType.ATTACHMENT).attachmentSize(filesize).build();
            }
            log.debug("Skipping the deletion of null historical attachment Version [{}]", (Object)historicalContent);
            return this.buildFailedDeletedAttachment();
        }
        catch (Exception ex) {
            log.error("Error deleting Historical attachment Version [{}] ", (Object)historicalContent, (Object)ex);
            throw new VersionRemovalException(historicalContent.getOriginalId());
        }
    }

    private DeletedHistoricalVersion buildFailedDeletedAttachment() {
        return new DeletedHistoricalVersion.Builder().contentType(ContentType.ATTACHMENT).isFailed().build();
    }
}

