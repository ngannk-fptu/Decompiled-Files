/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.core.instrument.FunctionCounter
 *  io.micrometer.core.instrument.MeterRegistry
 *  io.micrometer.core.instrument.Tags
 *  io.micrometer.core.instrument.binder.MeterBinder
 *  javax.annotation.Nullable
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.stat.EntityStatistics
 *  org.hibernate.stat.Statistics
 *  org.hibernate.stat.spi.StatisticsImplementor
 */
package com.atlassian.confluence.impl.hibernate.metrics;

import com.atlassian.confluence.impl.metrics.CoreMetrics;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;
import java.util.Arrays;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.stat.EntityStatistics;
import org.hibernate.stat.Statistics;
import org.hibernate.stat.spi.StatisticsImplementor;

final class HibernateEntityMetrics
implements MeterBinder {
    private final EntityStatistics statistics;
    private final Tags tags;

    static Stream<MeterBinder> getBinders(SessionFactoryImplementor sessionFactory) {
        StatisticsImplementor statistics = sessionFactory.getStatistics();
        if (statistics == null) {
            return Stream.empty();
        }
        return Arrays.stream(sessionFactory.getMetamodel().getAllEntityNames()).map(arg_0 -> HibernateEntityMetrics.lambda$getBinders$0((Statistics)statistics, arg_0));
    }

    private static MeterBinder createBinder(Statistics statistics, String entityName) {
        return new HibernateEntityMetrics(statistics.getEntityStatistics(entityName), Tags.of((String)"entityName", (String)entityName));
    }

    private HibernateEntityMetrics(@Nullable EntityStatistics statistics, Tags tags) {
        this.statistics = statistics;
        this.tags = tags;
    }

    public void bindTo(MeterRegistry meterRegistry) {
        this.counter(meterRegistry, CoreMetrics.HIBERNATE_ENTITY_FETCH, "The number of entities fetched from a database query", EntityStatistics::getFetchCount, new String[0]);
        this.counter(meterRegistry, CoreMetrics.HIBERNATE_ENTITY_LOAD, "The number of entities loaded without a database query", EntityStatistics::getLoadCount, new String[0]);
        this.counter(meterRegistry, CoreMetrics.HIBERNATE_ENTITY_INSERT, "The number of entities inserted", EntityStatistics::getInsertCount, new String[0]);
        this.counter(meterRegistry, CoreMetrics.HIBERNATE_ENTITY_DELETE, "The number of entities deleted", EntityStatistics::getDeleteCount, new String[0]);
        this.counter(meterRegistry, CoreMetrics.HIBERNATE_ENTITY_UPDATE, "The number of entities updated", EntityStatistics::getUpdateCount, new String[0]);
        this.counter(meterRegistry, CoreMetrics.HIBERNATE_ENTITY_LOCK_FAILURE, "The number of entity optimistic locking failures", EntityStatistics::getOptimisticFailureCount, new String[0]);
    }

    private void counter(MeterRegistry registry, CoreMetrics metric, String description, ToDoubleFunction<EntityStatistics> f, String ... extraTags) {
        metric.resolve(name -> FunctionCounter.builder((String)name, (Object)this.statistics, (ToDoubleFunction)f)).tags((Iterable)this.tags).tags(extraTags).description(description).register(registry);
    }

    private static /* synthetic */ MeterBinder lambda$getBinders$0(Statistics statistics, String entityName) {
        return HibernateEntityMetrics.createBinder(statistics, entityName);
    }
}

