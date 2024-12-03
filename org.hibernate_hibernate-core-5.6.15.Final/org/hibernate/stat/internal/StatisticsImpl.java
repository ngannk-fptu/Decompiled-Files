/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.stat.internal;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.spi.CacheImplementor;
import org.hibernate.cache.spi.QueryResultsCache;
import org.hibernate.cache.spi.QueryResultsRegion;
import org.hibernate.cache.spi.Region;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.metamodel.model.domain.NavigableRole;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.service.Service;
import org.hibernate.service.spi.Manageable;
import org.hibernate.stat.internal.CacheRegionStatisticsImpl;
import org.hibernate.stat.internal.CollectionStatisticsImpl;
import org.hibernate.stat.internal.DeprecatedNaturalIdCacheStatisticsImpl;
import org.hibernate.stat.internal.EntityStatisticsImpl;
import org.hibernate.stat.internal.NaturalIdStatisticsImpl;
import org.hibernate.stat.internal.QueryStatisticsImpl;
import org.hibernate.stat.internal.StatsNamedContainer;
import org.hibernate.stat.spi.StatisticsImplementor;

public class StatisticsImpl
implements StatisticsImplementor,
Service,
Manageable {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(StatisticsImpl.class);
    private final MetamodelImplementor metamodel;
    private final CacheImplementor cache;
    private final String cacheRegionPrefix;
    private final boolean secondLevelCacheEnabled;
    private final boolean queryCacheEnabled;
    private volatile boolean isStatisticsEnabled;
    private volatile long startTime;
    private final LongAdder sessionOpenCount = new LongAdder();
    private final LongAdder sessionCloseCount = new LongAdder();
    private final LongAdder flushCount = new LongAdder();
    private final LongAdder connectCount = new LongAdder();
    private final LongAdder prepareStatementCount = new LongAdder();
    private final LongAdder closeStatementCount = new LongAdder();
    private final LongAdder entityLoadCount = new LongAdder();
    private final LongAdder entityUpdateCount = new LongAdder();
    private final LongAdder entityInsertCount = new LongAdder();
    private final LongAdder entityDeleteCount = new LongAdder();
    private final LongAdder entityFetchCount = new LongAdder();
    private final LongAdder collectionLoadCount = new LongAdder();
    private final LongAdder collectionUpdateCount = new LongAdder();
    private final LongAdder collectionRemoveCount = new LongAdder();
    private final LongAdder collectionRecreateCount = new LongAdder();
    private final LongAdder collectionFetchCount = new LongAdder();
    private final LongAdder secondLevelCacheHitCount = new LongAdder();
    private final LongAdder secondLevelCacheMissCount = new LongAdder();
    private final LongAdder secondLevelCachePutCount = new LongAdder();
    private final LongAdder naturalIdCacheHitCount = new LongAdder();
    private final LongAdder naturalIdCacheMissCount = new LongAdder();
    private final LongAdder naturalIdCachePutCount = new LongAdder();
    private final LongAdder naturalIdQueryExecutionCount = new LongAdder();
    private final AtomicLong naturalIdQueryExecutionMaxTime = new AtomicLong();
    private volatile String naturalIdQueryExecutionMaxTimeRegion;
    private volatile String naturalIdQueryExecutionMaxTimeEntity;
    private final LongAdder queryExecutionCount = new LongAdder();
    private final AtomicLong queryExecutionMaxTime = new AtomicLong();
    private volatile String queryExecutionMaxTimeQueryString;
    private final LongAdder queryCacheHitCount = new LongAdder();
    private final LongAdder queryCacheMissCount = new LongAdder();
    private final LongAdder queryCachePutCount = new LongAdder();
    private final LongAdder queryPlanCacheHitCount = new LongAdder();
    private final LongAdder queryPlanCacheMissCount = new LongAdder();
    private final LongAdder updateTimestampsCacheHitCount = new LongAdder();
    private final LongAdder updateTimestampsCacheMissCount = new LongAdder();
    private final LongAdder updateTimestampsCachePutCount = new LongAdder();
    private final LongAdder committedTransactionCount = new LongAdder();
    private final LongAdder transactionCount = new LongAdder();
    private final LongAdder optimisticFailureCount = new LongAdder();
    private final StatsNamedContainer<EntityStatisticsImpl> entityStatsMap = new StatsNamedContainer();
    private final StatsNamedContainer<NaturalIdStatisticsImpl> naturalIdQueryStatsMap = new StatsNamedContainer();
    private final StatsNamedContainer<CollectionStatisticsImpl> collectionStatsMap = new StatsNamedContainer();
    private final StatsNamedContainer<QueryStatisticsImpl> queryStatsMap;
    private final StatsNamedContainer<CacheRegionStatisticsImpl> l2CacheStatsMap = new StatsNamedContainer();
    private final StatsNamedContainer<DeprecatedNaturalIdCacheStatisticsImpl> deprecatedNaturalIdStatsMap = new StatsNamedContainer();

    public StatisticsImpl(SessionFactoryImplementor sessionFactory) {
        Objects.requireNonNull(sessionFactory);
        SessionFactoryOptions sessionFactoryOptions = sessionFactory.getSessionFactoryOptions();
        this.queryStatsMap = new StatsNamedContainer(sessionFactory != null ? sessionFactoryOptions.getQueryStatisticsMaxSize() : 5000, 20);
        this.resetStartTime();
        this.metamodel = sessionFactory.getMetamodel();
        this.cache = sessionFactory.getCache();
        this.cacheRegionPrefix = sessionFactoryOptions.getCacheRegionPrefix();
        this.secondLevelCacheEnabled = sessionFactoryOptions.isSecondLevelCacheEnabled();
        this.queryCacheEnabled = sessionFactoryOptions.isQueryCacheEnabled();
    }

    @Override
    public void clear() {
        this.secondLevelCacheHitCount.reset();
        this.secondLevelCacheMissCount.reset();
        this.secondLevelCachePutCount.reset();
        this.naturalIdCacheHitCount.reset();
        this.naturalIdCacheMissCount.reset();
        this.naturalIdCachePutCount.reset();
        this.naturalIdQueryExecutionCount.reset();
        this.naturalIdQueryExecutionMaxTime.set(0L);
        this.naturalIdQueryExecutionMaxTimeRegion = null;
        this.naturalIdQueryExecutionMaxTimeEntity = null;
        this.sessionCloseCount.reset();
        this.sessionOpenCount.reset();
        this.flushCount.reset();
        this.connectCount.reset();
        this.prepareStatementCount.reset();
        this.closeStatementCount.reset();
        this.entityDeleteCount.reset();
        this.entityInsertCount.reset();
        this.entityUpdateCount.reset();
        this.entityLoadCount.reset();
        this.entityFetchCount.reset();
        this.collectionRemoveCount.reset();
        this.collectionUpdateCount.reset();
        this.collectionRecreateCount.reset();
        this.collectionLoadCount.reset();
        this.collectionFetchCount.reset();
        this.queryExecutionCount.reset();
        this.queryCacheHitCount.reset();
        this.queryExecutionMaxTime.set(0L);
        this.queryExecutionMaxTimeQueryString = null;
        this.queryCacheMissCount.reset();
        this.queryCachePutCount.reset();
        this.updateTimestampsCacheMissCount.reset();
        this.updateTimestampsCacheHitCount.reset();
        this.updateTimestampsCachePutCount.reset();
        this.transactionCount.reset();
        this.committedTransactionCount.reset();
        this.optimisticFailureCount.reset();
        this.entityStatsMap.clear();
        this.collectionStatsMap.clear();
        this.naturalIdQueryStatsMap.clear();
        this.l2CacheStatsMap.clear();
        this.queryStatsMap.clear();
        this.deprecatedNaturalIdStatsMap.clear();
        this.queryPlanCacheHitCount.reset();
        this.queryPlanCacheMissCount.reset();
        this.resetStartTime();
    }

    private void resetStartTime() {
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public long getStartTime() {
        return this.startTime;
    }

    @Override
    public boolean isStatisticsEnabled() {
        return this.isStatisticsEnabled;
    }

    @Override
    public void setStatisticsEnabled(boolean b) {
        this.isStatisticsEnabled = b;
    }

    @Override
    public String[] getEntityNames() {
        return this.metamodel.getAllEntityNames();
    }

    @Override
    public EntityStatisticsImpl getEntityStatistics(String entityName) {
        return this.entityStatsMap.getOrCompute(entityName, this::instantiateEntityStatistics);
    }

    @Override
    public long getEntityLoadCount() {
        return this.entityLoadCount.sum();
    }

    @Override
    public long getEntityFetchCount() {
        return this.entityFetchCount.sum();
    }

    @Override
    public long getEntityDeleteCount() {
        return this.entityDeleteCount.sum();
    }

    @Override
    public long getEntityInsertCount() {
        return this.entityInsertCount.sum();
    }

    @Override
    public long getEntityUpdateCount() {
        return this.entityUpdateCount.sum();
    }

    @Override
    public long getOptimisticFailureCount() {
        return this.optimisticFailureCount.sum();
    }

    @Override
    public void loadEntity(String entityName) {
        this.entityLoadCount.increment();
        this.getEntityStatistics(entityName).incrementLoadCount();
    }

    @Override
    public void fetchEntity(String entityName) {
        this.entityFetchCount.increment();
        this.getEntityStatistics(entityName).incrementFetchCount();
    }

    @Override
    public void updateEntity(String entityName) {
        this.entityUpdateCount.increment();
        this.getEntityStatistics(entityName).incrementUpdateCount();
    }

    @Override
    public void insertEntity(String entityName) {
        this.entityInsertCount.increment();
        this.getEntityStatistics(entityName).incrementInsertCount();
    }

    @Override
    public void deleteEntity(String entityName) {
        this.entityDeleteCount.increment();
        this.getEntityStatistics(entityName).incrementDeleteCount();
    }

    @Override
    public void optimisticFailure(String entityName) {
        this.optimisticFailureCount.increment();
        this.getEntityStatistics(entityName).incrementOptimisticFailureCount();
    }

    @Override
    public void entityCachePut(NavigableRole entityName, String regionName) {
        this.secondLevelCachePutCount.increment();
        this.getDomainDataRegionStatistics(regionName).incrementPutCount();
        this.getEntityStatistics(entityName.getFullPath()).incrementCachePutCount();
    }

    @Override
    public void entityCacheHit(NavigableRole entityName, String regionName) {
        this.secondLevelCacheHitCount.increment();
        this.getDomainDataRegionStatistics(regionName).incrementHitCount();
        this.getEntityStatistics(entityName.getFullPath()).incrementCacheHitCount();
    }

    @Override
    public void entityCacheMiss(NavigableRole entityName, String regionName) {
        this.secondLevelCacheMissCount.increment();
        this.getDomainDataRegionStatistics(regionName).incrementMissCount();
        this.getEntityStatistics(entityName.getFullPath()).incrementCacheMissCount();
    }

    @Override
    public String[] getCollectionRoleNames() {
        return this.metamodel.getAllCollectionRoles();
    }

    @Override
    public CollectionStatisticsImpl getCollectionStatistics(String role) {
        return this.collectionStatsMap.getOrCompute(role, this::instantiateCollectionStatistics);
    }

    @Override
    public long getCollectionLoadCount() {
        return this.collectionLoadCount.sum();
    }

    @Override
    public long getCollectionFetchCount() {
        return this.collectionFetchCount.sum();
    }

    @Override
    public long getCollectionUpdateCount() {
        return this.collectionUpdateCount.sum();
    }

    @Override
    public long getCollectionRemoveCount() {
        return this.collectionRemoveCount.sum();
    }

    @Override
    public long getCollectionRecreateCount() {
        return this.collectionRecreateCount.sum();
    }

    @Override
    public void loadCollection(String role) {
        this.collectionLoadCount.increment();
        this.getCollectionStatistics(role).incrementLoadCount();
    }

    @Override
    public void fetchCollection(String role) {
        this.collectionFetchCount.increment();
        this.getCollectionStatistics(role).incrementFetchCount();
    }

    @Override
    public void updateCollection(String role) {
        this.collectionUpdateCount.increment();
        this.getCollectionStatistics(role).incrementUpdateCount();
    }

    @Override
    public void recreateCollection(String role) {
        this.collectionRecreateCount.increment();
        this.getCollectionStatistics(role).incrementRecreateCount();
    }

    @Override
    public void removeCollection(String role) {
        this.collectionRemoveCount.increment();
        this.getCollectionStatistics(role).incrementRemoveCount();
    }

    @Override
    public void collectionCachePut(NavigableRole collectionRole, String regionName) {
        this.secondLevelCachePutCount.increment();
        this.getDomainDataRegionStatistics(regionName).incrementPutCount();
        this.getCollectionStatistics(collectionRole.getFullPath()).incrementCachePutCount();
    }

    @Override
    public void collectionCacheHit(NavigableRole collectionRole, String regionName) {
        this.secondLevelCacheHitCount.increment();
        this.getDomainDataRegionStatistics(regionName).incrementHitCount();
        this.getCollectionStatistics(collectionRole.getFullPath()).incrementCacheHitCount();
    }

    @Override
    public void collectionCacheMiss(NavigableRole collectionRole, String regionName) {
        this.secondLevelCacheMissCount.increment();
        this.getDomainDataRegionStatistics(regionName).incrementMissCount();
        this.getCollectionStatistics(collectionRole.getFullPath()).incrementCacheMissCount();
    }

    @Override
    public NaturalIdStatisticsImpl getNaturalIdStatistics(String rootEntityName) {
        return this.naturalIdQueryStatsMap.getOrCompute(rootEntityName, this::instantiateNaturalStatistics);
    }

    @Override
    public DeprecatedNaturalIdCacheStatisticsImpl getNaturalIdCacheStatistics(String regionName) {
        String key = this.cache.unqualifyRegionName(regionName);
        return this.deprecatedNaturalIdStatsMap.getOrCompute(key, this::instantiateDeprecatedNaturalIdCacheStatistics);
    }

    @Override
    public long getNaturalIdQueryExecutionCount() {
        return this.naturalIdQueryExecutionCount.sum();
    }

    @Override
    public long getNaturalIdQueryExecutionMaxTime() {
        return this.naturalIdQueryExecutionMaxTime.get();
    }

    @Override
    public String getNaturalIdQueryExecutionMaxTimeRegion() {
        return this.naturalIdQueryExecutionMaxTimeRegion;
    }

    @Override
    public String getNaturalIdQueryExecutionMaxTimeEntity() {
        return this.naturalIdQueryExecutionMaxTimeEntity;
    }

    @Override
    public long getNaturalIdCacheHitCount() {
        return this.naturalIdCacheHitCount.sum();
    }

    @Override
    public long getNaturalIdCacheMissCount() {
        return this.naturalIdCacheMissCount.sum();
    }

    @Override
    public long getNaturalIdCachePutCount() {
        return this.naturalIdCachePutCount.sum();
    }

    @Override
    public void naturalIdCachePut(NavigableRole rootEntityName, String regionName) {
        this.naturalIdCachePutCount.increment();
        this.getDomainDataRegionStatistics(regionName).incrementPutCount();
        this.getNaturalIdStatistics(rootEntityName.getFullPath()).incrementCachePutCount();
        this.getNaturalIdCacheStatistics(this.qualify(regionName)).incrementPutCount();
    }

    @Override
    public void naturalIdCacheHit(NavigableRole rootEntityName, String regionName) {
        this.naturalIdCacheHitCount.increment();
        this.getDomainDataRegionStatistics(regionName).incrementHitCount();
        this.getNaturalIdStatistics(rootEntityName.getFullPath()).incrementCacheHitCount();
        this.getNaturalIdCacheStatistics(this.qualify(regionName)).incrementHitCount();
    }

    @Override
    public void naturalIdCacheMiss(NavigableRole rootEntityName, String regionName) {
        this.naturalIdCacheMissCount.increment();
        this.getDomainDataRegionStatistics(regionName).incrementMissCount();
        this.getNaturalIdStatistics(rootEntityName.getFullPath()).incrementCacheMissCount();
        this.getNaturalIdCacheStatistics(this.qualify(regionName)).incrementMissCount();
    }

    private String qualify(String regionName) {
        return this.cacheRegionPrefix == null ? regionName : this.cacheRegionPrefix + '.' + regionName;
    }

    @Override
    public void naturalIdQueryExecuted(String rootEntityName, long time) {
        boolean isLongestQuery;
        this.naturalIdQueryExecutionCount.increment();
        long old = this.naturalIdQueryExecutionMaxTime.get();
        while ((isLongestQuery = time > old) && !this.naturalIdQueryExecutionMaxTime.compareAndSet(old, time)) {
            old = this.naturalIdQueryExecutionMaxTime.get();
        }
        if (isLongestQuery) {
            this.naturalIdQueryExecutionMaxTimeEntity = rootEntityName;
        }
        EntityPersister rootEntityPersister = this.metamodel.entityPersister(rootEntityName);
        this.getNaturalIdStatistics(rootEntityName).queryExecuted(time);
        if (rootEntityPersister.hasNaturalIdCache()) {
            String naturalIdRegionName = rootEntityPersister.getNaturalIdCacheAccessStrategy().getRegion().getName();
            this.getNaturalIdCacheStatistics(this.qualify(naturalIdRegionName)).queryExecuted(time);
            if (isLongestQuery) {
                this.naturalIdQueryExecutionMaxTimeRegion = naturalIdRegionName;
            }
        }
    }

    @Override
    public String[] getSecondLevelCacheRegionNames() {
        return this.cache.getSecondLevelCacheRegionNames();
    }

    @Override
    public CacheRegionStatisticsImpl getDomainDataRegionStatistics(String regionName) {
        return this.l2CacheStatsMap.getOrCompute(regionName, this::instantiateCacheRegionStatistics);
    }

    @Override
    public CacheRegionStatisticsImpl getQueryRegionStatistics(String regionName) {
        return this.l2CacheStatsMap.getOrCompute(regionName, this::computeQueryRegionStatistics);
    }

    private CacheRegionStatisticsImpl computeQueryRegionStatistics(String regionName) {
        QueryResultsCache regionAccess = this.cache.getQueryResultsCacheStrictly(regionName);
        if (regionAccess == null) {
            return null;
        }
        return new CacheRegionStatisticsImpl(regionAccess.getRegion());
    }

    @Override
    public CacheRegionStatisticsImpl getCacheRegionStatistics(String regionName) {
        if (!this.secondLevelCacheEnabled) {
            return null;
        }
        return this.l2CacheStatsMap.getOrCompute(regionName, this::createCacheRegionStatistics);
    }

    @Override
    public CacheRegionStatisticsImpl getSecondLevelCacheStatistics(String regionName) {
        return this.getCacheRegionStatistics(this.cache.unqualifyRegionName(regionName));
    }

    @Override
    public long getSecondLevelCacheHitCount() {
        return this.secondLevelCacheHitCount.sum();
    }

    @Override
    public long getSecondLevelCacheMissCount() {
        return this.secondLevelCacheMissCount.sum();
    }

    @Override
    public long getSecondLevelCachePutCount() {
        return this.secondLevelCachePutCount.sum();
    }

    @Override
    public long getUpdateTimestampsCacheHitCount() {
        return this.updateTimestampsCacheHitCount.sum();
    }

    @Override
    public long getUpdateTimestampsCacheMissCount() {
        return this.updateTimestampsCacheMissCount.sum();
    }

    @Override
    public long getUpdateTimestampsCachePutCount() {
        return this.updateTimestampsCachePutCount.sum();
    }

    @Override
    public void updateTimestampsCacheHit() {
        this.updateTimestampsCacheHitCount.increment();
    }

    @Override
    public void updateTimestampsCacheMiss() {
        this.updateTimestampsCacheMissCount.increment();
    }

    @Override
    public void updateTimestampsCachePut() {
        this.updateTimestampsCachePutCount.increment();
    }

    @Override
    public String[] getQueries() {
        return this.queryStatsMap.keysAsArray();
    }

    @Override
    public QueryStatisticsImpl getQueryStatistics(String queryString) {
        return this.queryStatsMap.getOrCompute(queryString, QueryStatisticsImpl::new);
    }

    @Override
    public long getQueryExecutionCount() {
        return this.queryExecutionCount.sum();
    }

    @Override
    public long getQueryCacheHitCount() {
        return this.queryCacheHitCount.sum();
    }

    @Override
    public long getQueryCacheMissCount() {
        return this.queryCacheMissCount.sum();
    }

    @Override
    public long getQueryCachePutCount() {
        return this.queryCachePutCount.sum();
    }

    @Override
    public String getQueryExecutionMaxTimeQueryString() {
        return this.queryExecutionMaxTimeQueryString;
    }

    @Override
    public long getQueryExecutionMaxTime() {
        return this.queryExecutionMaxTime.get();
    }

    @Override
    public void queryExecuted(String hql, int rows, long time) {
        boolean isLongestQuery;
        LOG.hql(hql, time, Long.valueOf(rows));
        this.queryExecutionCount.increment();
        long old = this.queryExecutionMaxTime.get();
        while ((isLongestQuery = time > old) && !this.queryExecutionMaxTime.compareAndSet(old, time)) {
            old = this.queryExecutionMaxTime.get();
        }
        if (isLongestQuery) {
            this.queryExecutionMaxTimeQueryString = hql;
        }
        if (hql != null) {
            this.getQueryStatistics(hql).executed(rows, time);
        }
    }

    @Override
    public void queryCacheHit(String hql, String regionName) {
        LOG.tracef("Statistics#queryCacheHit( `%s`, `%s` )", hql, regionName);
        this.queryCacheHitCount.increment();
        this.getQueryRegionStats(regionName).incrementHitCount();
        if (hql != null) {
            this.getQueryStatistics(hql).incrementCacheHitCount();
        }
    }

    @Override
    public void queryCacheMiss(String hql, String regionName) {
        LOG.tracef("Statistics#queryCacheMiss( `%s`, `%s` )", hql, regionName);
        this.queryCacheMissCount.increment();
        this.getQueryRegionStats(regionName).incrementMissCount();
        if (hql != null) {
            this.getQueryStatistics(hql).incrementCacheMissCount();
        }
    }

    @Override
    public void queryCachePut(String hql, String regionName) {
        LOG.tracef("Statistics#queryCachePut( `%s`, `%s` )", hql, regionName);
        this.queryCachePutCount.increment();
        this.getQueryRegionStats(regionName).incrementPutCount();
        if (hql != null) {
            this.getQueryStatistics(hql).incrementCachePutCount();
        }
    }

    @Override
    public long getQueryPlanCacheHitCount() {
        return this.queryPlanCacheHitCount.sum();
    }

    @Override
    public long getQueryPlanCacheMissCount() {
        return this.queryPlanCacheMissCount.sum();
    }

    @Override
    public void queryCompiled(String hql, long microseconds) {
        this.queryPlanCacheMissCount.increment();
        if (hql != null) {
            this.getQueryStatistics(hql).compiled(microseconds);
        }
    }

    @Override
    public void queryPlanCacheHit(String query) {
        this.queryPlanCacheHitCount.increment();
        if (query != null) {
            this.getQueryStatistics(query).incrementPlanCacheHitCount();
        }
    }

    @Override
    public void queryPlanCacheMiss(String query) {
        this.queryPlanCacheMissCount.increment();
        if (query != null) {
            this.getQueryStatistics(query).incrementPlanCacheMissCount();
        }
    }

    private CacheRegionStatisticsImpl getQueryRegionStats(String regionName) {
        return this.l2CacheStatsMap.getOrCompute(regionName, this::instantiateCacheRegionStatsForQueryResults);
    }

    @Override
    public long getSessionOpenCount() {
        return this.sessionOpenCount.sum();
    }

    @Override
    public long getSessionCloseCount() {
        return this.sessionCloseCount.sum();
    }

    @Override
    public long getFlushCount() {
        return this.flushCount.sum();
    }

    @Override
    public long getConnectCount() {
        return this.connectCount.sum();
    }

    @Override
    public long getSuccessfulTransactionCount() {
        return this.committedTransactionCount.sum();
    }

    @Override
    public long getTransactionCount() {
        return this.transactionCount.sum();
    }

    @Override
    public long getCloseStatementCount() {
        return this.closeStatementCount.sum();
    }

    @Override
    public long getPrepareStatementCount() {
        return this.prepareStatementCount.sum();
    }

    @Override
    public void openSession() {
        this.sessionOpenCount.increment();
    }

    @Override
    public void closeSession() {
        this.sessionCloseCount.increment();
    }

    @Override
    public void flush() {
        this.flushCount.increment();
    }

    @Override
    public void connect() {
        this.connectCount.increment();
    }

    @Override
    public void prepareStatement() {
        this.prepareStatementCount.increment();
    }

    @Override
    public void closeStatement() {
        this.closeStatementCount.increment();
    }

    @Override
    public void endTransaction(boolean success) {
        this.transactionCount.increment();
        if (success) {
            this.committedTransactionCount.increment();
        }
    }

    @Override
    public void logSummary() {
        LOG.loggingStatistics();
        LOG.startTime(this.startTime);
        LOG.sessionsOpened(this.sessionOpenCount.sum());
        LOG.sessionsClosed(this.sessionCloseCount.sum());
        LOG.transactions(this.transactionCount.sum());
        LOG.successfulTransactions(this.committedTransactionCount.sum());
        LOG.optimisticLockFailures(this.optimisticFailureCount.sum());
        LOG.flushes(this.flushCount.sum());
        LOG.connectionsObtained(this.connectCount.sum());
        LOG.statementsPrepared(this.prepareStatementCount.sum());
        LOG.statementsClosed(this.closeStatementCount.sum());
        LOG.secondLevelCachePuts(this.secondLevelCachePutCount.sum());
        LOG.secondLevelCacheHits(this.secondLevelCacheHitCount.sum());
        LOG.secondLevelCacheMisses(this.secondLevelCacheMissCount.sum());
        LOG.entitiesLoaded(this.entityLoadCount.sum());
        LOG.entitiesUpdated(this.entityUpdateCount.sum());
        LOG.entitiesInserted(this.entityInsertCount.sum());
        LOG.entitiesDeleted(this.entityDeleteCount.sum());
        LOG.entitiesFetched(this.entityFetchCount.sum());
        LOG.collectionsLoaded(this.collectionLoadCount.sum());
        LOG.collectionsUpdated(this.collectionUpdateCount.sum());
        LOG.collectionsRemoved(this.collectionRemoveCount.sum());
        LOG.collectionsRecreated(this.collectionRecreateCount.sum());
        LOG.collectionsFetched(this.collectionFetchCount.sum());
        LOG.naturalIdCachePuts(this.naturalIdCachePutCount.sum());
        LOG.naturalIdCacheHits(this.naturalIdCacheHitCount.sum());
        LOG.naturalIdCacheMisses(this.naturalIdCacheMissCount.sum());
        LOG.naturalIdMaxQueryTime(this.naturalIdQueryExecutionMaxTime.get());
        LOG.naturalIdQueriesExecuted(this.naturalIdQueryExecutionCount.sum());
        LOG.queriesExecuted(this.queryExecutionCount.sum());
        LOG.queryCachePuts(this.queryCachePutCount.sum());
        LOG.timestampCachePuts(this.updateTimestampsCachePutCount.sum());
        LOG.timestampCacheHits(this.updateTimestampsCacheHitCount.sum());
        LOG.timestampCacheMisses(this.updateTimestampsCacheMissCount.sum());
        LOG.queryCacheHits(this.queryCacheHitCount.sum());
        LOG.queryCacheMisses(this.queryCacheMissCount.sum());
        LOG.maxQueryTime(this.queryExecutionMaxTime.get());
        LOG.queryPlanCacheHits(this.queryPlanCacheHitCount.sum());
        LOG.queryPlanCacheMisses(this.queryPlanCacheMissCount.sum());
    }

    public String toString() {
        return "Statistics[" + "start time=" + this.startTime + ",sessions opened=" + this.sessionOpenCount + ",sessions closed=" + this.sessionCloseCount + ",transactions=" + this.transactionCount + ",successful transactions=" + this.committedTransactionCount + ",optimistic lock failures=" + this.optimisticFailureCount + ",flushes=" + this.flushCount + ",connections obtained=" + this.connectCount + ",statements prepared=" + this.prepareStatementCount + ",statements closed=" + this.closeStatementCount + ",second level cache puts=" + this.secondLevelCachePutCount + ",second level cache hits=" + this.secondLevelCacheHitCount + ",second level cache misses=" + this.secondLevelCacheMissCount + ",entities loaded=" + this.entityLoadCount + ",entities updated=" + this.entityUpdateCount + ",entities inserted=" + this.entityInsertCount + ",entities deleted=" + this.entityDeleteCount + ",entities fetched=" + this.entityFetchCount + ",collections loaded=" + this.collectionLoadCount + ",collections updated=" + this.collectionUpdateCount + ",collections removed=" + this.collectionRemoveCount + ",collections recreated=" + this.collectionRecreateCount + ",collections fetched=" + this.collectionFetchCount + ",naturalId queries executed to database=" + this.naturalIdQueryExecutionCount + ",naturalId cache puts=" + this.naturalIdCachePutCount + ",naturalId cache hits=" + this.naturalIdCacheHitCount + ",naturalId cache misses=" + this.naturalIdCacheMissCount + ",naturalId max query time=" + this.naturalIdQueryExecutionMaxTime + ",queries executed to database=" + this.queryExecutionCount + ",query cache puts=" + this.queryCachePutCount + ",query cache hits=" + this.queryCacheHitCount + ",query cache misses=" + this.queryCacheMissCount + ",update timestamps cache puts=" + this.updateTimestampsCachePutCount + ",update timestamps cache hits=" + this.updateTimestampsCacheHitCount + ",update timestamps cache misses=" + this.updateTimestampsCacheMissCount + ",max query time=" + this.queryExecutionMaxTime + ",query plan cache hits=" + this.queryPlanCacheHitCount + ",query plan cache misses=" + this.queryPlanCacheMissCount + ']';
    }

    private EntityStatisticsImpl instantiateEntityStatistics(String entityName) {
        return new EntityStatisticsImpl(this.metamodel.entityPersister(entityName));
    }

    private CollectionStatisticsImpl instantiateCollectionStatistics(String role) {
        return new CollectionStatisticsImpl(this.metamodel.collectionPersister(role));
    }

    private NaturalIdStatisticsImpl instantiateNaturalStatistics(String entityName) {
        EntityPersister entityDescriptor = this.metamodel.entityPersister(entityName);
        if (!entityDescriptor.hasNaturalIdentifier()) {
            throw new IllegalArgumentException("Given entity [" + entityName + "] does not define natural-id");
        }
        return new NaturalIdStatisticsImpl(entityDescriptor);
    }

    private DeprecatedNaturalIdCacheStatisticsImpl instantiateDeprecatedNaturalIdCacheStatistics(String unqualifiedRegionName) {
        return new DeprecatedNaturalIdCacheStatisticsImpl(unqualifiedRegionName, this.cache.getNaturalIdAccessesInRegion(unqualifiedRegionName));
    }

    private CacheRegionStatisticsImpl instantiateCacheRegionStatistics(String regionName) {
        Region region = this.cache.getRegion(regionName);
        if (region == null) {
            throw new IllegalArgumentException("Unknown cache region : " + regionName);
        }
        if (region instanceof QueryResultsRegion) {
            throw new IllegalArgumentException("Region name [" + regionName + "] referred to a query result region, not a domain data region");
        }
        return new CacheRegionStatisticsImpl(region);
    }

    private CacheRegionStatisticsImpl instantiateCacheRegionStatsForQueryResults(String regionName) {
        return new CacheRegionStatisticsImpl(this.cache.getQueryResultsCache(regionName).getRegion());
    }

    private CacheRegionStatisticsImpl createCacheRegionStatistics(String regionName) {
        Region region = this.cache.getRegion(regionName);
        if (region == null) {
            if (!this.queryCacheEnabled) {
                return null;
            }
            region = this.cache.getQueryResultsCache(regionName).getRegion();
        }
        return new CacheRegionStatisticsImpl(region);
    }
}

