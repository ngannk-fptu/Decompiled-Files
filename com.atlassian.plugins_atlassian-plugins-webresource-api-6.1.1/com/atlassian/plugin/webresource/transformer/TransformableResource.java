/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.elements.ResourceLocation
 *  com.atlassian.plugin.servlet.DownloadableResource
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.transformer;

import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.servlet.DownloadableResource;
import javax.annotation.Nonnull;

public class TransformableResource {
    private final ResourceLocation location;
    private final DownloadableResource nextResource;

    public TransformableResource(ResourceLocation location, DownloadableResource nextResource) {
        this.location = location;
        this.nextResource = nextResource;
    }

    @Deprecated
    public TransformableResource(ResourceLocation location, String filePath, DownloadableResource nextResource) {
        this.location = location;
        this.nextResource = nextResource;
    }

    public ResourceLocation location() {
        return this.location;
    }

    @Deprecated
    @Nonnull
    public String filePath() {
        return "";
    }

    public DownloadableResource nextResource() {
        return this.nextResource;
    }
}

