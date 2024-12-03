/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.applinks.api.auth.ImpersonatingAuthenticationProvider
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.user.UserManager
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.mywork.host.servlet;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.api.auth.ImpersonatingAuthenticationProvider;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.mywork.host.util.HostUtils;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.user.UserManager;
import java.io.IOException;
import java.net.URI;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;

public class AuthenticationRedirector
extends HttpServlet {
    private final UserManager userManager;
    private final LoginUriProvider loginUriProvider;
    private final ApplicationLinkService applicationLinkService;
    private final InternalHostApplication internalHostApplication;

    public AuthenticationRedirector(UserManager userManager, LoginUriProvider loginUriProvider, ApplicationLinkService applicationLinkService, @Qualifier(value="internalHostApplication") InternalHostApplication internalHostApplication) {
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.applicationLinkService = applicationLinkService;
        this.internalHostApplication = internalHostApplication;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = this.userManager.getRemoteUsername(req);
        if (username == null) {
            String queryString = req.getQueryString() != null ? "?" + req.getQueryString() : "";
            URI loginUri = this.loginUriProvider.getLoginUri(URI.create(req.getRequestURL().append(queryString).toString()));
            resp.sendRedirect(loginUri.toASCIIString());
            return;
        }
        String target = req.getParameter("target");
        ApplicationLink appLink = this.getApplicationLink(target);
        String callbackUrl = req.getParameter("callback");
        try {
            appLink.createAuthenticatedRequestFactory(ImpersonatingAuthenticationProvider.class).createRequest(Request.MethodType.GET, "404");
            resp.sendRedirect(this.createClientAuthUrl(appLink, callbackUrl));
        }
        catch (CredentialsRequiredException e) {
            resp.sendRedirect(AuthenticationRedirector.appendRelativePath(this.internalHostApplication.getBaseUrl(), callbackUrl));
        }
    }

    private String createClientAuthUrl(ApplicationLink appLink, String callbackUrl) {
        return appLink.getDisplayUrl().toASCIIString() + "/plugins/servlet/myworkauth?origin=" + this.internalHostApplication.getId().get() + "&callback=" + HostUtils.urlEncode(callbackUrl);
    }

    private ApplicationLink getApplicationLink(String origin) {
        try {
            return this.applicationLinkService.getApplicationLink(new ApplicationId(origin));
        }
        catch (TypeNotInstalledException e) {
            throw new RuntimeException(e);
        }
    }

    protected static String appendRelativePath(URI uri, String path) {
        URI extra = URI.create(path);
        String result = uri.resolve(extra.getPath() + (String)(extra.getRawQuery() != null ? "?" + extra.getRawQuery() : "")).toASCIIString();
        int fragmentIndex = path.indexOf(35);
        return result + (fragmentIndex == -1 ? "" : path.substring(fragmentIndex));
    }
}

