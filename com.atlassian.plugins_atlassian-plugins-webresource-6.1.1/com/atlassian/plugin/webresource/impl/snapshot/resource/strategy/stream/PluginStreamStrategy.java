/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.stream;

import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.impl.snapshot.Bundle;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.stream.StreamStrategy;
import java.io.InputStream;

public class PluginStreamStrategy
implements StreamStrategy {
    private final WebResourceIntegration integration;
    private final Bundle bundle;

    PluginStreamStrategy(WebResourceIntegration integration, Bundle bundle) {
        this.integration = integration;
        this.bundle = bundle;
    }

    @Override
    public InputStream getInputStream(String path) {
        return this.integration.getPluginAccessor().getEnabledPlugin(this.bundle.getKey()).getResourceAsStream(path);
    }
}

