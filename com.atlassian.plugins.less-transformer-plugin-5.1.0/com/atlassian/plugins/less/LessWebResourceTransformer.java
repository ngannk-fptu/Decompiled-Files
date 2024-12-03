/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.lesscss.LessCompiler
 *  com.atlassian.lesscss.Loader
 *  com.atlassian.lesscss.PluggableLoader
 *  com.atlassian.lesscss.spi.UriResolver
 *  com.atlassian.plugin.servlet.DownloadableResource
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.transformer.TransformableResource
 *  com.atlassian.plugin.webresource.transformer.UrlReadingWebResourceTransformer
 *  com.google.common.base.Predicate
 *  com.google.common.base.Supplier
 *  com.google.common.collect.Iterables
 */
package com.atlassian.plugins.less;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.lesscss.LessCompiler;
import com.atlassian.lesscss.Loader;
import com.atlassian.lesscss.PluggableLoader;
import com.atlassian.lesscss.spi.UriResolver;
import com.atlassian.plugin.servlet.DownloadableResource;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.transformer.TransformableResource;
import com.atlassian.plugin.webresource.transformer.UrlReadingWebResourceTransformer;
import com.atlassian.plugins.less.LessCompilerSupplier;
import com.atlassian.plugins.less.LessResource;
import com.atlassian.plugins.less.PreCompilationUtils;
import com.atlassian.plugins.less.PreCompiledLessResource;
import com.atlassian.plugins.less.UriResolverManager;
import com.atlassian.plugins.less.UriUtils;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import java.net.URI;

public class LessWebResourceTransformer
implements UrlReadingWebResourceTransformer {
    private final Supplier<LessCompiler> lessc;
    private final Loader loader;
    private final String pluginKey;
    private final UriResolverManager uriResolverManager;
    private final EventPublisher eventPublisher;

    public LessWebResourceTransformer(LessCompilerSupplier lessc, UriResolverManager uriResolverManager, String pluginKey, EventPublisher eventPublisher) {
        this.lessc = lessc;
        this.pluginKey = pluginKey;
        this.uriResolverManager = uriResolverManager;
        this.eventPublisher = eventPublisher;
        this.loader = new PluggableLoader(this.uriResolverManager.getResolvers());
    }

    public DownloadableResource transform(TransformableResource transformableResource, QueryParams params) {
        final URI uri = UriUtils.resolveUri(this.pluginKey, transformableResource.location());
        UriResolver uriResolver = (UriResolver)Iterables.find(this.uriResolverManager.getResolvers(), (Predicate)new Predicate<UriResolver>(){

            public boolean apply(UriResolver resolver) {
                return resolver.supports(uri);
            }
        });
        URI preCompiledUri = PreCompilationUtils.resolvePreCompiledUri(uriResolver, uri);
        return preCompiledUri == null ? new LessResource(transformableResource.nextResource(), (LessCompiler)this.lessc.get(), this.loader, uri, this.eventPublisher) : new PreCompiledLessResource(transformableResource.nextResource(), uriResolver, preCompiledUri);
    }
}

