/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.oauth.Request
 *  com.atlassian.oauth.ServiceProvider
 *  com.atlassian.oauth.consumer.ConsumerService
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 */
package com.atlassian.applinks.oauth.auth.twolo;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.oauth.auth.OAuthRequest;
import com.atlassian.oauth.ServiceProvider;
import com.atlassian.oauth.consumer.ConsumerService;
import com.atlassian.sal.api.net.Request;
import java.net.URI;

public class TwoLeggedOAuthRequest
extends OAuthRequest {
    public TwoLeggedOAuthRequest(String url, Request.MethodType methodType, Request wrappedRequest, ServiceProvider serviceProvider, ConsumerService consumerService, ApplicationId applicationId) {
        super(url, methodType, wrappedRequest, applicationId, serviceProvider, consumerService);
    }

    @Override
    protected com.atlassian.oauth.Request createUnsignedRequest() {
        return new com.atlassian.oauth.Request(TwoLeggedOAuthRequest.toOAuthMethodType(this.methodType), URI.create(this.url), this.toOAuthParameters(""));
    }
}

