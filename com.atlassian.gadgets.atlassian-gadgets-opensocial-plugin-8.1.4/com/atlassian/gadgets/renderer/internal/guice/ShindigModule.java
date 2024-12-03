/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.LocalGadgetSpecProvider
 *  com.atlassian.gadgets.opensocial.spi.Whitelist
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.collect.ImmutableList
 *  com.google.inject.AbstractModule
 *  com.google.inject.Binder
 *  com.google.inject.CreationException
 *  com.google.inject.Inject
 *  com.google.inject.Module
 *  com.google.inject.Provider
 *  com.google.inject.TypeLiteral
 *  com.google.inject.name.Names
 *  com.google.inject.spi.Message
 *  org.apache.commons.io.IOUtils
 *  org.apache.http.client.RedirectStrategy
 *  org.apache.http.conn.routing.HttpRoutePlanner
 *  org.apache.http.impl.client.DefaultRedirectStrategy
 *  org.apache.http.impl.conn.SystemDefaultRoutePlanner
 *  org.apache.shindig.auth.SecurityTokenDecoder
 *  org.apache.shindig.common.ContainerConfig
 *  org.apache.shindig.common.cache.CacheProvider
 *  org.apache.shindig.common.util.ResourceLoader
 *  org.apache.shindig.gadgets.DefaultGadgetSpecFactory
 *  org.apache.shindig.gadgets.GadgetSpecFactory
 *  org.apache.shindig.gadgets.http.HttpFetcher
 *  org.apache.shindig.gadgets.http.HttpResponse
 *  org.apache.shindig.gadgets.preload.HttpPreloader
 *  org.apache.shindig.gadgets.preload.Preloader
 *  org.apache.shindig.gadgets.render.RenderingContentRewriter
 *  org.apache.shindig.gadgets.rewrite.ContentRewriter
 *  org.apache.shindig.gadgets.rewrite.lexer.DefaultContentRewriter
 *  org.apache.shindig.gadgets.servlet.MakeRequestHandler
 */
package com.atlassian.gadgets.renderer.internal.guice;

import com.atlassian.gadgets.LocalGadgetSpecProvider;
import com.atlassian.gadgets.opensocial.spi.Whitelist;
import com.atlassian.gadgets.renderer.internal.AtlassianContainerConfig;
import com.atlassian.gadgets.renderer.internal.cache.ClearableCacheProvider;
import com.atlassian.gadgets.renderer.internal.guice.XercesParseModule;
import com.atlassian.gadgets.renderer.internal.http.HttpClientFetcher;
import com.atlassian.gadgets.renderer.internal.http.WhitelistAwareRedirectStrategy;
import com.atlassian.gadgets.renderer.internal.local.LocalGadgetSpecFactory;
import com.atlassian.gadgets.renderer.internal.rewrite.AtlassianGadgetsContentRewriter;
import com.atlassian.gadgets.renderer.internal.servlet.TrustedAppMakeRequestHandler;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.CreationException;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.spi.Message;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.ProxySelector;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.shindig.auth.SecurityTokenDecoder;
import org.apache.shindig.common.ContainerConfig;
import org.apache.shindig.common.cache.CacheProvider;
import org.apache.shindig.common.util.ResourceLoader;
import org.apache.shindig.gadgets.DefaultGadgetSpecFactory;
import org.apache.shindig.gadgets.GadgetSpecFactory;
import org.apache.shindig.gadgets.http.HttpFetcher;
import org.apache.shindig.gadgets.http.HttpResponse;
import org.apache.shindig.gadgets.preload.HttpPreloader;
import org.apache.shindig.gadgets.preload.Preloader;
import org.apache.shindig.gadgets.render.RenderingContentRewriter;
import org.apache.shindig.gadgets.rewrite.ContentRewriter;
import org.apache.shindig.gadgets.rewrite.lexer.DefaultContentRewriter;
import org.apache.shindig.gadgets.servlet.MakeRequestHandler;

public class ShindigModule
extends AbstractModule {
    private static final String SHINDIG_PROPERTIES = "shindig.properties";
    private static final String AG_PROPERTIES = "atlassian-gadgets.properties";
    private final Properties properties;
    private final SecurityTokenDecoder decoder;
    private final Iterable<LocalGadgetSpecProvider> localGadgetSpecProviders;
    private final Whitelist whitelist;
    private final ClearableCacheProvider clearableCacheProvider;
    private final UserManager userManager;

    public ShindigModule(SecurityTokenDecoder decoder, Iterable<LocalGadgetSpecProvider> localGadgetSpecProviders, Whitelist whitelist, ClearableCacheProvider clearableCacheProvider, UserManager userManager) {
        this.decoder = decoder;
        this.localGadgetSpecProviders = localGadgetSpecProviders;
        this.whitelist = whitelist;
        this.clearableCacheProvider = clearableCacheProvider;
        this.userManager = userManager;
        this.properties = new Properties();
        this.loadPropertiesFrom(SHINDIG_PROPERTIES, this.properties);
        this.loadPropertiesFrom(AG_PROPERTIES, this.properties);
    }

    private void loadPropertiesFrom(String propertiesFile, Properties properties) {
        InputStream is = null;
        try {
            is = ResourceLoader.openResource((String)propertiesFile);
            properties.load(is);
        }
        catch (IOException e) {
            throw new CreationException(Arrays.asList(new Message("Unable to load properties: " + propertiesFile)));
        }
        finally {
            IOUtils.closeQuietly((InputStream)is);
        }
    }

    protected void configure() {
        Names.bindProperties((Binder)this.binder(), (Properties)this.properties);
        this.bind(DefaultContentRewriter.class).to(AtlassianGadgetsContentRewriter.class);
        this.bind(MakeRequestHandler.class).to(TrustedAppMakeRequestHandler.class);
        this.bind(ContainerConfig.class).to(AtlassianContainerConfig.class);
        this.bind(SecurityTokenDecoder.class).toInstance((Object)this.decoder);
        this.bind(HttpFetcher.class).to(HttpClientFetcher.class);
        this.bind(CacheProvider.class).toInstance((Object)this.clearableCacheProvider);
        this.bind((TypeLiteral)new TypeLiteral<Iterable<LocalGadgetSpecProvider>>(){}).toInstance(this.localGadgetSpecProviders);
        this.bind(GadgetSpecFactory.class).annotatedWith((Annotation)Names.named((String)"fallback")).to(DefaultGadgetSpecFactory.class);
        this.bind(GadgetSpecFactory.class).to(LocalGadgetSpecFactory.class);
        this.bind(Whitelist.class).toInstance((Object)this.whitelist);
        this.bind(RedirectStrategy.class).toInstance((Object)new WhitelistAwareRedirectStrategy(this.whitelist, (RedirectStrategy)DefaultRedirectStrategy.INSTANCE, this.userManager));
        this.bind(HttpRoutePlanner.class).toInstance((Object)new SystemDefaultRoutePlanner(ProxySelector.getDefault()));
        this.install((Module)new XercesParseModule());
        this.bind((TypeLiteral)new TypeLiteral<List<ContentRewriter>>(){}).toProvider(ContentRewritersProvider.class);
        this.bind((TypeLiteral)new TypeLiteral<List<Preloader>>(){}).toProvider(PreloaderProvider.class);
        this.requestStaticInjection(new Class[]{HttpResponse.class});
    }

    static class PreloaderProvider
    implements Provider<List<Preloader>> {
        private final List<Preloader> preloaders;

        @Inject
        public PreloaderProvider(HttpPreloader httpPreloader) {
            this.preloaders = ImmutableList.of((Object)httpPreloader);
        }

        public List<Preloader> get() {
            return this.preloaders;
        }
    }

    static class ContentRewritersProvider
    implements Provider<List<ContentRewriter>> {
        private final List<ContentRewriter> rewriters;

        @Inject
        public ContentRewritersProvider(DefaultContentRewriter optimizingRewriter, RenderingContentRewriter renderingRewriter) {
            this.rewriters = ImmutableList.of((Object)optimizingRewriter, (Object)renderingRewriter);
        }

        public List<ContentRewriter> get() {
            return this.rewriters;
        }
    }
}

