/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.diagnostics.Issue
 *  com.atlassian.diagnostics.PluginDetails
 *  com.atlassian.event.api.AsynchronousPreferred
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.diagnostics.internal.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.diagnostics.Issue;
import com.atlassian.diagnostics.PluginDetails;
import com.atlassian.event.api.AsynchronousPreferred;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@AsynchronousPreferred
@EventName(value="diagnostics.daily.alerts")
public class DailyAlertCountAnalyticsEvent {
    private final long count;
    private final long epochDay;
    private final Issue issue;
    private final Set<String> nodeUuids;
    private final PluginDetails plugin;

    public DailyAlertCountAnalyticsEvent(long epochDay, Issue issue, PluginDetails pluginDetails, Set<String> nodeUuids, long count) {
        this.count = count;
        this.epochDay = epochDay;
        this.issue = Objects.requireNonNull(issue, "issue");
        this.plugin = Objects.requireNonNull(pluginDetails, "pluginDetails");
        this.nodeUuids = ImmutableSet.copyOf((Collection)Objects.requireNonNull(nodeUuids, "nodeUuids"));
    }

    public long getCount() {
        return this.count;
    }

    public long getEpochDay() {
        return this.epochDay;
    }

    @Nonnull
    public String getIssueId() {
        return this.issue.getId();
    }

    @Nonnull
    public String getIssueSeverity() {
        return this.issue.getSeverity().name();
    }

    @Nonnull
    public Set<String> getNodeUuids() {
        return this.nodeUuids;
    }

    @Nonnull
    public String getPluginKey() {
        return this.plugin.getKey();
    }

    @Nullable
    public String getPluginVersion() {
        return this.plugin.getVersion();
    }
}

