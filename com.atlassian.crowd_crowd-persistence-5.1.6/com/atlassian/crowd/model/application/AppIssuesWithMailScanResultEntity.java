/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model.application;

import java.time.Instant;
import java.util.Objects;

public class AppIssuesWithMailScanResultEntity {
    private long appId;
    private Instant scanTimestamp;
    private long invalidEmailsCount;
    private long duplicatedEmailsCount;

    public AppIssuesWithMailScanResultEntity() {
    }

    public AppIssuesWithMailScanResultEntity(long appId, Instant scanTimestamp, long invalidEmailsCount, long duplicatedEmailsCount) {
        this.appId = appId;
        this.scanTimestamp = scanTimestamp;
        this.invalidEmailsCount = invalidEmailsCount;
        this.duplicatedEmailsCount = duplicatedEmailsCount;
    }

    public long getAppId() {
        return this.appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    public Instant getScanTimestamp() {
        return this.scanTimestamp;
    }

    public void setScanTimestamp(Instant scanTimestamp) {
        this.scanTimestamp = scanTimestamp;
    }

    public long getInvalidEmailsCount() {
        return this.invalidEmailsCount;
    }

    public void setInvalidEmailsCount(long invalidEmailsCount) {
        this.invalidEmailsCount = invalidEmailsCount;
    }

    public long getDuplicatedEmailsCount() {
        return this.duplicatedEmailsCount;
    }

    public void setDuplicatedEmailsCount(long duplicatedEmailsCount) {
        this.duplicatedEmailsCount = duplicatedEmailsCount;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AppIssuesWithMailScanResultEntity that = (AppIssuesWithMailScanResultEntity)o;
        return this.appId == that.appId && this.invalidEmailsCount == that.invalidEmailsCount && this.duplicatedEmailsCount == that.duplicatedEmailsCount && this.scanTimestamp.getEpochSecond() == that.scanTimestamp.getEpochSecond();
    }

    public int hashCode() {
        return Objects.hash(this.appId, this.scanTimestamp.getEpochSecond(), this.invalidEmailsCount, this.duplicatedEmailsCount);
    }
}

