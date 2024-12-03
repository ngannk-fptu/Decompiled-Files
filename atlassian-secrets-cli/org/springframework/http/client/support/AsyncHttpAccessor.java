/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.client.support;

import java.io.IOException;
import java.net.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AsyncClientHttpRequest;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Deprecated
public class AsyncHttpAccessor {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private AsyncClientHttpRequestFactory asyncRequestFactory;

    public void setAsyncRequestFactory(AsyncClientHttpRequestFactory asyncRequestFactory) {
        Assert.notNull((Object)asyncRequestFactory, "AsyncClientHttpRequestFactory must not be null");
        this.asyncRequestFactory = asyncRequestFactory;
    }

    public AsyncClientHttpRequestFactory getAsyncRequestFactory() {
        Assert.state(this.asyncRequestFactory != null, "No AsyncClientHttpRequestFactory set");
        return this.asyncRequestFactory;
    }

    protected AsyncClientHttpRequest createAsyncRequest(URI url, HttpMethod method) throws IOException {
        AsyncClientHttpRequest request = this.getAsyncRequestFactory().createAsyncRequest(url, method);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Created asynchronous " + method.name() + " request for \"" + url + "\"");
        }
        return request;
    }
}

