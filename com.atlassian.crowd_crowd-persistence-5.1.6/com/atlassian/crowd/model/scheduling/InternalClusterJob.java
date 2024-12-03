/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model.scheduling;

import java.util.Date;

public class InternalClusterJob {
    private String id;
    private String runnerKey;
    private String timeZone;
    private String cronExpression;
    private Long interval;
    private Date nextRunTime;
    private long version;
    private byte[] rawParameters;
    private String claimNodeId;
    private Date claimTime;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRunnerKey() {
        return this.runnerKey;
    }

    public void setRunnerKey(String runnerKey) {
        this.runnerKey = runnerKey;
    }

    public String getTimeZone() {
        return this.timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getCronExpression() {
        return this.cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public Long getInterval() {
        return this.interval;
    }

    public void setInterval(Long interval) {
        this.interval = interval;
    }

    public Date getNextRunTime() {
        return this.nextRunTime;
    }

    public void setNextRunTime(Date nextRunDate) {
        this.nextRunTime = nextRunDate;
    }

    public Long getNextRunTimestamp() {
        return this.nextRunTime == null ? null : Long.valueOf(this.nextRunTime.getTime());
    }

    public void setNextRunTimestamp(Long millisTimestamp) {
        this.nextRunTime = millisTimestamp == null ? null : new Date(millisTimestamp);
    }

    public long getVersion() {
        return this.version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public byte[] getRawParameters() {
        return this.rawParameters;
    }

    public void setRawParameters(byte[] rawParameters) {
        this.rawParameters = rawParameters;
    }

    public String getClaimNodeId() {
        return this.claimNodeId;
    }

    public void setClaimNodeId(String claimNodeId) {
        this.claimNodeId = claimNodeId;
    }

    public Long getClaimTimestamp() {
        return this.claimTime == null ? null : Long.valueOf(this.claimTime.getTime());
    }

    public void setClaimTimestamp(Long millisTimestamp) {
        this.claimTime = millisTimestamp == null ? null : new Date(millisTimestamp);
    }

    public Date getClaimTime() {
        return this.claimTime;
    }

    public void setClaimTime(Date claimTime) {
        this.claimTime = claimTime;
    }
}

