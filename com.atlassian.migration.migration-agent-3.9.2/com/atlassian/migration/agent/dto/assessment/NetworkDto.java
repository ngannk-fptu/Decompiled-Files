/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.jetbrains.annotations.Nullable
 */
package com.atlassian.migration.agent.dto.assessment;

import lombok.Generated;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jetbrains.annotations.Nullable;

public final class NetworkDto {
    @JsonProperty
    @Nullable
    private final String effectiveType;
    @JsonProperty
    @Nullable
    private final Integer downlink;
    @JsonProperty
    @Nullable
    private final Integer rtt;

    @JsonCreator
    public NetworkDto(@JsonProperty(value="effectiveType") @Nullable String effectiveType, @JsonProperty(value="downlink") @Nullable Integer downlink, @JsonProperty(value="rtt") @Nullable Integer rtt) {
        this.effectiveType = effectiveType;
        this.downlink = downlink;
        this.rtt = rtt;
    }

    @Nullable
    @Generated
    public String getEffectiveType() {
        return this.effectiveType;
    }

    @Nullable
    @Generated
    public Integer getDownlink() {
        return this.downlink;
    }

    @Nullable
    @Generated
    public Integer getRtt() {
        return this.rtt;
    }
}

