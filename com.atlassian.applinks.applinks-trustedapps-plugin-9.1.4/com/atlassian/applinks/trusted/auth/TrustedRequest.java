/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkResponseHandler
 *  com.atlassian.applinks.core.auth.AbstractApplicationLinkRequest
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ResponseHandler
 *  com.atlassian.sal.api.net.ReturningResponseHandler
 *  com.atlassian.security.auth.trustedapps.CurrentApplication
 *  com.atlassian.security.auth.trustedapps.EncryptedCertificate
 *  com.atlassian.security.auth.trustedapps.TrustedApplicationUtils
 *  com.atlassian.security.auth.trustedapps.request.TrustedRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.trusted.auth;

import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.applinks.core.auth.AbstractApplicationLinkRequest;
import com.atlassian.applinks.trusted.auth.TrustedApplinksResponseHandler;
import com.atlassian.applinks.trusted.auth.TrustedApplinksReturningResponseHandler;
import com.atlassian.applinks.trusted.auth.TrustedResponseHandler;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;
import com.atlassian.sal.api.net.ReturningResponseHandler;
import com.atlassian.security.auth.trustedapps.CurrentApplication;
import com.atlassian.security.auth.trustedapps.EncryptedCertificate;
import com.atlassian.security.auth.trustedapps.TrustedApplicationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrustedRequest
extends AbstractApplicationLinkRequest {
    private static final Logger log = LoggerFactory.getLogger(TrustedRequest.class);
    private final CurrentApplication currentApplication;
    private final String username;

    public TrustedRequest(String url, Request wrappedRequest, CurrentApplication currentApplication, String username) {
        super(url, wrappedRequest);
        this.currentApplication = currentApplication;
        this.username = username;
    }

    public <R> R execute(ApplicationLinkResponseHandler<R> applicationLinkResponseHandler) throws ResponseException {
        this.signRequest();
        return (R)this.wrappedRequest.execute(this.ensureTrustedApplinksResponseHandler(applicationLinkResponseHandler));
    }

    private <R> ApplicationLinkResponseHandler<R> ensureTrustedApplinksResponseHandler(ApplicationLinkResponseHandler<R> applicationLinkResponseHandler) {
        if (applicationLinkResponseHandler instanceof TrustedApplinksResponseHandler) {
            return applicationLinkResponseHandler;
        }
        return new TrustedApplinksResponseHandler<R>(this.url, applicationLinkResponseHandler, (ApplicationLinkRequest)this, this.followRedirects);
    }

    public void execute(ResponseHandler responseHandler) throws ResponseException {
        this.signRequest();
        this.wrappedRequest.execute(this.ensureTrustedResponseHandler(responseHandler));
    }

    private ResponseHandler ensureTrustedResponseHandler(ResponseHandler responseHandler) {
        if (responseHandler instanceof TrustedResponseHandler) {
            return responseHandler;
        }
        return new TrustedResponseHandler(this.url, (ResponseHandler<Response>)responseHandler, (ApplicationLinkRequest)this, this.followRedirects);
    }

    public <RET> RET executeAndReturn(ReturningResponseHandler<? super Response, RET> responseHandler) throws ResponseException {
        this.signRequest();
        return (RET)this.wrappedRequest.executeAndReturn(this.ensureTrustedApplinksReturningResponseHandler(responseHandler));
    }

    private <R> ReturningResponseHandler<? super Response, R> ensureTrustedApplinksReturningResponseHandler(ReturningResponseHandler<? super Response, R> returningResponseHandler) {
        if (returningResponseHandler instanceof TrustedApplinksReturningResponseHandler) {
            return returningResponseHandler;
        }
        return new TrustedApplinksReturningResponseHandler(this.url, returningResponseHandler, (ApplicationLinkRequest)this, this.followRedirects);
    }

    public void signRequest() {
        this.signRequest(this.unsignedRequest());
    }

    public void signRequest(com.atlassian.security.auth.trustedapps.request.TrustedRequest unsignedRequest) {
        if (log.isDebugEnabled()) {
            log.debug("signRequest - signing request for url:" + this.url);
        }
        TrustedApplicationUtils.addRequestParameters((EncryptedCertificate)this.currentApplication.encode(this.username, this.url), (com.atlassian.security.auth.trustedapps.request.TrustedRequest)unsignedRequest);
    }

    public com.atlassian.security.auth.trustedapps.request.TrustedRequest unsignedRequest() {
        return (arg_0, arg_1) -> ((ApplicationLinkRequest)this.wrappedRequest).setHeader(arg_0, arg_1);
    }
}

