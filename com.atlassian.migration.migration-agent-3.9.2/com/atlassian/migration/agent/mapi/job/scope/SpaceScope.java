/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.mapi.job.scope;

import com.atlassian.migration.agent.mapi.job.scope.SpaceMode;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public class SpaceScope {
    @JsonProperty
    @Nullable
    private List<String> includedKeys = Collections.emptyList();
    @JsonProperty(value="includedData")
    @Nullable
    private SpaceMode includedData = SpaceMode.ALL;

    @Generated
    public SpaceScope(@Nullable List<String> includedKeys, @Nullable SpaceMode includedData) {
        this.includedKeys = includedKeys;
        this.includedData = includedData;
    }

    @Generated
    public SpaceScope() {
    }

    @Nullable
    @Generated
    public List<String> getIncludedKeys() {
        return this.includedKeys;
    }

    @Nullable
    @Generated
    public SpaceMode getIncludedData() {
        return this.includedData;
    }
}

