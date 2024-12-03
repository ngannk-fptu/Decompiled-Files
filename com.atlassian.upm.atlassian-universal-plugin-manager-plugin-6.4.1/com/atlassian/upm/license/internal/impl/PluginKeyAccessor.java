/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 */
package com.atlassian.upm.license.internal.impl;

import org.osgi.framework.Bundle;

public abstract class PluginKeyAccessor {
    private static final String ATLASSIAN_PLUGIN_KEY = "Atlassian-Plugin-Key";

    protected String getPluginKey(Bundle bundle) {
        String pluginKey = (String)bundle.getHeaders().get(ATLASSIAN_PLUGIN_KEY);
        if (pluginKey == null) {
            throw new IllegalStateException();
        }
        return pluginKey;
    }
}

