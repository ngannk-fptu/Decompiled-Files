/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.dao;

import com.atlassian.diagnostics.internal.dao.MinimalAlertEntity;
import java.time.Instant;
import java.util.Objects;
import javax.annotation.Nonnull;

public class SimpleMinimalAlertEntity
implements MinimalAlertEntity {
    private final int detailsLength;
    private final long id;
    private final String issueId;
    private final String nodeName;
    private final Instant timestamp;
    private final String triggerPluginKey;

    public SimpleMinimalAlertEntity(long id, long timestamp, String issueId, String triggerPluginKey, String nodeName, int detailsLength) {
        this.detailsLength = detailsLength;
        this.id = id;
        this.issueId = Objects.requireNonNull(issueId, "issueId");
        this.nodeName = Objects.requireNonNull(nodeName, "nodeName");
        this.timestamp = Instant.ofEpochMilli(timestamp);
        this.triggerPluginKey = Objects.requireNonNull(triggerPluginKey, "pluginKey");
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SimpleMinimalAlertEntity that = (SimpleMinimalAlertEntity)o;
        return this.detailsLength == that.detailsLength && this.id == that.id && Objects.equals(this.issueId, that.issueId) && Objects.equals(this.nodeName, that.nodeName) && Objects.equals(this.triggerPluginKey, that.triggerPluginKey) && Objects.equals(this.timestamp, that.timestamp);
    }

    @Override
    public int getDetailsJsonLength() {
        return this.detailsLength;
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
    public String getNodeName() {
        return this.nodeName;
    }

    @Override
    @Nonnull
    public Instant getTimestamp() {
        return this.timestamp;
    }

    @Override
    @Nonnull
    public String getTriggerPluginKey() {
        return this.triggerPluginKey;
    }

    public int hashCode() {
        return Objects.hash(this.detailsLength, this.id, this.issueId, this.nodeName, this.triggerPluginKey, this.timestamp);
    }
}

