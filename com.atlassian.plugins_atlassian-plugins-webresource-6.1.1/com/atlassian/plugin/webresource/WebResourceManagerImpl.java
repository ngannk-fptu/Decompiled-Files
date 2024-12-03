/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.util.Assertions
 *  com.atlassian.webresource.api.assembler.AssembledResources
 *  com.atlassian.webresource.api.assembler.RequiredResources
 *  com.atlassian.webresource.api.assembler.WebResource
 *  com.atlassian.webresource.api.assembler.WebResourceAssembler
 *  com.atlassian.webresource.api.assembler.WebResourceAssemblerFactory
 *  com.atlassian.webresource.api.assembler.WebResourceSet
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.base.Supplier
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nullable
 */
package com.atlassian.plugin.webresource;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.util.Assertions;
import com.atlassian.plugin.webresource.DefaultResourceBatchingConfiguration;
import com.atlassian.plugin.webresource.DefaultWebResourceFilter;
import com.atlassian.plugin.webresource.PluginResourceLocator;
import com.atlassian.plugin.webresource.ResourceBatchingConfiguration;
import com.atlassian.plugin.webresource.ResourceDependencyResolver;
import com.atlassian.plugin.webresource.ResourceUrl;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceFilter;
import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.plugin.webresource.assembler.DefaultPageBuilderService;
import com.atlassian.plugin.webresource.assembler.DefaultWebResourceAssemblerFactory;
import com.atlassian.plugin.webresource.assembler.DefaultWebResourceSet;
import com.atlassian.plugin.webresource.assembler.LegacyPageBuilderService;
import com.atlassian.plugin.webresource.assembler.UrlModeUtils;
import com.atlassian.plugin.webresource.prebake.PrebakeWebResourceAssemblerFactory;
import com.atlassian.webresource.api.assembler.AssembledResources;
import com.atlassian.webresource.api.assembler.RequiredResources;
import com.atlassian.webresource.api.assembler.WebResource;
import com.atlassian.webresource.api.assembler.WebResourceAssembler;
import com.atlassian.webresource.api.assembler.WebResourceAssemblerFactory;
import com.atlassian.webresource.api.assembler.WebResourceSet;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@Deprecated
public class WebResourceManagerImpl
implements WebResourceManager {
    protected final LegacyPageBuilderService pageBuilderService;
    @Deprecated
    protected final WebResourceIntegration webResourceIntegration;
    private final WebResourceAssemblerFactory webResourceAssemblerFactory;
    private final WebResourceUrlProvider webResourceUrlProvider;

    public WebResourceManagerImpl(PluginResourceLocator pluginResourceLocator, WebResourceIntegration webResourceIntegration, WebResourceUrlProvider webResourceUrlProvider) {
        this(pluginResourceLocator, webResourceIntegration, webResourceUrlProvider, new DefaultResourceBatchingConfiguration());
    }

    public WebResourceManagerImpl(PluginResourceLocator pluginResourceLocator, WebResourceIntegration webResourceIntegration, WebResourceUrlProvider webResourceUrlProvider, ResourceBatchingConfiguration batchingConfiguration) {
        this(new DefaultWebResourceAssemblerFactory(pluginResourceLocator.temporaryWayToGetGlobalsDoNotUseIt()), null, webResourceIntegration, webResourceUrlProvider, null);
    }

    public WebResourceManagerImpl(PrebakeWebResourceAssemblerFactory webResourceAssemblerFactory, PluginResourceLocator pluginResourceLocator, WebResourceIntegration webResourceIntegration, WebResourceUrlProvider webResourceUrlProvider, ResourceBatchingConfiguration batchingConfiguration) {
        this(webResourceAssemblerFactory, new DefaultPageBuilderService(webResourceIntegration, webResourceAssemblerFactory), null, webResourceIntegration, webResourceUrlProvider, null, null);
    }

    public WebResourceManagerImpl(WebResourceAssemblerFactory webResourceAssemblerFactory, LegacyPageBuilderService pageBuilderService, PluginResourceLocator pluginResourceLocator, WebResourceIntegration webResourceIntegration, WebResourceUrlProvider webResourceUrlProvider, ResourceBatchingConfiguration batchingConfiguration, ResourceDependencyResolver resourceDependencyResolver) {
        this.webResourceAssemblerFactory = (WebResourceAssemblerFactory)Assertions.notNull((String)"webResourceAssemblerFactory", (Object)webResourceAssemblerFactory);
        this.pageBuilderService = (LegacyPageBuilderService)Assertions.notNull((String)"pageBuilderService", (Object)pageBuilderService);
        this.webResourceIntegration = (WebResourceIntegration)Assertions.notNull((String)"webResourceIntegration", (Object)webResourceIntegration);
        this.webResourceUrlProvider = (WebResourceUrlProvider)Assertions.notNull((String)"webResourceUrlProvider", (Object)webResourceUrlProvider);
    }

    @Override
    public void requireResource(String moduleCompleteKey) {
        this.getRequestLocalRequiredResources().requireWebResource(moduleCompleteKey);
    }

    @Override
    public void requireResourcesForContext(String context) {
        this.getRequestLocalRequiredResources().requireContext(context);
    }

    @Override
    public void includeResources(Iterable<String> moduleCompleteKeys, Writer writer, UrlMode urlMode) {
        WebResourceAssembler webResourceAssembler = this.createSuperbatchingDisabledWebResourceAssembler();
        for (String moduleCompleteKey : moduleCompleteKeys) {
            webResourceAssembler.resources().requireWebResource(moduleCompleteKey);
        }
        WebResourceSet webResourceSet = webResourceAssembler.assembled().drainIncludedResources();
        webResourceSet.writeHtmlTags(writer, UrlModeUtils.convert(urlMode));
    }

    @Override
    public void includeResources(Writer writer, UrlMode urlMode) {
        this.includeResources(writer, urlMode, new DefaultWebResourceFilter());
    }

    @Override
    public void includeResources(Writer writer, UrlMode urlMode, WebResourceFilter webResourceFilter) {
        this.writeIncludedResources(writer, urlMode, webResourceFilter);
        this.clear();
    }

    @Override
    public String getRequiredResources(UrlMode urlMode) {
        return this.getRequiredResources(urlMode, new DefaultWebResourceFilter());
    }

    @Override
    public String getRequiredResources(UrlMode urlMode, WebResourceFilter filter) {
        return this.writeIncludedResources(new StringWriter(), urlMode, filter).toString();
    }

    protected String getRequiredResources(UrlMode urlMode, WebResourceFilter webResourceFilter, Set<String> excludedResourceKeys, List<String> excludedContexts) {
        return this.writeIncludedResources(new StringWriter(), urlMode, webResourceFilter, excludedResourceKeys, excludedContexts).toString();
    }

    private <W extends Writer> W writeIncludedResources(W writer, UrlMode urlMode, WebResourceFilter filter) {
        return this.writeIncludedResources(writer, urlMode, filter, Collections.emptySet(), Collections.emptyList());
    }

    private <W extends Writer> W writeIncludedResources(W writer, UrlMode urlMode, final WebResourceFilter filter, Set<String> excludedResourceKeys, List<String> excludedContexts) {
        if (null != excludedResourceKeys && !excludedResourceKeys.isEmpty() && null != excludedContexts && !excludedContexts.isEmpty()) {
            this.getRequestLocalRequiredResources().exclude(excludedResourceKeys, new HashSet<String>(excludedContexts));
        }
        DefaultWebResourceSet webResourceSet = (DefaultWebResourceSet)this.getRequestLocalAssembledResources().peek();
        webResourceSet.writeHtmlTags(writer, UrlModeUtils.convert(urlMode), (Predicate<WebResource>)Predicates.alwaysTrue(), new Predicate<ResourceUrl>(){

            public boolean apply(@Nullable ResourceUrl input) {
                return filter.matches(input.getName());
            }
        });
        return writer;
    }

    @Override
    public void requireResource(String moduleCompleteKey, Writer writer, UrlMode urlMode) {
        WebResourceAssembler webResourceAssembler = this.createSuperbatchingDisabledWebResourceAssembler();
        webResourceAssembler.resources().requireWebResource(moduleCompleteKey);
        WebResourceSet webResourceSet = webResourceAssembler.assembled().drainIncludedResources();
        webResourceSet.writeHtmlTags(writer, UrlModeUtils.convert(urlMode));
    }

    @Override
    public String getResourceTags(String moduleCompleteKey, UrlMode urlMode) {
        StringWriter writer = new StringWriter();
        this.requireResource(moduleCompleteKey, writer, urlMode);
        return writer.toString();
    }

    @Override
    @Deprecated
    public String getStaticPluginResource(String moduleCompleteKey, String resourceName, UrlMode urlMode) {
        return this.webResourceUrlProvider.getStaticPluginResourceUrl(moduleCompleteKey, resourceName, urlMode);
    }

    @Override
    @Deprecated
    public String getStaticPluginResource(ModuleDescriptor<?> moduleDescriptor, String resourceName, UrlMode urlMode) {
        return this.webResourceUrlProvider.getStaticPluginResourceUrl(moduleDescriptor, resourceName, urlMode);
    }

    @Override
    @Deprecated
    public String getStaticPluginResource(ModuleDescriptor<?> moduleDescriptor, String resourceName) {
        return this.getStaticPluginResource(moduleDescriptor, resourceName, UrlMode.AUTO);
    }

    @Override
    @Deprecated
    public String getStaticPluginResource(String moduleCompleteKey, String resourceName) {
        return this.getStaticPluginResource(moduleCompleteKey, resourceName, UrlMode.AUTO);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <T> T executeInNewContext(Supplier<T> nestedExecution) {
        Map<String, Object> cache = this.webResourceIntegration.getRequestCache();
        ImmutableMap storedState = ImmutableMap.copyOf(cache);
        cache.clear();
        try {
            Object object = nestedExecution.get();
            return (T)object;
        }
        finally {
            cache.clear();
            cache.putAll((Map<String, Object>)storedState);
        }
    }

    protected AssembledResources getRequestLocalAssembledResources() {
        return this.pageBuilderService.assembler().assembled();
    }

    protected RequiredResources getRequestLocalRequiredResources() {
        return this.pageBuilderService.assembler().resources();
    }

    private void clear() {
        this.pageBuilderService.clearRequestLocal();
    }

    private WebResourceAssembler createSuperbatchingDisabledWebResourceAssembler() {
        return this.webResourceAssemblerFactory.create().includeSuperbatchResources(false).build();
    }

    @Override
    public void includeResources(Writer writer) {
        this.includeResources(writer, UrlMode.AUTO);
    }

    @Override
    public String getRequiredResources() {
        return this.getRequiredResources(UrlMode.AUTO);
    }

    @Override
    public void requireResource(String moduleCompleteKey, Writer writer) {
        this.requireResource(moduleCompleteKey, writer, UrlMode.AUTO);
    }

    @Override
    public String getResourceTags(String moduleCompleteKey) {
        return this.getResourceTags(moduleCompleteKey, UrlMode.AUTO);
    }

    @Override
    public String getStaticResourcePrefix() {
        return this.webResourceUrlProvider.getStaticResourcePrefix(UrlMode.AUTO);
    }

    @Override
    public String getStaticResourcePrefix(String resourceCounter) {
        return this.webResourceUrlProvider.getStaticResourcePrefix(resourceCounter, UrlMode.AUTO);
    }
}

