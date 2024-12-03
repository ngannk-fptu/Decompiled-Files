/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.stream;

import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.impl.snapshot.Bundle;
import com.atlassian.plugin.webresource.impl.snapshot.resource.strategy.stream.StreamStrategy;
import java.io.InputStream;

public class WebResourceStreamStrategy
implements StreamStrategy {
    private final WebResourceIntegration webResourceIntegration;
    private final Bundle bundle;

    WebResourceStreamStrategy(WebResourceIntegration webResourceIntegration, Bundle bundle) {
        this.webResourceIntegration = webResourceIntegration;
        this.bundle = bundle;
    }

    @Override
    public InputStream getInputStream(String path) {
        return this.webResourceIntegration.getPluginAccessor().getEnabledPluginModule(this.bundle.getKey()).getPlugin().getResourceAsStream(path);
    }
}

