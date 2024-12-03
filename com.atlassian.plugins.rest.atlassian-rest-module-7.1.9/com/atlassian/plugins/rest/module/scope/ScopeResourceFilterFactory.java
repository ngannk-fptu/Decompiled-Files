/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.scope.ScopeManager
 *  com.atlassian.plugin.tracker.DefaultPluginModuleTracker
 *  com.atlassian.plugin.tracker.PluginModuleTracker
 */
package com.atlassian.plugins.rest.module.scope;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.scope.ScopeManager;
import com.atlassian.plugin.tracker.DefaultPluginModuleTracker;
import com.atlassian.plugin.tracker.PluginModuleTracker;
import com.atlassian.plugins.rest.module.RestModuleDescriptor;
import com.atlassian.plugins.rest.module.scope.ScopeResourceFilter;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.ws.rs.ext.Provider;

@Provider
public class ScopeResourceFilterFactory
implements ResourceFilterFactory {
    private final PluginModuleTracker<Object, RestModuleDescriptor> pluginModuleTracker;
    private final ScopeManager scopeManager;

    public ScopeResourceFilterFactory(PluginAccessor pluginAccessor, PluginEventManager pluginEventManager, ScopeManager scopeManager) {
        this.pluginModuleTracker = new DefaultPluginModuleTracker(pluginAccessor, pluginEventManager, RestModuleDescriptor.class);
        this.scopeManager = scopeManager;
    }

    @Override
    public List<ResourceFilter> create(AbstractMethod method) {
        Predicate<RestModuleDescriptor> configMatches;
        Predicate<RestModuleDescriptor> restApiContextIsPresent;
        Predicate<RestModuleDescriptor> moduleDescriptorPredicate;
        Class<?> clazz = method.getResource().getResourceClass();
        Iterable moduleDescriptors = this.pluginModuleTracker.getModuleDescriptors();
        Stream<RestModuleDescriptor> stream = StreamSupport.stream(moduleDescriptors.spliterator(), false);
        Optional<RestModuleDescriptor> descriptor = stream.filter(moduleDescriptorPredicate = (restApiContextIsPresent = m -> m.getRestApiContext() != null).and(configMatches = m -> m.getRestApiContext().getConfig().map(c -> c.getClasses().contains(clazz)).orElse(false))).findFirst();
        return descriptor.isPresent() ? Collections.singletonList(new ScopeResourceFilter(this.scopeManager, descriptor.get())) : Collections.emptyList();
    }
}

