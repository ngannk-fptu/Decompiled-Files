/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Lists
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 */
package com.atlassian.plugins.rest.module;

import com.atlassian.plugin.module.ContainerManagedPlugin;
import com.atlassian.plugins.rest.common.expand.AdditionalExpandsProvider;
import com.atlassian.plugins.rest.common.expand.SelfExpandingExpander;
import com.atlassian.plugins.rest.common.expand.interceptor.ExpandInterceptor;
import com.atlassian.plugins.rest.common.expand.resolver.ChainingEntityExpanderResolver;
import com.atlassian.plugins.rest.common.expand.resolver.CollectionEntityExpanderResolver;
import com.atlassian.plugins.rest.common.expand.resolver.ExpandConstraintEntityExpanderResolver;
import com.atlassian.plugins.rest.common.expand.resolver.IdentityEntityExpanderResolver;
import com.atlassian.plugins.rest.common.expand.resolver.ListWrapperEntityExpanderResolver;
import com.atlassian.plugins.rest.common.filter.ExtensionJerseyFilter;
import com.atlassian.plugins.rest.common.interceptor.impl.InterceptorChainBuilderProvider;
import com.atlassian.plugins.rest.common.security.jersey.AntiSniffingResponseFilter;
import com.atlassian.plugins.rest.module.OsgiResourceConfig;
import com.atlassian.plugins.rest.module.OsgiServiceAccessor;
import com.atlassian.plugins.rest.module.OsgiServiceReferenceResourceFilterFactory;
import com.atlassian.plugins.rest.module.expand.resolver.PluginEntityExpanderResolver;
import com.atlassian.plugins.rest.module.filter.AcceptHeaderJerseyMvcFilter;
import com.atlassian.plugins.rest.module.filter.AcceptLanguageFilter;
import com.atlassian.plugins.rest.module.filter.CorsAcceptOptionsPreflightFilter;
import com.atlassian.plugins.rest.module.json.JsonWithPaddingResponseFilter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import com.sun.jersey.spi.inject.InjectableProvider;
import com.sun.jersey.spi.template.TemplateProcessor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.ext.MessageBodyReader;
import org.codehaus.jackson.map.Module;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class ResourceConfigManager {
    private final OsgiServiceAccessor<ResourceFilterFactory> resourceFilterFactories;
    private final OsgiServiceAccessor<InjectableProvider> injectableProviders;
    private final OsgiServiceAccessor<TemplateProcessor> templateProcessors;
    private final OsgiServiceAccessor<MessageBodyReader> messageBodyReaders;
    private final OsgiServiceAccessor<ResourceMethodDispatchProvider> dispatchProviders;
    private final OsgiServiceAccessor<Module> modules;
    private final OsgiServiceAccessor<AdditionalExpandsProvider> additionalExpandsProvider;
    private final ContainerManagedPlugin plugin;
    private final Bundle bundle;

    public ResourceConfigManager(ContainerManagedPlugin plugin, Bundle bundle2) {
        this.plugin = plugin;
        this.bundle = bundle2;
        BundleContext bundleContext = bundle2.getBundleContext();
        this.resourceFilterFactories = new OsgiServiceAccessor<ResourceFilterFactory>(ResourceFilterFactory.class, bundleContext, OsgiServiceReferenceResourceFilterFactory::new);
        this.injectableProviders = new OsgiServiceAccessor<InjectableProvider>(InjectableProvider.class, bundleContext, (bundleContext13, serviceReference) -> (InjectableProvider)bundleContext13.getService(serviceReference));
        this.templateProcessors = new OsgiServiceAccessor<TemplateProcessor>(TemplateProcessor.class, bundleContext, (bundleContext14, serviceReference) -> (TemplateProcessor)bundleContext14.getService(serviceReference));
        this.messageBodyReaders = new OsgiServiceAccessor<MessageBodyReader>(MessageBodyReader.class, bundleContext, (bundleContext15, serviceReference) -> (MessageBodyReader)bundleContext15.getService(serviceReference));
        this.dispatchProviders = new OsgiServiceAccessor<ResourceMethodDispatchProvider>(ResourceMethodDispatchProvider.class, bundleContext, (bundleContext16, serviceReference) -> (ResourceMethodDispatchProvider)bundleContext16.getService(serviceReference));
        this.modules = new OsgiServiceAccessor<Module>(Module.class, bundleContext, (bundleContext17, serviceReference) -> (Module)bundleContext17.getService(serviceReference));
        this.additionalExpandsProvider = new OsgiServiceAccessor<AdditionalExpandsProvider>(AdditionalExpandsProvider.class, bundleContext, (bundleContext1, serviceReference) -> (AdditionalExpandsProvider)bundleContext1.getService(serviceReference));
    }

    public DefaultResourceConfig createResourceConfig(Map<String, Object> props, String[] excludes, Set<String> packages, boolean indexBundledJars) {
        List<String> excludesCollection = excludes != null ? Arrays.asList(excludes) : Collections.emptyList();
        ChainingEntityExpanderResolver expanderResolver = new ChainingEntityExpanderResolver(Arrays.asList(new PluginEntityExpanderResolver(this.plugin), new CollectionEntityExpanderResolver(), new ListWrapperEntityExpanderResolver(), new ExpandConstraintEntityExpanderResolver(), new SelfExpandingExpander.Resolver(), new IdentityEntityExpanderResolver()));
        LinkedList providers = Lists.newLinkedList();
        providers.addAll(this.injectableProviders.get());
        providers.addAll(this.templateProcessors.get());
        providers.addAll(this.messageBodyReaders.get());
        providers.addAll(this.dispatchProviders.get());
        providers.add(new InterceptorChainBuilderProvider(this.plugin, new ExpandInterceptor(expanderResolver, this.additionalExpandsProvider.get())));
        List<ContainerRequestFilter> containerRequestFilters = Arrays.asList(new ExtensionJerseyFilter(excludesCollection), new AcceptHeaderJerseyMvcFilter(), new AcceptLanguageFilter(), new CorsAcceptOptionsPreflightFilter());
        ImmutableList.Builder containerResponseFilters = new ImmutableList.Builder();
        containerResponseFilters.add((Object)new AntiSniffingResponseFilter());
        if (Boolean.getBoolean("atlassian.allow.jsonp")) {
            containerResponseFilters.add((Object)new JsonWithPaddingResponseFilter());
        }
        return new OsgiResourceConfig(this.bundle, packages, containerRequestFilters, (Collection<? extends ContainerResponseFilter>)containerResponseFilters.build(), this.resourceFilterFactories.get(), this.modules.get(), providers, indexBundledJars);
    }

    public void destroy() {
        this.resourceFilterFactories.release();
        this.injectableProviders.release();
        this.templateProcessors.release();
        this.messageBodyReaders.release();
        this.dispatchProviders.release();
        this.modules.release();
        this.additionalExpandsProvider.release();
    }
}

