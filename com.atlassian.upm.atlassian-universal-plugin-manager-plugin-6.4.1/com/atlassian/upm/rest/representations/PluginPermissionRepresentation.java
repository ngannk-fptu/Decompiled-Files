/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.rest.representations;

import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class PluginPermissionRepresentation {
    @JsonProperty
    private final String key;
    @JsonProperty
    private final String name;
    @JsonProperty
    private final String description;

    @JsonCreator
    PluginPermissionRepresentation(@JsonProperty(value="key") String key, @JsonProperty(value="name") String name, @JsonProperty(value="description") String description) {
        this.key = Objects.requireNonNull(key, "key");
        this.name = Objects.requireNonNull(name, "name");
        this.description = Objects.requireNonNull(description, "description");
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }
}

