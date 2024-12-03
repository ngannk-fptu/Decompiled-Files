/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.plugins.navlink.producer.navigation.plugin;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugins.navlink.producer.navigation.plugin.NavigationLinksModuleDescriptor;
import com.atlassian.plugins.navlink.producer.navigation.services.NavigationLinkRepository;
import com.atlassian.plugins.navlink.producer.navigation.services.NavigationLinkRepositoryService;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NavigationLinkRepositoryServiceImpl
implements NavigationLinkRepositoryService {
    private final PluginAccessor pluginAccessor;

    public NavigationLinkRepositoryServiceImpl(@Nonnull PluginAccessor pluginAccessor) {
        this.pluginAccessor = (PluginAccessor)Preconditions.checkNotNull((Object)pluginAccessor);
    }

    @Override
    @Nonnull
    public List<NavigationLinkRepository> getAllNavigationLinkRepositories() {
        List enabledModuleDescriptorsByClass = this.pluginAccessor.getEnabledModuleDescriptorsByClass(NavigationLinksModuleDescriptor.class);
        return Lists.transform((List)enabledModuleDescriptorsByClass, (Function)new Function<NavigationLinksModuleDescriptor, NavigationLinkRepository>(){

            public NavigationLinkRepository apply(@Nullable NavigationLinksModuleDescriptor modulDescriptor) {
                return modulDescriptor != null ? modulDescriptor.getModule() : null;
            }
        });
    }
}

