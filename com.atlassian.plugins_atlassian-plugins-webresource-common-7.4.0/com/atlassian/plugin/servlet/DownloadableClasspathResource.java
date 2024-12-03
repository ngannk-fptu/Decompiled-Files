/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.elements.ResourceLocation
 */
package com.atlassian.plugin.servlet;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.servlet.AbstractDownloadableResource;
import java.io.InputStream;

public class DownloadableClasspathResource
extends AbstractDownloadableResource {
    public DownloadableClasspathResource(Plugin plugin, ResourceLocation resourceLocation, String extraPath) {
        super(plugin, resourceLocation, extraPath);
    }

    @Override
    protected InputStream getResourceAsStream(String resourceLocation) {
        return this.plugin.getResourceAsStream(resourceLocation);
    }

    public ResourceLocation getResourceLocation() {
        return this.resourceLocation;
    }
}

