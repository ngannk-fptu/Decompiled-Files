/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginInformation
 *  com.atlassian.plugin.metadata.PluginMetadataManager
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.properties.appenders;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginInformation;
import com.atlassian.plugin.metadata.PluginMetadataManager;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.troubleshooting.stp.spi.RootLevelSupportDataAppender;
import com.atlassian.troubleshooting.stp.spi.SupportDataBuilder;
import java.util.Locale;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;

public class PluginsSupportDataAppender
extends RootLevelSupportDataAppender {
    private final PluginAccessor pluginAccessor;
    private final PluginMetadataManager pluginMetadataManager;
    private final I18nResolver i18nResolver;

    @Autowired
    public PluginsSupportDataAppender(PluginAccessor pluginAccessor, I18nResolver i18nResolver, PluginMetadataManager pluginMetadataManager) {
        this.pluginAccessor = pluginAccessor;
        this.pluginMetadataManager = pluginMetadataManager;
        this.i18nResolver = i18nResolver;
    }

    @Override
    protected void addSupportData(@Nonnull SupportDataBuilder builder) {
        Objects.requireNonNull(builder);
        this.addPluginsInformation(builder.addCategory("stp.properties.plugins"));
    }

    private void addPluginsInformation(@Nonnull SupportDataBuilder builder) {
        Objects.requireNonNull(builder);
        for (Plugin plugin : this.pluginAccessor.getPlugins()) {
            this.addPluginInformation(builder.addCategory("stp.properties.plugins.plugin"), plugin);
        }
    }

    private void addPluginInformation(SupportDataBuilder pluginBuilder, @Nonnull Plugin plugin) {
        Objects.requireNonNull(plugin);
        PluginInformation pluginInformation = plugin.getPluginInformation();
        pluginBuilder.addValue("stp.properties.plugins.plugin.key", plugin.getKey());
        pluginBuilder.addValue("stp.properties.plugins.plugin.name", plugin.getName());
        pluginBuilder.addValue("stp.properties.plugins.plugin.version", pluginInformation.getVersion());
        pluginBuilder.addValue("stp.properties.plugins.plugin.vendor", pluginInformation.getVendorName());
        pluginBuilder.addValue("stp.properties.plugins.plugin.status", plugin.getPluginState().toString());
        pluginBuilder.addValue("stp.properties.plugins.plugin.vendor.url", pluginInformation.getVendorUrl());
        pluginBuilder.addValue("stp.properties.plugins.plugin.framework.version", String.valueOf(plugin.getPluginsVersion()));
        String pluginBundled = plugin.isBundledPlugin() ? this.i18nResolver.getText(Locale.US, "stp.properties.plugins.plugin.bundled") : (this.pluginMetadataManager.isSystemProvided(plugin) ? this.i18nResolver.getText(Locale.US, "stp.properties.plugins.plugin.provided") : this.i18nResolver.getText(Locale.US, "stp.properties.plugins.plugin.user.installed"));
        pluginBuilder.addValue("stp.properties.plugins.plugin.bundled", pluginBundled);
    }
}

