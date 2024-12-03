/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.plugins.custom_apps.security.authentication;

import com.atlassian.plugins.custom_apps.util.servlet.HttpServletRequests;
import com.atlassian.sal.api.auth.LoginUriProvider;
import java.io.IOException;
import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginPage {
    private static final String JIRA_SERAPH_SECURITY_ORIGINAL_URL = "os_security_originalurl";
    private static final String CONF_SERAPH_SECURITY_ORIGINAL_URL = "seraph_originalurl";
    private final LoginUriProvider loginUriProvider;

    public LoginPage(LoginUriProvider loginUriProvider) {
        this.loginUriProvider = loginUriProvider;
    }

    public String getRedirectUrl(HttpServletRequest request) {
        URI requestUri = HttpServletRequests.getUri(request);
        return this.loginUriProvider.getLoginUri(requestUri).toASCIIString();
    }

    public String getRedirectUrl(URI requestUri) {
        return this.loginUriProvider.getLoginUri(requestUri).toASCIIString();
    }

    public void redirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        URI requestUri = HttpServletRequests.getUri(request);
        this.addSessionAttributes(request, requestUri.toASCIIString());
        response.sendRedirect(this.getRedirectUrl(requestUri));
    }

    private void addSessionAttributes(HttpServletRequest request, String uriString) {
        request.getSession().setAttribute(JIRA_SERAPH_SECURITY_ORIGINAL_URL, (Object)uriString);
        request.getSession().setAttribute(CONF_SERAPH_SECURITY_ORIGINAL_URL, (Object)uriString);
    }
}

