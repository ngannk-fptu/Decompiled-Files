/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.assembler.resource.PluginUrlResource$BatchType
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.annotate.JsonPropertyOrder
 */
package com.atlassian.webresource.plugin.rest.two.zero.model;

import com.atlassian.webresource.api.assembler.resource.PluginUrlResource;
import com.atlassian.webresource.plugin.rest.two.zero.model.ResourceType;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

@JsonPropertyOrder(alphabetic=true)
public final class UrlFetchableResourceJson {
    private final PluginUrlResource.BatchType batchType;
    private final String key;
    private final ResourceType resourceType;
    private final String url;

    @JsonCreator
    public UrlFetchableResourceJson(@Nonnull @JsonProperty(value="batchType") PluginUrlResource.BatchType batchType, @Nonnull @JsonProperty(value="key") String key, @Nonnull @JsonProperty(value="resourceType") ResourceType resourceType, @Nonnull @JsonProperty(value="url") String url) {
        this.batchType = Objects.requireNonNull(batchType, "The batch type is mandatory.");
        this.key = Objects.requireNonNull(key, "The resource key is mandatory.");
        this.resourceType = Objects.requireNonNull(resourceType, "The resource type is mandatory.");
        this.url = Objects.requireNonNull(url, "The resource url is mandatory.");
    }

    @JsonProperty
    @Nonnull
    public PluginUrlResource.BatchType getBatchType() {
        return this.batchType;
    }

    @JsonProperty
    @Nonnull
    public String getKey() {
        return this.key;
    }

    @JsonProperty
    @Nonnull
    public ResourceType getResourceType() {
        return this.resourceType;
    }

    @JsonProperty
    @Nonnull
    public String getUrl() {
        return this.url;
    }

    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject instanceof UrlFetchableResourceJson) {
            UrlFetchableResourceJson other = (UrlFetchableResourceJson)otherObject;
            return this.batchType == other.batchType && this.key.equals(other.key) && this.resourceType == other.resourceType && this.url.equals(other.url);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.batchType, this.key, this.resourceType, this.url});
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.JSON_STYLE);
    }
}

