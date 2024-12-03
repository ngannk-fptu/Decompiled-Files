/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.httpclient.api.HttpClient
 *  com.atlassian.httpclient.api.Request$Builder
 *  com.atlassian.httpclient.api.Request$Method
 *  com.atlassian.httpclient.api.Response
 *  com.atlassian.httpclient.api.ResponseTooLargeException
 *  com.atlassian.httpclient.api.factory.HttpClientFactory
 *  com.atlassian.httpclient.api.factory.HttpClientOptions
 *  com.atlassian.webhooks.WebhooksConfiguration
 *  com.atlassian.webhooks.WebhooksNotInitializedException
 *  com.atlassian.webhooks.request.WebhookHttpRequest
 *  com.atlassian.webhooks.request.WebhookHttpResponse
 *  com.google.common.base.MoreObjects
 *  com.google.common.primitives.Ints
 *  io.atlassian.util.concurrent.Promise$TryConsumer
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.webhooks.internal.client.request;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.httpclient.api.Request;
import com.atlassian.httpclient.api.Response;
import com.atlassian.httpclient.api.ResponseTooLargeException;
import com.atlassian.httpclient.api.factory.HttpClientFactory;
import com.atlassian.httpclient.api.factory.HttpClientOptions;
import com.atlassian.webhooks.WebhooksConfiguration;
import com.atlassian.webhooks.WebhooksNotInitializedException;
import com.atlassian.webhooks.internal.WebhooksLifecycleAware;
import com.atlassian.webhooks.internal.client.RequestExecutor;
import com.atlassian.webhooks.internal.client.request.DefaultRawResponse;
import com.atlassian.webhooks.request.WebhookHttpRequest;
import com.atlassian.webhooks.request.WebhookHttpResponse;
import com.google.common.base.MoreObjects;
import com.google.common.primitives.Ints;
import io.atlassian.util.concurrent.Promise;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultRequestExecutor
implements RequestExecutor,
WebhooksLifecycleAware {
    private static final Logger log = LoggerFactory.getLogger(DefaultRequestExecutor.class);
    private final HttpClientFactory httpClientFactory;
    private volatile HttpClient client;
    private volatile WebhooksConfiguration configuration;

    public DefaultRequestExecutor(HttpClientFactory httpClientFactory) {
        this.httpClientFactory = httpClientFactory;
    }

    @Override
    @Nonnull
    public CompletableFuture<WebhookHttpResponse> execute(@Nonnull WebhookHttpRequest request) {
        final CompletableFuture<WebhookHttpResponse> responseFuture = new CompletableFuture<WebhookHttpResponse>();
        try {
            HttpClient httpClient = this.client;
            if (httpClient == null) {
                throw new WebhooksNotInitializedException("The webhooks plugin hasn't been initialized yet. Webhook will not be dispatched.");
            }
            URI uri = URI.create(request.getUrl());
            Request.Builder builder = httpClient.newRequest().setUri(uri);
            request.getHeaders().forEach((arg_0, arg_1) -> ((Request.Builder)builder).setHeader(arg_0, arg_1));
            if (request.getContent() != null) {
                ((Request.Builder)builder.setEntityStream((InputStream)new ByteArrayInputStream(request.getContent()))).setContentType((String)request.getContentType().orElseThrow(() -> new IllegalStateException("If content is provided, Content-Type must also be specified")));
            }
            builder.execute(Request.Method.valueOf((String)request.getMethod().name())).then((Promise.TryConsumer)new Promise.TryConsumer<Response>(){

                public void accept(@Nonnull Response response) {
                    responseFuture.complete(DefaultRequestExecutor.this.transform(response));
                }

                public void fail(@Nonnull Throwable throwable) {
                    if (throwable instanceof ResponseTooLargeException) {
                        ResponseTooLargeException tooLarge = (ResponseTooLargeException)throwable;
                        this.accept(tooLarge.getResponse());
                    } else {
                        responseFuture.completeExceptionally(throwable);
                    }
                }
            });
        }
        catch (Exception e) {
            responseFuture.completeExceptionally(e);
        }
        return responseFuture;
    }

    @Override
    public void onStart(WebhooksConfiguration configuration) {
        this.configuration = configuration;
        HttpClientOptions options = new HttpClientOptions();
        options.setMaxCacheEntries(0);
        options.setIgnoreCookies(true);
        options.setMaxCallbackThreadPoolSize(configuration.getMaxCallbackThreads());
        options.setMaxConnectionsPerHost(configuration.getMaxHttpConnectionsPerHost());
        options.setMaxEntitySize(configuration.getMaxResponseBodySize());
        options.setConnectionPoolTimeToLive(1, TimeUnit.MINUTES);
        options.setMaxTotalConnections(configuration.getMaxHttpConnections());
        options.setConnectionTimeout(DefaultRequestExecutor.toSeconds(configuration.getConnectionTimeout()), TimeUnit.SECONDS);
        options.setSocketTimeout(DefaultRequestExecutor.toSeconds(configuration.getSocketTimeout()), TimeUnit.SECONDS);
        options.setIoThreadCount(configuration.getIoThreadCount());
        options.setBlacklistedAddresses(configuration.getBlacklistedAddresses());
        this.client = this.httpClientFactory.create(options);
    }

    @Override
    public void onStop() {
        HttpClient httpClient = this.client;
        this.client = null;
        this.configuration = null;
        if (httpClient != null) {
            try {
                this.httpClientFactory.dispose(httpClient);
            }
            catch (Exception e) {
                log.warn("Error while disposing webhooks HTTP client", (Throwable)e);
            }
        }
    }

    private static int toSeconds(Duration duration) {
        return Ints.saturatedCast((long)duration.getSeconds());
    }

    private WebhookHttpResponse transform(Response response) {
        return new DefaultRawResponse(response, ((WebhooksConfiguration)MoreObjects.firstNonNull((Object)this.configuration, (Object)WebhooksConfiguration.DEFAULT)).getMaxResponseBodySize());
    }
}

