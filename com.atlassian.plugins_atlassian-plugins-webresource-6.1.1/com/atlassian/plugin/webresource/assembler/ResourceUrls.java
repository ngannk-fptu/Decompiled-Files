/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.assembler.resource.PluginUrlResource
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.assembler;

import com.atlassian.plugin.webresource.ResourceUrl;
import com.atlassian.webresource.api.assembler.resource.PluginUrlResource;
import java.util.Objects;
import javax.annotation.Nonnull;

public class ResourceUrls {
    private final ResourceUrl resourceUrl;
    private final PluginUrlResource<?> pluginUrlResource;

    public ResourceUrls(@Nonnull ResourceUrl resourceUrl, @Nonnull PluginUrlResource<?> pluginUrlResource) {
        this.resourceUrl = Objects.requireNonNull(resourceUrl, "The resource url is mandatory.");
        this.pluginUrlResource = Objects.requireNonNull(pluginUrlResource, "The plugin url resource is mandatory.");
    }

    @Nonnull
    public PluginUrlResource<?> getPluginUrlResource() {
        return this.pluginUrlResource;
    }

    @Nonnull
    public ResourceUrl getResourceUrl() {
        return this.resourceUrl;
    }
}

