/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.upm.license;

import com.atlassian.plugin.Plugin;
import com.atlassian.upm.PluginInfoUtils;
import com.atlassian.upm.core.LicensingUsageVerifier;
import java.util.Arrays;
import java.util.List;

public abstract class LicensedPlugins {
    public static final String PLUGIN_INFO_USES_LICENSING_PARAM = "atlassian-licensing-enabled";
    public static final String PLUGIN_INFO_USES_LICENSING_ON_SERVER_PARAM = "server-licensing-enabled";
    public static final String PLUGIN_INFO_IS_LICENSED_WITH_ANOTHER_PLUGIN = "carebear-licensed-with";
    public static final String TC_KEY = "com.atlassian.confluence.extra.team-calendars";
    public static final String BF_KEY = "com.atlassian.bonfire.plugin";
    private static final String GH_KEY = "com.pyxis.greenhopper.jira";
    private static final String SP_KEY = "com.atlassian.confluence.extra.sharepoint";
    private static final List<String> ATLASSIAN_LICENSED_PLUGINS = Arrays.asList("com.atlassian.bonfire.plugin", "com.pyxis.greenhopper.jira", "com.atlassian.confluence.extra.sharepoint", "com.atlassian.confluence.extra.team-calendars");

    private LicensedPlugins() {
    }

    public static boolean usesLicensing(Plugin plugin, LicensingUsageVerifier licensingUsageVerifier) {
        return licensingUsageVerifier.usesLicensing(plugin);
    }

    public static boolean isLegacyLicensePlugin(String pluginKey) {
        return ATLASSIAN_LICENSED_PLUGINS.contains(pluginKey);
    }

    public static boolean hasLicensingEnabledParam(Plugin plugin) {
        return PluginInfoUtils.getBooleanPluginInfoParam(plugin.getPluginInformation(), PLUGIN_INFO_USES_LICENSING_PARAM);
    }
}

