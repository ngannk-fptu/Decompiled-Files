/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.core.auth.AbstractApplicationLinkResponseHandler
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ReturningResponseHandler
 */
package com.atlassian.applinks.trusted.auth;

import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.core.auth.AbstractApplicationLinkResponseHandler;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ReturningResponseHandler;

public class TrustedApplinksReturningResponseHandler<T extends Response, R>
extends AbstractApplicationLinkResponseHandler
implements ReturningResponseHandler<Response, R> {
    private ReturningResponseHandler<? super Response, R> returningResponseHandler;

    public TrustedApplinksReturningResponseHandler(String url, ReturningResponseHandler<? super Response, R> returningResponseHandler, ApplicationLinkRequest wrappedRequest, boolean followRedirects) {
        super(url, wrappedRequest, followRedirects);
        this.returningResponseHandler = returningResponseHandler;
    }

    public R handle(Response response) throws ResponseException {
        return this.followRedirects && this.redirectHelper.responseShouldRedirect(response) ? this.followRedirects(response) : this.handleNormally(response);
    }

    private R followRedirects(Response response) throws ResponseException {
        this.wrappedRequest.setUrl(this.redirectHelper.getNextRedirectLocation(response));
        return (R)this.wrappedRequest.executeAndReturn((ReturningResponseHandler)this);
    }

    private R handleNormally(Response response) throws ResponseException {
        return (R)this.returningResponseHandler.handle(response);
    }
}

