/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.healthcheck.spi.HealthCheckWhitelist
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.impl.UnloadablePlugin
 *  com.google.common.base.Preconditions
 */
package com.atlassian.healthcheck.checks.plugin;

import com.atlassian.healthcheck.checks.plugin.AbstractPluginHealthCheck;
import com.atlassian.healthcheck.checks.plugin.OnceOnlyLogger;
import com.atlassian.healthcheck.spi.HealthCheckWhitelist;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.impl.UnloadablePlugin;
import com.google.common.base.Preconditions;
import java.util.List;
import java.util.stream.Collectors;

public final class DisabledPluginsHealthCheck
extends AbstractPluginHealthCheck {
    private static final String HARD_FAIL_SYSPROP = "atlassian.healthcheck.DisabledPluginsHealthCheck.hardFail";
    private static final String FORCE_NO_FAIL_SYSPROP = "atlassian.healthcheck.DisabledPluginsHealthCheck.forceNoFail";
    private static final String DISABLE_CHECK_SYSPROP = "atlassian.healthcheck.DisabledPluginsHealthCheck.disable";
    private final PluginAccessor pluginAccessor;

    public DisabledPluginsHealthCheck(PluginAccessor pluginAccessor, OnceOnlyLogger logger, HealthCheckWhitelist healthCheckWhitelist) {
        super(logger, healthCheckWhitelist);
        this.pluginAccessor = (PluginAccessor)Preconditions.checkNotNull((Object)pluginAccessor);
    }

    @Override
    String getWhitelistKey() {
        return "DisabledPluginsHealthCheck";
    }

    @Override
    String getHardFailPropertyName() {
        return HARD_FAIL_SYSPROP;
    }

    @Override
    String getForceNoFailPropertyName() {
        return FORCE_NO_FAIL_SYSPROP;
    }

    @Override
    String getDisableCheckPropertyName() {
        return DISABLE_CHECK_SYSPROP;
    }

    @Override
    String getFailureMessagePreamble() {
        return "These plugins should be enabled, but are disabled. (Failed to start, manually or programmatically disabled, or a missing dependency)";
    }

    @Override
    List<String> getItemsFailingCheck() {
        return this.pluginAccessor.getPlugins(plugin -> !DisabledPluginsHealthCheck.pluginIsSupposedToBeDisabled(plugin) && !this.pluginAccessor.isPluginEnabled(plugin.getKey())).stream().map(Plugin::getKey).sorted().collect(Collectors.toList());
    }

    private static boolean pluginIsSupposedToBeDisabled(Plugin plugin) {
        if (plugin instanceof UnloadablePlugin) {
            return false;
        }
        return !plugin.isEnabledByDefault();
    }
}

