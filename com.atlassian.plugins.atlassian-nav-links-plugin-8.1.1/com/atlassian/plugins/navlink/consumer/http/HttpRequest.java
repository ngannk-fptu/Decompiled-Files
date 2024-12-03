/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  org.apache.http.client.HttpClient
 *  org.apache.http.client.ResponseHandler
 *  org.apache.http.client.methods.HttpGet
 *  org.apache.http.client.methods.HttpUriRequest
 */
package com.atlassian.plugins.navlink.consumer.http;

import com.atlassian.plugins.navlink.consumer.http.CleaningUpResponseHandler;
import com.google.common.base.Preconditions;
import java.io.IOException;
import javax.annotation.Nonnull;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

public class HttpRequest {
    private final HttpClient client;
    private final HttpGet request;

    public HttpRequest(@Nonnull HttpClient client, @Nonnull HttpGet request) {
        this.client = (HttpClient)Preconditions.checkNotNull((Object)client);
        this.request = (HttpGet)Preconditions.checkNotNull((Object)request);
    }

    @Nonnull
    public <T> T executeRequest(@Nonnull ResponseHandler<T> responseHandler) throws IOException {
        return (T)this.client.execute((HttpUriRequest)this.request, this.withCleanUpHandler((ResponseHandler)Preconditions.checkNotNull(responseHandler)));
    }

    private <T> ResponseHandler<T> withCleanUpHandler(@Nonnull ResponseHandler<T> responseHandler) {
        return new CleaningUpResponseHandler<T>(responseHandler);
    }
}

