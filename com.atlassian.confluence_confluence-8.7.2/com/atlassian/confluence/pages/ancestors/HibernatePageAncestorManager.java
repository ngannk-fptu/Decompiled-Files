/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.event.api.EventListener
 *  javax.persistence.PersistenceException
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.engine.spi.SessionImplementor
 *  org.hibernate.query.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Required
 */
package com.atlassian.confluence.pages.ancestors;

import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.core.BatchOperationManager;
import com.atlassian.confluence.event.events.security.AncestorsUpdateEvent;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.ancestors.AncestorRebuildCalculator;
import com.atlassian.confluence.pages.ancestors.AncestorRebuildException;
import com.atlassian.confluence.pages.ancestors.AncestorRebuildMetrics;
import com.atlassian.confluence.pages.ancestors.PageAncestorManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.event.api.EventListener;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.persistence.PersistenceException;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class HibernatePageAncestorManager
implements PageAncestorManager {
    private static final Logger log = LoggerFactory.getLogger(HibernatePageAncestorManager.class);
    private static final int ANCESTOR_INSERT_CHUNK_SIZE = 1000;
    static final String PAGE_ANCESTORS_ROLE_NAME = Page.class.getName() + ".ancestors";
    private SessionFactory sessionFactory;
    private BatchOperationManager batchOperationManager;

    @Override
    public void rebuildAll() throws AncestorRebuildException {
        this.rebuild(null);
    }

    @Override
    public void rebuildSpace(Space space) throws AncestorRebuildException {
        this.rebuild(space);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void rebuild(@Nullable Space space) throws AncestorRebuildException {
        AncestorRebuildMetrics metrics = new AncestorRebuildMetrics();
        log.info("Started");
        Session session = this.sessionFactory.getCurrentSession();
        this.clearAncestorsFromDatabaseAndCache(session, space, metrics);
        log.info("Ancestors cleared from database and cache in {} ms", (Object)metrics.getStopwatchMillis(AncestorRebuildMetrics.StopwatchKey.CLEAR_ANCESTORS));
        List<Object[]> childParentPairs = this.getChildParentPairsFromDatabase(session, space, metrics);
        log.info("Child/parent pairs loaded from database in {} ms", (Object)metrics.getStopwatchMillis(AncestorRebuildMetrics.StopwatchKey.GET_CHILD_PARENT_PAIRS));
        Map<Long, List<Long>> ancestorMap = new AncestorRebuildCalculator(childParentPairs, metrics).calculate();
        log.info("Parent map calculated in {} ms", (Object)metrics.getStopwatchMillis(AncestorRebuildMetrics.StopwatchKey.CALCULATE_PARENT_MAP));
        log.info("Ancestor map calculated in {} ms", (Object)metrics.getStopwatchMillis(AncestorRebuildMetrics.StopwatchKey.CALCULATE_ANCESTOR_MAP));
        PreparedStatement statement = this.getInsertAncestorsPreparedStatement(session);
        log.info("Storing ancestors in database...");
        try {
            metrics.startStopwatch(AncestorRebuildMetrics.StopwatchKey.STORE_ANCESTORS);
            int totalPages = ancestorMap.size();
            AddAncestorChunkTask task = new AddAncestorChunkTask(session, metrics, totalPages, statement);
            this.batchOperationManager.applyInChunks(ancestorMap.entrySet(), 1000, totalPages, task);
            metrics.stopStopwatch(AncestorRebuildMetrics.StopwatchKey.STORE_ANCESTORS);
        }
        finally {
            try {
                statement.close();
            }
            catch (SQLException e) {
                log.error("Unable to close statement", (Throwable)e);
            }
        }
        log.info("Ancestors persisted to database in {} ms", (Object)metrics.getStopwatchMillis(AncestorRebuildMetrics.StopwatchKey.STORE_ANCESTORS));
        log.info("Complete!");
        log.info("Statistics: Pages with parents processed = {}", (Object)metrics.totalChildren);
        log.info("Statistics: Ancestors inserted in database = {}", (Object)metrics.ancestorsCount);
        log.info("Statistics: Maximum ancestor depth = {}", (Object)metrics.maxAncestors);
        if (metrics.totalChildren > 0) {
            log.info("Statistics: Mean ancestors per page with parent = {}", (Object)(metrics.ancestorsCount / metrics.totalChildren));
        }
    }

    private void clearAncestorsFromDatabaseAndCache(Session session, @Nullable Space space, AncestorRebuildMetrics metrics) throws AncestorRebuildException {
        metrics.startStopwatch(AncestorRebuildMetrics.StopwatchKey.CLEAR_ANCESTORS);
        try {
            session.flush();
            HibernatePageAncestorManager.clearAncestorsTable(((SessionImplementor)session).connection(), space);
            try {
                ((SessionImplementor)session).connection().commit();
            }
            catch (SQLException ex) {
                throw new AncestorRebuildException("Failed to commit following ancestor deletetion");
            }
            session.clear();
        }
        catch (PersistenceException e) {
            throw new AncestorRebuildException("Error cleaning out the CONFANCESTORS table.", e);
        }
        this.clearHibernateCollectionCache(PAGE_ANCESTORS_ROLE_NAME);
        metrics.stopStopwatch(AncestorRebuildMetrics.StopwatchKey.CLEAR_ANCESTORS);
    }

    private PreparedStatement getInsertAncestorsPreparedStatement(Session session) throws AncestorRebuildException {
        try {
            return ((SessionImplementor)session).connection().prepareStatement("INSERT INTO CONFANCESTORS (DESCENDENTID, ANCESTORID, ANCESTORPOSITION) VALUES (?, ?, ?)");
        }
        catch (PersistenceException e) {
            throw new AncestorRebuildException("Error getting connection from Hibernate session", e);
        }
        catch (SQLException e) {
            throw new AncestorRebuildException("Error preparing ancestor table insert statement", e);
        }
    }

    private List<Object[]> getChildParentPairsFromDatabase(Session session, @Nullable Space space, AncestorRebuildMetrics metrics) throws AncestorRebuildException {
        metrics.startStopwatch(AncestorRebuildMetrics.StopwatchKey.GET_CHILD_PARENT_PAIRS);
        StringBuilder hql = new StringBuilder("select p.id, p.parent.id from Page as p where p.parent is not null and p.originalVersion is null and (p.contentStatus = 'current' or p.contentStatus = 'draft')");
        if (space != null) {
            hql.append(" and p.space.lowerKey = :spaceKey");
        }
        hql.append(" order by p.id");
        try {
            Query query = session.createQuery(hql.toString());
            if (space != null) {
                query.setParameter("spaceKey", (Object)space.getKey().toLowerCase());
            }
            List pairs = query.list();
            metrics.stopStopwatch(AncestorRebuildMetrics.StopwatchKey.GET_CHILD_PARENT_PAIRS);
            return pairs;
        }
        catch (PersistenceException e) {
            throw new AncestorRebuildException("Error loading child-parent id pairs from the CONTENT table.", e);
        }
    }

    private static void clearAncestorsTable(Connection connection, @Nullable Space space) throws AncestorRebuildException {
        try (PreparedStatement statement = HibernatePageAncestorManager.prepareClearAncestorsTableStatement(connection, space);){
            statement.execute();
        }
        catch (SQLException ex) {
            throw new AncestorRebuildException("Error cleaning out the CONFANCESTORS table", ex);
        }
    }

    private static PreparedStatement prepareClearAncestorsTableStatement(Connection connection, @Nullable Space space) throws SQLException {
        return space == null ? HibernatePageAncestorManager.prepareDeleteAncestorsStatement(connection) : HibernatePageAncestorManager.prepareDeleteAncestorsStatement(connection, space);
    }

    private static PreparedStatement prepareDeleteAncestorsStatement(Connection connection, Space space) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("delete from CONFANCESTORS where DESCENDENTID IN (select CONTENT.CONTENTID from CONTENT join SPACES on CONTENT.SPACEID=SPACES.SPACEID and SPACES.LOWERSPACEKEY=?)");
        statement.setString(1, space.getKey().toLowerCase());
        return statement;
    }

    private static PreparedStatement prepareDeleteAncestorsStatement(Connection connection) throws SQLException {
        return connection.prepareStatement("delete from CONFANCESTORS");
    }

    private void clearHibernateCollectionCache(String hibernateRoleName) {
        try {
            log.info("Evicting the contents of Hibernate '{}' collection cache", (Object)hibernateRoleName);
            this.sessionFactory.getCache().evictCollectionRegion(hibernateRoleName);
        }
        catch (PersistenceException e) {
            log.error("Failed to evict the Hibernate '{}' collection cache", (Object)hibernateRoleName);
        }
    }

    @Required
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Deprecated
    public void setCacheFactory(CacheFactory ignored) {
    }

    @Required
    public void setBatchOperationManager(BatchOperationManager batchOperationManager) {
        this.batchOperationManager = batchOperationManager;
    }

    @EventListener
    public void handleEvent(AncestorsUpdateEvent ancestorsUpdateEvent) {
        try {
            this.sessionFactory.getCache().evictCollection(PAGE_ANCESTORS_ROLE_NAME, (Serializable)ancestorsUpdateEvent.getPageId());
        }
        catch (Exception e) {
            log.error("Can't remove page from cache: " + e.getMessage(), (Throwable)e);
        }
    }

    private static class AddAncestorChunkTask
    implements Function<List<Map.Entry<Long, List<Long>>>, List<Void>> {
        private final Session session;
        private final AncestorRebuildMetrics metrics;
        private final int totalPages;
        private final PreparedStatement statement;

        public AddAncestorChunkTask(Session session, AncestorRebuildMetrics metrics, int totalPages, PreparedStatement statement) {
            this.session = session;
            this.metrics = metrics;
            this.totalPages = totalPages;
            this.statement = statement;
        }

        @Override
        public List<Void> apply(List<Map.Entry<Long, List<Long>>> ancestorEntries) {
            ancestorEntries.forEach(this::addAncestorsToTableForDescendant);
            try {
                this.session.flush();
            }
            catch (PersistenceException e) {
                log.error("Couldn't flush session", (Throwable)e);
            }
            int pageCount = Math.min(this.metrics.incrementChunkCount() * 1000, this.totalPages);
            log.info("Stored ancestors for child pages... {}/{}", (Object)pageCount, (Object)this.totalPages);
            return new ArrayList<Void>();
        }

        private void addAncestorsToTableForDescendant(Map.Entry<Long, List<Long>> ancestorEntry) {
            List<Long> ancestorIds = ancestorEntry.getValue();
            if (ancestorIds.isEmpty()) {
                return;
            }
            long descendantId = ancestorEntry.getKey();
            int ancestorCount = ancestorIds.size();
            for (int i = 0; i < ancestorCount; ++i) {
                Long ancestorId = ancestorIds.get(i);
                try {
                    this.statement.setLong(1, descendantId);
                    this.statement.setLong(2, ancestorId);
                    this.statement.setLong(3, i);
                    this.statement.executeUpdate();
                    int ancestorsCount = this.metrics.incrementAncestorsCount();
                    log.trace("Updating [{}] ({}/{})", new Object[]{descendantId, ancestorsCount, this.totalPages});
                    continue;
                }
                catch (SQLException e) {
                    log.error("Couldn't execute statement", (Throwable)e);
                }
            }
            this.metrics.setMaxAncestors(ancestorCount);
        }
    }
}

