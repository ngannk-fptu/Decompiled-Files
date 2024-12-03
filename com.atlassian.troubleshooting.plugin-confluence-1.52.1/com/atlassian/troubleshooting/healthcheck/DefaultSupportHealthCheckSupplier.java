/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck;

import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.troubleshooting.api.healthcheck.ExtendedSupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.HealthCheckFilter;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheckCondition;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheckSupplier;
import com.atlassian.troubleshooting.api.healthcheck.exception.SupportHealthCheckModuleDescriptorNotFoundException;
import com.atlassian.troubleshooting.healthcheck.impl.PluginSuppliedSupportHealthCheck;
import com.atlassian.troubleshooting.healthcheck.persistence.service.HealthCheckDisabledService;
import com.atlassian.troubleshooting.stp.spi.SupportHealthCheckModuleDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultSupportHealthCheckSupplier
implements SupportHealthCheckSupplier {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultSupportHealthCheckSupplier.class);
    private final PluginAccessor pluginAccessor;
    private final I18nResolver i18nResolver;
    private final HealthCheckDisabledService disabledService;

    @Autowired
    public DefaultSupportHealthCheckSupplier(PluginAccessor pluginAccessor, I18nResolver i18nResolver, HealthCheckDisabledService disabledService) {
        this.pluginAccessor = pluginAccessor;
        this.i18nResolver = i18nResolver;
        this.disabledService = disabledService;
    }

    private static boolean shouldDisplay(List<SupportHealthCheckCondition> conditions) {
        return conditions.stream().allMatch(SupportHealthCheckCondition::shouldDisplay);
    }

    @Override
    public Collection<ExtendedSupportHealthCheck> getHealthChecks(HealthCheckFilter filter) {
        Collection<SupportHealthCheckModuleDescriptor> healthCheckDescriptors = this.getDescriptors(filter);
        return this.healthChecksFrom(healthCheckDescriptors);
    }

    private Collection<SupportHealthCheckModuleDescriptor> getDescriptors(HealthCheckFilter filter) {
        if (!filter.getKeys().isEmpty()) {
            return this.getSupportHealthCheckModuleDescriptorsWithKeys(filter.getKeys());
        }
        if (!filter.getTags().isEmpty()) {
            return this.filterOnTags(this.getEnabledModuleDescriptorsByClass(), filter.getTags());
        }
        return this.getEnabledModuleDescriptorsByClass();
    }

    @Override
    public Optional<ExtendedSupportHealthCheck> getHealthCheck(String healthCheckKey) {
        return this.getHealthChecksWithKeys(Collections.singleton(healthCheckKey)).stream().findFirst();
    }

    @Override
    public Collection<ExtendedSupportHealthCheck> getHealthChecks() {
        return this.healthChecksFrom(this.getEnabledModuleDescriptorsByClass());
    }

    @Override
    public Collection<ExtendedSupportHealthCheck> getHealthChecksWithKeys(Set<String> keys) throws SupportHealthCheckModuleDescriptorNotFoundException {
        return this.healthChecksFrom(this.getSupportHealthCheckModuleDescriptorsWithKeys(keys));
    }

    @Override
    public Collection<ExtendedSupportHealthCheck> getHealthChecksWithTags(Set<String> tags) {
        return this.healthChecksFrom(this.filterOnTags(this.getEnabledModuleDescriptorsByClass(), tags));
    }

    @Override
    public Optional<ExtendedSupportHealthCheck> byInstance(@Nonnull SupportHealthCheck healthCheck) {
        return this.healthChecksFrom(this.getEnabledModuleDescriptorsByClass()).stream().filter(hc -> Objects.equals(hc.getClassName(), Objects.requireNonNull(healthCheck).getClass().getName())).findAny();
    }

    @Override
    public Optional<String> getHelpPathKey(@Nonnull SupportHealthCheck healthCheck) {
        return this.getEnabledModuleDescriptorsByClass().stream().filter(hc -> Objects.equals(hc.getClassName(), Objects.requireNonNull(healthCheck).getClass().getName())).findAny().map(SupportHealthCheckModuleDescriptor::getHelpPathKey);
    }

    private List<SupportHealthCheckModuleDescriptor> getEnabledModuleDescriptorsByClass() {
        return this.pluginAccessor.getEnabledModuleDescriptorsByClass(SupportHealthCheckModuleDescriptor.class);
    }

    private Collection<SupportHealthCheckModuleDescriptor> filterOnTags(Collection<SupportHealthCheckModuleDescriptor> healthCheckModuleDescriptors, Set<String> tags) {
        return healthCheckModuleDescriptors.stream().filter(Objects::nonNull).filter(md -> tags.contains(md.getTag())).collect(Collectors.toList());
    }

    private Collection<SupportHealthCheckModuleDescriptor> getSupportHealthCheckModuleDescriptorsWithKeys(Set<String> keys) throws SupportHealthCheckModuleDescriptorNotFoundException {
        ArrayList<SupportHealthCheckModuleDescriptor> healthCheckDescriptors = new ArrayList<SupportHealthCheckModuleDescriptor>(keys.size());
        for (String healthCheckKey : keys) {
            SupportHealthCheckModuleDescriptor enabledPluginModule = (SupportHealthCheckModuleDescriptor)this.pluginAccessor.getEnabledPluginModule(healthCheckKey);
            if (enabledPluginModule == null) {
                throw new SupportHealthCheckModuleDescriptorNotFoundException(healthCheckKey);
            }
            healthCheckDescriptors.add(enabledPluginModule);
        }
        return healthCheckDescriptors;
    }

    private Collection<ExtendedSupportHealthCheck> healthChecksFrom(Collection<SupportHealthCheckModuleDescriptor> healthCheckDescriptors) {
        Set<String> disabledChecks = this.disabledService.getDisabledHealthChecks();
        List allHealthChecks = this.pluginAccessor.getEnabledModuleDescriptorsByClass(SupportHealthCheckModuleDescriptor.class);
        return healthCheckDescriptors.stream().map(md -> this.asPluginSuppliedSupportHealthCheck((SupportHealthCheckModuleDescriptor)((Object)md), disabledChecks)).filter(Objects::nonNull).filter(extendedHealthCheck -> DefaultSupportHealthCheckSupplier.healthCheckIsNotOverridden(extendedHealthCheck, allHealthChecks)).collect(Collectors.toList());
    }

    private static boolean healthCheckIsNotOverridden(ExtendedSupportHealthCheck extendedHealthCheck, List<SupportHealthCheckModuleDescriptor> allHealthChecks) {
        if (extendedHealthCheck.getKey().startsWith("com.atlassian.troubleshooting.plugin-")) {
            ModuleCompleteKey healthCheckKey = new ModuleCompleteKey(extendedHealthCheck.getKey());
            return allHealthChecks.stream().map(AbstractModuleDescriptor::getCompleteKey).filter(completeKeyString -> !completeKeyString.equals(healthCheckKey.getCompleteKey())).map(ModuleCompleteKey::new).map(ModuleCompleteKey::getModuleKey).noneMatch(healthCheckKey.getModuleKey()::equals);
        }
        return true;
    }

    private ExtendedSupportHealthCheck asPluginSuppliedSupportHealthCheck(SupportHealthCheckModuleDescriptor md, Set<String> disabledChecks) {
        if (md != null && DefaultSupportHealthCheckSupplier.shouldDisplay(md.getConditions())) {
            LOG.debug("Supplying health check from descriptor: {}", (Object)md);
            return new PluginSuppliedSupportHealthCheck(md.getModule(), md.getCompleteKey(), this.getText(md.getI18nNameKey()), this.getText(md.getDescriptionKey()), md.getTimeOut(), this.getText(md.getTag()), md.getHelpPathKey(), md.getClassName(), this.i18nResolver, !disabledChecks.contains(md.getCompleteKey()));
        }
        return null;
    }

    private String getText(String name) {
        return name == null ? null : this.i18nResolver.getText(name);
    }
}

