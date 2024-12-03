/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.content.render.xhtml.analytics;

import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetrics;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MetricsOperations {
    private static final Logger log = LoggerFactory.getLogger(MetricsOperations.class);

    MetricsOperations() {
    }

    static @NonNull MarshallerMetrics add(MarshallerMetrics lhs, MarshallerMetrics rhs) {
        Preconditions.checkArgument((boolean)lhs.getAccumulationKey().equals(rhs.getAccumulationKey()), (Object)"Accumulation keys do not match");
        return new MarshallerMetrics(lhs.getAccumulationKey(), lhs.getExecutionCount() + rhs.getExecutionCount(), lhs.getCumulativeExecutionTimeNanos() + rhs.getCumulativeExecutionTimeNanos(), lhs.getCumulativeStreamingTimeNanos() + rhs.getCumulativeStreamingTimeNanos(), MetricsOperations.add(lhs.getCustomMetrics(), rhs.getCustomMetrics()));
    }

    private static Map<String, Long> add(Map<String, Long> lhs, Map<String, Long> rhs) {
        Sets.SetView commonMetricNames = Sets.intersection(lhs.keySet(), rhs.keySet());
        HashMap combinedCustomMetrics = Maps.newHashMap();
        for (String metricName : commonMetricNames) {
            Long thisValue = lhs.get(metricName);
            Long otherValue = rhs.get(metricName);
            combinedCustomMetrics.put(metricName, thisValue + otherValue);
        }
        MetricsOperations.warnIfSetsDiffer((Set<String>)commonMetricNames, lhs.keySet());
        MetricsOperations.warnIfSetsDiffer((Set<String>)commonMetricNames, rhs.keySet());
        return combinedCustomMetrics;
    }

    private static void warnIfSetsDiffer(Set<String> commonMetricNames, Set<String> inputMetricNames) {
        if (inputMetricNames.size() != commonMetricNames.size()) {
            log.warn("Found some custom metrics in one set but not the other {}. These metrics will not be published", (Object)Sets.difference(inputMetricNames, commonMetricNames));
        }
    }
}

