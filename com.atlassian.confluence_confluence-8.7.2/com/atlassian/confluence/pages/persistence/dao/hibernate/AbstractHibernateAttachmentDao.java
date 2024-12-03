/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.core.bean.EntityObject
 *  com.google.common.base.Predicate
 *  com.google.common.base.Throwables
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  javax.persistence.PersistenceException
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.FlushMode
 *  org.hibernate.ReplicationMode
 *  org.hibernate.engine.jdbc.spi.SqlExceptionHelper
 *  org.hibernate.engine.spi.SessionImplementor
 *  org.hibernate.query.Query
 *  org.hibernate.type.Type
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.jdbc.datasource.SingleConnectionDataSource
 *  org.springframework.orm.hibernate5.HibernateTemplate
 *  org.springframework.util.Assert
 */
package com.atlassian.confluence.pages.persistence.dao.hibernate;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.Versioned;
import com.atlassian.confluence.core.persistence.hibernate.HibernateObjectDao;
import com.atlassian.confluence.core.persistence.hibernate.SessionHelper;
import com.atlassian.confluence.core.persistence.hibernate.VersionedHibernateObjectDao;
import com.atlassian.confluence.internal.pages.persistence.AttachmentDaoInternal;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentDataNotFoundException;
import com.atlassian.confluence.pages.AttachmentDataStorageType;
import com.atlassian.confluence.pages.AttachmentStatisticsDTO;
import com.atlassian.confluence.pages.attachments.AttachmentDataStreamSizeMismatchException;
import com.atlassian.confluence.pages.attachments.DelegatingAttachmentDao;
import com.atlassian.confluence.pages.persistence.dao.AbstractAttachmentCopier;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDao;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataDao;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStream;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStreamType;
import com.atlassian.confluence.pages.persistence.dao.FlushableCachingDao;
import com.atlassian.confluence.pages.persistence.dao.NonTransactionalAttachmentDataDao;
import com.atlassian.confluence.pages.persistence.dao.TransactionalAttachmentDataDao;
import com.atlassian.confluence.pages.persistence.dao.hibernate.NonTransactionalHibernateAttachmentDao;
import com.atlassian.confluence.pages.persistence.dao.hibernate.TransactionalHibernateAttachmentDao;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.web.rangerequest.RangeRequest;
import com.atlassian.core.bean.EntityObject;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.PersistenceException;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.FlushMode;
import org.hibernate.ReplicationMode;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.query.Query;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.util.Assert;

public abstract class AbstractHibernateAttachmentDao
extends VersionedHibernateObjectDao<Attachment>
implements AttachmentDaoInternal,
FlushableCachingDao {
    private static final Logger log = LoggerFactory.getLogger(AbstractHibernateAttachmentDao.class);
    protected AttachmentDataDao dataDao;
    private static final String ATTACHMENT_SIZE_QUERY = "SELECT a.totalFileSize, b.totalCurrentFileSize, b.totalDeletedLatestFileSize, c.deletedVersionFileSize FROM (SELECT SUM(LONGVAL) totalFileSize       FROM CONTENTPROPERTIES       WHERE PROPERTYNAME = 'FILESIZE') a,      (SELECT SUM(CASE WHEN content.CONTENT_STATUS = 'current' then LONGVAL end) totalCurrentFileSize,              SUM(CASE WHEN content.CONTENT_STATUS = 'deleted' then LONGVAL end) totalDeletedLatestFileSize       FROM CONTENTPROPERTIES props,            CONTENT content       WHERE content.PREVVER is null         AND content.CONTENTID = props.CONTENTID         AND props.PROPERTYNAME = 'FILESIZE') b,      (SELECT SUM(LONGVAL) deletedVersionFileSize       FROM CONTENT currentAttachment,            CONTENT historicalAttachment,            CONTENTPROPERTIES props       WHERE currentAttachment.CONTENTTYPE = 'ATTACHMENT'         AND currentAttachment.CONTENT_STATUS = 'deleted'         AND currentAttachment.CONTENTID = historicalAttachment.PREVVER         AND historicalAttachment.CONTENTID = props.CONTENTID         AND props.PROPERTYNAME = 'FILESIZE') c ";
    private static final String ATTACHMENT_COUNTS_QUERY = "SELECT COUNT(*) AS allAttachments, SUM(case when PREVVER is null and CONTENT_STATUS = 'current' then 1 else 0 end) AS currentAttachments FROM CONTENT WHERE CONTENTTYPE = 'ATTACHMENT'";

    @Override
    public AttachmentDataDao getDataDao() {
        return this.dataDao;
    }

    public static AttachmentDao getInstance(AttachmentDataDao attachmentDataDao) {
        if (attachmentDataDao instanceof TransactionalAttachmentDataDao) {
            TransactionalHibernateAttachmentDao transactionalDao = new TransactionalHibernateAttachmentDao();
            transactionalDao.setDataDao((TransactionalAttachmentDataDao)attachmentDataDao);
            return transactionalDao;
        }
        if (attachmentDataDao instanceof NonTransactionalAttachmentDataDao) {
            NonTransactionalHibernateAttachmentDao nonTransactionalDao = new NonTransactionalHibernateAttachmentDao();
            nonTransactionalDao.setDataDao((NonTransactionalAttachmentDataDao)attachmentDataDao);
            return nonTransactionalDao;
        }
        throw new IllegalArgumentException("Attachment data dao must implement a transactional marker interface");
    }

    @Override
    public Class getPersistentClass() {
        return Attachment.class;
    }

    @Override
    public List<Attachment> getLatestVersionsOfAttachments(ContentEntityObject content) {
        Assert.notNull((Object)content, (String)"Content entity object should not be null");
        log.debug("Querying database for latest attachments on content ID: {}", (Object)content.getId());
        return this.findNamedQueryStringParam("confluence.attachment_findLatestByContent", "contentId", content.getId());
    }

    @Override
    public List<Attachment> getLatestVersionsOfAttachmentsForMultipleCeos(Iterable<? extends ContentEntityObject> contentEntityObjects) {
        Assert.notNull(contentEntityObjects, (String)"Content entity object list should not be null");
        ImmutableList.Builder builder = ImmutableList.builder();
        Iterable partition = Iterables.partition((Iterable)Iterables.transform(contentEntityObjects, EntityObject::getId), (int)1000);
        for (List contentIdList : partition) {
            List attachments = this.findNamedQueryStringParam("confluence.attachment_findLatestByContentIds", "contentIds", contentIdList);
            builder.addAll((Iterable)attachments);
        }
        return builder.build();
    }

    @Override
    public List<Attachment> getLatestVersionsOfAttachmentsWithAnyStatusForContainers(Iterable<? extends ContentEntityObject> contentEntityObjects) {
        Assert.notNull(contentEntityObjects, (String)"Content entity object list should not be null");
        ImmutableList.Builder builder = ImmutableList.builder();
        Iterable partition = Iterables.partition((Iterable)Iterables.transform(contentEntityObjects, EntityObject::getId), (int)1000);
        for (List contentIdList : partition) {
            Query query = this.getSessionFactory().getCurrentSession().createNamedQuery("confluence.attachment_findLatestByContentIdstWithAnyStatus", Attachment.class);
            query.setParameterList("contentIds", (Collection)contentIdList);
            List attachments = query.list();
            builder.addAll((Iterable)attachments);
        }
        return builder.build();
    }

    @Override
    public List<Attachment> getLatestVersionsOfAttachmentsWithAnyStatus(ContentEntityObject content) {
        Assert.notNull((Object)content, (String)"Content entity object should not be null");
        log.debug("Querying database for latest attachments (including trashed) on content ID: {}", (Object)content.getId());
        return this.findNamedQueryStringParam("confluence.attachment_findLatestByContentWithAnyStatus", "contentId", content.getId());
    }

    @Override
    @Deprecated
    public PageResponse<Attachment> getLatestVersionsOfAttachments(ContentEntityObject ceo, LimitedRequest request, Predicate<? super Attachment> predicate) {
        List attachments = this.findNamedQueryStringParams("confluence.attachment_findLatestByContent", true, request, "contentId", ceo.getId());
        return PageResponseImpl.filteredResponse((LimitedRequest)request, (List)attachments, predicate);
    }

    @Override
    public int countLatestVersionsOfAttachments(ContentEntityObject content) {
        Assert.notNull((Object)content, (String)"Content entity object should not be null");
        log.debug("Querying database for attachment count on content ID: {}", (Object)content.getId());
        List result = (List)this.getHibernateTemplate().execute(session -> {
            Query query = session.getNamedQuery("confluence.attachment_countLatestByContent");
            query.setParameter("contentId", (Object)content.getId());
            query.setHibernateFlushMode(FlushMode.MANUAL);
            return query.getResultList();
        });
        Assert.notNull((Object)result, (String)"Query result of confluence.attachment_countLatestByContent should not be null");
        return (Integer)result.get(0);
    }

    @Override
    public int countLatestVersionsOfAttachmentsWithAnyStatus(ContentEntityObject content) {
        Assert.notNull((Object)content, (String)"Content entity object should not be null");
        log.debug("Querying database for attachment count (including trashed) on content ID: {}", (Object)content.getId());
        List result = this.findNamedQueryStringParam("confluence.attachment_countLatestByContentWithAnyStatus", "contentId", content.getId());
        return (Integer)result.get(0);
    }

    @Override
    public int countLatestVersionsOfAttachmentsOnPageSince(ContentEntityObject content, Date since) {
        Assert.notNull((Object)content, (String)"Content entity object should not be null");
        log.debug("Querying database for attachment count (including trashed) on content ID: {} since {}", (Object)content.getId(), (Object)since);
        List result = this.findNamedQueryStringParams("confluence.attachment_countChangesOnPageSince", "contentId", content.getId(), "date", (Object)since);
        return (Integer)result.get(0);
    }

    @Override
    public Optional<AttachmentStatisticsDTO> getAttachmentStatistics() {
        Optional<AttachmentCounts> countResultSet = this.getAttachmentCounts();
        Optional<AttachmentSizes> sizeResultSet = this.getAttachmentSizes();
        if (countResultSet.isPresent() && sizeResultSet.isPresent()) {
            return Optional.of(new AttachmentStatisticsDTO(countResultSet.get().getTotalCount(), countResultSet.get().getCurrentCount(), sizeResultSet.get().getTotalSize(), sizeResultSet.get().getCurrentSize(), sizeResultSet.get().getDeletedSize()));
        }
        return Optional.empty();
    }

    private Optional<AttachmentCounts> getAttachmentCounts() {
        return (Optional)this.getHibernateTemplate().executeWithNativeSession(session -> {
            SingleConnectionDataSource dataSource = new SingleConnectionDataSource(((SessionImplementor)session).connection(), true);
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(ATTACHMENT_COUNTS_QUERY);
                 ResultSet resultSet = preparedStatement.executeQuery();){
                if (!resultSet.next()) return Optional.empty();
                Optional<AttachmentCounts> optional = Optional.of(new AttachmentCounts(resultSet.getInt(1), resultSet.getInt(2)));
                return optional;
            }
            catch (SQLException ex) {
                this.logger.warn((Object)"Unable to fetch result set for querySELECT COUNT(*) AS allAttachments, SUM(case when PREVVER is null and CONTENT_STATUS = 'current' then 1 else 0 end) AS currentAttachments FROM CONTENT WHERE CONTENTTYPE = 'ATTACHMENT'", (Throwable)new SqlExceptionHelper(true).convert(ex, ex.getMessage()));
            }
            return Optional.empty();
        });
    }

    private Optional<AttachmentSizes> getAttachmentSizes() {
        return (Optional)this.getHibernateTemplate().executeWithNativeSession(session -> {
            SingleConnectionDataSource dataSource = new SingleConnectionDataSource(((SessionImplementor)session).connection(), true);
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(ATTACHMENT_SIZE_QUERY);
                 ResultSet resultSet = preparedStatement.executeQuery();){
                if (!resultSet.next()) return Optional.empty();
                Optional<AttachmentSizes> optional = Optional.of(new AttachmentSizes(resultSet.getLong(1), resultSet.getLong(2), resultSet.getLong(3) + resultSet.getLong(4)));
                return optional;
            }
            catch (SQLException ex) {
                this.logger.warn((Object)"Unable to fetch result set for querySELECT a.totalFileSize, b.totalCurrentFileSize, b.totalDeletedLatestFileSize, c.deletedVersionFileSize FROM (SELECT SUM(LONGVAL) totalFileSize       FROM CONTENTPROPERTIES       WHERE PROPERTYNAME = 'FILESIZE') a,      (SELECT SUM(CASE WHEN content.CONTENT_STATUS = 'current' then LONGVAL end) totalCurrentFileSize,              SUM(CASE WHEN content.CONTENT_STATUS = 'deleted' then LONGVAL end) totalDeletedLatestFileSize       FROM CONTENTPROPERTIES props,            CONTENT content       WHERE content.PREVVER is null         AND content.CONTENTID = props.CONTENTID         AND props.PROPERTYNAME = 'FILESIZE') b,      (SELECT SUM(LONGVAL) deletedVersionFileSize       FROM CONTENT currentAttachment,            CONTENT historicalAttachment,            CONTENTPROPERTIES props       WHERE currentAttachment.CONTENTTYPE = 'ATTACHMENT'         AND currentAttachment.CONTENT_STATUS = 'deleted'         AND currentAttachment.CONTENTID = historicalAttachment.PREVVER         AND historicalAttachment.CONTENTID = props.CONTENTID         AND props.PROPERTYNAME = 'FILESIZE') c ", (Throwable)new SqlExceptionHelper(true).convert(ex, ex.getMessage()));
            }
            return Optional.empty();
        });
    }

    @Override
    public Attachment getById(long id) {
        return (Attachment)this.getByClassId(id);
    }

    @Override
    public List<Attachment> getByIds(List<Long> ids) {
        return this.findNamedQueryStringParam("confluence.attachment_getByIds", "ids", ids);
    }

    @Override
    public Attachment getAttachment(ContentEntityObject content, String fileName, int version) {
        Assert.notNull((Object)content, (String)"Content should not be null");
        Assert.notNull((Object)fileName, (String)"File name should not be null");
        log.debug("Querying database for attachment (contentId: {}, fileName: {}, version: {})", new Object[]{content.getId(), fileName, version});
        List results = this.findNamedQueryStringParams("confluence.attachment_findByContentFileNameAndVersion", "contentId", content.getId(), "title", (Object)fileName, "fileName", (Object)GeneralUtil.specialToLowerCase(fileName), "version", (Object)version, HibernateObjectDao.Cacheability.NOT_CACHEABLE);
        return (Attachment)this.findSingleObject(results);
    }

    @Override
    public Attachment getLatestAttachment(ContentEntityObject content, String fileName) {
        Assert.notNull((Object)content, (String)"Content should not be null");
        Assert.notNull((Object)fileName, (String)"File name should not be null");
        log.debug("Querying database for attachment (contentId: {}, fileName: {})", new Object[]{content.getId(), fileName});
        List results = this.findNamedQueryStringParams("confluence.attachment_findLatestByContentAndFileName", "contentId", content.getId(), "title", (Object)fileName, "fileName", (Object)GeneralUtil.specialToLowerCase(fileName), HibernateObjectDao.Cacheability.NOT_CACHEABLE);
        return (Attachment)this.findSingleObject(results.stream().distinct().collect(Collectors.toList()));
    }

    @Override
    public List<Attachment> getLastAddedVersionsOf(Attachment attachment) {
        if (attachment == null) {
            throw new IllegalArgumentException("attachment is required.");
        }
        if (!attachment.isLatestVersion()) {
            throw new IllegalArgumentException("attachment must be the latest version.");
        }
        return this.findNamedQueryStringParam("confluence.attachment_getLastAddedVersionsOfAttachment", "latestAttachmentId", attachment.getId(), HibernateObjectDao.Cacheability.NOT_CACHEABLE);
    }

    @Override
    public List<Attachment> findAllVersions(Attachment attachment) {
        return this.findAllVersions(attachment.getId());
    }

    public List<Attachment> findAllVersions(Long id) {
        return this.findNamedQueryStringParam("confluence.attachment_findVersionHistory", "originalVersionId", id);
    }

    @Override
    public InputStream getAttachmentData(Attachment attachment) throws AttachmentDataNotFoundException {
        return this.dataDao.getDataForAttachment(attachment);
    }

    @Override
    public InputStream getAttachmentData(Attachment attachment, Optional<RangeRequest> range) throws AttachmentDataNotFoundException {
        AttachmentDataStream dataStream = this.dataDao.getDataForAttachment(attachment, AttachmentDataStreamType.RAW_BINARY, range);
        try {
            return dataStream.getInputStream();
        }
        catch (IOException e) {
            throw new AttachmentDataNotFoundException("Error opening stream to attachment data", e);
        }
    }

    @Override
    public void saveNewAttachment(Attachment attachment, InputStream attachmentData) {
        this.validateAttachmentVersionComment(attachment.getVersionComment());
        attachment.setCreator(AuthenticatedUserThreadLocal.get());
        this.save(attachment);
        this.dataDao.saveDataForAttachment(attachment, attachmentData);
    }

    @Override
    public void saveNewAttachmentVersion(Attachment attachment, Attachment previousVersion, InputStream attachmentData) {
        this.validateAttachmentVersionComment(attachment.getVersionComment());
        if (previousVersion == null) {
            this.saveNewAttachment(attachment, attachmentData);
        } else {
            attachment.getProperties().removeProperty("FILESTORE_ID");
            attachment.getProperties().removeProperty("_atl_AttachmentStatus");
        }
        attachment.setCreator(AuthenticatedUserThreadLocal.get());
        attachment.setCreationDate(new Date());
        this.save(attachment, previousVersion);
        this.dataDao.saveDataForAttachmentVersion(attachment, previousVersion, attachmentData);
    }

    @Override
    public boolean isAttachmentPresent(Attachment attachment) {
        return this.dataDao.isAttachmentPresent(attachment);
    }

    @Override
    public void moveAttachment(Attachment latestVersion, Attachment oldAttachment, ContentEntityObject newContent) {
        if (!latestVersion.isLatestVersion()) {
            throw new IllegalArgumentException("The attachment to be moved must be the latest version.");
        }
        this.validateAttachmentVersionComment(latestVersion.getVersionComment());
        List<Attachment> allVersions = this.findAllVersions(latestVersion);
        for (Attachment attachment : allVersions) {
            if (attachment.isLatestVersion()) {
                this.save(attachment);
                continue;
            }
            this.saveRaw(attachment);
        }
        this.dataDao.moveAttachment(latestVersion, oldAttachment, newContent);
    }

    protected void removeAttachment(Attachment attachment, ContentEntityObject content) {
        content.removeAttachment(attachment);
        this.remove(attachment);
    }

    @Override
    public final void removeAttachmentVersionFromServer(Attachment attachmentVersionToBeRemoved) {
        if (attachmentVersionToBeRemoved == null) {
            throw new IllegalArgumentException("Given attachment version for removal was null.");
        }
        Attachment previousAttachmentVersion = null;
        if (attachmentVersionToBeRemoved.isLatestVersion()) {
            List<Attachment> attachmentVersions = this.findAllVersions(attachmentVersionToBeRemoved);
            if (attachmentVersions.size() == 0) {
                throw new IllegalArgumentException(String.format("Expected to find at least one version in storage (itself) for given [%s].", attachmentVersionToBeRemoved));
            }
            if (attachmentVersions.size() == 1) {
                Attachment foundAttachmentVersion = attachmentVersions.get(0);
                if (attachmentVersionToBeRemoved.getId() != foundAttachmentVersion.getId()) {
                    throw new IllegalArgumentException(String.format("Given [%s] does not represent the only version [%s] found in storage.", attachmentVersionToBeRemoved, foundAttachmentVersion));
                }
                this.removeAttachmentFromServer(attachmentVersionToBeRemoved);
                return;
            }
            if (attachmentVersions.size() > 1) {
                previousAttachmentVersion = attachmentVersions.get(1);
                ContentEntityObject attachmentVersionToBeRemovedContainer = attachmentVersionToBeRemoved.getContainer();
                ContentEntityObject previousAttachmentVersionContainer = previousAttachmentVersion.getContainer();
                if (attachmentVersionToBeRemovedContainer == null) {
                    throw new IllegalArgumentException("Given attachment for removal does not have a container");
                }
                if (previousAttachmentVersionContainer == null) {
                    throw new IllegalArgumentException("Previous version of given attachment for removal does not have a container");
                }
                if (attachmentVersionToBeRemovedContainer.getId() != previousAttachmentVersionContainer.getId()) {
                    throw new IllegalArgumentException(String.format("Expected [%s] of given [%s] to match [%s] of previous version [%s] retrieved from storage.", attachmentVersionToBeRemovedContainer, attachmentVersionToBeRemoved, previousAttachmentVersionContainer, previousAttachmentVersion));
                }
                attachmentVersionToBeRemoved.applyChildVersioningPolicy((Versioned)previousAttachmentVersion, this);
            }
        }
        this.removeAttachmentVersionFromServer(attachmentVersionToBeRemoved, previousAttachmentVersion);
    }

    protected abstract void removeAttachmentVersionFromServer(Attachment var1, @Nullable Attachment var2);

    protected void removeMetaData(Attachment attachment) {
        ContentEntityObject container = attachment.getContainer();
        if (container != null) {
            container.removeAttachment(attachment);
        }
        attachment.setOriginalVersion(null);
        this.remove(attachment);
    }

    protected void overwriteMetaData(Attachment sourceAttachment, Attachment targetAttachment) {
        HibernateTemplate hibernateSession = new HibernateTemplate(this.getSessionFactory());
        ContentEntityObject sourceAttachmentContainer = sourceAttachment.getContainer();
        this.removeMetaData(sourceAttachment);
        hibernateSession.flush();
        sourceAttachment.setContainer(sourceAttachmentContainer);
        hibernateSession.evict((Object)sourceAttachment);
        hibernateSession.flush();
        sourceAttachment.setId(targetAttachment.getId());
        hibernateSession.replicate((Object)sourceAttachment, ReplicationMode.OVERWRITE);
        hibernateSession.flush();
        hibernateSession.evict((Object)sourceAttachment);
        hibernateSession.flush();
        hibernateSession.refresh((Object)targetAttachment);
    }

    @Override
    public AttachmentDataStorageType getBackingStorageType() {
        return this.dataDao.getStorageType();
    }

    protected List<Attachment> removeAllAttachmentVersions(Attachment latestVersion, ContentEntityObject content) {
        List allVersions = Lists.reverse(this.findAllVersions(latestVersion));
        for (Attachment attachment : allVersions) {
            content.removeAttachment(attachment);
            this.remove(attachment);
        }
        return allVersions;
    }

    @Override
    public AttachmentDao.AttachmentMigrator getMigrator(AttachmentDao destinationDao) {
        return new IntraHibernateAttachmentMigrator(this.getHibernateDestinationDaoForCopy(destinationDao));
    }

    @Override
    public AttachmentDao.AttachmentCopier getCopier(AttachmentDao destinationDao) {
        return new IntraHibernateAttachmentCopier(this.getHibernateDestinationDaoForCopy(destinationDao));
    }

    @Override
    public void updateAttachment(Attachment attachment) {
        this.saveRaw(attachment);
    }

    private AbstractHibernateAttachmentDao getHibernateDestinationDaoForCopy(AttachmentDao destinationDao) throws IllegalArgumentException {
        if (destinationDao instanceof DelegatingAttachmentDao) {
            destinationDao = ((DelegatingAttachmentDao)((Object)destinationDao)).getDelegate();
        }
        if (this.equals(destinationDao)) {
            throw new IllegalArgumentException("Source and destination attachment dao must not be the same instance for copying");
        }
        if (!(destinationDao instanceof AbstractHibernateAttachmentDao)) {
            throw new IllegalArgumentException("Source and destination AttachmentDaos must be of type '" + AbstractHibernateAttachmentDao.class + "'.");
        }
        return (AbstractHibernateAttachmentDao)destinationDao;
    }

    @Override
    public void prepareForMigrationTo() {
        this.getDataDao().prepareForMigrationTo();
        int numberDeleted = this.clearAttachmentTable();
        log.info("{} records were deleted from the Attachment table to prepare for a migration to this data store.", (Object)numberDeleted);
    }

    @Override
    public void afterMigrationFrom() {
        this.getDataDao().afterMigrationFrom();
        int count = 0;
        Iterator latestAttachments = this.findLatestVersionsIterator();
        while (latestAttachments.hasNext()) {
            Attachment attachment = (Attachment)latestAttachments.next();
            for (Attachment previousVersion : this.findAllVersions(attachment)) {
                ContentEntityObject ceo = previousVersion.getContainer();
                if (ceo == null) {
                    log.info("Attachment '{}' doesn't have a ContentEntityObject. Weird.", (Object)previousVersion);
                    this.remove(previousVersion);
                    continue;
                }
                this.removeAttachment(previousVersion, ceo);
            }
            ++count;
        }
        log.info("{} records were deleted from the Attachment table to complete a migration from this data store.", (Object)count);
    }

    @Override
    public Map<Long, Long> getRemappedAttachmentIds() {
        List remappedIdResult = this.findNamedQuery("confluence.attachment_remappedIds", HibernateObjectDao.Cacheability.NOT_CACHEABLE);
        ImmutableMap.Builder remappedIds = ImmutableMap.builder();
        for (Object[] values : remappedIdResult) {
            Long oldAttachmentId = (Long)values[0];
            Long newAttachmentId = (Long)values[1];
            remappedIds.put((Object)oldAttachmentId, (Object)newAttachmentId);
        }
        return remappedIds.build();
    }

    private int clearAttachmentTable() {
        return SessionHelper.delete(this.getSessionFactory().getCurrentSession(), "from Attachment", new Object[0], new Type[0]);
    }

    private void validateAttachmentVersionComment(String versionComment) {
        if (versionComment != null && versionComment.length() > 255) {
            throw new IllegalArgumentException("The comment is longer than 255 characters");
        }
    }

    @Override
    public void replaceAttachmentData(Attachment attachment, InputStream attachmentData) {
        this.dataDao.replaceDataForAttachment(attachment, attachmentData);
    }

    @Override
    public void flush() {
        this.getSessionFactory().getCurrentSession().flush();
    }

    public Iterator<Long> findLatestVersionIdsIterator() {
        return this.findNamedQuery("confluence.attachment_getIdsOfLatestAttachments").iterator();
    }

    public Iterator<Long> findLatestVersionIdsIterator(List<Space> spacesToInclude) {
        return this.findNamedQueryStringParam("confluence.attachment_getIdsOfLatestAttachmentsInSpaces", "spaces", spacesToInclude).iterator();
    }

    public List<Long> findLatestVersionIds() {
        return this.findNamedQuery("confluence.attachment_getIdsOfLatestAttachments");
    }

    public List<Long> findLatestVersionIds(List<Space> spacesToInclude) {
        return this.findNamedQueryStringParam("confluence.attachment_getIdsOfLatestAttachmentsInSpaces", "spaces", spacesToInclude);
    }

    private List<Long> findLatestVersionIdsWithAnyStatus() {
        return this.findNamedQuery("confluence.attachment_getIdsOfLatestAttachmentsWithAnyStatus");
    }

    private List<Long> findLatestVersionIdsWithAnyStatus(List<Space> spacesToInclude) {
        return this.findNamedQueryStringParam("confluence.attachment_getIdsOfLatestAttachmentsInSpacesWithAnyStatus", "spaces", spacesToInclude);
    }

    private static class AttachmentSizes {
        private final long totalSize;
        private final long currentSize;
        private final long deletedSize;

        public AttachmentSizes(long totalSize, long currentSize, long deletedSize) {
            this.totalSize = totalSize;
            this.currentSize = currentSize;
            this.deletedSize = deletedSize;
        }

        public long getTotalSize() {
            return this.totalSize;
        }

        public long getCurrentSize() {
            return this.currentSize;
        }

        public long getDeletedSize() {
            return this.deletedSize;
        }
    }

    private static class AttachmentCounts {
        private final int totalCount;
        private final int currentCount;

        public AttachmentCounts(int totalCount, int currentCount) {
            this.totalCount = totalCount;
            this.currentCount = currentCount;
        }

        public int getTotalCount() {
            return this.totalCount;
        }

        public int getCurrentCount() {
            return this.currentCount;
        }
    }

    public class IntraHibernateAttachmentMigrator
    extends IntraHibernateAttachmentCopier
    implements AttachmentDao.AttachmentMigrator {
        protected IntraHibernateAttachmentMigrator(AbstractHibernateAttachmentDao destinationDao) {
            super(destinationDao);
        }

        @Override
        public void migrate() {
            log.info("Performing pre-migration tasks.");
            this.destinationDao.getDataDao().prepareForMigrationTo();
            super.copy();
            log.info("Performing post-migration tasks.");
            AbstractHibernateAttachmentDao.this.getDataDao().afterMigrationFrom();
            log.info("Flushing the DAOs (if necessary).");
            AbstractHibernateAttachmentDao.this.flush();
            this.flushDaoIfNecessary(this.destinationDao);
        }

        private void flushDaoIfNecessary(AttachmentDao attachmentDao) {
            if (!(attachmentDao instanceof FlushableCachingDao)) {
                return;
            }
            ((FlushableCachingDao)((Object)attachmentDao)).flush();
        }
    }

    public class IntraHibernateAttachmentCopier
    extends AbstractAttachmentCopier
    implements AttachmentDao.AttachmentCopier {
        private static final int BATCH_SIZE = 50;
        private final Logger log = LoggerFactory.getLogger(IntraHibernateAttachmentCopier.class);
        protected AbstractHibernateAttachmentDao destinationDao;

        protected IntraHibernateAttachmentCopier(AbstractHibernateAttachmentDao destinationDao) {
            this.destinationDao = destinationDao;
        }

        @Override
        public void copy() {
            AttachmentDataDao destinationDataDao = this.destinationDao.getDataDao();
            AttachmentDataDao sourceDataDao = AbstractHibernateAttachmentDao.this.getDataDao();
            this.log.info("Beginning copy from '{}' to '{}'", (Object)sourceDataDao, (Object)destinationDataDao);
            if (this.progress != null) {
                this.progress.setStatus("Finding Attachments");
                this.progress.setPercentage(0);
            }
            int totalAttachments = (int)AbstractHibernateAttachmentDao.this.findLatestVersionsCount(new String[]{"current", "deleted"});
            this.log.info("Found {} to copy", (Object)totalAttachments);
            List<Long> attachmentIds = this.getSpacesToInclude() == null || this.getSpacesToInclude().isEmpty() ? AbstractHibernateAttachmentDao.this.findLatestVersionIdsWithAnyStatus() : AbstractHibernateAttachmentDao.this.findLatestVersionIdsWithAnyStatus(this.getSpacesToInclude());
            int progressPadding = (int)Math.ceil((double)totalAttachments * 0.01);
            if (this.progress != null) {
                this.progress.setTotalObjects(totalAttachments + progressPadding * 2);
                this.progress.setStatus("Copying attachments");
                this.progress.setCurrentCount(progressPadding);
            }
            int currentAttachmentIndex = 0;
            for (List attachmentIdBatch : Iterables.partition(attachmentIds, (int)50)) {
                List<Attachment> attachments = AbstractHibernateAttachmentDao.this.getByIds(attachmentIdBatch);
                for (Attachment attachment : attachments) {
                    if (attachment.getContainer() == null) {
                        this.log.error("Attachment '{}' does not have a content object. Skipping.", (Object)attachment);
                        ++currentAttachmentIndex;
                        continue;
                    }
                    if (this.isContentExcluded(attachment.getContainer())) {
                        this.log.debug("Skipping attachment '{}' since its parent content '{}' is in the exclusion list.", (Object)attachment, (Object)attachment.getContainer());
                        ++currentAttachmentIndex;
                        continue;
                    }
                    List<Attachment> allAttachmentVersions = attachment.isNew() && attachment.isLatestVersion() ? Collections.singletonList(attachment) : AbstractHibernateAttachmentDao.this.findAllVersions(attachment);
                    this.log.debug("Copying attachment '{}' and {} previous versions.", (Object)attachment, (Object)(allAttachmentVersions.size() - 1));
                    for (Attachment attachmentVersion : allAttachmentVersions) {
                        this.saveAttachmentData(destinationDataDao, sourceDataDao, attachmentVersion);
                    }
                    if (this.progress == null) continue;
                    this.progress.setCurrentCount(progressPadding + ++currentAttachmentIndex);
                    this.progress.setStatus("Copied " + currentAttachmentIndex + " out of " + totalAttachments + " attachments.");
                }
                try {
                    AbstractHibernateAttachmentDao.this.getSessionFactory().getCurrentSession().flush();
                    AbstractHibernateAttachmentDao.this.getSessionFactory().getCurrentSession().clear();
                }
                catch (PersistenceException e) {
                    this.log.warn("Failed to flush session after copying attachment batch.", (Throwable)e);
                }
            }
            this.log.info("Copy completed.");
            if (this.progress != null) {
                this.progress.setCurrentCount(totalAttachments + progressPadding);
                this.progress.setStatus("Completed - " + totalAttachments + " attachments transferred.");
            }
        }

        private void saveAttachmentData(AttachmentDataDao destinationDataDao, AttachmentDataDao sourceDataDao, Attachment attachmentVersion) {
            for (int attempts = 0; attempts <= 1; ++attempts) {
                try (InputStream attachmentData = sourceDataDao.getDataForAttachment(attachmentVersion);){
                    destinationDataDao.saveDataForAttachment(attachmentVersion, attachmentData);
                    if (attempts > 0) {
                        this.log.warn("Second attempt to save {} succeeded", (Object)attachmentVersion);
                    }
                    return;
                }
                catch (AttachmentDataNotFoundException e) {
                    this.log.warn("There was a problem retrieving '{}' from the data store. Skipping. {}", (Object)attachmentVersion, (Object)Throwables.getRootCause((Throwable)e).getMessage());
                    this.log.debug("Exception details: ", (Throwable)e);
                    return;
                }
                catch (AttachmentDataStreamSizeMismatchException e) {
                    this.log.warn("Attachment data size for '{}' did not match attachment size. {}", (Object)attachmentVersion, (Object)e.getMessage());
                    if (e.getActualSize() <= 0L) {
                        this.log.warn("Attachment size is invalid ({}), Skipping. " + attachmentVersion, (Throwable)e);
                        return;
                    }
                    if (attempts > 0) {
                        this.log.warn("Second attempt failed. Skipping. " + attachmentVersion, (Throwable)e);
                        return;
                    }
                    this.log.warn("Attachment size of {} is greater than 0, so adjusting attachment data and retrying.", (Object)attachmentVersion);
                    attachmentVersion.setFileSize(e.getActualSize());
                    continue;
                }
                catch (IOException e) {
                    this.log.warn("Failed to save data for {}", (Object)attachmentVersion);
                }
            }
        }
    }
}

