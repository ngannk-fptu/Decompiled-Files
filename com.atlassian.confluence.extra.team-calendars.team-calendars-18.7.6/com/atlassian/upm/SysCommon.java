/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.upm.api.util.Option
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.upm;

import com.atlassian.upm.api.util.Option;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.StringUtils;

public abstract class SysCommon {
    public static final String UPM_ON_DEMAND = "atlassian.upm.on.demand";
    public static final String UPM_OD_PVA_BLACKLIST = "atlassian.upm.on.demand.pva.blacklist";
    private static final String DEFAULT_BLACKLIST_VALUE = "jira-timesheet-plugin,com.balsamiq.jira.plugins.mockups,com.balsamiq.confluence.plugins.mockups,org.swift.confluence.table";

    public static boolean isOnDemand() {
        return Boolean.getBoolean(UPM_ON_DEMAND);
    }

    public static Iterable<String> getOnDemandPaidViaAtlassianBlacklist() {
        return SysCommon.getPluginKeysFromSysProp(UPM_OD_PVA_BLACKLIST, (Option<String>)Option.some((Object)DEFAULT_BLACKLIST_VALUE));
    }

    public static Iterable<String> getPluginKeysFromSysProp(String propKey, Option<String> defaultValue) {
        String pluginKeys;
        String string = pluginKeys = defaultValue.isDefined() ? System.getProperty(propKey, (String)defaultValue.get()) : System.getProperty(propKey);
        if (!SysCommon.isOnDemand() || StringUtils.isBlank(pluginKeys)) {
            return ImmutableList.of();
        }
        return ImmutableList.of((Object[])pluginKeys.trim().split(","));
    }
}

