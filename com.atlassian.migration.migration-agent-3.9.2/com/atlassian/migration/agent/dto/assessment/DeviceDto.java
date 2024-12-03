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

public final class DeviceDto {
    @JsonProperty
    @Nullable
    private final Integer processors;
    @JsonProperty
    @Nullable
    private final Integer memory;

    @JsonCreator
    public DeviceDto(@JsonProperty(value="processors") @Nullable Integer processors, @JsonProperty(value="memory") @Nullable Integer memory) {
        this.processors = processors;
        this.memory = memory;
    }

    @Nullable
    @Generated
    public Integer getProcessors() {
        return this.processors;
    }

    @Nullable
    @Generated
    public Integer getMemory() {
        return this.memory;
    }
}

