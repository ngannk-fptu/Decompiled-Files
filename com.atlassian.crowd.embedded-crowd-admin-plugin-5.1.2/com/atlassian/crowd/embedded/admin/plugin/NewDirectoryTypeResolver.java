/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.ApplicationType
 *  com.atlassian.plugin.PluginAccessor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.embedded.admin.plugin;

import com.atlassian.crowd.embedded.admin.list.NewDirectoryType;
import com.atlassian.crowd.embedded.admin.plugin.SupportedNewDirectoryTypesModuleDescriptor;
import com.atlassian.crowd.model.application.ApplicationType;
import com.atlassian.plugin.PluginAccessor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewDirectoryTypeResolver {
    private static final Logger log = LoggerFactory.getLogger(NewDirectoryTypeResolver.class);
    private PluginAccessor pluginAccessor;

    public List<NewDirectoryType> getEnabledNewDirectoryTypes(ApplicationType applicationType) {
        List descriptors = this.pluginAccessor.getEnabledModuleDescriptorsByClass(SupportedNewDirectoryTypesModuleDescriptor.class);
        if (descriptors.isEmpty()) {
            return NewDirectoryType.getValidNewDirectoryTypes(applicationType);
        }
        ArrayList<NewDirectoryType> enabledOptions = new ArrayList<NewDirectoryType>();
        for (SupportedNewDirectoryTypesModuleDescriptor descriptor : descriptors) {
            Iterator iterator = descriptor.getModule().iterator();
            while (iterator.hasNext()) {
                NewDirectoryType directoryType = (NewDirectoryType)((Object)iterator.next());
                if (enabledOptions.contains((Object)directoryType)) continue;
                enabledOptions.add(directoryType);
            }
        }
        if (enabledOptions.isEmpty()) {
            log.warn("No new directory types are enabled for this server.");
        }
        return enabledOptions;
    }

    public void setPluginAccessor(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }
}

