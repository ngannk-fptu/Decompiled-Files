/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkResponseHandler
 *  com.atlassian.applinks.api.AuthorisationURIGenerator
 *  com.atlassian.sal.api.net.RequestFilePart
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ResponseHandler
 *  com.atlassian.sal.api.net.ReturningResponseHandler
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.rest.client;

import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.applinks.api.AuthorisationURIGenerator;
import com.atlassian.applinks.internal.rest.client.AuthorisationUriAwareRequest;
import com.atlassian.sal.api.net.RequestFilePart;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;
import com.atlassian.sal.api.net.ReturningResponseHandler;
import com.google.common.annotations.VisibleForTesting;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;

public class DefaultAuthorisationUriAwareRequest
implements AuthorisationUriAwareRequest {
    private final ApplicationLinkRequest delegateRequest;
    private final AuthorisationURIGenerator generator;

    public DefaultAuthorisationUriAwareRequest(@Nonnull ApplicationLinkRequest delegateRequest, @Nonnull AuthorisationURIGenerator generator) {
        this.delegateRequest = Objects.requireNonNull(delegateRequest, "request");
        this.generator = Objects.requireNonNull(generator, "generator");
    }

    @Override
    @Nonnull
    public AuthorisationURIGenerator getAuthorisationUriGenerator() {
        return this.generator;
    }

    public ApplicationLinkRequest setConnectionTimeout(int connectionTimeout) {
        this.delegateRequest.setConnectionTimeout(connectionTimeout);
        return this;
    }

    public ApplicationLinkRequest setSoTimeout(int soTimeout) {
        this.delegateRequest.setSoTimeout(soTimeout);
        return this;
    }

    public ApplicationLinkRequest setUrl(String url) {
        this.delegateRequest.setUrl(url);
        return this;
    }

    public ApplicationLinkRequest setRequestBody(String requestBody) {
        this.delegateRequest.setRequestBody(requestBody);
        return this;
    }

    public ApplicationLinkRequest setRequestBody(String requestBody, String contentType) {
        this.delegateRequest.setRequestBody(requestBody, contentType);
        return this;
    }

    public ApplicationLinkRequest setFiles(List<RequestFilePart> files) {
        this.delegateRequest.setFiles(files);
        return this;
    }

    public ApplicationLinkRequest setEntity(Object entity) {
        this.delegateRequest.setEntity(entity);
        return this;
    }

    public ApplicationLinkRequest addRequestParameters(String ... params) {
        this.delegateRequest.addRequestParameters(params);
        return this;
    }

    public ApplicationLinkRequest addBasicAuthentication(String hostname, String username, String password) {
        this.delegateRequest.addBasicAuthentication(hostname, username, password);
        return this;
    }

    public ApplicationLinkRequest addHeader(String headerName, String headerValue) {
        this.delegateRequest.addHeader(headerName, headerValue);
        return this;
    }

    public ApplicationLinkRequest setHeader(String headerName, String headerValue) {
        this.delegateRequest.setHeader(headerName, headerValue);
        return this;
    }

    public ApplicationLinkRequest setFollowRedirects(boolean follow) {
        this.delegateRequest.setFollowRedirects(follow);
        return this;
    }

    public Map<String, List<String>> getHeaders() {
        return this.delegateRequest.getHeaders();
    }

    public void execute(ResponseHandler<? super Response> responseHandler) throws ResponseException {
        this.delegateRequest.execute(responseHandler);
    }

    public String execute() throws ResponseException {
        return this.delegateRequest.execute();
    }

    public <RET> RET executeAndReturn(ReturningResponseHandler<? super Response, RET> responseHandler) throws ResponseException {
        return (RET)this.delegateRequest.executeAndReturn(responseHandler);
    }

    public <R> R execute(ApplicationLinkResponseHandler<R> responseHandler) throws ResponseException {
        return (R)this.delegateRequest.execute(responseHandler);
    }

    @VisibleForTesting
    ApplicationLinkRequest getDelegateRequest() {
        return this.delegateRequest;
    }
}

