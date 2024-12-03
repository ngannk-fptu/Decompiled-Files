/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.TimePeriod
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.Plugin
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.featurediscovery.service;

import com.atlassian.confluence.plugins.featurediscovery.FeatureCompleteKey;
import com.atlassian.confluence.plugins.featurediscovery.FeatureMetadata;
import com.atlassian.confluence.plugins.featurediscovery.manager.DiscoveredFeatureManager;
import com.atlassian.confluence.plugins.featurediscovery.manager.FeatureMetadataManager;
import com.atlassian.confluence.plugins.featurediscovery.model.DiscoveredFeature;
import com.atlassian.confluence.plugins.featurediscovery.service.DefaultPluginFeaturesService;
import com.atlassian.confluence.plugins.featurediscovery.service.FeatureDiscoveryService;
import com.atlassian.confluence.plugins.featurediscovery.service.PluginFeaturesService;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.TimePeriod;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.Plugin;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

public class DefaultFeatureDiscoveryService
implements FeatureDiscoveryService {
    private static final TimePeriod DEFAULT_NEW_TIME_PERIOD = new TimePeriod(7L, TimeUnit.DAYS);
    private final DiscoveredFeatureManager discoveredFeatureManager;
    private final FeatureMetadataManager featureMetadataManager;
    private final SystemInformationService sysInfoService;

    public DefaultFeatureDiscoveryService(DiscoveredFeatureManager discoveredFeatureManager, FeatureMetadataManager featureMetadataManager, SystemInformationService sysInfoService) {
        this.discoveredFeatureManager = discoveredFeatureManager;
        this.featureMetadataManager = featureMetadataManager;
        this.sysInfoService = sysInfoService;
    }

    @Override
    public boolean isNew(ModuleCompleteKey moduleCompleteKey) {
        return this.isNew(moduleCompleteKey.getPluginKey(), moduleCompleteKey.getModuleKey(), DEFAULT_NEW_TIME_PERIOD);
    }

    @Override
    public boolean isNew(ModuleCompleteKey moduleCompleteKey, TimePeriod timePeriod) {
        return this.isNew(moduleCompleteKey.getPluginKey(), moduleCompleteKey.getModuleKey(), timePeriod);
    }

    @Override
    public boolean isNew(String context, String key) {
        return this.isNew(context, key, DEFAULT_NEW_TIME_PERIOD);
    }

    @Override
    public boolean isNew(String context, String key, TimePeriod timePeriod) {
        Date moduleInstallationDate = this.featureMetadataManager.getInstallationDate(new FeatureCompleteKey(context, key));
        return this.isWithinNewPeriod(timePeriod, moduleInstallationDate);
    }

    @Override
    public List<ModuleCompleteKey> getNew(List<ModuleCompleteKey> moduleCompleteKeys) {
        return this.getNew(moduleCompleteKeys, DEFAULT_NEW_TIME_PERIOD);
    }

    @Override
    public List<ModuleCompleteKey> getNew(List<ModuleCompleteKey> moduleCompleteKeys, TimePeriod timePeriod) {
        List<FeatureMetadata> modulesWithInstallationDate = this.featureMetadataManager.getModules(moduleCompleteKeys);
        ImmutableList.Builder newModuleCompleteKeys = ImmutableList.builder();
        for (FeatureMetadata module : modulesWithInstallationDate) {
            if (!this.isWithinNewPeriod(timePeriod, module.getInstallationDate())) continue;
            newModuleCompleteKeys.add((Object)new ModuleCompleteKey(module.getContext(), module.getKey()));
        }
        return newModuleCompleteKeys.build();
    }

    @Override
    public List<FeatureCompleteKey> getNewFeatures(List<FeatureCompleteKey> featureCompleteKeys) {
        return this.getNewFeatures(featureCompleteKeys, DEFAULT_NEW_TIME_PERIOD);
    }

    @Override
    public List<FeatureCompleteKey> getNewFeatures(List<FeatureCompleteKey> featureCompleteKeys, TimePeriod timePeriod) {
        List<FeatureMetadata> featuresWithInstallationDate = this.featureMetadataManager.getFeatures(featureCompleteKeys);
        ImmutableList.Builder newFeatureCompleteKeys = ImmutableList.builder();
        for (FeatureMetadata feature : featuresWithInstallationDate) {
            if (!this.isWithinNewPeriod(timePeriod, feature.getInstallationDate())) continue;
            newFeatureCompleteKeys.add((Object)new FeatureCompleteKey(feature.getContext(), feature.getKey()));
        }
        return newFeatureCompleteKeys.build();
    }

    @Override
    public void register(ModuleCompleteKey moduleCompleteKey) {
        this.register(moduleCompleteKey.getPluginKey(), moduleCompleteKey.getModuleKey(), new Date());
    }

    @Override
    public void register(ModuleCompleteKey moduleCompleteKey, Date installationDate) {
        this.register(moduleCompleteKey.getPluginKey(), moduleCompleteKey.getModuleKey(), installationDate);
    }

    @Override
    public void register(FeatureCompleteKey featureCompleteKey) {
        this.register(featureCompleteKey.getContext(), featureCompleteKey.getKey(), new Date());
    }

    @Override
    public void register(FeatureCompleteKey featureCompleteKey, Date installationDate) {
        this.register(featureCompleteKey.getContext(), featureCompleteKey.getKey(), installationDate);
    }

    @Override
    public void register(String context, String key) {
        this.register(context, key, new Date());
    }

    @Override
    public void register(String context, String key, Date installationDate) {
        this.featureMetadataManager.save(new FeatureCompleteKey(context, key), installationDate);
    }

    @Override
    public void unregister(ModuleCompleteKey moduleCompleteKey) {
        this.unregister(moduleCompleteKey.getPluginKey(), moduleCompleteKey.getModuleKey());
    }

    @Override
    public void unregister(FeatureCompleteKey featureCompleteKey) {
        this.unregister(featureCompleteKey.getContext(), featureCompleteKey.getKey());
    }

    @Override
    public void unregister(String context, String key) {
        this.featureMetadataManager.delete(new FeatureCompleteKey(context, key));
    }

    @Override
    public PluginFeaturesService forPlugin(@Nonnull Plugin plugin) {
        Preconditions.checkNotNull((Object)plugin);
        return new DefaultPluginFeaturesService(this.discoveredFeatureManager, plugin);
    }

    @Override
    public List<DiscoveredFeature> getFeaturesDiscoveredByUser(@Nonnull ConfluenceUser user) {
        Preconditions.checkNotNull((Object)user);
        return this.discoveredFeatureManager.listForUser(user.getKey().getStringValue());
    }

    @Override
    public Date getFeatureInstallationDate(String context, String key) {
        return this.getFeatureInstallationDate(new FeatureCompleteKey(context, key));
    }

    @Override
    public Date getFeatureInstallationDate(@Nonnull FeatureCompleteKey featureCompleteKey) {
        return this.featureMetadataManager.getInstallationDate(featureCompleteKey);
    }

    @Override
    public Date getFeatureInstallationDate(@Nonnull ModuleCompleteKey moduleCompleteKey) {
        return this.getFeatureInstallationDate(moduleCompleteKey.getPluginKey(), moduleCompleteKey.getModuleKey());
    }

    private boolean isWithinNewPeriod(TimePeriod timePeriod, Date moduleInstallationDate) {
        if (moduleInstallationDate == null) {
            return false;
        }
        Date applicationInstallationDate = this.getFreshInstallationDate();
        Date minNewDate = this.getMinNewDate(timePeriod);
        return moduleInstallationDate.after(minNewDate) && !moduleInstallationDate.before(applicationInstallationDate);
    }

    private Date getFreshInstallationDate() {
        Calendar instance = Calendar.getInstance();
        instance.setTime(this.sysInfoService.getConfluenceInfo().getInstallationDate());
        return instance.getTime();
    }

    private Date getMinNewDate(TimePeriod timePeriod) {
        Calendar minNewDate = Calendar.getInstance();
        minNewDate.add(14, (int)(-1L * timePeriod.convertTo(TimeUnit.MILLISECONDS)));
        return minNewDate.getTime();
    }
}

