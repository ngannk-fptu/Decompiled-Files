/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.annotate.JsonPropertyOrder
 */
package com.atlassian.webresource.plugin.rest.one.zero.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonPropertyOrder(alphabetic=true)
public class ResolveResourcesJson {
    @Nonnull
    private final List<String> resources;
    @Nonnull
    private final List<String> contexts;
    @Nonnull
    private final List<String> excludeResources;
    @Nonnull
    private final List<String> excludeContexts;

    public ResolveResourcesJson() {
        this.resources = Collections.emptyList();
        this.contexts = Collections.emptyList();
        this.excludeResources = Collections.emptyList();
        this.excludeContexts = Collections.emptyList();
    }

    @JsonCreator
    public ResolveResourcesJson(@JsonProperty(value="r") @Nonnull List<String> resources, @JsonProperty(value="c") @Nonnull List<String> contexts, @JsonProperty(value="xr") @Nonnull List<String> excludeResources, @JsonProperty(value="xc") @Nonnull List<String> excludeContexts) {
        this.resources = resources;
        this.contexts = contexts;
        this.excludeResources = excludeResources;
        this.excludeContexts = excludeContexts;
    }

    @Nonnull
    @JsonProperty(value="r")
    public List<String> getResources() {
        return this.resources;
    }

    @Nonnull
    @JsonProperty(value="c")
    public List<String> getContexts() {
        return this.contexts;
    }

    @Nonnull
    @JsonProperty(value="xr")
    public List<String> getExcludeResources() {
        return this.excludeResources;
    }

    @Nonnull
    @JsonProperty(value="xc")
    public List<String> getExcludeContexts() {
        return this.excludeContexts;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ResolveResourcesJson that = (ResolveResourcesJson)o;
        return Objects.equals(this.resources, that.resources) && Objects.equals(this.contexts, that.contexts) && Objects.equals(this.excludeResources, that.excludeResources) && Objects.equals(this.excludeContexts, that.excludeContexts);
    }

    public int hashCode() {
        return Objects.hash(this.resources, this.contexts, this.excludeResources, this.excludeContexts);
    }
}

