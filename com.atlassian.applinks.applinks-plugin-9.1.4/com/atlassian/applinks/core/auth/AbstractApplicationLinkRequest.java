/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.RequestFilePart
 *  com.atlassian.sal.api.net.ResponseException
 */
package com.atlassian.applinks.core.auth;

import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.core.auth.ApplicationLinkRequestAdaptor;
import com.atlassian.applinks.core.auth.ApplicationLinksStringReturningResponseHandler;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFilePart;
import com.atlassian.sal.api.net.ResponseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractApplicationLinkRequest
implements ApplicationLinkRequest {
    protected String url;
    protected final ApplicationLinkRequest wrappedRequest;
    protected final Map<String, List<String>> parameters = new HashMap<String, List<String>>();
    protected boolean followRedirects = true;

    public AbstractApplicationLinkRequest(String url, Request wrappedRequest) {
        this.url = url;
        this.wrappedRequest = new ApplicationLinkRequestAdaptor(wrappedRequest);
        this.wrappedRequest.setFollowRedirects(false);
    }

    public ApplicationLinkRequest setConnectionTimeout(int i) {
        this.wrappedRequest.setConnectionTimeout(i);
        return this;
    }

    public ApplicationLinkRequest setSoTimeout(int i) {
        this.wrappedRequest.setSoTimeout(i);
        return this;
    }

    public ApplicationLinkRequest setUrl(String s) {
        this.url = s;
        this.wrappedRequest.setUrl(s);
        return this;
    }

    public ApplicationLinkRequest setRequestBody(String s) {
        this.wrappedRequest.setRequestBody(s);
        return this;
    }

    public ApplicationLinkRequest setRequestBody(String requestBody, String contentType) {
        this.wrappedRequest.setRequestBody(requestBody, contentType);
        return this;
    }

    public ApplicationLinkRequest setFiles(List<RequestFilePart> files) {
        this.wrappedRequest.setFiles(files);
        return this;
    }

    public ApplicationLinkRequest setEntity(Object o) {
        this.wrappedRequest.setEntity(o);
        return this;
    }

    public ApplicationLinkRequest addRequestParameters(String ... params) {
        this.wrappedRequest.addRequestParameters(params);
        for (int i = 0; i < params.length; i += 2) {
            String name = params[i];
            String value = params[i + 1];
            List<String> list = this.parameters.get(name);
            if (list == null) {
                list = new ArrayList<String>();
                this.parameters.put(name, list);
            }
            list.add(value);
        }
        return this;
    }

    public ApplicationLinkRequest addBasicAuthentication(String hostname, String username, String password) {
        this.wrappedRequest.addBasicAuthentication(hostname, username, password);
        return this;
    }

    public ApplicationLinkRequest addHeader(String s, String s1) {
        this.wrappedRequest.addHeader(s, s1);
        return this;
    }

    public ApplicationLinkRequest setHeader(String s, String s1) {
        this.wrappedRequest.setHeader(s, s1);
        return this;
    }

    public Map<String, List<String>> getHeaders() {
        return this.wrappedRequest.getHeaders();
    }

    public ApplicationLinkRequest setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
        return this;
    }

    public boolean getFollowRedirects() {
        return this.followRedirects;
    }

    public String execute() throws ResponseException {
        this.signRequest();
        return (String)this.executeAndReturn(new ApplicationLinksStringReturningResponseHandler());
    }

    protected abstract void signRequest() throws ResponseException;
}

