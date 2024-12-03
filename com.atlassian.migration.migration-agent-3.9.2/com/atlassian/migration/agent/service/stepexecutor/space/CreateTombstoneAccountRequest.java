/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.stepexecutor.space;

import com.atlassian.migration.agent.service.stepexecutor.space.CloudSiteInfo;
import lombok.Generated;

public class CreateTombstoneAccountRequest {
    private String sen;
    private CloudSiteInfo site;

    @Generated
    public String getSen() {
        return this.sen;
    }

    @Generated
    public CloudSiteInfo getSite() {
        return this.site;
    }

    @Generated
    public void setSen(String sen) {
        this.sen = sen;
    }

    @Generated
    public void setSite(CloudSiteInfo site) {
        this.site = site;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CreateTombstoneAccountRequest)) {
            return false;
        }
        CreateTombstoneAccountRequest other = (CreateTombstoneAccountRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$sen = this.getSen();
        String other$sen = other.getSen();
        if (this$sen == null ? other$sen != null : !this$sen.equals(other$sen)) {
            return false;
        }
        CloudSiteInfo this$site = this.getSite();
        CloudSiteInfo other$site = other.getSite();
        return !(this$site == null ? other$site != null : !((Object)this$site).equals(other$site));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof CreateTombstoneAccountRequest;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $sen = this.getSen();
        result = result * 59 + ($sen == null ? 43 : $sen.hashCode());
        CloudSiteInfo $site = this.getSite();
        result = result * 59 + ($site == null ? 43 : ((Object)$site).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "CreateTombstoneAccountRequest(sen=" + this.getSen() + ", site=" + this.getSite() + ")";
    }

    @Generated
    public CreateTombstoneAccountRequest(String sen, CloudSiteInfo site) {
        this.sen = sen;
        this.site = site;
    }
}

