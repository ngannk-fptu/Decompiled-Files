/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.assembler.resource.ResourcePhase
 *  com.atlassian.webresource.api.data.PluginDataResource
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.assembler;

import com.atlassian.plugin.webresource.ResourceUrl;
import com.atlassian.plugin.webresource.assembler.DefaultPluginCssResource;
import com.atlassian.plugin.webresource.assembler.DefaultPluginJsResource;
import com.atlassian.plugin.webresource.assembler.ResourceUrls;
import com.atlassian.webresource.api.assembler.resource.ResourcePhase;
import com.atlassian.webresource.api.data.PluginDataResource;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class WebResourceInformation {
    private final Collection<ResourceUrls> containers;
    private final Collection<PluginDataResource> data;
    private final ResourcePhase resourcePhase;
    private final Collection<ResourceUrl> urls;

    WebResourceInformation(@Nonnull Collection<PluginDataResource> data, @Nonnull ResourcePhase resourcePhase, @Nonnull Collection<ResourceUrl> urls) {
        this.data = data;
        this.resourcePhase = resourcePhase;
        this.urls = urls;
        this.containers = urls.stream().map(resourceUrl -> {
            String type;
            switch (type = resourceUrl.getType()) {
                case "js": {
                    DefaultPluginJsResource pluginUrlResource = new DefaultPluginJsResource((ResourceUrl)resourceUrl, resourcePhase);
                    return new ResourceUrls((ResourceUrl)resourceUrl, pluginUrlResource);
                }
                case "css": {
                    DefaultPluginCssResource pluginUrlResource = new DefaultPluginCssResource((ResourceUrl)resourceUrl, resourcePhase);
                    return new ResourceUrls((ResourceUrl)resourceUrl, pluginUrlResource);
                }
            }
            throw new RuntimeException("unsupported extension " + type);
        }).collect(Collectors.toCollection(LinkedList::new));
    }

    @Nonnull
    public Collection<ResourceUrls> getResourceUrls() {
        return this.containers;
    }

    @Nonnull
    public Collection<PluginDataResource> getData() {
        return this.data;
    }

    @Nonnull
    public ResourcePhase getResourcePhase() {
        return this.resourcePhase;
    }

    @Nonnull
    public Collection<ResourceUrl> getUrls() {
        return this.urls;
    }
}

