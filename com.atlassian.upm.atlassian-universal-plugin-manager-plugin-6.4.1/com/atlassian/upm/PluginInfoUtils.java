/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginInformation
 */
package com.atlassian.upm;

import com.atlassian.plugin.PluginInformation;
import com.atlassian.upm.api.util.Option;

public abstract class PluginInfoUtils {
    public static final String PLUGIN_INFO_DATA_CENTER_STATUS_KEY_PARAM = "atlassian-data-center-status";
    public static final String PLUGIN_INFO_DATA_CENTER_STATUS_COMPATIBLE = "compatible";
    public static final String PLUGIN_INFO_PLUGIN_TYPE_KEY_PARAM = "plugin-type";
    public static final String PLUGIN_INFO_PLUGIN_TYPE_DATACENTER = "data-center";
    public static final String PLUGIN_INFO_PLUGIN_TYPE_SERVER = "server";

    public static Option<String> getStringPluginInfoParam(PluginInformation info, String param) {
        return Option.option(info.getParameters().get(param));
    }

    public static boolean getBooleanPluginInfoParam(PluginInformation info, String param) {
        return Boolean.parseBoolean(PluginInfoUtils.getStringPluginInfoParam(info, param).getOrElse("false"));
    }

    public static boolean isStatusDataCenterCompatibleAccordingToPluginDescriptor(PluginInformation plugin) {
        return (Boolean)PluginInfoUtils.getStringPluginInfoParam(plugin, PLUGIN_INFO_DATA_CENTER_STATUS_KEY_PARAM).map(PLUGIN_INFO_DATA_CENTER_STATUS_COMPATIBLE::equalsIgnoreCase).getOrElse(false);
    }

    public static String getPluginType(PluginInformation plugin) {
        return PluginInfoUtils.getStringPluginInfoParam(plugin, PLUGIN_INFO_PLUGIN_TYPE_KEY_PARAM).getOrElse("");
    }

    public static boolean isDataCenterApp(PluginInformation plugin) {
        return (Boolean)PluginInfoUtils.getStringPluginInfoParam(plugin, PLUGIN_INFO_PLUGIN_TYPE_KEY_PARAM).map(PLUGIN_INFO_PLUGIN_TYPE_DATACENTER::equalsIgnoreCase).getOrElse(false);
    }

    public static boolean isServerApp(PluginInformation plugin) {
        return (Boolean)PluginInfoUtils.getStringPluginInfoParam(plugin, PLUGIN_INFO_PLUGIN_TYPE_KEY_PARAM).map(PLUGIN_INFO_PLUGIN_TYPE_SERVER::equalsIgnoreCase).getOrElse(false);
    }
}

