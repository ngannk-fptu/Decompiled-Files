/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFactory
 */
package com.atlassian.applinks.internal.common.net;

import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import java.net.URI;
import java.net.URISyntaxException;

public class BasicHttpAuthRequestFactory<T extends Request<?, ?>>
implements RequestFactory<T> {
    private final RequestFactory<T> requestFactory;
    private final String username;
    private final String password;

    public BasicHttpAuthRequestFactory(RequestFactory<T> requestFactory, String username, String password) {
        this.password = password;
        this.requestFactory = requestFactory;
        this.username = username;
    }

    public T createRequest(Request.MethodType methodType, String url) {
        URI uri;
        try {
            uri = new URI(url);
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException("invalid url '" + url + "'", e);
        }
        Request request = this.requestFactory.createRequest(methodType, url);
        request.addBasicAuthentication(uri.getHost(), this.username, this.password);
        return (T)request;
    }

    public boolean supportsHeader() {
        return this.requestFactory.supportsHeader();
    }
}

