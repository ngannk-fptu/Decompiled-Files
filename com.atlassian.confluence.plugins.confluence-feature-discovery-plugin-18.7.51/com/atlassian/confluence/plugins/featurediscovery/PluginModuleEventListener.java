/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.lifecycle.LifecycleManager
 *  com.atlassian.confluence.event.events.plugin.AsyncPluginFrameworkStartedEvent
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.confluence.user.DarkFeatureEnabledCondition
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 *  com.atlassian.plugin.web.Condition
 *  com.atlassian.plugin.web.descriptors.ConditionalDescriptor
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.featurediscovery;

import com.atlassian.config.lifecycle.LifecycleManager;
import com.atlassian.confluence.event.events.plugin.AsyncPluginFrameworkStartedEvent;
import com.atlassian.confluence.plugins.featurediscovery.FeatureCompleteKey;
import com.atlassian.confluence.plugins.featurediscovery.FeatureMetadata;
import com.atlassian.confluence.plugins.featurediscovery.manager.FeatureMetadataManager;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.user.DarkFeatureEnabledCondition;
import com.atlassian.event.api.EventListener;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.plugin.web.Condition;
import com.atlassian.plugin.web.descriptors.ConditionalDescriptor;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginModuleEventListener {
    private static final Logger log = LoggerFactory.getLogger(PluginModuleEventListener.class);
    protected static final String SELF_PLUGIN_KEY = "com.atlassian.confluence.plugins.confluence-feature-discovery-plugin";
    private final PluginAccessor pluginAccessor;
    private final FeatureMetadataManager featureMetadataManager;
    private final SystemInformationService sysInfoService;
    private final LifecycleManager lifecycleManager;
    private final TransactionTemplate transactionTemplate;
    private boolean initialScanCompleted;

    public PluginModuleEventListener(PluginAccessor pluginAccessor, FeatureMetadataManager featureMetadataManager, SystemInformationService sysInfoService, LifecycleManager lifecycleManager, TransactionTemplate transactionTemplate) {
        this.pluginAccessor = pluginAccessor;
        this.featureMetadataManager = featureMetadataManager;
        this.sysInfoService = sysInfoService;
        this.lifecycleManager = lifecycleManager;
        this.transactionTemplate = transactionTemplate;
    }

    @EventListener
    public void onApplicationStart(AsyncPluginFrameworkStartedEvent event) {
        this.scanForNewPluginModules();
    }

    @EventListener
    public void onPluginEnabled(PluginEnabledEvent event) {
        if (!SELF_PLUGIN_KEY.equals(event.getPlugin().getKey())) {
            return;
        }
        if (!this.lifecycleManager.isStartedUp()) {
            return;
        }
        this.scanForNewPluginModules();
    }

    @EventListener
    public void onPluginModuleEnabled(PluginModuleEnabledEvent event) {
        if (!this.lifecycleManager.isStartedUp()) {
            return;
        }
        ModuleDescriptor module = event.getModule();
        if (SELF_PLUGIN_KEY.equals(module.getPluginKey())) {
            return;
        }
        if (this.isDarkModule(module)) {
            return;
        }
        ModuleCompleteKey moduleCompleteKey = new ModuleCompleteKey(module.getPluginKey(), module.getKey());
        log.debug("Plugin module discovered: {}", (Object)moduleCompleteKey);
        if (!this.initialScanCompleted) {
            log.debug("A scan has never been completed since starting the feature discovery plugin. Doing one now.");
            this.scanForNewPluginModules();
        }
        Date now = new Date();
        this.transactionTemplate.execute(() -> this.featureMetadataManager.save(new FeatureCompleteKey(moduleCompleteKey), now));
    }

    private synchronized void scanForNewPluginModules() {
        Date installationDate;
        log.debug("Scanning for new plugin modules");
        Date date = installationDate = this.featureMetadataManager.hasData() ? new Date() : this.sysInfoService.getConfluenceInfo().getInstallationDate();
        if (installationDate == null) {
            log.warn("Confluence installation date is null, cannot complete feature discovery process.");
            return;
        }
        ImmutableList.Builder pluginModules = ImmutableList.builder();
        Collection enabledPlugins = this.pluginAccessor.getEnabledPlugins();
        for (Plugin plugin : enabledPlugins) {
            Collection moduleDescriptors = plugin.getModuleDescriptors();
            for (ModuleDescriptor moduleDescriptor : moduleDescriptors) {
                if (!moduleDescriptor.isEnabled() || this.isDarkModule(moduleDescriptor)) continue;
                ModuleCompleteKey moduleCompleteKey = new ModuleCompleteKey(moduleDescriptor.getPluginKey(), moduleDescriptor.getKey());
                pluginModules.add((Object)new FeatureMetadata(moduleCompleteKey, installationDate));
            }
        }
        this.transactionTemplate.execute(() -> {
            this.featureMetadataManager.save((List<FeatureMetadata>)pluginModules.build());
            this.initialScanCompleted = true;
            return null;
        });
        log.debug("Finished scanning for new plugin modules");
    }

    private boolean isDarkModule(ModuleDescriptor<?> moduleDescriptor) {
        if (!(moduleDescriptor instanceof ConditionalDescriptor)) {
            return false;
        }
        ConditionalDescriptor conditionalDescriptor = (ConditionalDescriptor)moduleDescriptor;
        Condition condition = conditionalDescriptor.getCondition();
        return condition instanceof DarkFeatureEnabledCondition;
    }
}

