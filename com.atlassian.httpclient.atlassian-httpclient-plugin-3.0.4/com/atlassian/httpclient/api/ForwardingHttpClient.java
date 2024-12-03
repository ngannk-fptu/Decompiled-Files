/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ForwardingObject
 */
package com.atlassian.httpclient.api;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.httpclient.api.Request;
import com.google.common.collect.ForwardingObject;
import java.net.URI;
import java.util.regex.Pattern;

public abstract class ForwardingHttpClient
extends ForwardingObject
implements HttpClient {
    protected ForwardingHttpClient() {
    }

    protected abstract HttpClient delegate();

    @Override
    public Request.Builder newRequest() {
        return this.delegate().newRequest();
    }

    @Override
    public Request.Builder newRequest(URI uri) {
        return this.delegate().newRequest(uri);
    }

    @Override
    public Request.Builder newRequest(String uri) {
        return this.delegate().newRequest(uri);
    }

    @Override
    public Request.Builder newRequest(URI uri, String contentType, String entity) {
        return this.delegate().newRequest(uri, contentType, entity);
    }

    @Override
    public Request.Builder newRequest(String uri, String contentType, String entity) {
        return this.delegate().newRequest(uri, contentType, entity);
    }

    @Override
    public void flushCacheByUriPattern(Pattern uriPattern) {
        this.delegate().flushCacheByUriPattern(uriPattern);
    }
}

