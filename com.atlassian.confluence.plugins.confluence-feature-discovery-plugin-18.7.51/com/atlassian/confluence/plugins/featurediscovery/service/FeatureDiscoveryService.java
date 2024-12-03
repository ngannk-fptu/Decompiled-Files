/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.TimePeriod
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.Plugin
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.featurediscovery.service;

import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.plugins.featurediscovery.FeatureCompleteKey;
import com.atlassian.confluence.plugins.featurediscovery.model.DiscoveredFeature;
import com.atlassian.confluence.plugins.featurediscovery.service.PluginFeaturesService;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.TimePeriod;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.Plugin;
import java.util.Date;
import java.util.List;
import javax.annotation.Nonnull;

@PublicApi
public interface FeatureDiscoveryService {
    public boolean isNew(ModuleCompleteKey var1);

    public boolean isNew(ModuleCompleteKey var1, TimePeriod var2);

    public boolean isNew(String var1, String var2);

    public boolean isNew(String var1, String var2, TimePeriod var3);

    public List<ModuleCompleteKey> getNew(List<ModuleCompleteKey> var1);

    public List<ModuleCompleteKey> getNew(List<ModuleCompleteKey> var1, TimePeriod var2);

    public List<FeatureCompleteKey> getNewFeatures(List<FeatureCompleteKey> var1);

    public List<FeatureCompleteKey> getNewFeatures(List<FeatureCompleteKey> var1, TimePeriod var2);

    public void register(ModuleCompleteKey var1);

    public void register(ModuleCompleteKey var1, Date var2);

    public void register(FeatureCompleteKey var1);

    public void register(FeatureCompleteKey var1, Date var2);

    public void register(String var1, String var2);

    public void register(String var1, String var2, Date var3);

    public void unregister(ModuleCompleteKey var1);

    public void unregister(FeatureCompleteKey var1);

    public void unregister(String var1, String var2);

    public PluginFeaturesService forPlugin(@Nonnull Plugin var1);

    public List<DiscoveredFeature> getFeaturesDiscoveredByUser(@Nonnull ConfluenceUser var1);

    public Date getFeatureInstallationDate(String var1, String var2);

    public Date getFeatureInstallationDate(@Nonnull FeatureCompleteKey var1);

    public Date getFeatureInstallationDate(@Nonnull ModuleCompleteKey var1);
}

