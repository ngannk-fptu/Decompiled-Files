/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.stat;

import javax.management.MXBean;
import org.hibernate.stat.CacheRegionStatistics;
import org.hibernate.stat.CollectionStatistics;
import org.hibernate.stat.EntityStatistics;
import org.hibernate.stat.NaturalIdCacheStatistics;
import org.hibernate.stat.NaturalIdStatistics;
import org.hibernate.stat.QueryStatistics;
import org.hibernate.stat.SecondLevelCacheStatistics;

@MXBean
public interface Statistics {
    public static final int DEFAULT_QUERY_STATISTICS_MAX_SIZE = 5000;

    public boolean isStatisticsEnabled();

    public void setStatisticsEnabled(boolean var1);

    public void clear();

    public void logSummary();

    public EntityStatistics getEntityStatistics(String var1);

    public CollectionStatistics getCollectionStatistics(String var1);

    public NaturalIdStatistics getNaturalIdStatistics(String var1);

    public QueryStatistics getQueryStatistics(String var1);

    public CacheRegionStatistics getDomainDataRegionStatistics(String var1);

    public CacheRegionStatistics getQueryRegionStatistics(String var1);

    public CacheRegionStatistics getCacheRegionStatistics(String var1);

    public long getEntityDeleteCount();

    public long getEntityInsertCount();

    public long getEntityLoadCount();

    public long getEntityFetchCount();

    public long getEntityUpdateCount();

    public long getQueryExecutionCount();

    public long getQueryExecutionMaxTime();

    public String getQueryExecutionMaxTimeQueryString();

    public long getQueryCacheHitCount();

    public long getQueryCacheMissCount();

    public long getQueryCachePutCount();

    public long getNaturalIdQueryExecutionCount();

    public long getNaturalIdQueryExecutionMaxTime();

    public String getNaturalIdQueryExecutionMaxTimeRegion();

    public String getNaturalIdQueryExecutionMaxTimeEntity();

    public long getNaturalIdCacheHitCount();

    public long getNaturalIdCacheMissCount();

    public long getNaturalIdCachePutCount();

    public long getUpdateTimestampsCacheHitCount();

    public long getUpdateTimestampsCacheMissCount();

    public long getUpdateTimestampsCachePutCount();

    public long getFlushCount();

    public long getConnectCount();

    public long getSecondLevelCacheHitCount();

    public long getSecondLevelCacheMissCount();

    public long getSecondLevelCachePutCount();

    public long getSessionCloseCount();

    public long getSessionOpenCount();

    public long getCollectionLoadCount();

    public long getCollectionFetchCount();

    public long getCollectionUpdateCount();

    public long getCollectionRemoveCount();

    public long getCollectionRecreateCount();

    public long getStartTime();

    public String[] getQueries();

    public String[] getEntityNames();

    public String[] getCollectionRoleNames();

    public String[] getSecondLevelCacheRegionNames();

    public long getSuccessfulTransactionCount();

    public long getTransactionCount();

    public long getPrepareStatementCount();

    public long getCloseStatementCount();

    public long getOptimisticFailureCount();

    @Deprecated
    public SecondLevelCacheStatistics getSecondLevelCacheStatistics(String var1);

    @Deprecated
    public NaturalIdCacheStatistics getNaturalIdCacheStatistics(String var1);

    default public long getQueryPlanCacheHitCount() {
        return 0L;
    }

    default public long getQueryPlanCacheMissCount() {
        return 0L;
    }
}

