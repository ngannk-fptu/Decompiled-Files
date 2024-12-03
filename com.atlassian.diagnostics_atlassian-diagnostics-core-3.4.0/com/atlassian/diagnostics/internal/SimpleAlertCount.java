/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.AlertCount
 *  com.atlassian.diagnostics.Issue
 *  com.atlassian.diagnostics.PluginDetails
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal;

import com.atlassian.diagnostics.AlertCount;
import com.atlassian.diagnostics.Issue;
import com.atlassian.diagnostics.PluginDetails;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;

public class SimpleAlertCount
implements AlertCount {
    private final Issue issue;
    private final Map<String, Long> countsByNodeName;
    private final PluginDetails pluginDetails;

    private SimpleAlertCount(Builder builder) {
        this.countsByNodeName = ImmutableMap.copyOf((Map)builder.countsByNodeName);
        this.issue = builder.issue;
        this.pluginDetails = builder.pluginDetails;
    }

    @Nonnull
    public Map<String, Long> getCountsByNodeName() {
        return this.countsByNodeName;
    }

    @Nonnull
    public Issue getIssue() {
        return this.issue;
    }

    @Nonnull
    public PluginDetails getPlugin() {
        return this.pluginDetails;
    }

    public long getTotalCount() {
        return this.countsByNodeName.values().stream().mapToLong(Long::longValue).sum();
    }

    public static class Builder {
        private final Issue issue;
        private final PluginDetails pluginDetails;
        private final Map<String, Long> countsByNodeName;

        public Builder(@Nonnull Issue issue, @Nonnull PluginDetails plugin) {
            this.issue = Objects.requireNonNull(issue, "issue");
            this.pluginDetails = Objects.requireNonNull(plugin, "plugin");
            this.countsByNodeName = new HashMap<String, Long>();
        }

        @Nonnull
        public SimpleAlertCount build() {
            return new SimpleAlertCount(this);
        }

        @Nonnull
        public Builder setCountForNode(@Nonnull String nodeName, long count) {
            this.countsByNodeName.put(nodeName, count);
            return this;
        }
    }
}

