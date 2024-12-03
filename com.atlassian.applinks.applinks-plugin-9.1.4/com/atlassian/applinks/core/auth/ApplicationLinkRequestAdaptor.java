/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.ApplicationLinkResponseHandler
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.RequestFilePart
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ResponseHandler
 *  com.atlassian.sal.api.net.ReturningResponseHandler
 */
package com.atlassian.applinks.core.auth;

import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFilePart;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;
import com.atlassian.sal.api.net.ReturningResponseHandler;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ApplicationLinkRequestAdaptor
implements ApplicationLinkRequest {
    private Request request;

    public ApplicationLinkRequestAdaptor(Request request) {
        this.request = Objects.requireNonNull(request, "request");
    }

    public ApplicationLinkRequest addHeader(String headerName, String headerValue) {
        return this.setDelegate(this.request.addHeader(headerName, headerValue));
    }

    public ApplicationLinkRequest addRequestParameters(String ... params) {
        return this.setDelegate(this.request.addRequestParameters(params));
    }

    public ApplicationLinkRequest addBasicAuthentication(String hostname, String username, String password) {
        return this.setDelegate(this.request.addBasicAuthentication(hostname, username, password));
    }

    public String execute() throws ResponseException {
        return this.request.execute();
    }

    public void execute(ResponseHandler responseHandler) throws ResponseException {
        this.request.execute(responseHandler);
    }

    public <RET> RET executeAndReturn(ReturningResponseHandler<? super Response, RET> responseHandler) throws ResponseException {
        return (RET)this.request.executeAndReturn(responseHandler);
    }

    public <R> R execute(ApplicationLinkResponseHandler<R> applicationLinkResponseHandler) throws ResponseException {
        return (R)this.request.executeAndReturn(applicationLinkResponseHandler);
    }

    public Map<String, List<String>> getHeaders() {
        return this.request.getHeaders();
    }

    public ApplicationLinkRequest setConnectionTimeout(int connectionTimeout) {
        return this.setDelegate(this.request.setConnectionTimeout(connectionTimeout));
    }

    public ApplicationLinkRequest setEntity(Object entity) {
        return this.setDelegate(this.request.setEntity(entity));
    }

    public ApplicationLinkRequest setHeader(String headerName, String headerValue) {
        return this.setDelegate(this.request.setHeader(headerName, headerValue));
    }

    public ApplicationLinkRequest setRequestBody(String requestBody) {
        return this.setDelegate(this.request.setRequestBody(requestBody));
    }

    public ApplicationLinkRequest setRequestBody(String requestBody, String contentType) {
        return this.setDelegate(this.request.setRequestBody(requestBody, contentType));
    }

    public ApplicationLinkRequest setFiles(List<RequestFilePart> files) {
        return this.setDelegate(this.request.setFiles(files));
    }

    public ApplicationLinkRequest setSoTimeout(int soTimeout) {
        return this.setDelegate(this.request.setSoTimeout(soTimeout));
    }

    public ApplicationLinkRequest setUrl(String url) {
        return this.setDelegate(this.request.setUrl(url));
    }

    private ApplicationLinkRequest setDelegate(Request request) {
        this.request = Objects.requireNonNull(request, "Method chaining response");
        return this;
    }

    public ApplicationLinkRequest setFollowRedirects(boolean follow) {
        return this.setDelegate(this.request.setFollowRedirects(follow));
    }
}

