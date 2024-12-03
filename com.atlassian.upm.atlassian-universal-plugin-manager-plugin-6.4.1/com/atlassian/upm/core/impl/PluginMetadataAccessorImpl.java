/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.metadata.PluginMetadataManager
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.google.common.collect.Iterables
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.upm.core.impl;

import com.atlassian.plugin.metadata.PluginMetadataManager;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.upm.PluginControlHandlerRegistry;
import com.atlassian.upm.PluginInfoUtils;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.DefaultHostApplicationInformation;
import com.atlassian.upm.core.HostingType;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginMetadataAccessor;
import com.atlassian.upm.core.Plugins;
import com.atlassian.upm.core.Sys;
import com.google.common.collect.Iterables;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public final class PluginMetadataAccessorImpl
implements PluginMetadataAccessor {
    public static final String PLUGIN_INFO_ICON_PARAM = "plugin-icon";
    public static final String PLUGIN_INFO_LOGO_PARAM = "plugin-logo";
    public static final String PLUGIN_INFO_BANNER_PARAM = "plugin-banner";
    public static final String PLUGIN_INFO_VENDOR_ICON_PARAM = "vendor-icon";
    public static final String PLUGIN_INFO_VENDOR_LOGO_PARAM = "vendor-logo";
    private final ApplicationProperties applicationProperties;
    private final PluginControlHandlerRegistry pluginControlHandlerRegistry;
    private final PluginMetadataManager pluginMetadataManager;
    private final DefaultHostApplicationInformation hostApplicationInformation;

    public PluginMetadataAccessorImpl(ApplicationProperties applicationProperties, PluginControlHandlerRegistry pluginControlHandlerRegistry, PluginMetadataManager pluginMetadataManager, DefaultHostApplicationInformation hostApplicationInformation) {
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        this.pluginControlHandlerRegistry = Objects.requireNonNull(pluginControlHandlerRegistry, "pluginControlHandlerRegistry");
        this.pluginMetadataManager = Objects.requireNonNull(pluginMetadataManager, "pluginMetadataManager");
        this.hostApplicationInformation = Objects.requireNonNull(hostApplicationInformation, "hostApplicationInformation");
    }

    @Override
    public boolean isUserInstalled(Plugin plugin) {
        return this.isUserInstalledInternal(plugin.getPlugin(), plugin.isConnect());
    }

    @Override
    public boolean isUserInstalled(com.atlassian.plugin.Plugin plugin) {
        return this.isUserInstalledInternal(plugin, Plugins.isConnectPlugin(plugin, this.pluginControlHandlerRegistry));
    }

    private boolean isUserInstalledInternal(com.atlassian.plugin.Plugin plugin, boolean isConnect) {
        if (Iterables.contains(Sys.getOverriddenRequiredPluginKeys(), (Object)plugin.getKey())) {
            return false;
        }
        if (isConnect || this.isDataCenterOnlyBundleOnServer(plugin)) {
            return true;
        }
        Iterator<Iterable<String>> iterator = Sys.getP2OverriddenUserInstalledPluginKeys().iterator();
        if (iterator.hasNext()) {
            Iterable<String> userInstalledP2WhitelistKeys = iterator.next();
            return Iterables.contains(userInstalledP2WhitelistKeys, (Object)plugin.getKey());
        }
        iterator = Sys.getLegacyOverriddenUserInstalledPluginKeys().iterator();
        if (iterator.hasNext()) {
            Iterable<String> userInstalledWhitelistKeys = iterator.next();
            return Iterables.contains(userInstalledWhitelistKeys, (Object)plugin.getKey()) || this.pluginMetadataManager.isUserInstalled(plugin);
        }
        return this.pluginMetadataManager.isUserInstalled(plugin);
    }

    private boolean isDataCenterOnlyBundleOnServer(com.atlassian.plugin.Plugin plugin) {
        return HostingType.SERVER.equals((Object)this.hostApplicationInformation.getHostingType()) && PluginInfoUtils.getBooleanPluginInfoParam(plugin.getPluginInformation(), "server-licensing-enabled");
    }

    @Override
    public boolean isOptional(Plugin plugin) {
        if (Iterables.contains(Sys.getOverriddenRequiredPluginKeys(), (Object)plugin.getKey())) {
            return false;
        }
        return this.pluginMetadataManager.isOptional(plugin.getPlugin());
    }

    @Override
    public boolean isOptional(Plugin.Module module) {
        if (Iterables.contains(Sys.getOverriddenRequiredModuleKeys(), (Object)module.getCompleteKey())) {
            return false;
        }
        return this.pluginMetadataManager.isOptional(module.getModuleDescriptor());
    }

    @Override
    public Option<URI> getConfigureUrl(Plugin plugin) {
        return this.getPluginUriParam(plugin, "configure.url");
    }

    @Override
    public Option<URI> getPostInstallUri(Plugin plugin) {
        return this.getPluginUriParam(plugin, "post.install.url");
    }

    @Override
    public Option<URI> getPostUpdateUri(Plugin plugin) {
        return this.getPluginUriParam(plugin, "post.update.url");
    }

    private Option<URI> getPluginUriParam(Plugin plugin, String paramName) {
        String s = (String)plugin.getPluginInformation().getParameters().get(paramName);
        if (StringUtils.isNotBlank((CharSequence)s)) {
            try {
                return Option.some(URI.create(this.applicationProperties.getBaseUrl() + s.trim()));
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        return Option.none();
    }

    @Override
    public Option<InputStream> getPluginIconInputStream(Plugin plugin) {
        return this.getInputStreamForResource(plugin, PLUGIN_INFO_ICON_PARAM);
    }

    @Override
    public Option<InputStream> getPluginLogoInputStream(Plugin plugin) {
        return this.getInputStreamForResource(plugin, PLUGIN_INFO_LOGO_PARAM);
    }

    @Override
    public Option<InputStream> getPluginBannerInputStream(Plugin plugin) {
        return this.getInputStreamForResource(plugin, PLUGIN_INFO_BANNER_PARAM);
    }

    @Override
    public Option<InputStream> getVendorIconInputStream(Plugin plugin) {
        return this.getInputStreamForResource(plugin, PLUGIN_INFO_VENDOR_ICON_PARAM);
    }

    @Override
    public Option<InputStream> getVendorLogoInputStream(Plugin plugin) {
        return this.getInputStreamForResource(plugin, PLUGIN_INFO_VENDOR_LOGO_PARAM);
    }

    private Option<InputStream> getInputStreamForResource(Plugin plugin, String resourceItem) {
        Iterator iterator = Option.option(plugin.getPluginInformation().getParameters().get(resourceItem)).iterator();
        if (iterator.hasNext()) {
            String locationString = (String)iterator.next();
            InputStream is = plugin.getPlugin().getResourceAsStream(locationString);
            return Option.option(is);
        }
        return Option.none();
    }
}

