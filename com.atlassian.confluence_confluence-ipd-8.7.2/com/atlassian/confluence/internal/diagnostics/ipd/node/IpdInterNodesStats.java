/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.internal.ipd.IpdMainRegistry
 *  com.atlassian.diagnostics.internal.ipd.IpdMetricRegistry
 *  com.atlassian.diagnostics.internal.ipd.metrics.IpdCustomMetric
 *  com.atlassian.diagnostics.internal.ipd.metrics.IpdStatsMetric
 *  com.atlassian.util.profiling.MetricTag
 *  com.atlassian.util.profiling.MetricTag$RequiredMetricTag
 */
package com.atlassian.confluence.internal.diagnostics.ipd.node;

import com.atlassian.confluence.internal.diagnostics.ipd.metric.type.IpdConnectionStateType;
import com.atlassian.diagnostics.internal.ipd.IpdMainRegistry;
import com.atlassian.diagnostics.internal.ipd.IpdMetricRegistry;
import com.atlassian.diagnostics.internal.ipd.metrics.IpdCustomMetric;
import com.atlassian.diagnostics.internal.ipd.metrics.IpdStatsMetric;
import com.atlassian.util.profiling.MetricTag;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class IpdInterNodesStats {
    private static final String DEST_NODE = "destNode";
    private static final String REGISTRY_PREFIX = "node";
    private final IpdMetricRegistry metricRegistry;

    public IpdInterNodesStats(IpdMainRegistry mainRegistry) {
        this.metricRegistry = Objects.requireNonNull(mainRegistry).createRegistry(b -> b.withPrefix(REGISTRY_PREFIX).asWorkInProgress());
    }

    public void setNodeDisconnected(String nodeId) {
        this.connectionMetric(nodeId).update(b -> b.setConnected(false));
        this.latencyMetric(nodeId).unregisterJmx();
    }

    public void updateNodeLatency(String nodeId, long latencyNs) {
        this.connectionMetric(nodeId).update(b -> b.setConnected(true));
        this.latencyMetric(nodeId).update(Long.valueOf(latencyNs), TimeUnit.NANOSECONDS);
    }

    public void remainMetricsForNodes(Set<String> nodesIds) {
        this.metricRegistry.removeIf(metric -> !nodesIds.contains(metric.getObjectName().getKeyProperty("tag.destNode")));
    }

    private IpdStatsMetric latencyMetric(String nodeId) {
        return this.metricRegistry.statsMetric("latency", new MetricTag.RequiredMetricTag[]{MetricTag.of((String)DEST_NODE, (String)nodeId)});
    }

    private IpdCustomMetric<IpdConnectionStateType> connectionMetric(String nodeId) {
        return this.metricRegistry.customMetric("connection.state", IpdConnectionStateType.class, new MetricTag.RequiredMetricTag[]{MetricTag.of((String)DEST_NODE, (String)nodeId)});
    }
}

