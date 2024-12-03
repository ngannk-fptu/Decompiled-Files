/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.google.common.base.Preconditions
 */
package com.atlassian.plugin.util.resource;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.util.resource.AlternativeResourceLoader;
import com.google.common.base.Preconditions;
import java.io.InputStream;
import java.net.URL;

public final class AlternativePluginResourceLoader
implements AlternativeResourceLoader {
    private final Plugin plugin;

    public AlternativePluginResourceLoader(Plugin plugin) {
        this.plugin = (Plugin)Preconditions.checkNotNull((Object)plugin);
    }

    @Override
    public URL getResource(String path) {
        return this.plugin.getResource(path);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return this.plugin.getResourceAsStream(name);
    }
}

