/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.failurecache.Cacheable
 *  com.atlassian.failurecache.ExpiringValue
 *  com.google.common.base.Preconditions
 *  io.atlassian.util.concurrent.ResettableLazyReference
 *  org.apache.http.client.HttpClient
 *  org.apache.http.client.ResponseHandler
 *  org.apache.http.client.methods.HttpGet
 *  org.apache.http.impl.client.CloseableHttpClient
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.plugins.navlink.consumer.http;

import com.atlassian.failurecache.Cacheable;
import com.atlassian.failurecache.ExpiringValue;
import com.atlassian.plugins.navlink.consumer.http.ExpiringValueResponseHandler;
import com.atlassian.plugins.navlink.consumer.http.HttpClientFactory;
import com.atlassian.plugins.navlink.consumer.http.HttpRequest;
import com.atlassian.plugins.navlink.consumer.http.HttpRequestFactory;
import com.atlassian.plugins.navlink.consumer.http.UserAgentProperty;
import com.atlassian.plugins.navlink.consumer.http.caching.HttpCacheExpiryService;
import com.google.common.base.Preconditions;
import io.atlassian.util.concurrent.ResettableLazyReference;
import java.io.IOException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public class HttpRequestFactoryImpl
implements HttpRequestFactory,
Cacheable,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(HttpRequestFactoryImpl.class);
    private final HttpClientFactory clientFactory;
    private final UserAgentProperty userAgentProperty;
    private final HttpCacheExpiryService httpCacheExpiryService;
    private final ResettableLazyReference<CloseableHttpClient> client = new ResettableLazyReference<CloseableHttpClient>(){

        protected CloseableHttpClient create() throws Exception {
            return HttpRequestFactoryImpl.this.clientFactory.createHttpClient();
        }
    };

    public HttpRequestFactoryImpl(HttpClientFactory clientFactory, UserAgentProperty userAgentProperty, HttpCacheExpiryService httpCacheExpiryService) {
        this.clientFactory = (HttpClientFactory)Preconditions.checkNotNull((Object)clientFactory);
        this.userAgentProperty = (UserAgentProperty)Preconditions.checkNotNull((Object)userAgentProperty);
        this.httpCacheExpiryService = (HttpCacheExpiryService)Preconditions.checkNotNull((Object)httpCacheExpiryService);
    }

    public int getCachePriority() {
        return 100;
    }

    public void clearCache() {
        if (this.client.isInitialized()) {
            try {
                ((CloseableHttpClient)this.client.get()).close();
            }
            catch (IOException e) {
                log.info("IOException when closing HttpClient: {}", (Object)e.getMessage());
                log.debug("Stacktrace:", (Throwable)e);
            }
        }
        this.client.reset();
    }

    public void destroy() throws Exception {
        this.clearCache();
    }

    @Override
    public <T> ExpiringValue<T> executeGetRequest(String url, ResponseHandler<T> responseHandler) throws IOException {
        return (ExpiringValue)this.createRequest((String)Preconditions.checkNotNull((Object)url)).executeRequest(new ExpiringValueResponseHandler((ResponseHandler)Preconditions.checkNotNull(responseHandler), this.httpCacheExpiryService));
    }

    private HttpRequest createRequest(String url) {
        return new HttpRequest((HttpClient)this.client.get(), this.createGetRequest(url));
    }

    private HttpGet createGetRequest(String url) {
        HttpGet request = new HttpGet(url);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-Type", "application/json");
        request.setHeader("User-Agent", this.userAgentProperty.get());
        return request;
    }
}

