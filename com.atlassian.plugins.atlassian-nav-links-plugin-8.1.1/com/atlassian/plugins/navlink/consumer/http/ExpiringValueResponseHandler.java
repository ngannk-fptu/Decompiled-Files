/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.failurecache.ExpiringValue
 *  com.google.common.base.Preconditions
 *  org.apache.http.HttpResponse
 *  org.apache.http.client.ResponseHandler
 */
package com.atlassian.plugins.navlink.consumer.http;

import com.atlassian.failurecache.ExpiringValue;
import com.atlassian.plugins.navlink.consumer.http.caching.HttpCacheExpiryService;
import com.google.common.base.Preconditions;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;

public class ExpiringValueResponseHandler<T>
implements ResponseHandler<ExpiringValue<T>> {
    private final ResponseHandler<T> delegatee;
    private final HttpCacheExpiryService httpCacheExpiryService;

    public ExpiringValueResponseHandler(ResponseHandler<T> delegatee, HttpCacheExpiryService httpCacheExpiryService) {
        this.delegatee = (ResponseHandler)Preconditions.checkNotNull(delegatee);
        this.httpCacheExpiryService = (HttpCacheExpiryService)Preconditions.checkNotNull((Object)httpCacheExpiryService);
    }

    public ExpiringValue<T> handleResponse(HttpResponse response) throws IOException {
        Object content = this.delegatee.handleResponse(response);
        return this.httpCacheExpiryService.createExpiringValueFrom(response, content);
    }
}

