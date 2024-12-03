/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  javax.persistence.PersistenceException
 *  org.hibernate.SessionFactory
 *  org.hibernate.query.NativeQuery
 *  org.hibernate.query.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.dao.support.DataAccessUtils
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.pages.persistence.dao;

import com.atlassian.confluence.content.ContentProperty;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.persistence.dao.SynchronyEvictionDao;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.persistence.PersistenceException;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class DefaultSynchronyEvictionDao
implements SynchronyEvictionDao {
    private static final Logger log = LoggerFactory.getLogger(DefaultSynchronyEvictionDao.class);
    private static final String DELETE_SQL_QUERY = "DELETE FROM CONTENTPROPERTIES WHERE CONTENTPROPERTIES.PROPERTYNAME = 'sync-rev'";
    private static final String UPDATE_SQL_QUERY = "UPDATE CONTENTPROPERTIES SET STRINGVAL = 'restored' WHERE PROPERTYNAME = 'sync-rev-source'";
    private final SessionFactory sessionFactory;

    public DefaultSynchronyEvictionDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Long> findSafeContentWithHistoryOlderThan(int synchronyThresholdHours, int draftThresholdHours, int limit) {
        HibernateTemplate template = new HibernateTemplate(this.sessionFactory);
        return (List)template.execute(session -> {
            Query softQuery = session.createNamedQuery("confluence.snapshots_softFind", Long.class);
            softQuery.setMaxResults(limit);
            softQuery.setParameter("synchronyThreshold", (Object)this.minusHours(synchronyThresholdHours));
            softQuery.setParameter("lastModifiedThreshold", (Object)this.minusHours(draftThresholdHours));
            return softQuery.list();
        });
    }

    @Override
    public List<Long> findContentWithAnyEventOlderThan(int eventThresholdHours, int limit) {
        return this.executeSynchronyHardFindQuery(eventThresholdHours, limit, "confluence.events_hardFind");
    }

    @Override
    public List<Long> findContentWithAnySnapshotOlderThan(int snapshotThresholdHours, int limit) {
        return this.executeSynchronyHardFindQuery(snapshotThresholdHours, limit, "confluence.snapshots_hardFind");
    }

    @Override
    public int removeAllSynchronyDataFor(Collection<Long> contentIds) {
        int rowsForEventsRemoval = this.removeEventsFor(contentIds);
        int rowsForSnapshotsRemoval = this.removeSnapshotsFor(contentIds);
        int rowsForPropertiesRemoval = this.removeSynchronyContentPropertiesFor(contentIds);
        return rowsForEventsRemoval + rowsForSnapshotsRemoval + rowsForPropertiesRemoval;
    }

    @Override
    public long getEventsCount(Long contentId) {
        HibernateTemplate template = new HibernateTemplate(this.sessionFactory);
        List list = (List)template.execute(session -> {
            Query query = contentId == null ? session.createNamedQuery("confluence.events_count", Long.class) : session.createNamedQuery("confluence.events_countForContentId", Long.class).setParameter("contentId", (Object)contentId);
            return query.list();
        });
        return DataAccessUtils.longResult((Collection)list);
    }

    @Override
    public long getSnapshotsCount(Long contentId) {
        HibernateTemplate template = new HibernateTemplate(this.sessionFactory);
        List list = (List)template.execute(session -> {
            Query query = contentId == null ? session.createNamedQuery("confluence.snapshots_count", Long.class) : session.createNamedQuery("confluence.snapshots_countForContentId", Long.class).setParameter("contentId", (Object)contentId);
            return query.list();
        });
        return DataAccessUtils.longResult((Collection)list);
    }

    @Override
    @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"})
    public int removeApplicationIds(Collection<String> applicationIds) {
        HibernateTemplate template = new HibernateTemplate(this.sessionFactory);
        return Objects.requireNonNull((Integer)template.execute(session -> {
            Query removalQuery = session.createNamedQuery("confluence.secrets_deleteByAppId");
            removalQuery.setParameter("appKeys", (Object)applicationIds);
            return removalQuery.executeUpdate();
        }));
    }

    @Override
    public void removeContentProperties() throws PersistenceException {
        NativeQuery deleteQuery = this.sessionFactory.getCurrentSession().createNativeQuery(DELETE_SQL_QUERY);
        deleteQuery.addSynchronizedEntityClass(ContentProperty.class);
        deleteQuery.addSynchronizedEntityClass(ContentEntityObject.class);
        int numberOfDeletedContentProperties = deleteQuery.executeUpdate();
        log.debug("Number of content properties deleted is {}", (Object)numberOfDeletedContentProperties);
        NativeQuery updateQuery = this.sessionFactory.getCurrentSession().createNativeQuery(UPDATE_SQL_QUERY);
        updateQuery.addSynchronizedEntityClass(ContentProperty.class);
        updateQuery.addSynchronizedEntityClass(ContentEntityObject.class);
        int numberOfUpdatedContentProperties = updateQuery.executeUpdate();
        log.debug("Number of content properties updated is {}", (Object)numberOfUpdatedContentProperties);
    }

    private int removeEventsFor(Collection<Long> contentIds) {
        return this.executeSynchronyRemovalQuery(contentIds, "confluence.events_deleteByContentId");
    }

    private int removeSnapshotsFor(Collection<Long> contentIds) {
        return this.executeSynchronyRemovalQuery(contentIds, "confluence.snapshots_deleteByContentId");
    }

    private List<Long> executeSynchronyHardFindQuery(int snapshotThresholdHours, int limit, String queryName) {
        HibernateTemplate template = new HibernateTemplate(this.sessionFactory);
        return (List)template.execute(session -> {
            Query softQuery = session.createNamedQuery(queryName, Long.class);
            softQuery.setMaxResults(limit);
            softQuery.setParameter("synchronyThreshold", (Object)this.minusHours(snapshotThresholdHours));
            return softQuery.list();
        });
    }

    private int executeSynchronyRemovalQuery(Collection<Long> contentIds, String queryName) {
        HibernateTemplate template = new HibernateTemplate(this.sessionFactory);
        return Objects.requireNonNull((Integer)template.execute(session -> {
            Query removalQuery = session.createNamedQuery(queryName);
            removalQuery.setParameter("contentIds", (Object)contentIds);
            return removalQuery.executeUpdate();
        }));
    }

    private int removeSynchronyContentPropertiesFor(Collection<Long> contentIds) {
        HibernateTemplate template = new HibernateTemplate(this.sessionFactory);
        template.execute(session -> {
            Query query = session.createNamedQuery("confluence.content_withSharedDrafts", ContentEntityObject.class);
            query.setParameter("contentIds", (Object)contentIds);
            List pagesAndDrafts = query.getResultList();
            pagesAndDrafts.forEach(pageOrDraft -> {
                pageOrDraft.getProperties().removeProperty("sync-rev");
                pageOrDraft.getProperties().setStringProperty("sync-rev-source", "restored");
            });
            return null;
        });
        return contentIds.size() * 4;
    }

    private Timestamp minusHours(int amount) {
        return new Timestamp(Instant.now().minus(amount, ChronoUnit.HOURS).toEpochMilli());
    }
}

