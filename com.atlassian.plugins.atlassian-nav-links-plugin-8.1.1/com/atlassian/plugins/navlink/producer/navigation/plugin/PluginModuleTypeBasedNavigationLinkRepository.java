/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.google.common.base.Predicate
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.plugins.navlink.producer.navigation.plugin;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugins.navlink.producer.navigation.plugin.NavigationLinkModuleDescriptor;
import com.atlassian.plugins.navlink.producer.navigation.services.NavigationLinkRepository;
import com.atlassian.plugins.navlink.producer.navigation.services.RawNavigationLink;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PluginModuleTypeBasedNavigationLinkRepository
implements NavigationLinkRepository {
    private final PluginAccessor pluginAccessor;

    public PluginModuleTypeBasedNavigationLinkRepository(@Nonnull PluginAccessor pluginAccessor) {
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor);
    }

    @Override
    @Deprecated
    public Iterable<RawNavigationLink> matching(@Nullable com.google.common.base.Predicate<RawNavigationLink> criteria) {
        return this.matching((Predicate<RawNavigationLink>)criteria);
    }

    @Override
    public List<RawNavigationLink> matching(@Nullable Predicate<RawNavigationLink> criteria) {
        return this.getEnabledModuleDescriptors().stream().map(this::mapToNavigationLink).filter(criteria != null ? criteria : o -> true).collect(Collectors.toList());
    }

    @Override
    public Iterable<RawNavigationLink> all() {
        return this.matching((com.google.common.base.Predicate<RawNavigationLink>)null);
    }

    @Nonnull
    private List<NavigationLinkModuleDescriptor> getEnabledModuleDescriptors() {
        List<NavigationLinkModuleDescriptor> result = this.pluginAccessor.getEnabledModuleDescriptorsByClass(NavigationLinkModuleDescriptor.class);
        return result != null ? result : Collections.emptyList();
    }

    public RawNavigationLink mapToNavigationLink(@Nullable NavigationLinkModuleDescriptor input) {
        if (input != null) {
            Map context = input.getContextProvider() == null ? Collections.emptyMap() : input.getContextProvider().getContextMap(Collections.emptyMap());
            return input.getCondition() == null || input.getCondition().shouldDisplay(context) ? input.getModule() : null;
        }
        return null;
    }
}

