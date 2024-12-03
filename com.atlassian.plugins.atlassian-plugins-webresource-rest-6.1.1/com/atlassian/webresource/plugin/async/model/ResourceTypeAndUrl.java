/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.UrlMode
 *  com.atlassian.webresource.api.assembler.resource.PluginUrlResource
 *  com.atlassian.webresource.api.assembler.resource.PluginUrlResource$BatchType
 *  javax.annotation.Nonnull
 */
package com.atlassian.webresource.plugin.async.model;

import com.atlassian.webresource.api.UrlMode;
import com.atlassian.webresource.api.assembler.resource.PluginUrlResource;
import com.atlassian.webresource.plugin.async.model.ResourceType;
import java.util.Objects;
import javax.annotation.Nonnull;

public class ResourceTypeAndUrl {
    private final PluginUrlResource<?> pluginUrlResource;

    public ResourceTypeAndUrl(@Nonnull PluginUrlResource<?> pluginUrlResource) {
        this.pluginUrlResource = pluginUrlResource;
    }

    @Nonnull
    public PluginUrlResource.BatchType getBatchType() {
        return this.pluginUrlResource.getBatchType();
    }

    @Nonnull
    public String getKey() {
        return this.pluginUrlResource.getKey();
    }

    @Nonnull
    public PluginUrlResource<?> getPluginUrlResource() {
        return this.pluginUrlResource;
    }

    @Nonnull
    public ResourceType getResourceType() {
        return ResourceType.getResourceType(this.pluginUrlResource);
    }

    @Nonnull
    public String getUrl() {
        return this.pluginUrlResource.getStaticUrl(UrlMode.RELATIVE);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ResourceTypeAndUrl) {
            ResourceTypeAndUrl otherResourceTypeAndUrl = (ResourceTypeAndUrl)other;
            return this.getBatchType() == otherResourceTypeAndUrl.getBatchType() && this.getKey().equals(otherResourceTypeAndUrl.getKey()) && this.getResourceType() == otherResourceTypeAndUrl.getResourceType() && this.getUrl().equals(otherResourceTypeAndUrl.getUrl());
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.getBatchType(), this.getKey(), this.getResourceType(), this.getUrl()});
    }

    public String toString() {
        return "ResourceTypeAndUrl{batchType=" + this.getBatchType() + ",key=" + this.getKey() + ",resourceType=" + (Object)((Object)this.getResourceType()) + ",url=" + this.getUrl() + '}';
    }
}

