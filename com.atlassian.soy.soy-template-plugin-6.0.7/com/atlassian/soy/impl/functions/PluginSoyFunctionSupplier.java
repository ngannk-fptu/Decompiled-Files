/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginDisabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleDisabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 *  com.atlassian.plugin.webresource.impl.PrebakeErrorFactory
 *  com.atlassian.plugin.webresource.transformer.TransformerUrlBuilder
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 *  com.atlassian.soy.renderer.SoyClientFunction
 *  com.atlassian.soy.renderer.SoyFunction
 *  com.atlassian.soy.renderer.SoyFunctionModuleDescriptor
 *  com.atlassian.soy.renderer.StatefulSoyClientFunction
 *  com.atlassian.webresource.api.prebake.Coordinate
 *  com.atlassian.webresource.api.prebake.DimensionAwareTransformerUrlBuilder
 *  com.atlassian.webresource.api.prebake.Dimensions
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Iterables
 *  io.atlassian.util.concurrent.ResettableLazyReference
 */
package com.atlassian.soy.impl.functions;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.plugin.webresource.impl.PrebakeErrorFactory;
import com.atlassian.plugin.webresource.transformer.TransformerUrlBuilder;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import com.atlassian.soy.impl.functions.UrlEncodingSoyFunctionSupplier;
import com.atlassian.soy.renderer.SoyClientFunction;
import com.atlassian.soy.renderer.SoyFunction;
import com.atlassian.soy.renderer.SoyFunctionModuleDescriptor;
import com.atlassian.soy.renderer.StatefulSoyClientFunction;
import com.atlassian.webresource.api.prebake.Coordinate;
import com.atlassian.webresource.api.prebake.DimensionAwareTransformerUrlBuilder;
import com.atlassian.webresource.api.prebake.Dimensions;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import io.atlassian.util.concurrent.ResettableLazyReference;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class PluginSoyFunctionSupplier
implements UrlEncodingSoyFunctionSupplier {
    private static final Predicate<ModuleDescriptor<?>> IS_SOY_FUNCTION = descriptor -> descriptor instanceof SoyFunctionModuleDescriptor;
    private static final Function<SoyFunctionModuleDescriptor, SoyFunction> TO_MODULE = SoyFunctionModuleDescriptor::getModule;
    private final PluginAccessor pluginAccessor;
    private final PluginEventManager pluginEventManager;
    private final Set<SoyFunctionModuleDescriptor> moduleDescriptors;
    @TenantAware(value=TenancyScope.TENANTLESS, comment="UrlState contains list of plugin versions and list of module descriptors, same for all tenants!")
    private final ResettableLazyReference<UrlState> state;

    public PluginSoyFunctionSupplier(PluginAccessor pluginAccessor, PluginEventManager pluginEventManager) {
        this.pluginAccessor = pluginAccessor;
        this.pluginEventManager = pluginEventManager;
        this.moduleDescriptors = new CopyOnWriteArraySet<SoyFunctionModuleDescriptor>();
        this.state = new ResettableLazyReference<UrlState>(){

            protected UrlState create() {
                return PluginSoyFunctionSupplier.this.buildState();
            }
        };
    }

    public void registerListeners() {
        this.pluginEventManager.register((Object)this);
        this.addDescriptors(this.pluginAccessor.getEnabledModuleDescriptorsByClass(SoyFunctionModuleDescriptor.class));
    }

    public void unregisterListeners() {
        this.pluginEventManager.unregister((Object)this);
    }

    public Iterable<SoyFunction> get() {
        return Iterables.transform(this.moduleDescriptors, TO_MODULE);
    }

    public void addToUrl(UrlBuilder urlBuilder) {
        ((UrlState)this.state.get()).addToUrl(urlBuilder);
    }

    @PluginEventListener
    public void onPluginModuleEnabled(PluginModuleEnabledEvent event) {
        this.addDescriptors(Collections2.filter(Collections.singleton(event.getModule()), IS_SOY_FUNCTION));
    }

    @PluginEventListener
    public void onPluginModuleDisabled(PluginModuleDisabledEvent event) {
        this.removeDescriptors(Collections2.filter(Collections.singleton(event.getModule()), IS_SOY_FUNCTION));
    }

    @PluginEventListener
    public void onPluginDisabled(PluginDisabledEvent event) {
        this.removeDescriptors(Collections2.filter((Collection)event.getPlugin().getModuleDescriptors(), IS_SOY_FUNCTION));
    }

    private void addDescriptors(Collection<SoyFunctionModuleDescriptor> descriptors) {
        if (this.moduleDescriptors.addAll(descriptors)) {
            this.state.reset();
        }
    }

    private void removeDescriptors(Collection<SoyFunctionModuleDescriptor> descriptors) {
        if (this.moduleDescriptors.removeAll(descriptors)) {
            this.state.reset();
        }
    }

    private UrlState buildState() {
        LinkedHashMap<String, String> pluginKeysToVersions = new LinkedHashMap<String, String>();
        ImmutableSet.Builder statefulFunctionDescriptors = ImmutableSet.builder();
        for (SoyFunctionModuleDescriptor descriptor : this.moduleDescriptors) {
            if (!SoyClientFunction.class.isAssignableFrom(descriptor.getModuleClass())) continue;
            pluginKeysToVersions.put(descriptor.getPlugin().getKey(), descriptor.getPlugin().getPluginInformation().getVersion());
            if (!TransformerUrlBuilder.class.isAssignableFrom(descriptor.getModuleClass())) continue;
            statefulFunctionDescriptors.add((Object)descriptor);
        }
        return new UrlState(pluginKeysToVersions, (Iterable<SoyFunctionModuleDescriptor>)statefulFunctionDescriptors.build());
    }

    public void addToUrl(UrlBuilder urlBuilder, Coordinate coordinate) {
        ((UrlState)this.state.get()).addToUrl(urlBuilder, coordinate);
    }

    @Override
    public Dimensions computeDimensions() {
        return ((UrlState)this.state.get()).computeDimensions();
    }

    private static class UrlState
    implements DimensionAwareTransformerUrlBuilder {
        private final String globalState;
        private final Iterable<SoyFunctionModuleDescriptor> statefulFunctionDescriptors;

        UrlState(Map<String, String> pluginKeyToVersion, Iterable<SoyFunctionModuleDescriptor> statefulFunctionDescriptors) {
            this.globalState = pluginKeyToVersion.toString();
            this.statefulFunctionDescriptors = statefulFunctionDescriptors;
        }

        public void addToUrl(UrlBuilder urlBuilder) {
            urlBuilder.addToHash("soyGlobalState", (Object)this.globalState);
            for (SoyFunctionModuleDescriptor descriptor : this.statefulFunctionDescriptors) {
                ((TransformerUrlBuilder)descriptor.getModule()).addToUrl(urlBuilder);
            }
        }

        public void addToUrl(UrlBuilder urlBuilder, Coordinate coordinate) {
            urlBuilder.addToHash("soyGlobalState", (Object)this.globalState);
            for (SoyFunctionModuleDescriptor descriptor : this.statefulFunctionDescriptors) {
                if (descriptor.getModule() instanceof StatefulSoyClientFunction) {
                    ((StatefulSoyClientFunction)descriptor.getModule()).addToUrl(urlBuilder, coordinate);
                    continue;
                }
                urlBuilder.addPrebakeError(PrebakeErrorFactory.from((String)("Soy function " + descriptor.getModule().getClass().getName() + " is stateful but not dimension aware (StatefulSoyClientFunction)! Descriptor: " + descriptor.toString())));
            }
        }

        Dimensions computeDimensions() {
            Dimensions d = Dimensions.empty();
            for (SoyFunctionModuleDescriptor descriptor : this.statefulFunctionDescriptors) {
                if (!(descriptor.getModule() instanceof DimensionAwareTransformerUrlBuilder)) continue;
                d.product(((StatefulSoyClientFunction)descriptor.getModule()).computeDimensions());
            }
            return d;
        }
    }
}

