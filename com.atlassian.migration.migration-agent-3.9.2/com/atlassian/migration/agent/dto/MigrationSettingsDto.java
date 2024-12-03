/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto;

import com.atlassian.migration.agent.dto.CloudType;
import com.atlassian.migration.agent.dto.ConcurrencySettingsEnum;
import java.util.Map;
import javax.annotation.Nullable;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public class MigrationSettingsDto {
    @JsonProperty(value="cloudTypes")
    @Nullable
    private Map<CloudType, Boolean> cloudType;
    @JsonProperty(value="concurrency")
    @Nullable
    private Map<ConcurrencySettingsEnum, Integer> concurrency;

    @Nullable
    @Generated
    public Map<CloudType, Boolean> getCloudType() {
        return this.cloudType;
    }

    @Nullable
    @Generated
    public Map<ConcurrencySettingsEnum, Integer> getConcurrency() {
        return this.concurrency;
    }

    @Generated
    public void setCloudType(@Nullable Map<CloudType, Boolean> cloudType) {
        this.cloudType = cloudType;
    }

    @Generated
    public void setConcurrency(@Nullable Map<ConcurrencySettingsEnum, Integer> concurrency) {
        this.concurrency = concurrency;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MigrationSettingsDto)) {
            return false;
        }
        MigrationSettingsDto other = (MigrationSettingsDto)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Map<CloudType, Boolean> this$cloudType = this.getCloudType();
        Map<CloudType, Boolean> other$cloudType = other.getCloudType();
        if (this$cloudType == null ? other$cloudType != null : !((Object)this$cloudType).equals(other$cloudType)) {
            return false;
        }
        Map<ConcurrencySettingsEnum, Integer> this$concurrency = this.getConcurrency();
        Map<ConcurrencySettingsEnum, Integer> other$concurrency = other.getConcurrency();
        return !(this$concurrency == null ? other$concurrency != null : !((Object)this$concurrency).equals(other$concurrency));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof MigrationSettingsDto;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Map<CloudType, Boolean> $cloudType = this.getCloudType();
        result = result * 59 + ($cloudType == null ? 43 : ((Object)$cloudType).hashCode());
        Map<ConcurrencySettingsEnum, Integer> $concurrency = this.getConcurrency();
        result = result * 59 + ($concurrency == null ? 43 : ((Object)$concurrency).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "MigrationSettingsDto(cloudType=" + this.getCloudType() + ", concurrency=" + this.getConcurrency() + ")";
    }

    @Generated
    public MigrationSettingsDto() {
    }
}

