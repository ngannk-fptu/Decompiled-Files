/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLinkResponseHandler
 *  com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService
 *  com.atlassian.oauth.Request
 *  com.atlassian.oauth.ServiceProvider
 *  com.atlassian.oauth.consumer.ConsumerService
 *  com.atlassian.oauth.consumer.ConsumerToken
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.ResponseException
 *  net.oauth.OAuthMessage
 */
package com.atlassian.applinks.oauth.auth;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService;
import com.atlassian.applinks.oauth.auth.OAuthApplinksResponseHandler;
import com.atlassian.applinks.oauth.auth.OAuthHelper;
import com.atlassian.applinks.oauth.auth.OAuthRequest;
import com.atlassian.oauth.ServiceProvider;
import com.atlassian.oauth.consumer.ConsumerService;
import com.atlassian.oauth.consumer.ConsumerToken;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.ResponseException;
import java.io.IOException;
import java.net.URI;
import net.oauth.OAuthMessage;

public class ThreeLeggedOAuthRequest
extends OAuthRequest {
    private final ConsumerToken consumerToken;
    private final ConsumerTokenStoreService consumerTokenStoreService;
    private final String username;

    public ThreeLeggedOAuthRequest(String url, Request.MethodType methodType, Request wrappedRequest, ServiceProvider serviceProvider, ConsumerService consumerService, ConsumerToken consumerToken, ConsumerTokenStoreService consumerTokenStoreService, ApplicationId applicationId, String username) {
        super(url, methodType, wrappedRequest, applicationId, serviceProvider, consumerService);
        this.consumerToken = consumerToken;
        this.consumerTokenStoreService = consumerTokenStoreService;
        this.username = username;
    }

    @Override
    protected com.atlassian.oauth.Request createUnsignedRequest() {
        return new com.atlassian.oauth.Request(ThreeLeggedOAuthRequest.toOAuthMethodType(this.methodType), URI.create(this.url), this.toOAuthParameters(this.consumerToken.getToken()));
    }

    @Override
    public <R> R execute(ApplicationLinkResponseHandler<R> applicationLinkResponseHandler) throws ResponseException {
        this.signRequest();
        return (R)this.wrappedRequest.execute(new OAuthApplinksResponseHandler<R>(this.url, applicationLinkResponseHandler, this.consumerTokenStoreService, this, this.applicationId, this.username, this.followRedirects));
    }

    @Override
    protected void signRequest() throws ResponseException {
        com.atlassian.oauth.Request oAuthRequest = this.createUnsignedRequest();
        com.atlassian.oauth.Request signedRequest = this.consumerService.sign(oAuthRequest, this.serviceProvider, this.consumerToken);
        OAuthMessage oAuthMessage = OAuthHelper.asOAuthMessage(signedRequest);
        try {
            this.wrappedRequest.setHeader("Authorization", oAuthMessage.getAuthorizationHeader(null));
        }
        catch (IOException e) {
            throw new ResponseException("Unable to generate OAuth Authorization request header.", (Throwable)e);
        }
    }
}

