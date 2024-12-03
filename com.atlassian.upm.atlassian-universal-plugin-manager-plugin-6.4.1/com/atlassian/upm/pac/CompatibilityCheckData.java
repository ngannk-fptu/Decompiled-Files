/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.pac;

import com.atlassian.marketplace.client.model.AddonVersion;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Plugin;

public class CompatibilityCheckData {
    private final Plugin installedPlugin;
    private final Option<AddonVersion> installedVersionListing;
    private final Option<AddonVersion> latestVersionCompatibleWithTargetProduct;

    CompatibilityCheckData(Plugin installedPlugin, Option<AddonVersion> installedVersionListing, Option<AddonVersion> latestVersionCompatibleWithTargetProduct) {
        this.installedPlugin = installedPlugin;
        this.installedVersionListing = installedVersionListing;
        this.latestVersionCompatibleWithTargetProduct = latestVersionCompatibleWithTargetProduct;
    }

    public Plugin getInstalledPlugin() {
        return this.installedPlugin;
    }

    public Option<AddonVersion> getInstalledVersionListing() {
        return this.installedVersionListing;
    }

    public Option<AddonVersion> getLatestVersionCompatibleWithTargetProduct() {
        return this.latestVersionCompatibleWithTargetProduct;
    }
}

