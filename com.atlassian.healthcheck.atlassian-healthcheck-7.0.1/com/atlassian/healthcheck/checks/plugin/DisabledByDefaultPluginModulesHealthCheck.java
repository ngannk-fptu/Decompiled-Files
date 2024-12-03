/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.healthcheck.spi.HealthCheckWhitelist
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 *  com.google.common.base.Preconditions
 */
package com.atlassian.healthcheck.checks.plugin;

import com.atlassian.healthcheck.checks.plugin.AbstractPluginHealthCheck;
import com.atlassian.healthcheck.checks.plugin.OnceOnlyLogger;
import com.atlassian.healthcheck.spi.HealthCheckWhitelist;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.google.common.base.Preconditions;
import java.util.List;
import java.util.stream.Collectors;

public final class DisabledByDefaultPluginModulesHealthCheck
extends AbstractPluginHealthCheck {
    private static final String HARD_FAIL_SYSPROP = "atlassian.healthcheck.DisabledByDefaultPluginModulesHealthCheck.hardFail";
    private static final String FORCE_NO_FAIL_SYSPROP = "atlassian.healthcheck.DisabledByDefaultPluginModulesHealthCheck.forceNoFail";
    private static final String DISABLE_CHECK_SYSPROP = "atlassian.healthcheck.DisabledByDefaultPluginModulesHealthCheck.disable";
    private final PluginAccessor pluginAccessor;

    public DisabledByDefaultPluginModulesHealthCheck(PluginAccessor pluginAccessor, OnceOnlyLogger logger, HealthCheckWhitelist healthCheckWhitelist) {
        super(logger, healthCheckWhitelist);
        this.pluginAccessor = (PluginAccessor)Preconditions.checkNotNull((Object)pluginAccessor);
    }

    @Override
    String getWhitelistKey() {
        return "DisabledByDefaultPluginModulesHealthCheck";
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
        return "These plugin modules should be disabled, but are enabled. (Manually or programmatically enabled?)";
    }

    @Override
    List<String> getItemsFailingCheck() {
        return this.pluginAccessor.getModuleDescriptors(moduleDescriptor -> !moduleDescriptor.isEnabledByDefault() && this.pluginAccessor.isPluginModuleEnabled(moduleDescriptor.getCompleteKey())).stream().map(ModuleDescriptor::getCompleteKey).sorted().collect(Collectors.toList());
    }
}

