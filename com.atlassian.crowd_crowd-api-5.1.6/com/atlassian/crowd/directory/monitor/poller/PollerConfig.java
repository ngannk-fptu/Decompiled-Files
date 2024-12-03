/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.monitor.poller;

import java.io.Serializable;
import java.util.Map;

public class PollerConfig
implements Serializable {
    private static final long serialVersionUID = 569229678073217022L;
    private String cronExpression = "0 0 * ? * *";
    private String synchronisationType = "pollingInterval";
    private long pollingIntervalInMin = 60L;

    public void copyFrom(Map<String, String> attributes) {
        String cacheSynchroniseInterval = attributes.get("directory.cache.synchronise.interval");
        this.cronExpression = attributes.get("directory.cache.synchronise.cron");
        this.synchronisationType = attributes.get("directory.cache.synchronise.type");
        this.pollingIntervalInMin = cacheSynchroniseInterval == null ? 0L : Long.parseLong(cacheSynchroniseInterval) / 60L;
    }

    public String getCronExpression() {
        return this.cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getSynchronisationType() {
        return this.synchronisationType;
    }

    public void setSynchronisationType(String synchronisationType) {
        this.synchronisationType = synchronisationType;
    }

    public long getPollingIntervalInMin() {
        return this.pollingIntervalInMin;
    }

    public void setPollingIntervalInMin(long pollingIntervalInMin) {
        this.pollingIntervalInMin = pollingIntervalInMin;
    }

    public void copyTo(Map<String, String> attributes) {
        attributes.put("directory.cache.synchronise.interval", Long.toString(this.pollingIntervalInMin * 60L));
        attributes.put("directory.cache.synchronise.cron", this.cronExpression);
        attributes.put("directory.cache.synchronise.type", this.synchronisationType);
    }
}

