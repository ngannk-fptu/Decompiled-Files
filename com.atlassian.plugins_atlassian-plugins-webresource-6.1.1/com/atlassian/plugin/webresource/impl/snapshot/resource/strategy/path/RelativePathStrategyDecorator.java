/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.path;

import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.path.PathStrategy;

public class RelativePathStrategyDecorator
implements PathStrategy {
    private final PathStrategy pathStrategy;
    private final String relativePath;

    RelativePathStrategyDecorator(PathStrategy pathStrategy, String relativePath) {
        this.pathStrategy = pathStrategy;
        this.relativePath = relativePath;
    }

    @Override
    public String getPath() {
        String baseLocation = this.pathStrategy.getPath();
        if (baseLocation.endsWith("/")) {
            return baseLocation + this.relativePath;
        }
        return baseLocation + "/" + this.relativePath;
    }
}

