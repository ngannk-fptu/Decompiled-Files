/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.featurediscovery.service.FeatureDiscoveryService
 *  com.atlassian.confluence.plugins.recentlyviewed.RecentlyViewedManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 */
package com.atlassian.confluence.plugins.dashboard.service;

import com.atlassian.confluence.plugins.dashboard.service.OnboardingService;
import com.atlassian.confluence.plugins.featurediscovery.service.FeatureDiscoveryService;
import com.atlassian.confluence.plugins.recentlyviewed.RecentlyViewedManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;

public class DefaultOnboardingService
implements OnboardingService {
    private final UserAccessor userAccessor;
    private final PluginAccessor pluginAccessor;
    private final FeatureDiscoveryService featureDiscoveryService;
    private final RecentlyViewedManager recentlyViewedManager;
    private static final String PLUGIN_KEY = "com.atlassian.confluence.plugins.confluence-dashboard";
    private static final String CONFLUENCE_ONBOARDING_KEY = "efi.store.onboarding.onboarding-state:introWorkflow";

    public DefaultOnboardingService(UserAccessor userAccessor, PluginAccessor pluginAccessor, FeatureDiscoveryService featureDiscoveryService, RecentlyViewedManager recentlyViewedManager) {
        this.userAccessor = userAccessor;
        this.pluginAccessor = pluginAccessor;
        this.featureDiscoveryService = featureDiscoveryService;
        this.recentlyViewedManager = recentlyViewedManager;
    }

    @Override
    public boolean shouldShowDialog(ConfluenceUser user) {
        return this.hasNotSeenConfluenceOnboarding(user) && !this.hasDiscoveredDashboardDialog(user);
    }

    @Override
    public boolean shouldShowTips(ConfluenceUser user) {
        return this.hasNotSeenConfluenceOnboarding(user) && !this.hasDiscoveredDashboardTips(user);
    }

    @Override
    public boolean isNewUser(ConfluenceUser user) {
        return this.recentlyViewedManager.getRecentlyViewed(user.getKey().getStringValue(), 1).isEmpty();
    }

    protected boolean hasNotSeenConfluenceOnboarding(ConfluenceUser user) {
        return !this.userAccessor.getPropertySet(user).exists(CONFLUENCE_ONBOARDING_KEY);
    }

    protected boolean hasDiscoveredDashboardDialog(ConfluenceUser user) {
        Plugin plugin = this.pluginAccessor.getPlugin(PLUGIN_KEY);
        return this.featureDiscoveryService.forPlugin(plugin).isDiscovered(user, "dialog");
    }

    protected boolean hasDiscoveredDashboardTips(ConfluenceUser user) {
        Plugin plugin = this.pluginAccessor.getPlugin(PLUGIN_KEY);
        return this.featureDiscoveryService.forPlugin(plugin).isDiscovered(user, "tips");
    }
}

