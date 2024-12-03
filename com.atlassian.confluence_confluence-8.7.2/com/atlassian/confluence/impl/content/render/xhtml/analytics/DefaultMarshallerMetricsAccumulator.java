/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Effect
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Ticker
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  net.jcip.annotations.ThreadSafe
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.content.render.xhtml.analytics;

import com.atlassian.confluence.impl.content.render.xhtml.analytics.DeferredMetricsCollector;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetrics;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsAccumulationKey;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsAccumulator;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsCollector;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MetricsOperations;
import com.atlassian.fugue.Effect;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Ticker;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import net.jcip.annotations.ThreadSafe;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ThreadSafe
public class DefaultMarshallerMetricsAccumulator
implements MarshallerMetricsAccumulator {
    private static final Logger log = LoggerFactory.getLogger(DefaultMarshallerMetricsAccumulator.class);
    private final ConcurrentMap<MarshallerMetricsAccumulationKey, MarshallerMetrics> accumulations = Maps.newConcurrentMap();
    private final Ticker ticker;

    public DefaultMarshallerMetricsAccumulator() {
        this(Ticker.systemTicker());
    }

    @VisibleForTesting
    DefaultMarshallerMetricsAccumulator(Ticker ticker) {
        this.ticker = (Ticker)Preconditions.checkNotNull((Object)ticker);
    }

    @Override
    public @NonNull MarshallerMetricsCollector newMetricsCollector(MarshallerMetricsAccumulationKey accumulationKey) {
        return new DeferredMetricsCollector(accumulationKey, this.ticker, (Effect<MarshallerMetrics>)((Effect)this::accumulate));
    }

    private void accumulate(MarshallerMetrics newMetrics) {
        MarshallerMetricsAccumulationKey accumulationKey = newMetrics.getAccumulationKey();
        MarshallerMetrics existingMetrics = (MarshallerMetrics)this.accumulations.get(accumulationKey);
        if (existingMetrics != null) {
            log.debug("Existing metrics found for key [{}], attemting to replace them with combined metrics", (Object)accumulationKey);
            if (!this.accumulations.replace(accumulationKey, existingMetrics, MetricsOperations.add(existingMetrics, newMetrics))) {
                log.debug("Metrics replacement failed for key [{}], re-trying", (Object)accumulationKey);
                this.accumulate(newMetrics);
            }
        } else {
            log.debug("No existing metrics found for key [{}], attemting to store new ones", (Object)accumulationKey);
            if (this.accumulations.putIfAbsent(accumulationKey, newMetrics) != null) {
                log.debug("New metrics store failed fpr key [{}], re-trying", (Object)accumulationKey);
                this.accumulate(newMetrics);
            }
        }
    }

    public @NonNull List<MarshallerMetrics> getMetricsSnapshots(Predicate<? super MarshallerMetricsAccumulationKey> accumulationKeyFilter) {
        return ImmutableList.copyOf(Maps.filterKeys(this.accumulations, accumulationKeyFilter).values());
    }
}

