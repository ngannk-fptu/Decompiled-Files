/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkResponseHandler
 *  com.atlassian.applinks.core.auth.AbstractApplicationLinkResponseHandler
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 */
package com.atlassian.applinks.trusted.auth;

import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.applinks.core.auth.AbstractApplicationLinkResponseHandler;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;

public class TrustedApplinksResponseHandler<R>
extends AbstractApplicationLinkResponseHandler
implements ApplicationLinkResponseHandler<R> {
    private final ApplicationLinkResponseHandler<R> applicationLinkResponseHandler;

    public TrustedApplinksResponseHandler(String url, ApplicationLinkResponseHandler<R> applicationLinkResponseHandler, ApplicationLinkRequest wrappedRequest, boolean followRedirects) {
        super(url, wrappedRequest, followRedirects);
        this.applicationLinkResponseHandler = applicationLinkResponseHandler;
    }

    public R credentialsRequired(Response response) throws ResponseException {
        return (R)this.applicationLinkResponseHandler.credentialsRequired(response);
    }

    public R handle(Response response) throws ResponseException {
        return this.followRedirects && this.redirectHelper.responseShouldRedirect(response) ? this.followRedirects(response) : this.handleNormally(response);
    }

    private R followRedirects(Response response) throws ResponseException {
        this.wrappedRequest.setUrl(this.redirectHelper.getNextRedirectLocation(response));
        return (R)this.wrappedRequest.execute((ApplicationLinkResponseHandler)this);
    }

    private R handleNormally(Response response) throws ResponseException {
        return (R)this.applicationLinkResponseHandler.handle(response);
    }
}

