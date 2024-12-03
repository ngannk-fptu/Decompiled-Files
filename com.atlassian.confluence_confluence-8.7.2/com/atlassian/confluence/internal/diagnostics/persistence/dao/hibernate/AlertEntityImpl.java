/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Alert
 *  com.atlassian.diagnostics.AlertTrigger
 *  com.atlassian.diagnostics.Issue
 *  com.atlassian.diagnostics.Severity
 *  com.atlassian.diagnostics.internal.dao.AlertEntity
 *  com.google.common.base.MoreObjects
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.diagnostics.persistence.dao.hibernate;

import com.atlassian.diagnostics.Alert;
import com.atlassian.diagnostics.AlertTrigger;
import com.atlassian.diagnostics.Issue;
import com.atlassian.diagnostics.Severity;
import com.atlassian.diagnostics.internal.dao.AlertEntity;
import com.google.common.base.MoreObjects;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlertEntityImpl
implements AlertEntity {
    private static final Logger log = LoggerFactory.getLogger(AlertEntityImpl.class);
    private String detailsJson;
    private long id;
    private String issueId;
    private String issueComponentId;
    private Severity issueSeverity;
    private String nodeName;
    private String nodeNameLower;
    private String triggerModule;
    private String triggerPluginKey;
    private String triggerPluginKeyLower;
    private String triggerPluginVersion;
    private long timestampUtc;

    protected AlertEntityImpl() {
    }

    public AlertEntityImpl(Alert alert) {
        Issue issue = Objects.requireNonNull(alert, "alert").getIssue();
        AlertTrigger trigger = alert.getTrigger();
        this.detailsJson = alert.getDetails().map(details -> this.generateJson(details, issue)).orElse(null);
        this.id = 0L;
        this.issueComponentId = Objects.requireNonNull(StringUtils.trimToNull((String)Objects.requireNonNull(issue.getComponent(), "issueComponent").getId()), "issueComponentId");
        this.issueId = Objects.requireNonNull(StringUtils.trimToNull((String)issue.getId()), "issueId");
        this.issueSeverity = Objects.requireNonNull(issue.getSeverity(), "issueSeverity");
        this.nodeName = Objects.requireNonNull(StringUtils.trimToNull((String)alert.getNodeName()), "nodeName");
        this.nodeNameLower = StringUtils.lowerCase((String)this.nodeName, (Locale)Locale.ROOT);
        this.timestampUtc = Objects.requireNonNull(alert.getTimestamp(), "timestamp").toEpochMilli();
        this.triggerModule = trigger.getModule().map(StringUtils::trimToNull).orElse(null);
        this.triggerPluginKey = (String)MoreObjects.firstNonNull((Object)StringUtils.trimToNull((String)trigger.getPluginKey()), (Object)"not-detected");
        this.triggerPluginKeyLower = this.triggerPluginKey.toLowerCase(Locale.ROOT);
        this.triggerPluginVersion = trigger.getPluginVersion().map(StringUtils::trimToNull).orElse(null);
    }

    public @Nullable String getDetailsJson() {
        return this.detailsJson;
    }

    public long getId() {
        return this.id;
    }

    public @NonNull String getIssueId() {
        return this.issueId;
    }

    public @NonNull String getIssueComponentId() {
        return this.issueComponentId;
    }

    public @NonNull Severity getIssueSeverity() {
        return this.issueSeverity;
    }

    public @NonNull String getNodeName() {
        return this.nodeName;
    }

    public @Nullable String getTriggerModule() {
        return this.triggerModule;
    }

    public @NonNull String getTriggerPluginKey() {
        return this.triggerPluginKey;
    }

    public @Nullable String getTriggerPluginVersion() {
        return this.triggerPluginVersion;
    }

    public @NonNull Instant getTimestamp() {
        return Instant.ofEpochMilli(this.timestampUtc);
    }

    private String generateJson(Object details, Issue issue) {
        try {
            return Optional.ofNullable(details).map(d -> issue.getJsonMapper().toJson(d)).map(StringUtils::stripToNull).orElse(null);
        }
        catch (Exception e) {
            log.warn("Failed to store as JSON the details of an alert for issue {}", (Object)issue.getId());
            return null;
        }
    }

    private String getNodeNameLower() {
        return this.nodeNameLower;
    }

    private long getTimestampUtc() {
        return this.timestampUtc;
    }

    private String getTriggerPluginKeyLower() {
        return this.triggerPluginKeyLower;
    }

    private void setDetailsJson(String detailsJson) {
        this.detailsJson = detailsJson;
    }

    private void setId(long id) {
        this.id = id;
    }

    private void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    private void setIssueComponentId(String issueComponentId) {
        this.issueComponentId = issueComponentId;
    }

    private void setIssueSeverity(Severity issueSeverity) {
        this.issueSeverity = issueSeverity;
    }

    private void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    private void setNodeNameLower(String nodeNameLower) {
        this.nodeNameLower = nodeNameLower;
    }

    private void setTriggerModule(String triggerModule) {
        this.triggerModule = triggerModule;
    }

    private void setTriggerPluginKey(String triggerPluginKey) {
        this.triggerPluginKey = triggerPluginKey;
    }

    private void setTriggerPluginKeyLower(String triggerPluginKeyLower) {
        this.triggerPluginKeyLower = triggerPluginKeyLower;
    }

    private void setTriggerPluginVersion(String triggerPluginVersion) {
        this.triggerPluginVersion = triggerPluginVersion;
    }

    private void setTimestampUtc(long timestamp) {
        this.timestampUtc = timestamp;
    }
}

