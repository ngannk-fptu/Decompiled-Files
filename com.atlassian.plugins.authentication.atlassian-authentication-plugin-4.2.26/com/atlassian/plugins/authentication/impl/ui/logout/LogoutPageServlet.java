/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.inject.Inject
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.ws.rs.core.UriBuilder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.ui.logout;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogoutPageServlet
extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(LogoutPageServlet.class);
    public static final String URL = "/plugins/servlet/sso-logout";
    static final String TEMPLATE_NAME = "AuthenticationPlugin.Logout.display";
    private final SoyTemplateRenderer renderer;
    private final ApplicationProperties applicationProperties;
    private final LoginUriProvider loginUriProvider;

    @Inject
    public LogoutPageServlet(@ComponentImport SoyTemplateRenderer renderer, @ComponentImport ApplicationProperties applicationProperties, @ComponentImport LoginUriProvider loginUriProvider) {
        this.renderer = renderer;
        this.applicationProperties = applicationProperties;
        this.loginUriProvider = loginUriProvider;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        URI loginUri;
        if (Boolean.parseBoolean(request.getParameter("sd-logout"))) {
            log.trace("Detected JSM logout, setting login URI to customer portal login URL");
            loginUri = UriBuilder.fromUri((String)this.applicationProperties.getBaseUrl(UrlMode.RELATIVE)).path("/servicedesk/customer/user/login").build(new Object[0]);
        } else {
            log.trace("JSM logout parameter not present, using regular product login URL");
            loginUri = this.loginUriProvider.getLoginUri(UriBuilder.fromUri((String)"/").build(new Object[0]));
        }
        ImmutableMap cfg = new ImmutableMap.Builder().put((Object)"loginUrl", (Object)loginUri.toString()).put((Object)"productName", (Object)this.applicationProperties.getDisplayName()).build();
        response.setContentType("text/html");
        this.renderer.render((Appendable)response.getWriter(), "com.atlassian.plugins.authentication.atlassian-authentication-plugin:templates", TEMPLATE_NAME, (Map)cfg);
    }
}

