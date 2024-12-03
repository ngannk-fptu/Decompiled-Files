/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.stream;

import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.stream.StreamStrategy;
import java.io.InputStream;

public class ESModuleStreamStrategy
implements StreamStrategy {
    private final WebResourceIntegration webResourceIntegration;
    private final String pluginKey;

    ESModuleStreamStrategy(WebResourceIntegration webResourceIntegration, String pluginKey) {
        this.webResourceIntegration = webResourceIntegration;
        this.pluginKey = pluginKey;
    }

    @Override
    public InputStream getInputStream(String path) {
        return this.webResourceIntegration.getPluginAccessor().getPlugin(this.pluginKey).getResourceAsStream(path);
    }
}

