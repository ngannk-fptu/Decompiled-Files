/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  io.atlassian.util.concurrent.ResettableLazyReference
 *  org.osgi.framework.BundleContext
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.upm.core.pac;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.marketplace.client.MarketplaceClient;
import com.atlassian.marketplace.client.MarketplaceClientFactory;
import com.atlassian.marketplace.client.http.HttpConfiguration;
import com.atlassian.marketplace.client.http.RequestDecorator;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.upm.LazyReferences;
import com.atlassian.upm.UpmFugueConverters;
import com.atlassian.upm.core.Sys;
import com.atlassian.upm.core.pac.ClientContextFactory;
import com.atlassian.upm.core.pac.MarketplaceBaseUrlChangedEvent;
import com.atlassian.upm.core.pac.MarketplaceClientConfiguration;
import com.atlassian.upm.core.pac.MarketplaceClientManager;
import io.atlassian.util.concurrent.ResettableLazyReference;
import java.net.URI;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public abstract class AbstractMarketplaceClientManager
implements DisposableBean,
InitializingBean,
MarketplaceClientManager {
    private final ApplicationProperties applicationProperties;
    private final ResettableLazyReference<MarketplaceClient> client;
    private final EventPublisher eventPublisher;
    private final String version;
    private static final boolean urlModeSupported = AbstractMarketplaceClientManager.isUrlModeSupported();

    public AbstractMarketplaceClientManager(ApplicationProperties applicationProperties, final ClientContextFactory clientContextFactory, BundleContext bundleContext, EventPublisher eventPublisher) {
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        Objects.requireNonNull(clientContextFactory, "clientContextFactory");
        this.client = new ResettableLazyReference<MarketplaceClient>(){

            protected MarketplaceClient create() {
                RequestDecoratorImpl rd = new RequestDecoratorImpl(clientContextFactory, AbstractMarketplaceClientManager.this.getRequestOrigin(), AbstractMarketplaceClientManager.this.getUserAgent());
                HttpConfiguration httpConfig = MarketplaceClientConfiguration.httpConfigurationFromSystemProperties().requestDecorator(UpmFugueConverters.fugueSome(rd)).build();
                return MarketplaceClientFactory.createMarketplaceClient(URI.create(Sys.getMpacBaseUrl()), httpConfig);
            }
        };
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher");
        Dictionary headers = bundleContext.getBundle().getHeaders();
        this.version = headers.get("Bundle-Version").toString();
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    @Override
    public MarketplaceClient getMarketplaceClient() {
        return LazyReferences.safeGet(this.client);
    }

    @EventListener
    public void changedMpacBaseUrl(MarketplaceBaseUrlChangedEvent event) {
        if (this.client.isInitialized()) {
            ((MarketplaceClient)this.client.get()).close();
            this.client.reset();
        }
    }

    public void destroy() {
        if (this.client.isInitialized()) {
            ((MarketplaceClient)this.client.get()).close();
        }
        this.eventPublisher.unregister((Object)this);
    }

    private String getRequestOrigin() {
        String baseUrl = this.getBaseUrl();
        int p = baseUrl.indexOf(47, baseUrl.indexOf("//") + 2);
        return p < 0 ? baseUrl : baseUrl.substring(0, p);
    }

    private String getBaseUrl() {
        if (urlModeSupported) {
            return this.applicationProperties.getBaseUrl(UrlMode.CANONICAL);
        }
        return this.applicationProperties.getBaseUrl();
    }

    private static boolean isUrlModeSupported() {
        try {
            Class.forName("com.atlassian.sal.api.UrlMode");
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    public abstract String getUserAgentPrefix();

    @Override
    public String getUserAgent() {
        return System.getProperty("http.pac.userAgent", this.getUserAgentPrefix() + this.getVersion());
    }

    protected String getVersion() {
        return this.version;
    }

    private static class RequestDecoratorImpl
    implements RequestDecorator {
        private final ClientContextFactory clientContextFactory;
        private final String origin;
        private final String userAgent;

        RequestDecoratorImpl(ClientContextFactory clientContextFactory, String origin, String userAgent) {
            this.clientContextFactory = clientContextFactory;
            this.origin = origin;
            this.userAgent = userAgent;
        }

        @Override
        public Map<String, String> getRequestHeaders() {
            HashMap<String, String> ret = new HashMap<String, String>();
            ret.put("User-Agent", this.userAgent);
            ret.put("Origin", this.origin);
            ret.put("X-Pac-Client-Info", this.clientContextFactory.getClientContext().toString());
            return Collections.unmodifiableMap(ret);
        }
    }
}

