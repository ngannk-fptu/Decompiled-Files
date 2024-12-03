/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Severity
 *  com.google.common.base.MoreObjects
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.diagnostics.internal.dao;

import com.atlassian.diagnostics.Severity;
import com.google.common.base.MoreObjects;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

public class AlertMetric {
    private final long count;
    private final String issueId;
    private final Severity issueSeverity;
    private final String nodeName;
    private final String pluginKey;
    private final String pluginVersion;

    public AlertMetric(@Nonnull String issueId, @Nonnull Severity severity, String pluginKey, String pluginVersion, @Nonnull String nodeName, long count) {
        this.count = count;
        this.issueId = Objects.requireNonNull(issueId, "issueId");
        this.issueSeverity = Objects.requireNonNull(severity, "severity");
        this.nodeName = Objects.requireNonNull(nodeName, "nodeName");
        this.pluginKey = (String)MoreObjects.firstNonNull((Object)StringUtils.trimToNull((String)pluginKey), (Object)"not-detected");
        this.pluginVersion = pluginVersion;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AlertMetric metric = (AlertMetric)o;
        return this.count == metric.count && Objects.equals(this.issueId, metric.issueId) && Objects.equals(this.nodeName, metric.nodeName) && Objects.equals(this.pluginKey, metric.pluginKey) && Objects.equals(this.pluginVersion, metric.pluginVersion);
    }

    public long getCount() {
        return this.count;
    }

    @Nonnull
    public String getIssueId() {
        return this.issueId;
    }

    @Nonnull
    public Severity getIssueSeverity() {
        return this.issueSeverity;
    }

    @Nonnull
    public String getNodeName() {
        return this.nodeName;
    }

    public int hashCode() {
        return Objects.hash(this.issueId, this.nodeName, this.pluginKey, this.pluginVersion, this.count);
    }

    @Nonnull
    public String getPluginKey() {
        return this.pluginKey;
    }

    public String getPluginVersion() {
        return this.pluginVersion;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("issueId", (Object)this.issueId).add("issueSeverity", (Object)this.issueSeverity).add("nodeName", (Object)this.nodeName).add("pluginKey", (Object)this.pluginKey).add("pluginVersion", (Object)this.pluginVersion).add("count", this.count).toString();
    }
}

