/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginDisabledEvent
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleDisabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 *  com.atlassian.plugin.servlet.DownloadableResource
 *  com.atlassian.plugin.servlet.ServletContextFactory
 *  com.atlassian.sourcemap.ReadableSourceMap
 *  com.atlassian.webresource.spi.NoOpResourceCompiler
 *  com.atlassian.webresource.spi.ResourceCompiler
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.cache.filecache.Cache;
import com.atlassian.plugin.cache.filecache.impl.PassThroughCache;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.plugin.event.events.PluginModuleDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.plugin.servlet.DownloadableResource;
import com.atlassian.plugin.servlet.ServletContextFactory;
import com.atlassian.plugin.webresource.DefaultResourceBatchingConfiguration;
import com.atlassian.plugin.webresource.PluginResourceLocator;
import com.atlassian.plugin.webresource.ResourceBatchingConfiguration;
import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.WebResourceModuleDescriptor;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.plugin.webresource.analytics.EventFiringHelper;
import com.atlassian.plugin.webresource.analytics.events.ServerResourceCacheInvalidationCause;
import com.atlassian.plugin.webresource.analytics.events.ServerResourceCacheInvalidationEvent;
import com.atlassian.plugin.webresource.impl.Globals;
import com.atlassian.plugin.webresource.impl.config.Config;
import com.atlassian.plugin.webresource.impl.helpers.Helpers;
import com.atlassian.plugin.webresource.impl.http.Controller;
import com.atlassian.plugin.webresource.impl.http.Router;
import com.atlassian.plugin.webresource.impl.support.Content;
import com.atlassian.plugin.webresource.impl.support.ContentImpl;
import com.atlassian.plugin.webresource.impl.support.http.Request;
import com.atlassian.plugin.webresource.impl.support.http.Response;
import com.atlassian.plugin.webresource.transformer.DefaultStaticTransformers;
import com.atlassian.plugin.webresource.transformer.DefaultStaticTransformersSupplier;
import com.atlassian.plugin.webresource.transformer.StaticTransformers;
import com.atlassian.plugin.webresource.transformer.TransformerCache;
import com.atlassian.sourcemap.ReadableSourceMap;
import com.atlassian.webresource.spi.NoOpResourceCompiler;
import com.atlassian.webresource.spi.ResourceCompiler;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

@Deprecated
public class PluginResourceLocatorImpl
implements PluginResourceLocator {
    private volatile Globals globals;

    public PluginResourceLocatorImpl(WebResourceIntegration webResourceIntegration, ServletContextFactory servletContextFactory, WebResourceUrlProvider webResourceUrlProvider, PluginEventManager pluginEventManager, ResourceCompiler resourceCompiler) {
        this(webResourceIntegration, servletContextFactory, webResourceUrlProvider, (ResourceBatchingConfiguration)new DefaultResourceBatchingConfiguration(), pluginEventManager, resourceCompiler);
    }

    public PluginResourceLocatorImpl(WebResourceIntegration webResourceIntegration, ServletContextFactory servletContextFactory, WebResourceUrlProvider webResourceUrlProvider, ResourceBatchingConfiguration batchingConfiguration, PluginEventManager pluginEventManager, ResourceCompiler resourceCompiler) {
        Config config = new Config(batchingConfiguration, webResourceIntegration, webResourceUrlProvider, servletContextFactory, new TransformerCache(pluginEventManager, webResourceIntegration.getPluginAccessor()), resourceCompiler);
        DefaultStaticTransformers staticTransformers = new DefaultStaticTransformers(new DefaultStaticTransformersSupplier(webResourceIntegration, webResourceUrlProvider, config.getCdnResourceUrlTransformer()));
        config.setStaticTransformers(staticTransformers);
        this.initialize(pluginEventManager, config, webResourceIntegration.getEventPublisher());
    }

    public PluginResourceLocatorImpl(WebResourceIntegration webResourceIntegration, ServletContextFactory servletContextFactory, WebResourceUrlProvider webResourceUrlProvider, ResourceBatchingConfiguration batchingConfiguration, PluginEventManager pluginEventManager, StaticTransformers staticTransformers, ResourceCompiler resourceCompiler) {
        Config config = new Config(batchingConfiguration, webResourceIntegration, webResourceUrlProvider, servletContextFactory, new TransformerCache(pluginEventManager, webResourceIntegration.getPluginAccessor()), resourceCompiler);
        config.setStaticTransformers(staticTransformers);
        this.initialize(pluginEventManager, config, webResourceIntegration.getEventPublisher());
    }

    public PluginResourceLocatorImpl(PluginEventManager pluginEventManager, Config config, EventPublisher eventPublisher) {
        this.initialize(pluginEventManager, config, eventPublisher);
    }

    public PluginResourceLocatorImpl(PluginEventManager pluginEventManager, Globals globals) {
        this.initialize(pluginEventManager, globals);
    }

    public PluginResourceLocatorImpl(WebResourceIntegration webResourceIntegration, ServletContextFactory servletContextFactory, WebResourceUrlProvider webResourceUrlProvider, PluginEventManager pluginEventManager) {
        this(webResourceIntegration, servletContextFactory, webResourceUrlProvider, (ResourceBatchingConfiguration)new DefaultResourceBatchingConfiguration(), pluginEventManager, (ResourceCompiler)new NoOpResourceCompiler());
    }

    public PluginResourceLocatorImpl(WebResourceIntegration webResourceIntegration, ServletContextFactory servletContextFactory, WebResourceUrlProvider webResourceUrlProvider, ResourceBatchingConfiguration batchingConfiguration, PluginEventManager pluginEventManager) {
        this(webResourceIntegration, servletContextFactory, webResourceUrlProvider, batchingConfiguration, pluginEventManager, (ResourceCompiler)new NoOpResourceCompiler());
    }

    public PluginResourceLocatorImpl(WebResourceIntegration webResourceIntegration, ServletContextFactory servletContextFactory, WebResourceUrlProvider webResourceUrlProvider, ResourceBatchingConfiguration batchingConfiguration, PluginEventManager pluginEventManager, StaticTransformers staticTransformers) {
        this(webResourceIntegration, servletContextFactory, webResourceUrlProvider, batchingConfiguration, pluginEventManager, staticTransformers, (ResourceCompiler)new NoOpResourceCompiler());
    }

    protected void initialize(PluginEventManager pluginEventManager, Config config, EventPublisher eventPublisher) {
        this.initialize(pluginEventManager, new Globals(config, eventPublisher, pluginEventManager));
    }

    protected void initialize(PluginEventManager pluginEventManager, @Nonnull Globals globals) {
        this.globals = globals;
        pluginEventManager.register((Object)this);
    }

    @PluginEventListener
    public void onPluginDisabled(PluginDisabledEvent event) {
        this.globals.triggerStateChange();
        this.fireServerResourceCacheInvalidationEvent(ServerResourceCacheInvalidationCause.PLUGIN_DISABLED_EVENT);
    }

    @PluginEventListener
    public void onPluginEnabled(PluginEnabledEvent event) {
        this.globals.triggerStateChange();
        this.fireServerResourceCacheInvalidationEvent(ServerResourceCacheInvalidationCause.PLUGIN_ENABLED_EVENT);
    }

    @PluginEventListener
    public void onPluginModuleEnabled(PluginModuleEnabledEvent event) {
        if (event.getModule() instanceof WebResourceModuleDescriptor) {
            this.globals.triggerStateChange();
            this.fireServerResourceCacheInvalidationEvent(ServerResourceCacheInvalidationCause.PLUGIN_WEBRESOURCE_MODULE_ENABLED);
        }
    }

    @PluginEventListener
    public void onPluginModuleDisabled(PluginModuleDisabledEvent event) {
        if (event.getModule() instanceof WebResourceModuleDescriptor) {
            this.globals.triggerStateChange();
            this.fireServerResourceCacheInvalidationEvent(ServerResourceCacheInvalidationCause.PLUGIN_WEBRESOURCE_MODULE_DISABLED);
        }
    }

    @Override
    public boolean matches(String url) {
        return this.globals.getRouter().canDispatch(url);
    }

    @Override
    public DownloadableResource getDownloadableResource(String url, Map<String, String> queryParams) {
        if (queryParams == null) {
            queryParams = new HashMap<String, String>();
        }
        final DownloadableResource[] downloadableResource = new DownloadableResource[]{null};
        Router router = new Router(this.globals){

            @Override
            protected Controller createController(Globals globals, Request request, Response response) {
                return new Controller(globals, request, response){

                    @Override
                    protected void sendCached(final Content content, Map<String, String> params, final boolean isCachingEnabled) {
                        downloadableResource[0] = Helpers.asDownloadableResource(new ContentImpl(content.getContentType(), content.isTransformed()){

                            @Override
                            public ReadableSourceMap writeTo(OutputStream out, boolean isSourceMapEnabled) {
                                Cache cache = isCachingEnabled ? globals.getContentCache() : new PassThroughCache();
                                cache.cache("http", request.getUrl(), out, producerOut -> content.writeTo(producerOut, false));
                                return null;
                            }
                        });
                    }

                    @Override
                    protected boolean checkIfCachedAndNotModified(Date updatedAt) {
                        return false;
                    }
                };
            }
        };
        router.dispatch(new Request(this.globals, url, queryParams), null);
        return downloadableResource[0];
    }

    @Override
    public Globals temporaryWayToGetGlobalsDoNotUseIt() {
        return this.globals;
    }

    private void fireServerResourceCacheInvalidationEvent(@Nonnull ServerResourceCacheInvalidationCause eventCause) {
        if (this.globals.getConfig().isPerformanceTrackingEnabled()) {
            EventFiringHelper.publishIfEventPublisherNonNull(this.globals.getEventPublisher(), new ServerResourceCacheInvalidationEvent(eventCause));
        }
    }
}

