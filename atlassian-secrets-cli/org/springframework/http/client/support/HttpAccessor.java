/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.client.support;

import java.io.IOException;
import java.net.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.Assert;

public abstract class HttpAccessor {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private ClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

    public void setRequestFactory(ClientHttpRequestFactory requestFactory) {
        Assert.notNull((Object)requestFactory, "ClientHttpRequestFactory must not be null");
        this.requestFactory = requestFactory;
    }

    public ClientHttpRequestFactory getRequestFactory() {
        return this.requestFactory;
    }

    protected ClientHttpRequest createRequest(URI url, HttpMethod method) throws IOException {
        ClientHttpRequest request = this.getRequestFactory().createRequest(url, method);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Created " + method.name() + " request for \"" + url + "\"");
        }
        return request;
    }
}

