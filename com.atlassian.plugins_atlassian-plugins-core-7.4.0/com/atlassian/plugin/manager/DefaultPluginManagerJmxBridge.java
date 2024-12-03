/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginArtifact
 *  com.atlassian.plugin.PluginInformation
 */
package com.atlassian.plugin.manager;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.PluginInformation;
import com.atlassian.plugin.jmx.AbstractJmxBridge;
import com.atlassian.plugin.jmx.JmxUtil;
import com.atlassian.plugin.jmx.PluginManagerMXBean;
import com.atlassian.plugin.manager.DefaultPluginManager;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

class DefaultPluginManagerJmxBridge
extends AbstractJmxBridge<PluginManagerMXBean>
implements PluginManagerMXBean {
    private static final AtomicInteger nextJmxInstance = new AtomicInteger();
    private final DefaultPluginManager defaultPluginManager;

    DefaultPluginManagerJmxBridge(DefaultPluginManager defaultPluginManager) {
        super(JmxUtil.objectName(nextJmxInstance, "PluginManager"), PluginManagerMXBean.class);
        this.defaultPluginManager = defaultPluginManager;
    }

    @Override
    protected PluginManagerMXBean getMXBean() {
        return this;
    }

    @Override
    public PluginManagerMXBean.PluginData[] getPlugins() {
        return (PluginManagerMXBean.PluginData[])this.defaultPluginManager.getPlugins().stream().map(plugin -> new PluginManagerMXBean.PluginData((Plugin)plugin){
            final /* synthetic */ Plugin val$plugin;
            {
                this.val$plugin = plugin;
            }

            @Override
            public String getKey() {
                return this.val$plugin.getKey();
            }

            @Override
            public String getVersion() {
                PluginInformation pluginInformation = this.val$plugin.getPluginInformation();
                return null == pluginInformation ? null : pluginInformation.getVersion();
            }

            @Override
            public String getLocation() {
                PluginArtifact pluginArtifact = this.val$plugin.getPluginArtifact();
                return null == pluginArtifact ? null : pluginArtifact.toFile().getAbsolutePath();
            }

            @Override
            public Long getDateLoaded() {
                Date dateLoaded = this.val$plugin.getDateLoaded();
                return null == dateLoaded ? null : Long.valueOf(dateLoaded.getTime());
            }

            @Override
            public Long getDateInstalled() {
                Date dateInstalled = this.val$plugin.getDateInstalled();
                return null == dateInstalled ? null : Long.valueOf(dateInstalled.getTime());
            }

            @Override
            public boolean isEnabled() {
                return DefaultPluginManagerJmxBridge.this.defaultPluginManager.isPluginEnabled(this.val$plugin.getKey());
            }

            @Override
            public boolean isEnabledByDefault() {
                return this.val$plugin.isEnabledByDefault();
            }

            @Override
            public boolean isBundledPlugin() {
                return this.val$plugin.isBundledPlugin();
            }
        }).toArray(PluginManagerMXBean.PluginData[]::new);
    }

    @Override
    public int scanForNewPlugins() {
        return this.defaultPluginManager.scanForNewPlugins();
    }
}

