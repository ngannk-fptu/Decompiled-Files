/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.healthcheck.spi.HealthCheckWhitelist
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.google.common.base.Preconditions
 */
package com.atlassian.healthcheck.checks.plugin;

import com.atlassian.healthcheck.checks.plugin.AbstractPluginHealthCheck;
import com.atlassian.healthcheck.checks.plugin.OnceOnlyLogger;
import com.atlassian.healthcheck.spi.HealthCheckWhitelist;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.google.common.base.Preconditions;
import java.util.List;
import java.util.stream.Collectors;

public final class DisabledByDefaultPluginsHealthCheck
extends AbstractPluginHealthCheck {
    private static final String HARD_FAIL_SYSPROP = "atlassian.healthcheck.DisabledByDefaultPluginsHealthCheck.hardFail";
    private static final String FORCE_NO_FAIL_SYSPROP = "atlassian.healthcheck.DisabledByDefaultPluginsHealthCheck.forceNoFail";
    private static final String DISABLE_CHECK_SYSPROP = "atlassian.healthcheck.DisabledByDefaultPluginsHealthCheck.disable";
    private final PluginAccessor pluginAccessor;

    public DisabledByDefaultPluginsHealthCheck(PluginAccessor pluginAccessor, OnceOnlyLogger logger, HealthCheckWhitelist healthCheckWhitelist) {
        super(logger, healthCheckWhitelist);
        this.pluginAccessor = (PluginAccessor)Preconditions.checkNotNull((Object)pluginAccessor);
    }

    @Override
    String getWhitelistKey() {
        return "DisabledByDefaultPluginsHealthCheck";
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
        return "These plugins should be disabled, but are enabled. (Manually or programmatically enabled?)";
    }

    @Override
    List<String> getItemsFailingCheck() {
        return this.pluginAccessor.getPlugins(plugin -> !plugin.isEnabledByDefault() && this.pluginAccessor.isPluginEnabled(plugin.getKey())).stream().map(Plugin::getKey).sorted().collect(Collectors.toList());
    }
}

