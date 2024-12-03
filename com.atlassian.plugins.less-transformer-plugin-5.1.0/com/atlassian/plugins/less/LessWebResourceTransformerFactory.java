/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.lesscss.spi.DimensionAwareUriResolver
 *  com.atlassian.lesscss.spi.UriResolver
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.elements.ResourceDescriptor
 *  com.atlassian.plugin.webresource.transformer.TransformerParameters
 *  com.atlassian.plugin.webresource.transformer.UrlReadingWebResourceTransformer
 *  com.atlassian.webresource.api.prebake.DimensionAwareTransformerUrlBuilder
 *  com.atlassian.webresource.api.prebake.DimensionAwareWebResourceTransformerFactory
 *  com.atlassian.webresource.api.prebake.Dimensions
 */
package com.atlassian.plugins.less;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.lesscss.spi.DimensionAwareUriResolver;
import com.atlassian.lesscss.spi.UriResolver;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.elements.ResourceDescriptor;
import com.atlassian.plugin.webresource.transformer.TransformerParameters;
import com.atlassian.plugin.webresource.transformer.UrlReadingWebResourceTransformer;
import com.atlassian.plugins.less.LessCompilerSupplier;
import com.atlassian.plugins.less.LessTransformerUrlBuilder;
import com.atlassian.plugins.less.LessWebResourceTransformer;
import com.atlassian.plugins.less.UriResolverManager;
import com.atlassian.plugins.less.UriStateManager;
import com.atlassian.plugins.less.UriUtils;
import com.atlassian.webresource.api.prebake.DimensionAwareTransformerUrlBuilder;
import com.atlassian.webresource.api.prebake.DimensionAwareWebResourceTransformerFactory;
import com.atlassian.webresource.api.prebake.Dimensions;
import java.net.URI;
import java.util.ArrayList;

public class LessWebResourceTransformerFactory
implements DimensionAwareWebResourceTransformerFactory {
    private final LessCompilerSupplier lessCompiler;
    private final PluginAccessor pluginAccessor;
    private final UriStateManager uriStateManager;
    private final UriResolverManager uriResolverManager;
    private final EventPublisher eventPublisher;

    public LessWebResourceTransformerFactory(LessCompilerSupplier lessCompiler, PluginAccessor pluginAccessor, UriStateManager uriStateManager, UriResolverManager uriResolverManager, EventPublisher eventPublisher) {
        this.lessCompiler = lessCompiler;
        this.pluginAccessor = pluginAccessor;
        this.uriStateManager = uriStateManager;
        this.uriResolverManager = uriResolverManager;
        this.eventPublisher = eventPublisher;
    }

    public Dimensions computeDimensions() {
        Dimensions dimensions = Dimensions.empty();
        for (UriResolver uriResolver : this.uriResolverManager.getResolvers()) {
            if (!(uriResolver instanceof DimensionAwareUriResolver)) continue;
            DimensionAwareUriResolver dimensionAwareUriResolver = (DimensionAwareUriResolver)uriResolver;
            dimensions = dimensions.product(dimensionAwareUriResolver.computeDimensions());
        }
        return dimensions;
    }

    public DimensionAwareTransformerUrlBuilder makeUrlBuilder(TransformerParameters params) {
        ArrayList<URI> resources = new ArrayList<URI>();
        String webResourceKey = params.getPluginKey() + ":" + params.getModuleKey();
        ModuleDescriptor descriptor = this.pluginAccessor.getEnabledPluginModule(webResourceKey);
        for (ResourceDescriptor resourceDescriptor : descriptor.getResourceDescriptors()) {
            if (!resourceDescriptor.getLocation().endsWith(".less")) continue;
            resources.add(UriUtils.resolveUri(descriptor.getPluginKey(), resourceDescriptor.getResourceLocationForName(null)));
        }
        return new LessTransformerUrlBuilder(resources, this.uriStateManager);
    }

    public UrlReadingWebResourceTransformer makeResourceTransformer(TransformerParameters params) {
        return new LessWebResourceTransformer(this.lessCompiler, this.uriResolverManager, params.getPluginKey(), this.eventPublisher);
    }
}

