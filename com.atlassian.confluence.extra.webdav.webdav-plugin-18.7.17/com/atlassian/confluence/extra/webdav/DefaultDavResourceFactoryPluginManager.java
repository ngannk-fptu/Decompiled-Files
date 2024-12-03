/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.tracker.DefaultPluginModuleTracker
 *  org.apache.commons.lang.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.webdav;

import com.atlassian.confluence.extra.webdav.DavResourceFactoryModuleDescriptor;
import com.atlassian.confluence.extra.webdav.DavResourceFactoryPluginManager;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.tracker.DefaultPluginModuleTracker;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultDavResourceFactoryPluginManager
implements DavResourceFactoryPluginManager {
    private DefaultPluginModuleTracker<DavResourceFactory, DavResourceFactoryModuleDescriptor> customDavResourceFactoryTracker;

    @Autowired
    public DefaultDavResourceFactoryPluginManager(@ComponentImport PluginAccessor pluginAccessor, @ComponentImport PluginEventManager pluginEventManager) {
        this.customDavResourceFactoryTracker = new DefaultPluginModuleTracker(pluginAccessor, pluginEventManager, DavResourceFactoryModuleDescriptor.class);
    }

    @Override
    public DavResourceFactory getFactoryForWorkspace(String workspaceName) {
        for (DavResourceFactoryModuleDescriptor davResourceFactoryModuleDescriptor : this.customDavResourceFactoryTracker.getModuleDescriptors()) {
            if (!StringUtils.equals((String)workspaceName, (String)davResourceFactoryModuleDescriptor.getWorkspaceName())) continue;
            return davResourceFactoryModuleDescriptor.getModule();
        }
        return null;
    }
}

