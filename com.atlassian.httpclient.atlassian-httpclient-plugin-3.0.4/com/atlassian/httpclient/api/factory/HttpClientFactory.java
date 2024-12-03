/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.executor.ThreadLocalContextManager
 *  javax.annotation.Nonnull
 */
package com.atlassian.httpclient.api.factory;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.httpclient.api.factory.HttpClientOptions;
import com.atlassian.sal.api.executor.ThreadLocalContextManager;
import javax.annotation.Nonnull;

public interface HttpClientFactory {
    @Nonnull
    public HttpClient create(@Nonnull HttpClientOptions var1);

    @Nonnull
    public <C> HttpClient create(@Nonnull HttpClientOptions var1, @Nonnull ThreadLocalContextManager<C> var2);

    public void dispose(@Nonnull HttpClient var1) throws Exception;
}

