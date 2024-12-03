/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.AlertCount
 *  com.atlassian.diagnostics.Issue
 *  com.atlassian.diagnostics.PluginDetails
 */
package com.atlassian.diagnostics.internal;

import com.atlassian.diagnostics.AlertCount;
import com.atlassian.diagnostics.Issue;
import com.atlassian.diagnostics.PluginDetails;
import com.atlassian.diagnostics.internal.IssueSupplier;
import com.atlassian.diagnostics.internal.PluginDetailsSupplier;
import com.atlassian.diagnostics.internal.SimpleAlertCount;
import com.atlassian.diagnostics.internal.dao.AlertMetric;
import java.util.Objects;

class AlertCountCollector {
    private final IssueSupplier issueSupplier;
    private final PluginDetailsSupplier pluginSupplier;
    private volatile SimpleAlertCount.Builder builder;
    private volatile AlertMetric prevRow;

    AlertCountCollector(IssueSupplier issueSupplier, PluginDetailsSupplier pluginSupplier) {
        this.issueSupplier = issueSupplier;
        this.pluginSupplier = pluginSupplier;
    }

    AlertCount onRow(AlertMetric row) {
        AlertCount result = null;
        if (this.builder != null && !this.isSameIssueAndPlugin(this.prevRow, row)) {
            result = this.maybeEmit();
        }
        if (this.builder == null) {
            Issue issue = this.issueSupplier.getIssue(row.getIssueId(), row.getIssueSeverity());
            PluginDetails plugin = this.pluginSupplier.getPluginDetails(row.getPluginKey(), row.getPluginVersion());
            this.builder = new SimpleAlertCount.Builder(issue, plugin);
        }
        this.builder.setCountForNode(row.getNodeName(), row.getCount());
        this.prevRow = row;
        return result;
    }

    AlertCount onEnd() {
        return this.maybeEmit();
    }

    private AlertCount maybeEmit() {
        if (this.builder != null) {
            SimpleAlertCount result = this.builder.build();
            this.builder = null;
            return result;
        }
        return null;
    }

    private boolean isSameIssueAndPlugin(AlertMetric metric1, AlertMetric metric2) {
        return Objects.equals(metric1.getIssueId(), metric2.getIssueId()) && Objects.equals(metric1.getPluginKey(), metric2.getPluginKey()) && Objects.equals(metric1.getPluginVersion(), metric2.getPluginVersion());
    }
}

