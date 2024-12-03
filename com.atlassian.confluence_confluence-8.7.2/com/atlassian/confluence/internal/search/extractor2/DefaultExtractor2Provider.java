/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.osgi.container.OsgiContainerManager
 *  io.atlassian.util.concurrent.LazyReference
 *  org.osgi.framework.ServiceReference
 *  org.osgi.util.tracker.ServiceTracker
 */
package com.atlassian.confluence.internal.search.extractor2;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.internal.search.extractor2.Extractor2Provider;
import com.atlassian.confluence.plugin.descriptor.Extractor2ModuleDescriptor;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.osgi.container.OsgiContainerManager;
import io.atlassian.util.concurrent.LazyReference;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

@Internal
public class DefaultExtractor2Provider
implements Extractor2Provider {
    private final LazyReference<ServiceTracker> serviceTrackerRef;
    private final PluginAccessor pluginAccessor;

    public DefaultExtractor2Provider(final OsgiContainerManager osgiContainerManager, PluginAccessor pluginAccessor) {
        this.serviceTrackerRef = new LazyReference<ServiceTracker>(){

            protected ServiceTracker create() {
                return osgiContainerManager.getServiceTracker(Extractor2.class.getName());
            }
        };
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor, "pluginAccessor");
    }

    @Override
    public List<Extractor2> get(SearchIndex searchIndex, boolean requiresLatestVersion) {
        return Stream.concat(this.getServices(searchIndex, requiresLatestVersion), this.getModules(searchIndex, requiresLatestVersion)).collect(Collectors.toList());
    }

    private Stream<Extractor2> getServices(SearchIndex searchIndex, boolean requiresLatestVersion) {
        if (searchIndex != SearchIndex.CONTENT || !requiresLatestVersion) {
            return Stream.empty();
        }
        ServiceTracker serviceTracker = (ServiceTracker)this.serviceTrackerRef.get();
        if (serviceTracker == null) {
            throw new IllegalStateException("osgi service tracker must not be null");
        }
        ServiceReference[] serviceReferences = serviceTracker.getServiceReferences();
        return Stream.of(serviceReferences).map(arg_0 -> ((ServiceTracker)serviceTracker).getService(arg_0)).filter(service -> service instanceof Extractor2).map(Extractor2.class::cast);
    }

    private Stream<Extractor2> getModules(SearchIndex searchIndex, boolean requiresLatestVersion) {
        return this.pluginAccessor.getEnabledModuleDescriptorsByClass(Extractor2ModuleDescriptor.class).stream().filter(descriptor -> descriptor.getSearchIndex() == searchIndex).filter(descriptor -> descriptor.requiresLatestVersion() == requiresLatestVersion).sorted().map(ModuleDescriptor::getModule);
    }
}

