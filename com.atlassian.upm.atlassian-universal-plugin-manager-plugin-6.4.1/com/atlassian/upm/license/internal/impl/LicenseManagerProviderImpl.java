/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.LicenseManager
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.core.AtlassianLicenseFactory
 *  com.atlassian.extras.core.DefaultAtlassianLicenseFactory
 *  com.atlassian.extras.core.DefaultLicenseManager
 *  com.atlassian.extras.core.ProductLicenseFactory
 *  com.atlassian.extras.core.bamboo.BambooProductLicenseFactory
 *  com.atlassian.extras.core.clover.CloverProductLicenseFactory
 *  com.atlassian.extras.core.confluence.ConfluenceProductLicenseFactory
 *  com.atlassian.extras.core.crowd.CrowdProductLicenseFactory
 *  com.atlassian.extras.core.crucible.CrucibleProductLicenseFactory
 *  com.atlassian.extras.core.fisheye.FisheyeProductLicenseFactory
 *  com.atlassian.extras.core.greenhopper.GreenHopperProductLicenseFactory
 *  com.atlassian.extras.core.jira.JiraProductLicenseFactory
 *  com.atlassian.extras.core.plugins.PluginLicenseFactory
 *  com.atlassian.extras.core.stash.StashProductLicenseFactory
 *  com.atlassian.extras.decoder.api.DelegatingLicenseDecoder
 *  com.atlassian.extras.decoder.api.LicenseDecoder
 *  com.atlassian.extras.decoder.v1.Version1LicenseDecoder
 *  com.atlassian.extras.decoder.v2.Version2LicenseDecoder
 */
package com.atlassian.upm.license.internal.impl;

import com.atlassian.extras.api.LicenseManager;
import com.atlassian.extras.api.Product;
import com.atlassian.extras.core.AtlassianLicenseFactory;
import com.atlassian.extras.core.DefaultAtlassianLicenseFactory;
import com.atlassian.extras.core.DefaultLicenseManager;
import com.atlassian.extras.core.ProductLicenseFactory;
import com.atlassian.extras.core.bamboo.BambooProductLicenseFactory;
import com.atlassian.extras.core.clover.CloverProductLicenseFactory;
import com.atlassian.extras.core.confluence.ConfluenceProductLicenseFactory;
import com.atlassian.extras.core.crowd.CrowdProductLicenseFactory;
import com.atlassian.extras.core.crucible.CrucibleProductLicenseFactory;
import com.atlassian.extras.core.fisheye.FisheyeProductLicenseFactory;
import com.atlassian.extras.core.greenhopper.GreenHopperProductLicenseFactory;
import com.atlassian.extras.core.jira.JiraProductLicenseFactory;
import com.atlassian.extras.core.plugins.PluginLicenseFactory;
import com.atlassian.extras.core.stash.StashProductLicenseFactory;
import com.atlassian.extras.decoder.api.DelegatingLicenseDecoder;
import com.atlassian.extras.decoder.api.LicenseDecoder;
import com.atlassian.extras.decoder.v1.Version1LicenseDecoder;
import com.atlassian.extras.decoder.v2.Version2LicenseDecoder;
import com.atlassian.upm.license.internal.LicenseManagerProvider;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LicenseManagerProviderImpl
implements LicenseManagerProvider {
    private final LicenseDecoder decoder;
    private final Map<Product, ProductLicenseFactory> productLicenseFactories = new HashMap<Product, ProductLicenseFactory>();
    private LicenseManager licenseManager;

    public LicenseManagerProviderImpl() {
        this.decoder = new DelegatingLicenseDecoder(Arrays.asList(new Version2LicenseDecoder(), new Version1LicenseDecoder()));
        this.productLicenseFactories.put(Product.JIRA, (ProductLicenseFactory)new JiraProductLicenseFactory());
        this.productLicenseFactories.put(Product.CONFLUENCE, (ProductLicenseFactory)new ConfluenceProductLicenseFactory());
        this.productLicenseFactories.put(Product.BAMBOO, (ProductLicenseFactory)new BambooProductLicenseFactory());
        this.productLicenseFactories.put(Product.CROWD, (ProductLicenseFactory)new CrowdProductLicenseFactory());
        this.productLicenseFactories.put(Product.CLOVER, (ProductLicenseFactory)new CloverProductLicenseFactory());
        this.productLicenseFactories.put(Product.FISHEYE, (ProductLicenseFactory)new FisheyeProductLicenseFactory());
        this.productLicenseFactories.put(Product.CRUCIBLE, (ProductLicenseFactory)new CrucibleProductLicenseFactory());
        this.productLicenseFactories.put(Product.STASH, (ProductLicenseFactory)new StashProductLicenseFactory());
        this.productLicenseFactories.put(Product.EDIT_LIVE_PLUGIN, (ProductLicenseFactory)new PluginLicenseFactory(Product.EDIT_LIVE_PLUGIN));
        this.productLicenseFactories.put(Product.VSS_PLUGIN, (ProductLicenseFactory)new PluginLicenseFactory(Product.VSS_PLUGIN));
        this.productLicenseFactories.put(Product.SHAREPOINT_PLUGIN, (ProductLicenseFactory)new PluginLicenseFactory(Product.SHAREPOINT_PLUGIN));
        this.productLicenseFactories.put(Product.PERFORCE_PLUGIN, (ProductLicenseFactory)new PluginLicenseFactory(Product.PERFORCE_PLUGIN));
        this.productLicenseFactories.put(Product.GREENHOPPER, (ProductLicenseFactory)new GreenHopperProductLicenseFactory());
        this.productLicenseFactories.put(Product.TEAM_CALENDARS, (ProductLicenseFactory)new PluginLicenseFactory(Product.TEAM_CALENDARS));
        this.productLicenseFactories.put(Product.BONFIRE, (ProductLicenseFactory)new PluginLicenseFactory(Product.BONFIRE));
        this.productLicenseFactories.put(Product.ALL_PLUGINS, (ProductLicenseFactory)new PluginLicenseFactory(Product.ALL_PLUGINS));
        this.rebuildLicenseManager();
    }

    @Override
    public synchronized LicenseManager getLicenseManager() {
        return this.licenseManager;
    }

    @Override
    public synchronized LicenseManager registerPlugin(Product plugin) {
        if (!this.productLicenseFactories.containsKey(plugin) || this.licenseManager == null) {
            this.productLicenseFactories.put(plugin, (ProductLicenseFactory)new PluginLicenseFactory(plugin));
            this.rebuildLicenseManager();
        }
        return this.licenseManager;
    }

    private void rebuildLicenseManager() {
        DefaultAtlassianLicenseFactory licenseFactory = new DefaultAtlassianLicenseFactory(Collections.unmodifiableMap(this.productLicenseFactories));
        this.licenseManager = new DefaultLicenseManager(this.decoder, (AtlassianLicenseFactory)licenseFactory);
    }
}

