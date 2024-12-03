/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.bean.EntityObject
 *  com.google.common.io.CountingInputStream
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.hibernate.HibernateException
 *  org.hibernate.LockMode
 *  org.hibernate.Session
 *  org.hibernate.engine.spi.SessionImplementor
 *  org.hibernate.event.spi.EventSource
 *  org.hibernate.proxy.HibernateProxy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.dao.DataAccessException
 *  org.springframework.dao.IncorrectResultSizeDataAccessException
 *  org.springframework.jdbc.core.JdbcTemplate
 *  org.springframework.jdbc.datasource.SingleConnectionDataSource
 */
package com.atlassian.confluence.pages.persistence.dao.hibernate;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.persistence.hibernate.HibernateObjectDao;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentData;
import com.atlassian.confluence.pages.AttachmentDataNotFoundException;
import com.atlassian.confluence.pages.AttachmentDataStorageType;
import com.atlassian.confluence.pages.attachments.AttachmentDataStreamSizeMismatchException;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStream;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStreamType;
import com.atlassian.confluence.pages.persistence.dao.LegacyAttachmentDataDaoSupport;
import com.atlassian.confluence.pages.persistence.dao.TransactionalAttachmentDataDao;
import com.atlassian.confluence.web.rangerequest.RangeRequest;
import com.atlassian.core.bean.EntityObject;
import com.google.common.io.CountingInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.event.spi.EventSource;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class HibernateAttachmentDataDao
extends HibernateObjectDao
implements TransactionalAttachmentDataDao {
    private static final Logger log = LoggerFactory.getLogger(HibernateAttachmentDataDao.class);

    @Override
    public @NonNull List findAll() {
        throw new UnsupportedOperationException("Retrieving all attachment data is not supported");
    }

    @Override
    public @NonNull List findAllSorted(String s) {
        throw new UnsupportedOperationException("Retrieving all attachment data is not supported");
    }

    @Override
    public void save(EntityObject objectToSave) {
        super.save(objectToSave);
        Session session = this.getSessionFactory().getCurrentSession();
        session.flush();
        if (objectToSave instanceof HibernateProxy || ((EventSource)session).getPersistenceContext().isEntryFor((Object)objectToSave)) {
            session.evict((Object)objectToSave);
        }
    }

    @Override
    public Class getPersistentClass() {
        return AttachmentData.class;
    }

    protected AttachmentData getAttachmentDataForAttachment(Attachment attachment) throws AttachmentDataNotFoundException {
        List dataObjects = this.getHibernateTemplate().findByNamedQueryAndNamedParam("confluence.ad_findAttachmentDataByAttachment", new String[]{"attachment", "version"}, new Object[]{attachment, attachment.getVersion()});
        if (dataObjects.size() == 0) {
            throw new AttachmentDataNotFoundException("No AttachmentData object was returned when it was expected for attachment: " + attachment);
        }
        if (dataObjects.size() > 1) {
            throw new IncorrectResultSizeDataAccessException("Multiple AttachmentData objects were returned when only one was expected for attachment: " + attachment, 1, dataObjects.size());
        }
        return (AttachmentData)dataObjects.get(0);
    }

    @Override
    public InputStream getDataForAttachment(Attachment attachment) throws AttachmentDataNotFoundException {
        if (attachment == null) {
            throw new IllegalArgumentException("Attachment must be non-null");
        }
        AttachmentData attachmentData = this.getAttachmentDataForAttachment(attachment);
        InputStream result = attachmentData.getData();
        this.getHibernateTemplate().evict((Object)attachmentData);
        return result;
    }

    @Override
    public void removeDataForAttachment(Attachment attachment, ContentEntityObject originalContent) {
        this.checkAttachmentAndOriginalContentArgumentsNotNull(attachment, originalContent);
        if (!attachment.isLatestVersion()) {
            throw new IllegalArgumentException("Attachment must be latest version");
        }
        try {
            for (AttachmentData attachmentData : this.getAllAttachmentData(attachment)) {
                attachmentData.setAttachment(null);
                this.remove(attachmentData);
            }
        }
        catch (AttachmentDataNotFoundException adnfe) {
            this.handleAttachmendDataNotFoundException(attachment, adnfe);
        }
    }

    @Override
    public void removeDataForAttachmentVersion(Attachment attachment, ContentEntityObject originalContent) {
        try {
            AttachmentData attachmentData = this.getAttachmentDataForAttachment(attachment);
            attachmentData.setAttachment(null);
            this.remove(attachmentData);
        }
        catch (AttachmentDataNotFoundException adnfe) {
            this.handleAttachmendDataNotFoundException(attachment, adnfe);
        }
    }

    @Override
    public void removeDataForAttachmentVersion(Attachment attachmentVersion, ContentEntityObject originalContent, AttachmentDataStreamType dataStreamType) {
        if (dataStreamType == AttachmentDataStreamType.EXTRACTED_TEXT) {
            return;
        }
        this.removeDataForAttachment(attachmentVersion, originalContent);
    }

    @Override
    public void moveDataForAttachmentVersion(Attachment sourceAttachmentVersion, Attachment targetAttachmentVersion) {
        AttachmentData sourceAttachmentData = null;
        try {
            sourceAttachmentData = this.getAttachmentDataForAttachment(sourceAttachmentVersion);
        }
        catch (AttachmentDataNotFoundException e) {
            throw new IllegalArgumentException(String.format("Could not find data for [%s], see cause.", sourceAttachmentData), e);
        }
        this.removeDataForAttachment(targetAttachmentVersion, targetAttachmentVersion.getContainer());
        sourceAttachmentData.setAttachment(targetAttachmentVersion);
        this.save(sourceAttachmentData);
    }

    private List<AttachmentData> getAllAttachmentData(Attachment latestVersion) throws AttachmentDataNotFoundException {
        if (!latestVersion.isNew()) {
            List data = this.getHibernateTemplate().findByNamedQueryAndNamedParam("confluence.ad_findAllHistoricalAttachmentDataByLatestAttachment", "attachment", (Object)latestVersion);
            ArrayList<AttachmentData> result = new ArrayList<AttachmentData>(data.size() + 1);
            result.addAll(data);
            result.add(this.getAttachmentDataForAttachment(latestVersion));
            return result;
        }
        return Collections.singletonList(this.getAttachmentDataForAttachment(latestVersion));
    }

    @Override
    public void saveDataForAttachment(Attachment attachment, InputStream data) {
        AttachmentData attachmentData = new AttachmentData();
        attachmentData.setAttachment(attachment);
        attachmentData.setVersion(attachment.getVersion());
        attachmentData.setData(data);
        this.saveAttachmentData(attachmentData);
    }

    @Override
    public void saveDataForAttachmentVersion(Attachment attachment, Attachment previousVersion, InputStream data) {
        AttachmentData previousAttachmentData;
        Attachment previousAttachment = new Attachment();
        previousAttachment.setId(attachment.getId());
        previousAttachment.setVersion(previousVersion.getVersion());
        try {
            previousAttachmentData = this.getAttachmentDataForAttachment(previousAttachment);
        }
        catch (AttachmentDataNotFoundException e) {
            throw new RuntimeException("There was a problem retrieving the previous version of the Attachment data for Attachment '" + previousAttachment + "'.", e);
        }
        previousAttachmentData.setAttachment(previousVersion);
        previousAttachmentData.setVersion(previousVersion.getVersion());
        this.save(previousAttachmentData);
        AttachmentData newAttachmentData = new AttachmentData();
        newAttachmentData.setAttachment(attachment);
        newAttachmentData.setVersion(attachment.getVersion());
        newAttachmentData.setData(data);
        this.saveAttachmentData(newAttachmentData);
    }

    void saveAttachmentData(AttachmentData attachmentData) {
        CountingInputStream countingInputStream = new CountingInputStream(attachmentData.getData());
        attachmentData.setData((InputStream)countingInputStream);
        this.save(attachmentData);
        long expectedFileSize = attachmentData.getAttachment().getFileSize();
        if (countingInputStream.getCount() != expectedFileSize) {
            Session session = this.getSessionFactory().getCurrentSession();
            session.lock((Object)attachmentData, LockMode.NONE);
            session.delete((Object)attachmentData);
            session.flush();
            if (attachmentData instanceof HibernateProxy || ((EventSource)session).getPersistenceContext().isEntryFor((Object)attachmentData)) {
                session.evict((Object)attachmentData);
            }
            throw new AttachmentDataStreamSizeMismatchException(expectedFileSize, countingInputStream.getCount());
        }
    }

    @Override
    public void replaceDataForAttachment(Attachment attachment, InputStream data) {
        AttachmentData attachmentData = null;
        try {
            attachmentData = this.getAttachmentDataForAttachment(attachment);
        }
        catch (AttachmentDataNotFoundException attachmentDataNotFoundException) {
            // empty catch block
        }
        if (attachmentData == null) {
            this.saveDataForAttachment(attachment, data);
        } else {
            attachmentData.setData(data);
            this.saveAttachmentData(attachmentData);
        }
    }

    @Override
    public boolean isAttachmentPresent(Attachment attachment) {
        return !this.getHibernateTemplate().findByNamedQueryAndNamedParam("confluence.ad_findAttachmentDataByAttachment", new String[]{"attachment", "version"}, new Object[]{attachment, attachment.getVersion()}).isEmpty();
    }

    @Override
    public void moveAttachment(Attachment attachment, Attachment oldAttachment, ContentEntityObject newContent) {
    }

    @Override
    public void prepareForMigrationTo() {
        int numberDeleted = this.clearAttachmentDataTable();
        if (log.isInfoEnabled()) {
            log.info(numberDeleted + " records were deleted from the AttachmentData table to prepare for a migration to this data store.");
        }
    }

    @Override
    public void afterMigrationFrom() {
        int numberDeleted = this.clearAttachmentDataTable();
        if (log.isInfoEnabled()) {
            log.info(numberDeleted + " records were deleted from the AttachmentData table to complete a migration from this data store.");
        }
    }

    private int clearAttachmentDataTable() {
        try {
            Session session = this.getSessionFactory().getCurrentSession();
            this.getSessionFactory().getCache().evict(AttachmentData.class);
            JdbcTemplate template = new JdbcTemplate((DataSource)new SingleConnectionDataSource(((SessionImplementor)session).connection(), true));
            return template.update("DELETE FROM ATTACHMENTDATA");
        }
        catch (DataAccessException e) {
            log.debug(e.getMessage(), (Throwable)e);
            throw new RuntimeException("There was a problem removing all records in the AttachmentData table, while performing a migration task.", e);
        }
        catch (HibernateException e) {
            log.error(e.getMessage(), (Throwable)e);
            throw new RuntimeException("There was a problem removing all records in the AttachmentData table, while performing a migration task.", e);
        }
    }

    private void checkAttachmentAndOriginalContentArgumentsNotNull(Attachment attachment, ContentEntityObject originalContent) {
        if (attachment == null) {
            throw new IllegalArgumentException("The attachment must not be null.");
        }
        if (originalContent == null) {
            throw new IllegalArgumentException("The content must not be null.");
        }
    }

    private void handleAttachmendDataNotFoundException(Attachment attachment, AttachmentDataNotFoundException e) {
        log.warn("Did not find data for attachment [{}].", (Object)attachment);
        log.debug(e.getMessage(), (Throwable)e);
    }

    @Override
    public AttachmentDataStorageType getStorageType() {
        return AttachmentDataStorageType.DATABASE;
    }

    @Override
    public AttachmentDataStream getDataForAttachment(Attachment attachment, AttachmentDataStreamType dataStreamType) throws AttachmentDataNotFoundException {
        return this.legacyDaoSupport().getAttachmentDataStream(attachment, dataStreamType);
    }

    @Override
    public AttachmentDataStream getDataForAttachment(Attachment attachment, AttachmentDataStreamType dataStreamType, Optional<RangeRequest> range) throws AttachmentDataNotFoundException {
        if (range != null && range.isPresent()) {
            throw new UnsupportedOperationException("This implementation has been deprecated. Use FileSystemAttachmentDataDao instead.");
        }
        return this.getDataForAttachment(attachment, dataStreamType);
    }

    @Override
    public void saveDataForAttachment(Attachment attachment, AttachmentDataStream dataStream) {
        this.legacyDaoSupport().saveDataForAttachment(attachment, dataStream);
    }

    @Override
    public void saveDataForAttachmentVersion(Attachment attachment, Attachment previousVersion, AttachmentDataStream dataStream) {
        this.legacyDaoSupport().saveDataForAttachmentVersion(attachment, previousVersion, dataStream);
    }

    @Override
    public void replaceDataForAttachment(Attachment attachment, AttachmentDataStream dataStream) {
        this.legacyDaoSupport().replaceDataForAttachment(attachment, dataStream);
    }

    private LegacyAttachmentDataDaoSupport legacyDaoSupport() {
        return new LegacyAttachmentDataDaoSupport(this);
    }
}

