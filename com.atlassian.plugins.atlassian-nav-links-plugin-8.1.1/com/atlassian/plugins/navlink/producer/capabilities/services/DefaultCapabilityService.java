/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.sal.api.ApplicationProperties
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.navlink.producer.capabilities.services;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugins.navlink.producer.capabilities.ApplicationWithCapabilities;
import com.atlassian.plugins.navlink.producer.capabilities.Capability;
import com.atlassian.plugins.navlink.producer.capabilities.plugin.CapabilityModuleDescriptor;
import com.atlassian.plugins.navlink.producer.capabilities.services.ApplicationTypeService;
import com.atlassian.plugins.navlink.producer.capabilities.services.CapabilityService;
import com.atlassian.sal.api.ApplicationProperties;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultCapabilityService
implements CapabilityService {
    private final Logger logger = LoggerFactory.getLogger(DefaultCapabilityService.class);
    private final PluginAccessor pluginAccessor;
    private final ApplicationTypeService applicationTypeService;
    private final ApplicationProperties applicationProperties;

    public DefaultCapabilityService(PluginAccessor pluginAccessor, ApplicationTypeService applicationTypeService, ApplicationProperties applicationProperties) {
        this.pluginAccessor = pluginAccessor;
        this.applicationTypeService = applicationTypeService;
        this.applicationProperties = applicationProperties;
    }

    @Override
    @Nonnull
    public ApplicationWithCapabilities getHostApplication() {
        String applicationType = this.applicationTypeService.get();
        List<Capability> capabilitiesFromPluginModules = this.getCapabilitiesFromPluginModules();
        List<Capability> usableCapabilities = capabilitiesFromPluginModules.stream().filter(this.byApplicationType(applicationType)).collect(Collectors.toList());
        Map<String, String> capabilities = this.asMap(usableCapabilities);
        ZonedDateTime buildDate = this.mapNullToNow(this.applicationProperties.getBuildDate());
        return new ApplicationWithCapabilities(applicationType, buildDate, capabilities);
    }

    private ZonedDateTime mapNullToNow(Date date) {
        Instant instant = date != null ? date.toInstant() : Instant.now();
        return instant.atZone(ZoneOffset.UTC);
    }

    @Nonnull
    private List<Capability> getCapabilitiesFromPluginModules() {
        return this.getEnabledModuleDescriptors().stream().map(this::toCapability).collect(Collectors.toList());
    }

    @Nonnull
    private List<CapabilityModuleDescriptor> getEnabledModuleDescriptors() {
        List<CapabilityModuleDescriptor> moduleDescriptors = this.pluginAccessor.getEnabledModuleDescriptorsByClass(CapabilityModuleDescriptor.class);
        return moduleDescriptors != null ? moduleDescriptors : Collections.emptyList();
    }

    @Nullable
    private Capability toCapability(@Nullable CapabilityModuleDescriptor input) {
        return input != null ? input.getModule() : null;
    }

    @Nonnull
    private Predicate<Capability> byApplicationType(@Nonnull String applicationType) {
        return input -> {
            if (input != null) {
                String type = input.getType();
                return type == null || type.isEmpty() || applicationType.equals(type);
            }
            return false;
        };
    }

    @Nonnull
    private Map<String, String> asMap(@Nonnull Iterable<Capability> usableCapabilities) {
        HashMap<String, String> capabilities = new HashMap<String, String>();
        for (Capability capability : usableCapabilities) {
            this.logger.debug("Capability: {} at URL {}", (Object)capability.getName(), (Object)capability.getUrl());
            capabilities.put(capability.getName(), capability.getUrl());
        }
        return capabilities;
    }
}

