/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.elements.ResourceLocation
 */
package com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.path;

import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.path.DefaultPathStrategy;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.path.PathStrategy;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.path.RelativePathStrategyDecorator;

public class PathStrategyFactory {
    public PathStrategy createPath(ResourceLocation resourceLocation) {
        return new DefaultPathStrategy(resourceLocation);
    }

    public PathStrategy createRelativePath(ResourceLocation resourceLocation, String relativePath) {
        return new RelativePathStrategyDecorator(new DefaultPathStrategy(resourceLocation), relativePath);
    }
}

