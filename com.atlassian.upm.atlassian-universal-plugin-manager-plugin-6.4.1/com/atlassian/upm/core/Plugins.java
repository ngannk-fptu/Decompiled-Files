/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginInformation
 *  com.atlassian.plugin.PluginRestartState
 *  com.atlassian.plugin.osgi.factory.OsgiPlugin
 */
package com.atlassian.upm.core;

import com.atlassian.marketplace.client.model.Addon;
import com.atlassian.marketplace.client.model.AddonVersionBase;
import com.atlassian.plugin.PluginInformation;
import com.atlassian.plugin.PluginRestartState;
import com.atlassian.plugin.osgi.factory.OsgiPlugin;
import com.atlassian.upm.PluginControlHandlerRegistry;
import com.atlassian.upm.PluginInfoUtils;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.DefaultHostApplicationInformation;
import com.atlassian.upm.core.HostApplicationDescriptor;
import com.atlassian.upm.core.HostingType;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginMetadataAccessor;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.install.ConnectPluginControlHandlerRegistryImpl;
import com.atlassian.upm.spi.PluginControlHandler;
import java.text.Collator;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class Plugins {
    public static final String PLUGIN_INFO_LEGACY_DATA_CENTER_COMPATIBLE_KEY_PARAM = "atlassian-data-center-compatible";
    public static Function<Plugin, String> toPluginKey = Plugin::getKey;
    public static Function<Plugin, String> toPluginName = Plugin::getName;
    public static Function<Plugin, String> plugToPluginKey = Plugin::getKey;
    public static Function<Plugin, com.atlassian.plugin.Plugin> toPlugPlugin = Plugin::getPlugin;

    public static boolean isConnectPlugin(com.atlassian.plugin.Plugin plugin, PluginControlHandlerRegistry pluginControlHandlerRegistry) {
        Dictionary headers;
        if (plugin instanceof OsgiPlugin && (headers = ((OsgiPlugin)plugin).getBundle().getHeaders()).get("Atlassian-Connect-Addon") != null) {
            return true;
        }
        if (pluginControlHandlerRegistry instanceof ConnectPluginControlHandlerRegistryImpl) {
            for (PluginControlHandler controller : pluginControlHandlerRegistry.getHandlers()) {
                if (!controller.canControl(plugin.getKey())) continue;
                return true;
            }
        }
        return Plugins.isXmlConnectAddon(plugin);
    }

    public static boolean isXmlConnectAddon(com.atlassian.plugin.Plugin plugin) {
        Dictionary headers;
        return plugin instanceof OsgiPlugin && (headers = ((OsgiPlugin)plugin).getBundle().getHeaders()).get("Remote-Plugin") != null;
    }

    public static Predicate<Plugin> enabled(boolean enabled, PluginRetriever pluginRetriever) {
        return plugin -> enabled == pluginRetriever.isPluginEnabled(plugin.getKey());
    }

    public static Predicate<Plugin> userInstalled(PluginMetadataAccessor metadata) {
        return metadata::isUserInstalled;
    }

    public static Predicate<Plugin> optional(PluginMetadataAccessor metadata) {
        return metadata::isOptional;
    }

    public static Predicate<Plugin> upmPlugin() {
        return Plugin::isUpmPlugin;
    }

    public static Predicate<Plugin> waitingForRestart() {
        return Plugins::hasRestartRequiredChange;
    }

    public static boolean hasRestartRequiredChange(Plugin plugin) {
        return !PluginRestartState.NONE.equals((Object)plugin.getRestartState());
    }

    public static Function<String, Option<Plugin>> toInstalledPlugin(PluginRetriever retriever) {
        return retriever::getPlugin;
    }

    public static boolean isStatusDataCenterCompatibleAccordingToPluginDescriptor(Plugin plugin) {
        return PluginInfoUtils.isStatusDataCenterCompatibleAccordingToPluginDescriptor(plugin.getPluginInformation());
    }

    public static boolean isLegacyDataCenterCompatibleAccordingToPluginDescriptor(Plugin plugin) {
        return PluginInfoUtils.getBooleanPluginInfoParam(plugin.getPluginInformation(), PLUGIN_INFO_LEGACY_DATA_CENTER_COMPATIBLE_KEY_PARAM);
    }

    public static HostingType getPluginHostingType(PluginInformation pluginInformation, DefaultHostApplicationInformation hostApplicationInformation) {
        return PluginInfoUtils.isStatusDataCenterCompatibleAccordingToPluginDescriptor(pluginInformation) ? hostApplicationInformation.getHostingType() : HostingType.SERVER;
    }

    public static HostingType getAddonHostingType(Addon addon, DefaultHostApplicationInformation hostApplicationInformation) {
        return (HostingType)((Object)addon.getVersion().filter(AddonVersionBase::isDataCenterStatusCompatible).map(av -> hostApplicationInformation.getHostingType()).getOrElse((Object)HostingType.SERVER));
    }

    public static Predicate<Integer> hasCloudFreeUsers(HostApplicationDescriptor hostApplicationDescriptor) {
        return cloudFreeUsers -> hostApplicationDescriptor.getActiveEditionCount() <= cloudFreeUsers;
    }

    public static final class PluginOrdering
    implements Comparator<Plugin> {
        private final Collator collator;

        public PluginOrdering(Locale locale) {
            this.collator = Collator.getInstance(locale);
        }

        @Override
        public int compare(Plugin p1, Plugin p2) {
            int result = this.collator.compare(PluginOrdering.getNameOrKey(p1), PluginOrdering.getNameOrKey(p2));
            return result != 0 ? result : this.collator.compare(p1.getKey(), p2.getKey());
        }

        private static String getNameOrKey(Plugin p) {
            String name = p.getName();
            return name != null ? name : p.getKey();
        }

        public List<Plugin> sortedCopy(List<Plugin> elements) {
            return elements.stream().sorted(this).collect(Collectors.toList());
        }
    }
}

