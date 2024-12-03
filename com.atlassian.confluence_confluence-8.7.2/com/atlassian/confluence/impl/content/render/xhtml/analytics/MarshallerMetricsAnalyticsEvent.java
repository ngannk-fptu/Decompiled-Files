/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.confluence.impl.content.render.xhtml.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.event.api.AsynchronousPreferred;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

@AsynchronousPreferred
public class MarshallerMetricsAnalyticsEvent {
    private final String requestCorrelationId;
    private final boolean asyncRenderSafe;
    private final String contentId;
    private final String containerId;
    private final String renderContext;
    private final String outputDeviceType;
    private final String contentEntityType;
    private final int executionCount;
    private final String accumulationKey;
    private final String eventName;
    private final long totalExecutionTimeNanos;
    private final long totalStreamingTimeNanos;
    private final Map<String, Long> customMetrics;

    MarshallerMetricsAnalyticsEvent(String requestCorrelationId, String eventName, String accumulationKey, int executionCount, long totalExecutionTimeNanos, long totalStreamingTimeNanos, Map<String, Long> customMetrics, String contentId, String containerId, String renderContext, String outputDeviceType, String contentEntityType, boolean asyncRenderSafe) {
        this.requestCorrelationId = requestCorrelationId;
        this.asyncRenderSafe = asyncRenderSafe;
        this.contentId = (String)Preconditions.checkNotNull((Object)contentId);
        this.containerId = (String)Preconditions.checkNotNull((Object)containerId);
        this.renderContext = (String)Preconditions.checkNotNull((Object)renderContext);
        this.outputDeviceType = (String)Preconditions.checkNotNull((Object)outputDeviceType);
        this.contentEntityType = (String)Preconditions.checkNotNull((Object)contentEntityType);
        this.accumulationKey = (String)Preconditions.checkNotNull((Object)accumulationKey);
        this.eventName = (String)Preconditions.checkNotNull((Object)eventName);
        this.totalExecutionTimeNanos = totalExecutionTimeNanos;
        this.totalStreamingTimeNanos = totalStreamingTimeNanos;
        this.executionCount = executionCount;
        this.customMetrics = ImmutableMap.copyOf(customMetrics);
        Preconditions.checkArgument((totalExecutionTimeNanos >= 0L ? 1 : 0) != 0);
        Preconditions.checkArgument((totalStreamingTimeNanos >= 0L ? 1 : 0) != 0);
        Preconditions.checkArgument((executionCount >= 0 ? 1 : 0) != 0);
    }

    @EventName
    public String getEventName() {
        return this.eventName;
    }

    public String getContentId() {
        return this.contentId;
    }

    public String getContainerId() {
        return this.containerId;
    }

    public Map<String, Long> getCustomMetrics() {
        return this.customMetrics;
    }

    public String getAccumulationKey() {
        return this.accumulationKey;
    }

    public int getExecutionCount() {
        return this.executionCount;
    }

    public long getTotalExecutionTimeNanos() {
        return this.totalExecutionTimeNanos;
    }

    public long getTotalStreamingTimeNanos() {
        return this.totalStreamingTimeNanos;
    }

    public String getRenderContext() {
        return this.renderContext;
    }

    public String getOutputDeviceType() {
        return this.outputDeviceType;
    }

    public String getContentEntityType() {
        return this.contentEntityType;
    }

    public String getRequestCorrelationId() {
        return this.requestCorrelationId;
    }

    public boolean isAsyncRenderSafe() {
        return this.asyncRenderSafe;
    }
}

