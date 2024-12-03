/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto.state;

import org.codehaus.jackson.annotate.JsonProperty;

public class GlobalStateDto {
    @JsonProperty
    public final boolean hasPlans;

    public GlobalStateDto(boolean hasPlans) {
        this.hasPlans = hasPlans;
    }
}

