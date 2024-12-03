/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 *  io.micrometer.common.lang.Nullable
 *  javax.persistence.EntityManagerFactory
 *  javax.persistence.PersistenceException
 *  org.hibernate.SessionFactory
 *  org.hibernate.stat.Statistics
 */
package io.micrometer.core.instrument.binder.jpa;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.binder.MeterBinder;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;

@NonNullApi
@NonNullFields
@Deprecated
public class HibernateMetrics
implements MeterBinder {
    private static final String SESSION_FACTORY_TAG_NAME = "entityManagerFactory";
    private final Iterable<Tag> tags;
    @Nullable
    private final Statistics statistics;

    public static void monitor(MeterRegistry registry, SessionFactory sessionFactory, String sessionFactoryName, String ... tags) {
        HibernateMetrics.monitor(registry, sessionFactory, sessionFactoryName, (Iterable<Tag>)Tags.of(tags));
    }

    public static void monitor(MeterRegistry registry, SessionFactory sessionFactory, String sessionFactoryName, Iterable<Tag> tags) {
        new HibernateMetrics(sessionFactory, sessionFactoryName, tags).bindTo(registry);
    }

    @Deprecated
    public static void monitor(MeterRegistry registry, EntityManagerFactory entityManagerFactory, String entityManagerFactoryName, String ... tags) {
        HibernateMetrics.monitor(registry, entityManagerFactory, entityManagerFactoryName, (Iterable<Tag>)Tags.of(tags));
    }

    @Deprecated
    public static void monitor(MeterRegistry registry, EntityManagerFactory entityManagerFactory, String entityManagerFactoryName, Iterable<Tag> tags) {
        new HibernateMetrics(entityManagerFactory, entityManagerFactoryName, tags).bindTo(registry);
    }

    public HibernateMetrics(SessionFactory sessionFactory, String sessionFactoryName, Iterable<Tag> tags) {
        this.tags = Tags.concat(tags, SESSION_FACTORY_TAG_NAME, sessionFactoryName);
        Statistics statistics = sessionFactory.getStatistics();
        this.statistics = statistics.isStatisticsEnabled() ? statistics : null;
    }

    @Deprecated
    public HibernateMetrics(EntityManagerFactory entityManagerFactory, String entityManagerFactoryName, Iterable<Tag> tags) {
        Statistics statistics;
        this.tags = Tags.concat(tags, SESSION_FACTORY_TAG_NAME, entityManagerFactoryName);
        SessionFactory sessionFactory = this.unwrap(entityManagerFactory);
        this.statistics = sessionFactory != null ? ((statistics = sessionFactory.getStatistics()).isStatisticsEnabled() ? statistics : null) : null;
    }

    private void counter(MeterRegistry registry, String name, String description, ToDoubleFunction<Statistics> f, String ... extraTags) {
        if (this.statistics == null) {
            return;
        }
        FunctionCounter.builder(name, this.statistics, f).tags(this.tags).tags(extraTags).description(description).register(registry);
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        if (this.statistics == null) {
            return;
        }
        this.counter(registry, "hibernate.sessions.open", "Sessions opened", Statistics::getSessionOpenCount, new String[0]);
        this.counter(registry, "hibernate.sessions.closed", "Sessions closed", Statistics::getSessionCloseCount, new String[0]);
        this.counter(registry, "hibernate.transactions", "The number of transactions we know to have been successful", Statistics::getSuccessfulTransactionCount, "result", "success");
        this.counter(registry, "hibernate.transactions", "The number of transactions we know to have failed", s -> s.getTransactionCount() - s.getSuccessfulTransactionCount(), "result", "failure");
        this.counter(registry, "hibernate.optimistic.failures", "The number of StaleObjectStateExceptions that have occurred", Statistics::getOptimisticFailureCount, new String[0]);
        this.counter(registry, "hibernate.flushes", "The global number of flushes executed by sessions (either implicit or explicit)", Statistics::getFlushCount, new String[0]);
        this.counter(registry, "hibernate.connections.obtained", "Get the global number of connections asked by the sessions (the actual number of connections used may be much smaller depending whether you use a connection pool or not)", Statistics::getConnectCount, new String[0]);
        this.counter(registry, "hibernate.statements", "The number of prepared statements that were acquired", Statistics::getPrepareStatementCount, "status", "prepared");
        this.counter(registry, "hibernate.statements", "The number of prepared statements that were released", Statistics::getCloseStatementCount, "status", "closed");
        Arrays.stream(this.statistics.getSecondLevelCacheRegionNames()).filter(this::hasDomainDataRegionStatistics).forEach(regionName -> {
            this.counter(registry, "hibernate.second.level.cache.requests", "The number of cacheable entities/collections successfully retrieved from the cache", stats -> stats.getDomainDataRegionStatistics(regionName).getHitCount(), "region", (String)regionName, "result", "hit");
            this.counter(registry, "hibernate.second.level.cache.requests", "The number of cacheable entities/collections not found in the cache and loaded from the database", stats -> stats.getDomainDataRegionStatistics(regionName).getMissCount(), "region", (String)regionName, "result", "miss");
            this.counter(registry, "hibernate.second.level.cache.puts", "The number of cacheable entities/collections put in the cache", stats -> stats.getDomainDataRegionStatistics(regionName).getPutCount(), "region", (String)regionName);
        });
        this.counter(registry, "hibernate.entities.deletes", "The number of entity deletes", Statistics::getEntityDeleteCount, new String[0]);
        this.counter(registry, "hibernate.entities.fetches", "The number of entity fetches", Statistics::getEntityFetchCount, new String[0]);
        this.counter(registry, "hibernate.entities.inserts", "The number of entity inserts", Statistics::getEntityInsertCount, new String[0]);
        this.counter(registry, "hibernate.entities.loads", "The number of entity loads", Statistics::getEntityLoadCount, new String[0]);
        this.counter(registry, "hibernate.entities.updates", "The number of entity updates", Statistics::getEntityUpdateCount, new String[0]);
        this.counter(registry, "hibernate.collections.deletes", "The number of collection deletes", Statistics::getCollectionRemoveCount, new String[0]);
        this.counter(registry, "hibernate.collections.fetches", "The number of collection fetches", Statistics::getCollectionFetchCount, new String[0]);
        this.counter(registry, "hibernate.collections.loads", "The number of collection loads", Statistics::getCollectionLoadCount, new String[0]);
        this.counter(registry, "hibernate.collections.recreates", "The number of collections recreated", Statistics::getCollectionRecreateCount, new String[0]);
        this.counter(registry, "hibernate.collections.updates", "The number of collection updates", Statistics::getCollectionUpdateCount, new String[0]);
        this.counter(registry, "hibernate.cache.natural.id.requests", "The number of cached naturalId lookups successfully retrieved from cache", Statistics::getNaturalIdCacheHitCount, "result", "hit");
        this.counter(registry, "hibernate.cache.natural.id.requests", "The number of cached naturalId lookups not found in cache", Statistics::getNaturalIdCacheMissCount, "result", "miss");
        this.counter(registry, "hibernate.cache.natural.id.puts", "The number of cacheable naturalId lookups put in cache", Statistics::getNaturalIdCachePutCount, new String[0]);
        this.counter(registry, "hibernate.query.natural.id.executions", "The number of naturalId queries executed against the database", Statistics::getNaturalIdQueryExecutionCount, new String[0]);
        TimeGauge.builder("hibernate.query.natural.id.executions.max", this.statistics, TimeUnit.MILLISECONDS, Statistics::getNaturalIdQueryExecutionMaxTime).description("The maximum query time for naturalId queries executed against the database").tags(this.tags).register(registry);
        this.counter(registry, "hibernate.query.executions", "The number of executed queries", Statistics::getQueryExecutionCount, new String[0]);
        TimeGauge.builder("hibernate.query.executions.max", this.statistics, TimeUnit.MILLISECONDS, Statistics::getQueryExecutionMaxTime).description("The time of the slowest query").tags(this.tags).register(registry);
        this.counter(registry, "hibernate.cache.update.timestamps.requests", "The number of timestamps successfully retrieved from cache", Statistics::getUpdateTimestampsCacheHitCount, "result", "hit");
        this.counter(registry, "hibernate.cache.update.timestamps.requests", "The number of tables for which no update timestamps was not found in cache", Statistics::getUpdateTimestampsCacheMissCount, "result", "miss");
        this.counter(registry, "hibernate.cache.update.timestamps.puts", "The number of timestamps put in cache", Statistics::getUpdateTimestampsCachePutCount, new String[0]);
        this.counter(registry, "hibernate.cache.query.requests", "The number of cached queries successfully retrieved from cache", Statistics::getQueryCacheHitCount, "result", "hit");
        this.counter(registry, "hibernate.cache.query.requests", "The number of cached queries not found in cache", Statistics::getQueryCacheMissCount, "result", "miss");
        this.counter(registry, "hibernate.cache.query.puts", "The number of cacheable queries put in cache", Statistics::getQueryCachePutCount, new String[0]);
        this.counter(registry, "hibernate.cache.query.plan", "The global number of query plans successfully retrieved from cache", Statistics::getQueryPlanCacheHitCount, "result", "hit");
        this.counter(registry, "hibernate.cache.query.plan", "The global number of query plans lookups not found in cache", Statistics::getQueryPlanCacheMissCount, "result", "miss");
    }

    private boolean hasDomainDataRegionStatistics(String regionName) {
        try {
            return this.statistics.getDomainDataRegionStatistics(regionName) != null;
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Nullable
    private SessionFactory unwrap(EntityManagerFactory entityManagerFactory) {
        try {
            return (SessionFactory)entityManagerFactory.unwrap(SessionFactory.class);
        }
        catch (PersistenceException ex) {
            return null;
        }
    }
}

