/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sisyphus.plugin.compatiblity;

import com.atlassian.sisyphus.dm.ScannedProperty;
import com.atlassian.sisyphus.dm.ScannedPropertySet;

public class PluginData {
    String pluginName;
    String pluginKey;
    String pluginVendor;
    String pluginVersion;
    String pluginUserInstalled;
    String pluginVendorUrl;

    public PluginData(ScannedPropertySet properties) {
        for (ScannedProperty property : properties.getProperties()) {
            String pname = property.getName();
            if (pname.equals("plugin.userinstalled")) {
                this.pluginUserInstalled = property.getValue();
                continue;
            }
            if (pname.equals("plugin.vendor")) {
                this.pluginVendor = property.getValue();
                continue;
            }
            if (pname.equals("plugin.key")) {
                this.pluginKey = property.getValue();
                continue;
            }
            if (pname.equals("plugin.version")) {
                this.pluginVersion = property.getValue();
                continue;
            }
            if (pname.equals("plugin.name")) {
                this.pluginName = property.getValue();
                continue;
            }
            if (!pname.equals("plugin.vendor.url")) continue;
            this.pluginVendorUrl = property.getValue();
        }
    }

    public String getPluginName() {
        return this.pluginName;
    }

    public String getPluginKey() {
        return this.pluginKey;
    }

    public String getPluginVendor() {
        return this.pluginVendor;
    }

    public String getPluginVersion() {
        return this.pluginVersion;
    }

    public String getPluginUserInstalled() {
        return this.pluginUserInstalled;
    }

    public String getPluginVendorUrl() {
        return this.pluginVendorUrl;
    }
}

