/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.auth.ImpersonatingAuthenticationProvider
 *  com.atlassian.applinks.host.spi.HostApplication
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugins.whitelist.OutboundWhitelist
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.collect.ImmutableMap
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.mywork.client.servlet;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.auth.ImpersonatingAuthenticationProvider;
import com.atlassian.applinks.host.spi.HostApplication;
import com.atlassian.mywork.client.ClientUtil;
import com.atlassian.mywork.service.HostService;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugins.whitelist.OutboundWhitelist;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthenticationServlet
extends HttpServlet {
    private static final long serialVersionUID = 0L;
    private final transient UserManager userManager;
    private final transient LoginUriProvider loginUriProvider;
    private final transient HostApplication hostApplication;
    private final transient TemplateRenderer templateRenderer;
    private final transient HostService hostService;
    private final transient OutboundWhitelist outboundWhitelist;

    public AuthenticationServlet(UserManager userManager, LoginUriProvider loginUriProvider, HostApplication hostApplication, TemplateRenderer templateRenderer, HostService hostService, OutboundWhitelist outboundWhitelist) {
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.hostApplication = hostApplication;
        this.templateRenderer = templateRenderer;
        this.hostService = hostService;
        this.outboundWhitelist = outboundWhitelist;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = this.userManager.getRemoteUsername(req);
        if (username == null) {
            String queryString = req.getQueryString() != null ? "?" + req.getQueryString() : "";
            URI loginUri = this.loginUriProvider.getLoginUri(URI.create(req.getRequestURL().append(queryString).toString()));
            resp.sendRedirect(loginUri.toASCIIString());
            return;
        }
        Iterator iterator = this.hostService.getActiveHost().iterator();
        if (iterator.hasNext()) {
            ApplicationLink applicationLink = (ApplicationLink)iterator.next();
            String origin = req.getParameter("origin");
            if (origin == null || applicationLink.getId().get().equals(origin)) {
                this.doGet(req, resp, applicationLink);
                return;
            }
            throw new ServletException("Request did not originate from the configured host '" + applicationLink.getDisplayUrl() + "'");
        }
        throw new ServletException("Host is not configured");
    }

    private void doGet(HttpServletRequest req, HttpServletResponse resp, ApplicationLink applicationLink) throws IOException, ServletException {
        String callBackUrl = req.getParameter("callback");
        if (ClientUtil.credentialsRequired(applicationLink, this.hostApplication.getId())) {
            Object authServletUrl = callBackUrl != null ? AuthenticationServlet.appendRelativePath(applicationLink.getDisplayUrl(), callBackUrl) : this.hostApplication.getBaseUrl() + "/plugins/servlet/myworkauth";
            ApplicationLinkRequestFactory requestFactory = applicationLink.createAuthenticatedRequestFactory(ImpersonatingAuthenticationProvider.class);
            resp.sendRedirect(requestFactory.getAuthorisationURI(URI.create((String)authServletUrl)).toString());
        } else if (callBackUrl != null) {
            String redirectUrl = AuthenticationServlet.appendRelativePath(applicationLink.getDisplayUrl(), callBackUrl);
            if (this.outboundWhitelist.isAllowed(URI.create(redirectUrl))) {
                resp.sendRedirect(redirectUrl);
            } else {
                resp.sendError(403, "Request should be originate from the configured host through Application Links or should be whitelisted");
            }
        } else {
            resp.setContentType("text/html; charset=UTF-8");
            ImmutableMap context = ImmutableMap.of((Object)"writer", (Object)resp.getWriter(), (Object)"urlMode", (Object)UrlMode.RELATIVE);
            this.templateRenderer.render("templates/authentication-completed.vm", (Map)context, (Writer)resp.getWriter());
        }
    }

    protected static String appendRelativePath(URI uri, String path) {
        URI extra = URI.create(path);
        String result = uri.resolve(extra.getPath() + (String)(extra.getRawQuery() != null ? "?" + extra.getRawQuery() : "")).toASCIIString();
        int fragmentIndex = path.indexOf(35);
        return result + (fragmentIndex == -1 ? "" : path.substring(fragmentIndex));
    }
}

