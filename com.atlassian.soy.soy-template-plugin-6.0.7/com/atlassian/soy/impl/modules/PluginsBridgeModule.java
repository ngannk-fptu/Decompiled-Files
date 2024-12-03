/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.google.inject.AbstractModule
 */
package com.atlassian.soy.impl.modules;

import com.atlassian.plugin.webresource.WebResourceManager;
import com.google.inject.AbstractModule;

class PluginsBridgeModule
extends AbstractModule {
    private final WebResourceManager webResourceManager;

    public PluginsBridgeModule(WebResourceManager webResourceManager) {
        this.webResourceManager = webResourceManager;
    }

    public void configure() {
        this.binder().bind(WebResourceManager.class).toInstance((Object)this.webResourceManager);
    }
}

