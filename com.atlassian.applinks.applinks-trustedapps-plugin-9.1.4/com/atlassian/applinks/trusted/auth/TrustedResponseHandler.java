/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.core.auth.AbstractApplicationLinkResponseHandler
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ResponseHandler
 */
package com.atlassian.applinks.trusted.auth;

import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.core.auth.AbstractApplicationLinkResponseHandler;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;

public class TrustedResponseHandler<T extends Response>
extends AbstractApplicationLinkResponseHandler
implements ResponseHandler<Response> {
    private final ResponseHandler<Response> responseHandler;

    public TrustedResponseHandler(String url, ResponseHandler<Response> responseHandler, ApplicationLinkRequest wrappedRequest, boolean followRedirects) {
        super(url, wrappedRequest, followRedirects);
        this.responseHandler = responseHandler;
    }

    public void handle(Response response) throws ResponseException {
        if (this.followRedirects && this.redirectHelper.responseShouldRedirect(response)) {
            this.wrappedRequest.setUrl(this.redirectHelper.getNextRedirectLocation(response));
            this.wrappedRequest.execute((ResponseHandler)this);
        } else {
            this.responseHandler.handle(response);
        }
    }
}

