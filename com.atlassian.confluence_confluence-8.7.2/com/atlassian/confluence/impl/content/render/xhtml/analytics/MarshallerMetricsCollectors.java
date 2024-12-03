/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Maybe
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.content.render.xhtml.analytics;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsAccumulationKey;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsAccumulator;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsAccumulatorStack;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsCollector;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.NoOpMetricsCollector;
import com.atlassian.fugue.Maybe;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarshallerMetricsCollectors {
    private static final Logger log = LoggerFactory.getLogger(MarshallerMetricsCollectors.class);

    public static @NonNull MarshallerMetricsCollector metricsCollector(ConversionContext context, MarshallerMetricsAccumulationKey accumulationKey) {
        Maybe<MarshallerMetricsAccumulator> accumulator = MarshallerMetricsAccumulatorStack.getCurrentMetricsAccumulator(context);
        if (accumulator.isEmpty()) {
            log.debug("MetricsCollectorFactory stack is empty, returning no-op collector");
            return NoOpMetricsCollector.INSTANCE;
        }
        log.debug("Returning new metrics collector from the factory at the top of the stack");
        return ((MarshallerMetricsAccumulator)accumulator.get()).newMetricsCollector(accumulationKey);
    }
}

