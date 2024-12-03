/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.elements.ResourceLocation
 */
package com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.path;

import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.path.PathStrategy;

public class DefaultPathStrategy
implements PathStrategy {
    private final ResourceLocation resourceLocation;

    DefaultPathStrategy(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    @Override
    public String getPath() {
        return this.resourceLocation.getLocation();
    }
}

