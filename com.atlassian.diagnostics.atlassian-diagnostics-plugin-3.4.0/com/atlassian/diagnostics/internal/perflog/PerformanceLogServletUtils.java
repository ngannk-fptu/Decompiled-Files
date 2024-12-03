/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.user.UserRole
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.diagnostics.internal.perflog;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserRole;
import java.io.IOException;
import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PerformanceLogServletUtils {
    private static final String CONF_SERAPH_SECURITY_ORIGINAL_URL = "seraph_originalurl";
    private static final String JIRA_SERAPH_SECURITY_ORIGINAL_URL = "os_security_originalurl";
    private final ApplicationProperties applicationProperties;
    private final LoginUriProvider loginUriProvider;

    public void redirectToAdminLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String platformId;
        URI uri = this.getUri(request);
        switch (platformId = this.applicationProperties.getPlatformId()) {
            case "jira": {
                request.getSession().setAttribute(JIRA_SERAPH_SECURITY_ORIGINAL_URL, (Object)uri.toASCIIString());
                break;
            }
            case "conf": {
                request.getSession().setAttribute(CONF_SERAPH_SECURITY_ORIGINAL_URL, (Object)uri.toASCIIString());
            }
        }
        response.sendRedirect(this.loginUriProvider.getLoginUriForRole(uri, UserRole.ADMIN).toASCIIString());
    }

    private URI getUri(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        if (request.getQueryString() != null) {
            requestURL.append("?").append(request.getQueryString());
        }
        return URI.create(requestURL.toString());
    }

    public PerformanceLogServletUtils(ApplicationProperties applicationProperties, LoginUriProvider loginUriProvider) {
        this.applicationProperties = applicationProperties;
        this.loginUriProvider = loginUriProvider;
    }
}

