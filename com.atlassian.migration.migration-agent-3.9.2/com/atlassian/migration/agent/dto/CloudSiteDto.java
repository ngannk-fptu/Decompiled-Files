/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.domain.Edition
 *  javax.annotation.Nullable
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto;

import com.atlassian.cmpt.domain.Edition;
import com.atlassian.migration.agent.dto.CloudType;
import javax.annotation.Nullable;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class CloudSiteDto {
    @JsonProperty
    private final String cloudUrl;
    @JsonProperty
    private final String cloudId;
    @JsonProperty
    @Nullable
    private final Edition edition;
    @JsonProperty
    private final CloudType cloudType;

    @JsonCreator
    public CloudSiteDto(@JsonProperty(value="cloudUrl") String cloudUrl, @JsonProperty(value="cloudId") String cloudId, @Nullable @JsonProperty(value="edition") Edition edition, @Nullable @JsonProperty(value="cloudType") CloudType cloudType) {
        this.cloudUrl = cloudUrl;
        this.cloudId = cloudId;
        this.edition = edition;
        this.cloudType = cloudType;
    }

    @Generated
    public String getCloudUrl() {
        return this.cloudUrl;
    }

    @Generated
    public String getCloudId() {
        return this.cloudId;
    }

    @Nullable
    @Generated
    public Edition getEdition() {
        return this.edition;
    }

    @Generated
    public CloudType getCloudType() {
        return this.cloudType;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CloudSiteDto)) {
            return false;
        }
        CloudSiteDto other = (CloudSiteDto)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$cloudUrl = this.getCloudUrl();
        String other$cloudUrl = other.getCloudUrl();
        if (this$cloudUrl == null ? other$cloudUrl != null : !this$cloudUrl.equals(other$cloudUrl)) {
            return false;
        }
        String this$cloudId = this.getCloudId();
        String other$cloudId = other.getCloudId();
        if (this$cloudId == null ? other$cloudId != null : !this$cloudId.equals(other$cloudId)) {
            return false;
        }
        Edition this$edition = this.getEdition();
        Edition other$edition = other.getEdition();
        if (this$edition == null ? other$edition != null : !this$edition.equals(other$edition)) {
            return false;
        }
        CloudType this$cloudType = this.getCloudType();
        CloudType other$cloudType = other.getCloudType();
        return !(this$cloudType == null ? other$cloudType != null : !((Object)((Object)this$cloudType)).equals((Object)other$cloudType));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof CloudSiteDto;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $cloudUrl = this.getCloudUrl();
        result = result * 59 + ($cloudUrl == null ? 43 : $cloudUrl.hashCode());
        String $cloudId = this.getCloudId();
        result = result * 59 + ($cloudId == null ? 43 : $cloudId.hashCode());
        Edition $edition = this.getEdition();
        result = result * 59 + ($edition == null ? 43 : $edition.hashCode());
        CloudType $cloudType = this.getCloudType();
        result = result * 59 + ($cloudType == null ? 43 : ((Object)((Object)$cloudType)).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "CloudSiteDto(cloudUrl=" + this.getCloudUrl() + ", cloudId=" + this.getCloudId() + ", edition=" + this.getEdition() + ", cloudType=" + (Object)((Object)this.getCloudType()) + ")";
    }
}

