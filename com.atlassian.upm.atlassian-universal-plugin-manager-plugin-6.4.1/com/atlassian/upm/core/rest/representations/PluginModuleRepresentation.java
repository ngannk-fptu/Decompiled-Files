/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.core.rest.representations;

import com.google.common.collect.ImmutableMap;
import java.net.URI;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class PluginModuleRepresentation {
    @JsonProperty
    private final String key;
    @JsonProperty
    private final String completeKey;
    @JsonProperty
    private final Map<String, URI> links;
    @JsonProperty
    private final boolean enabled;
    @JsonProperty
    private final boolean optional;
    @JsonProperty
    private final String name;
    @JsonProperty
    private final String description;
    @JsonProperty
    private final boolean recognisableType;
    @JsonProperty
    private final boolean broken;

    @JsonCreator
    public PluginModuleRepresentation(@JsonProperty(value="key") String key, @JsonProperty(value="completeKey") String completeKey, @JsonProperty(value="links") Map<String, URI> links, @JsonProperty(value="enabled") boolean enabled, @JsonProperty(value="optional") boolean optional, @JsonProperty(value="name") String name, @JsonProperty(value="description") String description, @JsonProperty(value="recognisableType") boolean recognisableType, @JsonProperty(value="broken") boolean broken) {
        this.key = key;
        this.completeKey = completeKey;
        this.links = ImmutableMap.copyOf(links);
        this.name = name;
        this.enabled = enabled;
        this.description = description;
        this.optional = optional;
        this.recognisableType = recognisableType;
        this.broken = broken;
    }

    public String getKey() {
        return this.key;
    }

    public String getCompleteKey() {
        return this.completeKey;
    }

    public URI getSelfLink() {
        return this.links.get("self");
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean hasRecognisableType() {
        return this.recognisableType;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isOptional() {
        return this.optional;
    }

    public boolean isBroken() {
        return this.broken;
    }
}

