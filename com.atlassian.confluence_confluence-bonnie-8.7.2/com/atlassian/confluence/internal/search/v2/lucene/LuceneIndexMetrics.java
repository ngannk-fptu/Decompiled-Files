/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.MetricTimer
 *  com.atlassian.util.profiling.Metrics
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timer
 *  com.atlassian.util.profiling.Timers
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.util.profiling.MetricTimer;
import com.atlassian.util.profiling.Metrics;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timer;
import com.atlassian.util.profiling.Timers;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public class LuceneIndexMetrics {
    private final String indexName;

    public LuceneIndexMetrics(@Nonnull String indexName) {
        this.indexName = indexName;
    }

    public LuceneIndexMetrics() {
        this.indexName = "Others";
    }

    private static Timer toTimer(final MetricTimer metricTimer) {
        return new Timer(){

            @Nonnull
            public Ticker start(String ... callParameters) {
                return metricTimer.start();
            }

            @Nonnull
            public Ticker start(Object ... callParameters) {
                return metricTimer.start();
            }
        };
    }

    public Timer timer(String ... names) {
        return Timers.concat((Timer[])((Timer[])Stream.of(names).map(this::metricName).map(Metrics::timer).map(LuceneIndexMetrics::toTimer).toArray(Timer[]::new)));
    }

    private String metricName(String name) {
        return this.indexName.isEmpty() ? name : this.indexName + "." + name;
    }
}

