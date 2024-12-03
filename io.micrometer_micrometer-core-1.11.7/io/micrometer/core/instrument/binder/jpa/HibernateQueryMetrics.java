/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 *  org.hibernate.SessionFactory
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.event.service.spi.EventListenerRegistry
 *  org.hibernate.event.spi.EventType
 *  org.hibernate.event.spi.PostLoadEvent
 *  org.hibernate.event.spi.PostLoadEventListener
 *  org.hibernate.stat.QueryStatistics
 *  org.hibernate.stat.Statistics
 */
package io.micrometer.core.instrument.binder.jpa;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.binder.MeterBinder;
import java.util.concurrent.TimeUnit;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostLoadEvent;
import org.hibernate.event.spi.PostLoadEventListener;
import org.hibernate.stat.QueryStatistics;
import org.hibernate.stat.Statistics;

@NonNullApi
@NonNullFields
@Deprecated
public class HibernateQueryMetrics
implements MeterBinder {
    private static final String SESSION_FACTORY_TAG_NAME = "entityManagerFactory";
    private final Iterable<Tag> tags;
    private final SessionFactory sessionFactory;

    public static void monitor(MeterRegistry registry, SessionFactory sessionFactory, String sessionFactoryName, String ... tags) {
        HibernateQueryMetrics.monitor(registry, sessionFactory, sessionFactoryName, Tags.of(tags));
    }

    public static void monitor(MeterRegistry registry, SessionFactory sessionFactory, String sessionFactoryName, Iterable<Tag> tags) {
        new HibernateQueryMetrics(sessionFactory, sessionFactoryName, tags).bindTo(registry);
    }

    public HibernateQueryMetrics(SessionFactory sessionFactory, String sessionFactoryName, Iterable<Tag> tags) {
        this.tags = Tags.concat(tags, SESSION_FACTORY_TAG_NAME, sessionFactoryName);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void bindTo(MeterRegistry meterRegistry) {
        if (this.sessionFactory instanceof SessionFactoryImplementor) {
            EventListenerRegistry eventListenerRegistry = (EventListenerRegistry)((SessionFactoryImplementor)this.sessionFactory).getServiceRegistry().getService(EventListenerRegistry.class);
            MetricsEventHandler metricsEventHandler = new MetricsEventHandler(meterRegistry);
            eventListenerRegistry.appendListeners(EventType.POST_LOAD, (Object[])new PostLoadEventListener[]{metricsEventHandler});
        }
    }

    class MetricsEventHandler
    implements PostLoadEventListener {
        private final MeterRegistry meterRegistry;

        MetricsEventHandler(MeterRegistry meterRegistry) {
            this.meterRegistry = meterRegistry;
        }

        public void onPostLoad(PostLoadEvent event) {
            this.registerQueryMetric((Statistics)event.getSession().getFactory().getStatistics());
        }

        void registerQueryMetric(Statistics statistics) {
            for (String query : statistics.getQueries()) {
                QueryStatistics queryStatistics = statistics.getQueryStatistics(query);
                FunctionCounter.builder("hibernate.query.cache.requests", queryStatistics, QueryStatistics::getCacheHitCount).tags(HibernateQueryMetrics.this.tags).tags("result", "hit", "query", query).description("Number of query cache hits").register(this.meterRegistry);
                FunctionCounter.builder("hibernate.query.cache.requests", queryStatistics, QueryStatistics::getCacheMissCount).tags(HibernateQueryMetrics.this.tags).tags("result", "miss", "query", query).description("Number of query cache misses").register(this.meterRegistry);
                FunctionCounter.builder("hibernate.query.cache.puts", queryStatistics, QueryStatistics::getCachePutCount).tags(HibernateQueryMetrics.this.tags).tags("query", query).description("Number of cache puts for a query").register(this.meterRegistry);
                FunctionTimer.builder("hibernate.query.execution.total", queryStatistics, QueryStatistics::getExecutionCount, QueryStatistics::getExecutionTotalTime, TimeUnit.MILLISECONDS).tags(HibernateQueryMetrics.this.tags).tags("query", query).description("Query executions").register(this.meterRegistry);
                TimeGauge.builder("hibernate.query.execution.max", queryStatistics, TimeUnit.MILLISECONDS, QueryStatistics::getExecutionMaxTime).tags(HibernateQueryMetrics.this.tags).tags("query", query).description("Query maximum execution time").register(this.meterRegistry);
                TimeGauge.builder("hibernate.query.execution.min", queryStatistics, TimeUnit.MILLISECONDS, QueryStatistics::getExecutionMinTime).tags(HibernateQueryMetrics.this.tags).tags("query", query).description("Query minimum execution time").register(this.meterRegistry);
                FunctionCounter.builder("hibernate.query.execution.rows", queryStatistics, QueryStatistics::getExecutionRowCount).tags(HibernateQueryMetrics.this.tags).tags("query", query).description("Number of rows processed for a query").register(this.meterRegistry);
            }
        }
    }
}

