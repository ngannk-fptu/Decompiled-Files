/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ReturningResponseHandler
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.oauth.auth;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService;
import com.atlassian.applinks.oauth.auth.OAuthRedirectingApplicationLinkResponseHandler;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ReturningResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuthApplinksReturningResponseHandler<R>
extends OAuthRedirectingApplicationLinkResponseHandler
implements ReturningResponseHandler<Response, R> {
    private static final Logger log = LoggerFactory.getLogger(OAuthApplinksReturningResponseHandler.class);
    private ReturningResponseHandler<? super Response, R> returningResponseHandler;

    public OAuthApplinksReturningResponseHandler(String url, ReturningResponseHandler<Response, R> returningResponseHandler, ConsumerTokenStoreService consumerTokenStoreService, ApplicationLinkRequest wrappedRequest, ApplicationId applicationId, String username, boolean followRedirects) {
        super(url, wrappedRequest, consumerTokenStoreService, applicationId, username, followRedirects);
        this.returningResponseHandler = returningResponseHandler;
    }

    public OAuthApplinksReturningResponseHandler(String url, ReturningResponseHandler<? super Response, R> returningResponseHandler, ApplicationLinkRequest wrappedRequest, ApplicationId applicationId, boolean followRedirects) {
        super(url, wrappedRequest, null, applicationId, null, followRedirects);
        this.returningResponseHandler = returningResponseHandler;
    }

    public R handle(Response response) throws ResponseException {
        this.checkForOAuthProblemAndRemoveConsumerTokenIfNecessary(response);
        if (this.followRedirects && this.redirectHelper.responseShouldRedirect(response)) {
            this.wrappedRequest.setUrl(this.redirectHelper.getNextRedirectLocation(response));
            return (R)this.wrappedRequest.executeAndReturn((ReturningResponseHandler)this);
        }
        return (R)this.returningResponseHandler.handle(response);
    }
}

