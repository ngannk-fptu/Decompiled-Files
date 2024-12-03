/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkResponseHandler
 *  com.atlassian.applinks.api.AuthorisationURIGenerator
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 */
package com.atlassian.confluence.extra.jira.applink;

import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.applinks.api.AuthorisationURIGenerator;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.confluence.extra.jira.api.services.JiraResponseHandler;
import com.atlassian.confluence.extra.jira.exception.TrustedAppsException;
import com.atlassian.confluence.extra.jira.util.JiraUtil;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import java.io.IOException;

public class JiraAppLinkResponseHandler
implements ApplicationLinkResponseHandler {
    private final JiraResponseHandler responseHandler;
    private final String url;
    private final AuthorisationURIGenerator requestFactory;

    public JiraAppLinkResponseHandler(JiraResponseHandler.HandlerType handlerType, String url, AuthorisationURIGenerator requestFactory) {
        this.url = url;
        this.requestFactory = requestFactory;
        this.responseHandler = JiraUtil.createResponseHandler(handlerType, url);
    }

    public Object handle(Response resp) throws ResponseException {
        try {
            if ("ERROR".equals(resp.getHeader("X-Seraph-Trusted-App-Status"))) {
                String taError = resp.getHeader("X-Seraph-Trusted-App-Error");
                throw new TrustedAppsException(taError);
            }
            JiraUtil.checkForErrors(resp, this.url);
            this.responseHandler.handleJiraResponse(resp.getResponseBodyAsStream(), null);
            return this.responseHandler;
        }
        catch (IOException e) {
            throw new ResponseException((Throwable)e);
        }
    }

    public Object credentialsRequired(Response response) throws ResponseException {
        throw new ResponseException((Throwable)new CredentialsRequiredException(this.requestFactory, ""));
    }

    public JiraResponseHandler getResponseHandler() {
        return this.responseHandler;
    }
}

