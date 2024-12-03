/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.pac;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.marketplace.client.api.AddonQuery;
import com.atlassian.marketplace.client.api.Page;
import com.atlassian.marketplace.client.model.AddonReference;
import com.atlassian.marketplace.client.model.AddonSummary;
import com.atlassian.marketplace.client.model.ApplicationVersion;
import com.atlassian.marketplace.client.model.Links;
import com.atlassian.upm.ProductUpdatePluginCompatibility;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.pac.AvailableAddonWithVersion;
import com.atlassian.upm.pac.IncompatiblePluginData;
import com.atlassian.upm.pac.PluginVersionPair;
import java.util.Collection;
import java.util.Optional;

public interface PacClient {
    public static final int PAGE_SIZE = 10;

    public Page<AddonSummary> findPlugins(AddonQuery var1) throws MpacException;

    public Option<AvailableAddonWithVersion> getAvailablePlugin(String var1) throws MpacException;

    public Collection<AvailableAddonWithVersion> getPlugins(Collection<String> var1);

    public Collection<AvailableAddonWithVersion> getLatestVersionOfPlugins(Collection<String> var1);

    public Option<PluginVersionPair> getSpecificAndLatestAvailablePluginVersions(Plugin var1, String var2);

    public Collection<String> getCategories() throws MpacException;

    public Page<AddonReference> findBanners(AddonQuery var1) throws MpacException;

    public Collection<AddonReference> getPluginRecommendations(String var1, int var2) throws MpacException;

    public Collection<ApplicationVersion> getProductUpdates() throws MpacException;

    public Collection<AvailableAddonWithVersion> getUpdates() throws MpacException;

    public Collection<AvailableAddonWithVersion> getUpdatesViaAutomatedJob() throws MpacException;

    public Optional<AvailableAddonWithVersion> getUpdate(Plugin var1) throws MpacException;

    public ProductUpdatePluginCompatibility getProductUpdatePluginCompatibility(Collection<Plugin> var1, int var2) throws MpacException;

    public Collection<IncompatiblePluginData> getIncompatiblePlugins(Collection<String> var1) throws MpacException;

    public Option<IncompatiblePluginData> getPluginIncompatibility(Plugin var1) throws MpacException;

    public Option<Boolean> isUnknownProductVersion();

    public boolean isPacReachable();

    public Option<Links> getMarketplaceRootLinks();

    public void forgetPacReachableState(boolean var1);

    public void clearAllCachedMarketplaceState();
}

