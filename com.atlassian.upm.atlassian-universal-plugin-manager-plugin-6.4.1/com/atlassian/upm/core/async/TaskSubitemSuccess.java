/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.core.async;

import com.google.common.collect.ImmutableMap;
import java.net.URI;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public final class TaskSubitemSuccess {
    @JsonProperty
    private final String name;
    @JsonProperty
    private final String key;
    @JsonProperty
    private final String version;
    @JsonProperty
    private final Map<String, URI> links;

    @JsonCreator
    public TaskSubitemSuccess(@JsonProperty(value="name") String name, @JsonProperty(value="key") String key, @JsonProperty(value="version") String version, @JsonProperty(value="links") Map<String, URI> links) {
        this.name = name;
        this.key = key;
        this.version = version;
        this.links = links == null ? null : ImmutableMap.copyOf(links);
    }

    public String getName() {
        return this.name;
    }

    public String getKey() {
        return this.key;
    }

    public String getVersion() {
        return this.version;
    }

    public Map<String, URI> getLinks() {
        return this.links;
    }

    public String toString() {
        return "TaskSubitemSuccess(" + this.name + ", " + this.key + ", " + this.version + ", " + this.links + ")";
    }
}

