/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.stepexecutor.space;

import lombok.Generated;

public class CloudSiteInfo {
    private String cloudId;
    private String cloudUrl;

    @Generated
    public String getCloudId() {
        return this.cloudId;
    }

    @Generated
    public String getCloudUrl() {
        return this.cloudUrl;
    }

    @Generated
    public void setCloudId(String cloudId) {
        this.cloudId = cloudId;
    }

    @Generated
    public void setCloudUrl(String cloudUrl) {
        this.cloudUrl = cloudUrl;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CloudSiteInfo)) {
            return false;
        }
        CloudSiteInfo other = (CloudSiteInfo)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$cloudId = this.getCloudId();
        String other$cloudId = other.getCloudId();
        if (this$cloudId == null ? other$cloudId != null : !this$cloudId.equals(other$cloudId)) {
            return false;
        }
        String this$cloudUrl = this.getCloudUrl();
        String other$cloudUrl = other.getCloudUrl();
        return !(this$cloudUrl == null ? other$cloudUrl != null : !this$cloudUrl.equals(other$cloudUrl));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof CloudSiteInfo;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $cloudId = this.getCloudId();
        result = result * 59 + ($cloudId == null ? 43 : $cloudId.hashCode());
        String $cloudUrl = this.getCloudUrl();
        result = result * 59 + ($cloudUrl == null ? 43 : $cloudUrl.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "CloudSiteInfo(cloudId=" + this.getCloudId() + ", cloudUrl=" + this.getCloudUrl() + ")";
    }

    @Generated
    public CloudSiteInfo(String cloudId, String cloudUrl) {
        this.cloudId = cloudId;
        this.cloudUrl = cloudUrl;
    }
}

