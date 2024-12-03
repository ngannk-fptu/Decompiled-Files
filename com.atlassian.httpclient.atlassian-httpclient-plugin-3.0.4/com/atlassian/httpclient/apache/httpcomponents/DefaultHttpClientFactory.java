/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.executor.ThreadLocalContextManager
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.httpclient.apache.httpcomponents;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.httpclient.apache.httpcomponents.ApacheAsyncHttpClient;
import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.httpclient.api.factory.HttpClientFactory;
import com.atlassian.httpclient.api.factory.HttpClientOptions;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.executor.ThreadLocalContextManager;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.DisposableBean;

public final class DefaultHttpClientFactory<C>
implements HttpClientFactory,
DisposableBean {
    private final EventPublisher eventPublisher;
    private final ApplicationProperties applicationProperties;
    private final ThreadLocalContextManager<C> threadLocalContextManager;
    private final Set<ApacheAsyncHttpClient> httpClients = new CopyOnWriteArraySet<ApacheAsyncHttpClient>();

    public DefaultHttpClientFactory(@Nonnull EventPublisher eventPublisher, @Nonnull ApplicationProperties applicationProperties, @Nonnull ThreadLocalContextManager<C> threadLocalContextManager) {
        this.eventPublisher = (EventPublisher)Preconditions.checkNotNull((Object)eventPublisher);
        this.applicationProperties = (ApplicationProperties)Preconditions.checkNotNull((Object)applicationProperties);
        this.threadLocalContextManager = (ThreadLocalContextManager)Preconditions.checkNotNull(threadLocalContextManager);
    }

    @Override
    @Nonnull
    public HttpClient create(@Nonnull HttpClientOptions options) {
        return this.doCreate(options, this.threadLocalContextManager);
    }

    @Override
    @Nonnull
    public <C> HttpClient create(@Nonnull HttpClientOptions options, @Nonnull ThreadLocalContextManager<C> threadLocalContextManager) {
        return this.doCreate(options, threadLocalContextManager);
    }

    @Override
    public void dispose(@Nonnull HttpClient httpClient) throws Exception {
        ApacheAsyncHttpClient client;
        if (httpClient instanceof ApacheAsyncHttpClient) {
            client = (ApacheAsyncHttpClient)httpClient;
            if (!this.httpClients.remove(client)) {
                throw new IllegalStateException("Client is already disposed");
            }
        } else {
            throw new IllegalArgumentException("Given client is not disposable");
        }
        client.destroy();
    }

    private <C> HttpClient doCreate(@Nonnull HttpClientOptions options, ThreadLocalContextManager<C> threadLocalContextManager) {
        Preconditions.checkNotNull((Object)options);
        ApacheAsyncHttpClient<C> httpClient = new ApacheAsyncHttpClient<C>(this.eventPublisher, this.applicationProperties, threadLocalContextManager, options);
        this.httpClients.add(httpClient);
        return httpClient;
    }

    public void destroy() throws Exception {
        for (ApacheAsyncHttpClient httpClient : this.httpClients) {
            httpClient.destroy();
        }
    }

    @Nonnull
    @VisibleForTesting
    Iterable<ApacheAsyncHttpClient> getHttpClients() {
        return this.httpClients;
    }
}

