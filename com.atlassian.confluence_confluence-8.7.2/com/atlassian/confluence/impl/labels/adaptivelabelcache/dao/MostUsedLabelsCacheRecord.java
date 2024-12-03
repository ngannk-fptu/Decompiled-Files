/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.labels.adaptivelabelcache.dao;

import com.atlassian.confluence.core.NotExportable;

public class MostUsedLabelsCacheRecord
implements NotExportable {
    static final int EXPECTED_VERSION = 1;
    private long spaceId;
    private long requestTs;
    private long expirationTs;
    private int requestLimit;
    private String labels;
    private int version;

    public long getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(long spaceId) {
        this.spaceId = spaceId;
    }

    public long getExpirationTs() {
        return this.expirationTs;
    }

    public void setExpirationTs(long expirationTs) {
        this.expirationTs = expirationTs;
    }

    public String getLabels() {
        return this.labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public int getRequestLimit() {
        return this.requestLimit;
    }

    public void setRequestLimit(int requestLimit) {
        this.requestLimit = requestLimit;
    }

    public long getRequestTs() {
        return this.requestTs;
    }

    public void setRequestTs(long requestTs) {
        this.requestTs = requestTs;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}

