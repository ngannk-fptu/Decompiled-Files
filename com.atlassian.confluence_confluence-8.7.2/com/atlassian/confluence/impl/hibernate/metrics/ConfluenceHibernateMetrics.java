/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.core.instrument.MeterRegistry
 *  io.micrometer.core.instrument.binder.MeterBinder
 *  org.hibernate.SessionFactory
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 */
package com.atlassian.confluence.impl.hibernate.metrics;

import com.atlassian.confluence.impl.hibernate.metrics.HibernateCollectionMetrics;
import com.atlassian.confluence.impl.hibernate.metrics.HibernateEntityMetrics;
import com.atlassian.confluence.impl.hibernate.metrics.HibernateL2CacheMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import java.util.function.Function;
import java.util.stream.Stream;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;

public final class ConfluenceHibernateMetrics
implements MeterBinder {
    private final SessionFactoryImplementor sessionFactory;

    public ConfluenceHibernateMetrics(SessionFactoryImplementor sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void bindTo(MeterRegistry registry) {
        this.getBinders().forEach(binder -> binder.bindTo(registry));
    }

    private Stream<? extends MeterBinder> getBinders() {
        return Stream.of(HibernateL2CacheMetrics.getBinders((SessionFactory)this.sessionFactory), HibernateEntityMetrics.getBinders(this.sessionFactory), HibernateCollectionMetrics.getBinders(this.sessionFactory)).flatMap(Function.identity());
    }
}

