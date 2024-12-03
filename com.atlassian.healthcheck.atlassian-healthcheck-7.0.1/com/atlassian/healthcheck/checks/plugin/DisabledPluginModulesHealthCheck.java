/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.healthcheck.spi.HealthCheckWhitelist
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.descriptors.UnloadableModuleDescriptor
 *  com.atlassian.plugin.descriptors.UnrecognisedModuleDescriptor
 *  com.google.common.base.Preconditions
 */
package com.atlassian.healthcheck.checks.plugin;

import com.atlassian.healthcheck.checks.plugin.AbstractPluginHealthCheck;
import com.atlassian.healthcheck.checks.plugin.OnceOnlyLogger;
import com.atlassian.healthcheck.spi.HealthCheckWhitelist;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.descriptors.UnloadableModuleDescriptor;
import com.atlassian.plugin.descriptors.UnrecognisedModuleDescriptor;
import com.google.common.base.Preconditions;
import java.util.List;
import java.util.stream.Collectors;

public final class DisabledPluginModulesHealthCheck
extends AbstractPluginHealthCheck {
    private static final String HARD_FAIL_SYSPROP = "atlassian.healthcheck.DisabledPluginModulesHealthCheck.hardFail";
    private static final String FORCE_NO_FAIL_SYSPROP = "atlassian.healthcheck.DisabledPluginModulesHealthCheck.forceNoFail";
    private static final String DISABLE_CHECK_SYSPROP = "atlassian.healthcheck.DisabledPluginModulesHealthCheck.disable";
    private final PluginAccessor pluginAccessor;

    public DisabledPluginModulesHealthCheck(PluginAccessor pluginAccessor, OnceOnlyLogger logger, HealthCheckWhitelist healthCheckWhitelist) {
        super(logger, healthCheckWhitelist);
        this.pluginAccessor = (PluginAccessor)Preconditions.checkNotNull((Object)pluginAccessor);
    }

    @Override
    String getWhitelistKey() {
        return "DisabledPluginModulesHealthCheck";
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
        return "These plugin modules should be enabled, but are disabled. (Failed to start, manually or programmatically disabled, or a missing dependency)";
    }

    @Override
    List<String> getItemsFailingCheck() {
        return this.pluginAccessor.getEnabledPlugins().stream().flatMap(plugin -> plugin.getModuleDescriptors().stream().filter(moduleDescriptor -> !DisabledPluginModulesHealthCheck.moduleIsSupposedToBeDisabled(moduleDescriptor) && !this.pluginAccessor.isPluginModuleEnabled(moduleDescriptor.getCompleteKey()) && this.moduleIsStillInstalled((ModuleDescriptor<?>)moduleDescriptor))).map(ModuleDescriptor::getCompleteKey).sorted().collect(Collectors.toList());
    }

    private boolean moduleIsStillInstalled(ModuleDescriptor<?> moduleDescriptor) {
        return this.pluginAccessor.getPluginModule(moduleDescriptor.getCompleteKey()) != null;
    }

    private static boolean moduleIsSupposedToBeDisabled(ModuleDescriptor<?> moduleDescriptor) {
        if (moduleDescriptor instanceof UnrecognisedModuleDescriptor || moduleDescriptor instanceof UnloadableModuleDescriptor) {
            return false;
        }
        return !moduleDescriptor.isEnabledByDefault();
    }
}

