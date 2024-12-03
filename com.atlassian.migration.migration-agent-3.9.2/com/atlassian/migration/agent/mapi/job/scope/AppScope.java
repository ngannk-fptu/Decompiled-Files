/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.mapi.job.scope;

import java.util.Collections;
import java.util.List;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public class AppScope {
    @JsonProperty
    private List<String> includedKeys = Collections.emptyList();

    @Generated
    public AppScope(List<String> includedKeys) {
        this.includedKeys = includedKeys;
    }

    @Generated
    public AppScope() {
    }

    @Generated
    public List<String> getIncludedKeys() {
        return this.includedKeys;
    }
}

