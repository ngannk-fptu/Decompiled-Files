/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Collections2
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.healthcheck.core;

import com.atlassian.healthcheck.core.ExtendedHealthCheck;
import com.atlassian.healthcheck.core.HealthCheckModuleDescriptorNotFoundException;
import com.atlassian.healthcheck.core.HealthCheckSupplier;
import com.atlassian.healthcheck.core.impl.HealthCheckModuleDescriptor;
import com.atlassian.healthcheck.core.impl.PluginSuppliedHealthCheck;
import com.atlassian.plugin.PluginAccessor;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultHealthCheckSupplier
implements HealthCheckSupplier {
    private static final Logger log = LoggerFactory.getLogger(DefaultHealthCheckSupplier.class);
    private final PluginAccessor pluginAccessor;

    public DefaultHealthCheckSupplier(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    @Override
    public Collection<ExtendedHealthCheck> getHealthChecks() {
        return DefaultHealthCheckSupplier.healthChecksFrom(this.pluginAccessor.getEnabledModuleDescriptorsByClass(HealthCheckModuleDescriptor.class));
    }

    @Override
    public Collection<ExtendedHealthCheck> getHealthChecksWithKeys(Set<String> keys) throws HealthCheckModuleDescriptorNotFoundException {
        return DefaultHealthCheckSupplier.healthChecksFrom(this.getHealthCheckModuleDescriptorsWithKeys(keys));
    }

    @Override
    public Collection<ExtendedHealthCheck> getHealthChecksWithTags(Set<String> tags) {
        return DefaultHealthCheckSupplier.healthChecksFrom(this.filterOnTags(this.pluginAccessor.getEnabledModuleDescriptorsByClass(HealthCheckModuleDescriptor.class), tags));
    }

    private Collection<HealthCheckModuleDescriptor> filterOnTags(Collection<HealthCheckModuleDescriptor> healthCheckModuleDescriptors, final Set<String> tags) {
        return Collections2.filter(healthCheckModuleDescriptors, (Predicate)new Predicate<HealthCheckModuleDescriptor>(){

            public boolean apply(@Nullable HealthCheckModuleDescriptor md) {
                return md != null && tags.contains(md.getTag());
            }
        });
    }

    private Collection<HealthCheckModuleDescriptor> getHealthCheckModuleDescriptorsWithKeys(Set<String> keys) throws HealthCheckModuleDescriptorNotFoundException {
        ArrayList<HealthCheckModuleDescriptor> healthCheckDescriptors = new ArrayList<HealthCheckModuleDescriptor>(keys.size());
        for (String healthCheckKey : keys) {
            try {
                HealthCheckModuleDescriptor enabledPluginModule = (HealthCheckModuleDescriptor)this.pluginAccessor.getEnabledPluginModule(healthCheckKey);
                if (enabledPluginModule == null) {
                    throw new HealthCheckModuleDescriptorNotFoundException(healthCheckKey);
                }
                healthCheckDescriptors.add(enabledPluginModule);
            }
            catch (IllegalArgumentException e) {
                throw new HealthCheckModuleDescriptorNotFoundException(healthCheckKey, e);
            }
        }
        return healthCheckDescriptors;
    }

    private static Collection<ExtendedHealthCheck> healthChecksFrom(Collection<HealthCheckModuleDescriptor> healthCheckDescriptors) {
        return Collections2.transform(healthCheckDescriptors, (Function)new Function<HealthCheckModuleDescriptor, ExtendedHealthCheck>(){

            public ExtendedHealthCheck apply(@Nullable HealthCheckModuleDescriptor md) {
                if (md != null) {
                    log.debug("Supplying health check from descriptor: {}", (Object)md);
                    return new PluginSuppliedHealthCheck(md.getModule(), md.getCompleteKey(), md.getName(), md.getDescription(), md.getTag(), md.getTimeOut());
                }
                return null;
            }
        });
    }
}

