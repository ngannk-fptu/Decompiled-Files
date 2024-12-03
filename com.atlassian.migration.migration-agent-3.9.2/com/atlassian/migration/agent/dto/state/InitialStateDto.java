/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto.state;

import com.atlassian.migration.agent.dto.state.GlobalStateDto;
import org.codehaus.jackson.annotate.JsonProperty;

public class InitialStateDto {
    @JsonProperty
    public final GlobalStateDto global;

    public InitialStateDto(GlobalStateDto global) {
        this.global = global;
    }
}

