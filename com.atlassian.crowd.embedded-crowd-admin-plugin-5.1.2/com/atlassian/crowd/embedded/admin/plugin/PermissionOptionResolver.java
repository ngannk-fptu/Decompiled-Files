/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.PermissionOption
 *  com.atlassian.plugin.PluginAccessor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.embedded.admin.plugin;

import com.atlassian.crowd.embedded.admin.crowd.CrowdPermissionOption;
import com.atlassian.crowd.embedded.admin.plugin.SupportedDirectoryPermissionOptionsModuleDescriptor;
import com.atlassian.crowd.embedded.api.PermissionOption;
import com.atlassian.plugin.PluginAccessor;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PermissionOptionResolver {
    private static final Logger log = LoggerFactory.getLogger(PermissionOptionResolver.class);
    private PluginAccessor pluginAccessor;

    public Set<PermissionOption> getEnabledPermissionOptions() {
        List descriptors = this.pluginAccessor.getEnabledModuleDescriptorsByClass(SupportedDirectoryPermissionOptionsModuleDescriptor.class);
        if (descriptors.isEmpty()) {
            return EnumSet.allOf(PermissionOption.class);
        }
        HashSet enabledOptions = new HashSet();
        for (SupportedDirectoryPermissionOptionsModuleDescriptor descriptor : descriptors) {
            enabledOptions.addAll(descriptor.getModule());
        }
        if (enabledOptions.isEmpty()) {
            log.warn("No directory permission options are enabled for this server.");
        }
        return EnumSet.copyOf(enabledOptions);
    }

    public Set<CrowdPermissionOption> getEnabledCrowdPermissionOptions() {
        Set<PermissionOption> enabledLdapOptions = this.getEnabledPermissionOptions();
        return this.asCrowdPermissionOptions(enabledLdapOptions);
    }

    protected Set<CrowdPermissionOption> asCrowdPermissionOptions(Set<PermissionOption> enabledLdapOptions) {
        HashSet<CrowdPermissionOption> enabledCrowdOptions = new HashSet<CrowdPermissionOption>();
        if (enabledLdapOptions.contains(PermissionOption.READ_ONLY)) {
            enabledCrowdOptions.add(CrowdPermissionOption.READ_ONLY);
        }
        if (enabledLdapOptions.contains(PermissionOption.READ_WRITE)) {
            enabledCrowdOptions.add(CrowdPermissionOption.READ_WRITE);
        }
        return enabledCrowdOptions;
    }

    public void setPluginAccessor(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }
}

