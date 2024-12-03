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
 *  org.hibernate.stat.CollectionStatistics
 *  org.hibernate.stat.Statistics
 *  org.hibernate.stat.spi.StatisticsImplementor
 */
package com.atlassian.confluence.impl.hibernate.metrics;

import com.atlassian.confluence.impl.metrics.CoreMetrics;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.stat.CollectionStatistics;
import org.hibernate.stat.Statistics;
import org.hibernate.stat.spi.StatisticsImplementor;

final class HibernateCollectionMetrics
implements MeterBinder {
    private final CollectionStatistics statistics;
    private final Tags tags;

    static Stream<MeterBinder> getBinders(SessionFactoryImplementor sessionFactory) {
        StatisticsImplementor statistics = sessionFactory.getStatistics();
        if (statistics == null) {
            return Stream.empty();
        }
        return sessionFactory.getMetamodel().collectionPersisters().keySet().stream().map(arg_0 -> HibernateCollectionMetrics.lambda$getBinders$0((Statistics)statistics, arg_0));
    }

    private static MeterBinder createBinder(Statistics statistics, String collectionName) {
        return new HibernateCollectionMetrics(statistics.getCollectionStatistics(collectionName), Tags.of((String)"collectionName", (String)collectionName));
    }

    private HibernateCollectionMetrics(@Nullable CollectionStatistics statistics, Tags tags) {
        this.statistics = statistics;
        this.tags = tags;
    }

    public void bindTo(MeterRegistry meterRegistry) {
        this.counter(meterRegistry, CoreMetrics.HIBERNATE_COLLECTION_FETCH, "The number of collections fetched from a database query", CollectionStatistics::getFetchCount, new String[0]);
        this.counter(meterRegistry, CoreMetrics.HIBERNATE_COLLECTION_LOAD, "The number of collections loaded without a database query", CollectionStatistics::getLoadCount, new String[0]);
        this.counter(meterRegistry, CoreMetrics.HIBERNATE_COLLECTION_RECREATE, "The number of collections recreated", CollectionStatistics::getRecreateCount, new String[0]);
        this.counter(meterRegistry, CoreMetrics.HIBERNATE_COLLECTION_REMOVE, "The number of collections removed", CollectionStatistics::getRemoveCount, new String[0]);
        this.counter(meterRegistry, CoreMetrics.HIBERNATE_COLLECTION_UPDATE, "The number of collections updated", CollectionStatistics::getUpdateCount, new String[0]);
    }

    private void counter(MeterRegistry registry, CoreMetrics metric, String description, ToDoubleFunction<CollectionStatistics> f, String ... extraTags) {
        metric.resolve(name -> FunctionCounter.builder((String)name, (Object)this.statistics, (ToDoubleFunction)f)).tags((Iterable)this.tags).tags(extraTags).description(description).register(registry);
    }

    private static /* synthetic */ MeterBinder lambda$getBinders$0(Statistics statistics, String collectionName) {
        return HibernateCollectionMetrics.createBinder(statistics, collectionName);
    }
}

