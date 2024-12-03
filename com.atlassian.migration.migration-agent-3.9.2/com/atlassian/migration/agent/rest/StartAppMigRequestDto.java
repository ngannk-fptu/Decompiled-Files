/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.rest;

import java.util.Set;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class StartAppMigRequestDto {
    @JsonProperty
    public Set<String> includeServerAppKeys;

    @JsonCreator
    public StartAppMigRequestDto(@JsonProperty(value="includeServerAppKeys") Set<String> includeServerAppKeys) {
        this.includeServerAppKeys = includeServerAppKeys;
    }
}

