/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sisyphus.dm;

import com.atlassian.sisyphus.dm.PluginCompatibility;

public class ScannedPlugin {
    private String name;
    private String version;
    private String vendor;
    private String vendorUrl;
    private String key;
    private PluginCompatibility compatibility;
    private String userInstalled;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVendor() {
        return this.vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public PluginCompatibility getCompatibility() {
        return this.compatibility;
    }

    public void setCompatibility(PluginCompatibility compatibility) {
        this.compatibility = compatibility;
    }

    public String getUserInstalled() {
        return this.userInstalled;
    }

    public void setUserInstalled(String userInstalled) {
        this.userInstalled = userInstalled;
    }

    public String getVendorUrl() {
        return this.vendorUrl;
    }

    public void setVendorUrl(String vendorUrl) {
        this.vendorUrl = vendorUrl;
    }
}

