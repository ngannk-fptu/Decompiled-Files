/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto.assessment;

import java.util.Collection;
import java.util.Collections;
import org.codehaus.jackson.annotate.JsonProperty;

public class AppListResponse<T> {
    @JsonProperty
    private final Collection<T> apps;

    public AppListResponse() {
        this(Collections.emptyList());
    }

    public AppListResponse(Collection<T> apps) {
        this.apps = apps;
    }

    public Collection<T> getApps() {
        return this.apps;
    }
}

