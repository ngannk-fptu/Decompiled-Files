/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 */
package com.atlassian.plugin.osgi.bridge;

import org.osgi.framework.Bundle;

class PluginBundleUtils {
    PluginBundleUtils() {
    }

    static String getPluginKey(Bundle bundle) {
        return PluginBundleUtils.getPluginKey(bundle.getSymbolicName(), bundle.getHeaders().get("Atlassian-Plugin-Key"), bundle.getHeaders().get("Bundle-Version"));
    }

    private static String getPluginKey(Object bundleName, Object atlKey, Object version) {
        Object key = atlKey;
        if (key == null) {
            key = bundleName + "-" + version;
        }
        return key.toString();
    }
}

