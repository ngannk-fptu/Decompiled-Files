/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.client;

import java.io.IOException;
import java.net.URI;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.Assert;

public abstract class AbstractClientHttpRequestFactoryWrapper
implements ClientHttpRequestFactory {
    private final ClientHttpRequestFactory requestFactory;

    protected AbstractClientHttpRequestFactoryWrapper(ClientHttpRequestFactory requestFactory) {
        Assert.notNull((Object)requestFactory, "ClientHttpRequestFactory must not be null");
        this.requestFactory = requestFactory;
    }

    @Override
    public final ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        return this.createRequest(uri, httpMethod, this.requestFactory);
    }

    protected abstract ClientHttpRequest createRequest(URI var1, HttpMethod var2, ClientHttpRequestFactory var3) throws IOException;
}

