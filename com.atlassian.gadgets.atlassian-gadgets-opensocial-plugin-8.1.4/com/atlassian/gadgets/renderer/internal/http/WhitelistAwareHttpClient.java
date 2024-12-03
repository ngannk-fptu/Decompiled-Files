/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.opensocial.spi.Whitelist
 *  com.atlassian.gadgets.util.IllegalHttpTargetHostException
 *  com.atlassian.sal.api.user.UserManager
 *  org.apache.http.HttpHost
 *  org.apache.http.HttpRequest
 *  org.apache.http.HttpResponse
 *  org.apache.http.client.ClientProtocolException
 *  org.apache.http.client.HttpClient
 *  org.apache.http.client.ResponseHandler
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.conn.ClientConnectionManager
 *  org.apache.http.params.HttpParams
 *  org.apache.http.protocol.HttpContext
 */
package com.atlassian.gadgets.renderer.internal.http;

import com.atlassian.gadgets.opensocial.spi.Whitelist;
import com.atlassian.gadgets.util.IllegalHttpTargetHostException;
import com.atlassian.sal.api.user.UserManager;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

public class WhitelistAwareHttpClient
implements HttpClient {
    private final HttpClient delegate;
    private final Whitelist whitelist;
    private final UserManager userManager;

    public WhitelistAwareHttpClient(HttpClient delegate, Whitelist whitelist, UserManager userManager) {
        this.delegate = delegate;
        this.whitelist = whitelist;
        this.userManager = userManager;
    }

    @Deprecated
    public HttpParams getParams() {
        return this.delegate.getParams();
    }

    @Deprecated
    public ClientConnectionManager getConnectionManager() {
        return this.delegate.getConnectionManager();
    }

    public HttpResponse execute(HttpUriRequest request) throws IOException, ClientProtocolException {
        this.validateRequestTargetAgainstWhitelist((HttpRequest)request);
        return this.delegate.execute(request);
    }

    public HttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException, ClientProtocolException {
        this.validateRequestTargetAgainstWhitelist((HttpRequest)request);
        return this.delegate.execute(request, context);
    }

    public HttpResponse execute(HttpHost target, HttpRequest request) throws IOException, ClientProtocolException {
        this.validateRequestTargetAgainstWhitelist(request);
        return this.delegate.execute(target, request);
    }

    public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException {
        this.validateRequestTargetAgainstWhitelist(request);
        return this.delegate.execute(target, request, context);
    }

    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        this.validateRequestTargetAgainstWhitelist((HttpRequest)request);
        return (T)this.delegate.execute(request, responseHandler);
    }

    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
        this.validateRequestTargetAgainstWhitelist((HttpRequest)request);
        return (T)this.delegate.execute(request, responseHandler, context);
    }

    public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        this.validateRequestTargetAgainstWhitelist(request);
        return (T)this.delegate.execute(target, request, responseHandler);
    }

    public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
        this.validateRequestTargetAgainstWhitelist(request);
        return (T)this.delegate.execute(target, request, responseHandler, context);
    }

    private void validateRequestTargetAgainstWhitelist(HttpRequest request) {
        try {
            URI target = new URI(request.getRequestLine().getUri());
            if (!this.whitelist.allows(target, this.userManager.getRemoteUserKey())) {
                throw new IllegalHttpTargetHostException(target.toString());
            }
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }
}

