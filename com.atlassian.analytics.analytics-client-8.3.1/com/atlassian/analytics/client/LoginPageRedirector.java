/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.analytics.client;

import com.atlassian.sal.api.auth.LoginUriProvider;
import java.io.IOException;
import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginPageRedirector {
    private static final String JIRA_SERAPH_SECURITY_ORIGINAL_URL = "os_security_originalurl";
    private static final String CONF_SERAPH_SECURITY_ORIGINAL_URL = "seraph_originalurl";
    private final LoginUriProvider loginUriProvider;

    public LoginPageRedirector(LoginUriProvider loginUriProvider) {
        this.loginUriProvider = loginUriProvider;
    }

    public void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        URI uri = this.getUri(request);
        this.addSessionAttributes(request, uri.toASCIIString());
        response.sendRedirect(this.loginUriProvider.getLoginUri(uri).toASCIIString());
    }

    private URI getUri(HttpServletRequest request) {
        StringBuffer builder = request.getRequestURL();
        if (request.getQueryString() != null) {
            builder.append("?");
            builder.append(request.getQueryString());
        }
        return URI.create(builder.toString());
    }

    private void addSessionAttributes(HttpServletRequest request, String uriString) {
        request.getSession().setAttribute(JIRA_SERAPH_SECURITY_ORIGINAL_URL, (Object)uriString);
        request.getSession().setAttribute(CONF_SERAPH_SECURITY_ORIGINAL_URL, (Object)uriString);
    }
}

