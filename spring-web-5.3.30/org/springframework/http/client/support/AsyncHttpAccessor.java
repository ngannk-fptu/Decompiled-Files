/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.http.client.support;

import java.io.IOException;
import java.net.URI;
import org.apache.commons.logging.Log;
import org.springframework.http.HttpLogging;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AsyncClientHttpRequest;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Deprecated
public class AsyncHttpAccessor {
    protected final Log logger = HttpLogging.forLogName(this.getClass());
    @Nullable
    private AsyncClientHttpRequestFactory asyncRequestFactory;

    public void setAsyncRequestFactory(AsyncClientHttpRequestFactory asyncRequestFactory) {
        Assert.notNull((Object)asyncRequestFactory, (String)"AsyncClientHttpRequestFactory must not be null");
        this.asyncRequestFactory = asyncRequestFactory;
    }

    public AsyncClientHttpRequestFactory getAsyncRequestFactory() {
        Assert.state((this.asyncRequestFactory != null ? 1 : 0) != 0, (String)"No AsyncClientHttpRequestFactory set");
        return this.asyncRequestFactory;
    }

    protected AsyncClientHttpRequest createAsyncRequest(URI url, HttpMethod method) throws IOException {
        AsyncClientHttpRequest request = this.getAsyncRequestFactory().createAsyncRequest(url, method);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Created asynchronous " + method.name() + " request for \"" + url + "\""));
        }
        return request;
    }
}

