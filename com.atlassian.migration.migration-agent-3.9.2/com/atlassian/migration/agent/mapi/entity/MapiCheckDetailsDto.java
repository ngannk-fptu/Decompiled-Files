/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.mapi.entity;

import java.util.List;
import javax.annotation.Nullable;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public class MapiCheckDetailsDto {
    @JsonProperty(value="occurrences")
    @Nullable
    private List<String> occurrences;

    @Generated
    public MapiCheckDetailsDto(@Nullable List<String> occurrences) {
        this.occurrences = occurrences;
    }

    @Generated
    public void setOccurrences(@Nullable List<String> occurrences) {
        this.occurrences = occurrences;
    }

    @Nullable
    @Generated
    public List<String> getOccurrences() {
        return this.occurrences;
    }
}

