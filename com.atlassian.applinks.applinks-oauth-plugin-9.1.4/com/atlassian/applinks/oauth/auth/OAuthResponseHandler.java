/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ResponseHandler
 */
package com.atlassian.applinks.oauth.auth;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService;
import com.atlassian.applinks.oauth.auth.OAuthRedirectingApplicationLinkResponseHandler;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;

public class OAuthResponseHandler<T extends Response>
extends OAuthRedirectingApplicationLinkResponseHandler
implements ResponseHandler<Response> {
    private final ResponseHandler<Response> responseHandler;

    public OAuthResponseHandler(String url, ResponseHandler<Response> responseHandler, ConsumerTokenStoreService consumerTokenStoreService, ApplicationLinkRequest wrappedRequest, ApplicationId applicationId, String username, boolean followRedirects) {
        super(url, wrappedRequest, consumerTokenStoreService, applicationId, username, followRedirects);
        this.responseHandler = responseHandler;
    }

    public OAuthResponseHandler(String url, ResponseHandler<Response> responseHandler, ApplicationLinkRequest wrappedRequest, ApplicationId applicationId, boolean followRedirects) {
        super(url, wrappedRequest, null, applicationId, null, followRedirects);
        this.responseHandler = responseHandler;
    }

    public void handle(Response response) throws ResponseException {
        this.checkForOAuthProblemAndRemoveConsumerTokenIfNecessary(response);
        if (this.followRedirects && this.redirectHelper.responseShouldRedirect(response)) {
            this.wrappedRequest.setUrl(this.redirectHelper.getNextRedirectLocation(response));
            this.wrappedRequest.execute((ResponseHandler)this);
        } else {
            this.responseHandler.handle(response);
        }
    }
}

