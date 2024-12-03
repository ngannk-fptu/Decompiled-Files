/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.sisyphus.plugin.compatiblity;

import com.atlassian.sisyphus.dm.PluginCompatibility;
import com.atlassian.sisyphus.dm.PropScanResult;
import com.atlassian.sisyphus.dm.ScannedPlugin;
import com.atlassian.sisyphus.dm.ScannedPropertySet;
import com.atlassian.sisyphus.marketplace.Compatibility;
import com.atlassian.sisyphus.marketplace.CompatibleApplication;
import com.atlassian.sisyphus.marketplace.MarketPlaceData;
import com.atlassian.sisyphus.marketplace.MarketPlaceService;
import com.atlassian.sisyphus.marketplace.Version;
import com.atlassian.sisyphus.plugin.compatiblity.PluginData;
import com.atlassian.sisyphus.plugin.compatiblity.ProductData;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompatibilityData {
    private static final Logger log = LoggerFactory.getLogger(CompatibilityData.class);
    private final MarketPlaceService marketPlaceService;

    public CompatibilityData(MarketPlaceService marketPlaceService) {
        this.marketPlaceService = marketPlaceService;
    }

    public PropScanResult updatePluginCompatibility(PropScanResult propScanResult) {
        ProductData productData = new ProductData(propScanResult);
        List<ScannedPropertySet> properties = propScanResult.getScannedProperties();
        for (ScannedPropertySet property : properties) {
            if (property.getTitle() != "title.plugin") continue;
            PluginData pluginData = new PluginData(property);
            try {
                PluginCompatibility reason = null != pluginData.getPluginUserInstalled() && pluginData.getPluginUserInstalled().length() > 0 ? this.handleUserInstalledPlugin(productData, pluginData) : this.handleOldPlugins(productData, pluginData);
                if (reason != PluginCompatibility.IGNORED) {
                    ScannedPlugin plugin = new ScannedPlugin();
                    plugin.setName(pluginData.getPluginName());
                    plugin.setCompatibility(reason);
                    plugin.setKey(pluginData.getPluginKey());
                    plugin.setUserInstalled(pluginData.getPluginUserInstalled());
                    plugin.setVendor(pluginData.getPluginVendor());
                    plugin.setVendorUrl(pluginData.getPluginVendorUrl());
                    plugin.setVersion(pluginData.getPluginVersion());
                    propScanResult.addPlugin(plugin);
                    continue;
                }
                log.debug("Ignoring " + pluginData.getPluginKey());
            }
            catch (IOException e) {
                log.error("Unable to determine plugin compatibility - " + pluginData.getPluginKey());
            }
        }
        return propScanResult;
    }

    public boolean isMatchingProduct(String name, MarketPlaceData marketPlaceData) {
        if (null != marketPlaceData && null != name) {
            for (CompatibleApplication compatibleApplication : marketPlaceData.getCompatibleApplications()) {
                if (!compatibleApplication.getName().equals(name)) continue;
                return true;
            }
        }
        return false;
    }

    public boolean isPluginCompatible(ProductData productData, PluginData pluginData) throws IOException {
        MarketPlaceData marketPlaceData = this.marketPlaceService.getPluginData(pluginData.getPluginKey());
        int installedVersion = Integer.parseInt(productData.getBuildNumber());
        if (null != marketPlaceData && this.isMatchingProduct(productData.getProductName(), marketPlaceData)) {
            for (Version marketplaceVersion : marketPlaceData.getVersions().getVersions()) {
                if (!marketplaceVersion.getVersion().equals(pluginData.getPluginVersion())) continue;
                for (Compatibility compatibility : marketplaceVersion.getCompatibilities()) {
                    if (compatibility.getMax().getBuildNumber() < (long)installedVersion || compatibility.getMin().getBuildNumber() > (long)installedVersion) continue;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isIgnorable(String pluginVendor) {
        return null == pluginVendor || pluginVendor.isEmpty() || pluginVendor.toLowerCase().contains("atlassian") || pluginVendor.toLowerCase().contains("apache");
    }

    private PluginCompatibility handleOldPlugins(ProductData productData, PluginData pluginData) throws IOException {
        MarketPlaceData marketPlaceData = this.marketPlaceService.getPluginData(pluginData.getPluginKey());
        if (null == marketPlaceData) {
            if (this.isIgnorable(pluginData.getPluginVendor())) {
                log.debug("Ignoring Atlassian NOT_IN_MPAC plugin " + pluginData.getPluginKey());
                return PluginCompatibility.IGNORED;
            }
            return PluginCompatibility.PLUGIN_NOT_FOUND_MPAC;
        }
        if (!this.isMatchingProduct(productData.getProductName(), marketPlaceData)) {
            log.warn("Invalid product plugin found - " + pluginData.getPluginKey());
            return PluginCompatibility.INVALID_PRODUCT;
        }
        if (!this.isPluginCompatible(productData, pluginData)) {
            log.debug("Incompatible plugin found - " + pluginData.getPluginKey());
            return PluginCompatibility.NOT_COMPATIBLE;
        }
        if (this.isIgnorable(pluginData.getPluginVendor())) {
            log.debug("Ignoring Atlassian/Labs compatible plugin " + pluginData.getPluginKey());
            return PluginCompatibility.IGNORED;
        }
        return PluginCompatibility.COMPATIBLE;
    }

    protected PluginCompatibility handleUserInstalledPlugin(ProductData productData, PluginData pluginData) throws IOException {
        if (!pluginData.getPluginUserInstalled().toLowerCase().equals("true")) {
            log.debug("Ignoring system plugin - " + pluginData.getPluginKey());
            return PluginCompatibility.IGNORED;
        }
        MarketPlaceData marketPlaceData = this.marketPlaceService.getPluginData(pluginData.getPluginKey());
        if (null == marketPlaceData) {
            if (!this.isIgnorable(pluginData.getPluginVendor())) {
                return PluginCompatibility.PLUGIN_NOT_FOUND_MPAC;
            }
            return PluginCompatibility.IGNORED;
        }
        if (!this.isMatchingProduct(productData.getProductName(), marketPlaceData)) {
            return PluginCompatibility.INVALID_PRODUCT;
        }
        if (!this.isPluginCompatible(productData, pluginData)) {
            return PluginCompatibility.NOT_COMPATIBLE;
        }
        return PluginCompatibility.COMPATIBLE;
    }
}

