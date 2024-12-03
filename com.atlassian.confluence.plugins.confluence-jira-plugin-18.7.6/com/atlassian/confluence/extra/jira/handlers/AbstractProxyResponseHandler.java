/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.ApplicationLinkResponseHandler
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.extra.jira.handlers;

import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractProxyResponseHandler
implements ApplicationLinkResponseHandler {
    protected final HttpServletRequest req;
    protected final ApplicationLinkRequestFactory requestFactory;
    protected final HttpServletResponse resp;

    protected AbstractProxyResponseHandler(HttpServletRequest req, ApplicationLinkRequestFactory requestFactory, HttpServletResponse resp) {
        this.req = req;
        this.requestFactory = requestFactory;
        this.resp = resp;
    }

    public Object handle(Response response) throws ResponseException {
        if (response.isSuccessful()) {
            if (response.getStatusCode() >= 300 && response.getStatusCode() < 400) {
                return this.retryRequest(response);
            }
            return this.processSuccess(response);
        }
        try {
            this.resp.sendError(response.getStatusCode(), response.getStatusText());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    protected abstract Object retryRequest(Response var1) throws ResponseException;

    protected abstract Object processSuccess(Response var1) throws ResponseException;

    public Object credentialsRequired(Response response) {
        this.resp.setStatus(401);
        this.resp.setHeader("WWW-Authenticate", "OAuth realm=\"" + this.requestFactory.getAuthorisationURI().toString() + "\"");
        return null;
    }
}

