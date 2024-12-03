/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.core.util.URIUtil
 *  com.atlassian.oauth.ServiceProvider
 *  com.atlassian.oauth.consumer.ConsumerService
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.ResponseException
 *  com.google.common.annotations.VisibleForTesting
 */
package com.atlassian.applinks.oauth.auth.twolo.impersonation;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.core.util.URIUtil;
import com.atlassian.applinks.oauth.auth.twolo.TwoLeggedOAuthRequest;
import com.atlassian.oauth.ServiceProvider;
import com.atlassian.oauth.consumer.ConsumerService;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.ResponseException;
import com.google.common.annotations.VisibleForTesting;
import java.util.Collections;

public class TwoLeggedOAuthWithImpersonationRequest
extends TwoLeggedOAuthRequest {
    @VisibleForTesting
    static final String XOAUTH_REQUESTOR_ID = "xoauth_requestor_id";
    private String username;

    public TwoLeggedOAuthWithImpersonationRequest(String url, Request.MethodType methodType, Request wrappedRequest, ServiceProvider serviceProvider, ConsumerService consumerService, ApplicationId applicationId, String username) {
        super(url, methodType, wrappedRequest, serviceProvider, consumerService, applicationId);
        this.username = username;
    }

    @Override
    protected void signRequest() throws ResponseException {
        this.wrappedRequest.setUrl(this.addUsernameToUrl(this.url));
        this.parameters.put(XOAUTH_REQUESTOR_ID, Collections.singletonList(this.username));
        super.signRequest();
    }

    private String addUsernameToUrl(String url) {
        if (url.contains("?")) {
            return url + "&" + XOAUTH_REQUESTOR_ID + "=" + URIUtil.utf8Encode((String)this.username);
        }
        return url + "?" + XOAUTH_REQUESTOR_ID + "=" + URIUtil.utf8Encode((String)this.username);
    }
}

