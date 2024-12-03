/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.mapi.job.scope;

import com.atlassian.migration.agent.mapi.job.scope.MapiGlobalEntitiesType;
import java.util.Collections;
import java.util.List;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public class GlobalEntitiesScope {
    @JsonProperty
    private List<MapiGlobalEntitiesType> includedTypes = Collections.emptyList();

    @Generated
    public GlobalEntitiesScope(List<MapiGlobalEntitiesType> includedTypes) {
        this.includedTypes = includedTypes;
    }

    @Generated
    public GlobalEntitiesScope() {
    }

    @Generated
    public List<MapiGlobalEntitiesType> getIncludedTypes() {
        return this.includedTypes;
    }
}

