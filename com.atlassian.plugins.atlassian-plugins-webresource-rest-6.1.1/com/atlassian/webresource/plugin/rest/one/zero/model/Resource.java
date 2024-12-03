/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.UrlMode
 *  com.atlassian.webresource.api.assembler.resource.PluginCssResource
 *  com.atlassian.webresource.api.assembler.resource.PluginCssResourceParams
 *  com.atlassian.webresource.api.assembler.resource.PluginJsResource
 *  com.atlassian.webresource.api.assembler.resource.PluginJsResourceParams
 *  com.atlassian.webresource.api.assembler.resource.PluginUrlResource$BatchType
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.annotate.JsonPropertyOrder
 */
package com.atlassian.webresource.plugin.rest.one.zero.model;

import com.atlassian.webresource.api.UrlMode;
import com.atlassian.webresource.api.assembler.resource.PluginCssResource;
import com.atlassian.webresource.api.assembler.resource.PluginCssResourceParams;
import com.atlassian.webresource.api.assembler.resource.PluginJsResource;
import com.atlassian.webresource.api.assembler.resource.PluginJsResourceParams;
import com.atlassian.webresource.api.assembler.resource.PluginUrlResource;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonPropertyOrder(alphabetic=true)
public class Resource {
    private final String url;
    private final ResourceType resourceType;
    private final String conditionalComment;
    private final boolean ieOnly;
    private final String media;
    private final String key;
    private final String batchType;

    public Resource(@Nonnull PluginJsResource resource) {
        this(resource.getStaticUrl(UrlMode.RELATIVE), ResourceType.JAVASCRIPT, ((PluginJsResourceParams)resource.getParams()).conditionalComment(), ((PluginJsResourceParams)resource.getParams()).ieOnly(), null, resource.getKey(), resource.getBatchType());
    }

    public Resource(@Nonnull PluginCssResource cssResource) {
        this(cssResource.getStaticUrl(UrlMode.RELATIVE), ResourceType.CSS, ((PluginCssResourceParams)cssResource.getParams()).conditionalComment(), ((PluginCssResourceParams)cssResource.getParams()).ieOnly(), ((PluginCssResourceParams)cssResource.getParams()).media(), cssResource.getKey(), cssResource.getBatchType());
    }

    @Deprecated
    @JsonCreator
    public Resource(@Nonnull @JsonProperty(value="url") String url, @Nonnull @JsonProperty(value="resourceType") ResourceType resourceType, @Nonnull @JsonProperty(value="conditionalComment") String conditionalComment, @JsonProperty(value="ieOnly") boolean ieOnly, @Nullable @JsonProperty(value="media") String media, @Nonnull @JsonProperty(value="key") String key, @Nonnull @JsonProperty(value="batchType") PluginUrlResource.BatchType batchType) {
        this.url = url;
        this.resourceType = resourceType;
        this.media = media;
        this.key = key;
        this.batchType = batchType.name().toUpperCase();
        this.ieOnly = ieOnly;
        this.conditionalComment = conditionalComment;
    }

    public Resource(@Nonnull String url, @Nonnull ResourceType resourceType, @Nonnull String media, @Nonnull String key, @Nonnull PluginUrlResource.BatchType batchType) {
        this(url, resourceType, "", false, media, key, batchType);
    }

    @JsonProperty
    @Nonnull
    public PluginUrlResource.BatchType getBatchType() {
        return PluginUrlResource.BatchType.valueOf((String)this.batchType);
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

    @JsonProperty
    @Deprecated
    public String getConditionalComment() {
        return this.conditionalComment;
    }

    @JsonProperty
    @Deprecated
    public boolean isIeOnly() {
        return this.ieOnly;
    }

    @JsonProperty
    public String getMedia() {
        return this.media;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Resource) {
            Resource otherResource = (Resource)other;
            return Objects.equals(this.url, otherResource.url) && Objects.equals((Object)this.resourceType, (Object)otherResource.resourceType) && Objects.equals(this.conditionalComment, otherResource.conditionalComment) && this.ieOnly == otherResource.ieOnly && Objects.equals(this.media, otherResource.media) && Objects.equals(this.key, otherResource.key) && Objects.equals(this.batchType, otherResource.batchType);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.url, this.resourceType, this.conditionalComment, this.ieOnly, this.media, this.key, this.batchType});
    }

    public String toString() {
        return "Resource{key='" + this.key + '\'' + ", batchType='" + this.batchType + '\'' + ", resourceType=" + (Object)((Object)this.resourceType) + ", media='" + this.media + '\'' + ", url='" + this.url + '\'' + ", ieOnly=" + this.ieOnly + ", conditionalComment='" + this.conditionalComment + '\'' + '}';
    }

    public static enum ResourceType {
        JAVASCRIPT,
        CSS;

    }
}

