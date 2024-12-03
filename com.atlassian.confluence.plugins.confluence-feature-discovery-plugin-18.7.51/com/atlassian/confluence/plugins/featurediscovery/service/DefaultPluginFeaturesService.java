/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.Plugin
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.featurediscovery.service;

import com.atlassian.confluence.plugins.featurediscovery.manager.DiscoveredFeatureManager;
import com.atlassian.confluence.plugins.featurediscovery.service.PluginFeaturesService;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.Plugin;
import java.util.Date;
import java.util.Objects;
import javax.annotation.Nonnull;

public class DefaultPluginFeaturesService
implements PluginFeaturesService {
    private final DiscoveredFeatureManager discoveredFeatureManager;
    private final Plugin plugin;

    public DefaultPluginFeaturesService(DiscoveredFeatureManager discoveredFeatureManager, Plugin plugin) {
        this.discoveredFeatureManager = discoveredFeatureManager;
        this.plugin = plugin;
    }

    @Override
    public boolean isDiscovered(@Nonnull ConfluenceUser user, @Nonnull String featureKey) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(featureKey);
        return this.discoveredFeatureManager.find(this.plugin.getKey(), featureKey, user.getKey().getStringValue()) != null;
    }

    @Override
    public void markDiscovered(@Nonnull ConfluenceUser user, @Nonnull String featureKey) {
        if (this.discoveredFeatureManager.find(this.plugin.getKey(), featureKey, user.getKey().getStringValue()) == null) {
            this.discoveredFeatureManager.create(this.plugin.getKey(), featureKey, user.getKey().getStringValue(), new Date());
        }
    }

    @Override
    public void markUndiscovered(@Nonnull ConfluenceUser user, @Nonnull String featureKey) {
        this.discoveredFeatureManager.delete(this.plugin.getKey(), featureKey, user.getKey().getStringValue());
    }
}

