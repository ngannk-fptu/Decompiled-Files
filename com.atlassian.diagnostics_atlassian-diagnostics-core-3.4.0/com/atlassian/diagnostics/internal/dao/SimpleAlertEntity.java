/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Alert
 *  com.atlassian.diagnostics.AlertTrigger
 *  com.atlassian.diagnostics.Issue
 *  com.atlassian.diagnostics.Severity
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.dao;

import com.atlassian.diagnostics.Alert;
import com.atlassian.diagnostics.AlertTrigger;
import com.atlassian.diagnostics.Issue;
import com.atlassian.diagnostics.Severity;
import com.atlassian.diagnostics.internal.dao.AlertEntity;
import java.time.Instant;
import javax.annotation.Nonnull;

public class SimpleAlertEntity
implements AlertEntity {
    private final String detailsJson;
    private final long id;
    private final String issueId;
    private final String issueComponentId;
    private final Severity issueSeverity;
    private final String nodeName;
    private final Instant timestamp;
    private final String triggerModule;
    private final String triggerPluginKey;
    private final String triggerPluginVersion;

    public SimpleAlertEntity(Alert alert) {
        this(alert, alert.getId());
    }

    public SimpleAlertEntity(Alert alert, long id) {
        String json;
        this.id = id;
        Issue issue = alert.getIssue();
        try {
            json = alert.getDetails().map(details -> issue.getJsonMapper().toJson(details)).orElse(null);
        }
        catch (Exception e) {
            json = null;
        }
        this.detailsJson = json;
        this.issueId = issue.getId();
        this.issueComponentId = issue.getComponent().getId();
        this.issueSeverity = issue.getSeverity();
        this.nodeName = alert.getNodeName();
        this.timestamp = alert.getTimestamp();
        AlertTrigger trigger = alert.getTrigger();
        this.triggerModule = trigger.getModule().orElse(null);
        this.triggerPluginKey = trigger.getPluginKey();
        this.triggerPluginVersion = trigger.getPluginVersion().orElse(null);
    }

    @Override
    public String getDetailsJson() {
        return this.detailsJson;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    @Nonnull
    public String getIssueId() {
        return this.issueId;
    }

    @Override
    @Nonnull
    public String getIssueComponentId() {
        return this.issueComponentId;
    }

    @Override
    @Nonnull
    public Severity getIssueSeverity() {
        return this.issueSeverity;
    }

    @Override
    @Nonnull
    public String getNodeName() {
        return this.nodeName;
    }

    @Override
    @Nonnull
    public Instant getTimestamp() {
        return this.timestamp;
    }

    @Override
    public String getTriggerModule() {
        return this.triggerModule;
    }

    @Override
    @Nonnull
    public String getTriggerPluginKey() {
        return this.triggerPluginKey;
    }

    @Override
    public String getTriggerPluginVersion() {
        return this.triggerPluginVersion;
    }
}

