/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.ui.login;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.data.LoginGatewayDataProvider;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import java.io.IOException;
import java.util.Collections;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginGatewayServlet
extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(LoginGatewayServlet.class);
    public static final String URL = "/plugins/servlet/login";
    private static final String TEMPLATE_NAME = "AuthenticationPlugin.LoginPage.display";
    private static final String ATTRIBUTE_WHEN_FORWARDED_TO_SERVLET = "javax.servlet.forward.request_uri";
    private final ApplicationProperties applicationProperties;
    private final PageBuilderService pageBuilderService;
    private final SoyTemplateRenderer renderer;
    private final LoginGatewayDataProvider loginGatewayDataProvider;

    public LoginGatewayServlet(@ComponentImport SoyTemplateRenderer renderer, LoginGatewayDataProvider loginGatewayDataProvider, @ComponentImport ApplicationProperties applicationProperties, @ComponentImport PageBuilderService pageBuilderService) {
        this.renderer = renderer;
        this.loginGatewayDataProvider = loginGatewayDataProvider;
        this.applicationProperties = applicationProperties;
        this.pageBuilderService = pageBuilderService;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        String refererUrl = (String)request.getAttribute(ATTRIBUTE_WHEN_FORWARDED_TO_SERVLET);
        if (refererUrl == null) {
            log.trace("Direct request made to login gateway, redirecting to application base URL.");
            response.sendRedirect(this.applicationProperties.getBaseUrl(UrlMode.RELATIVE));
            return;
        }
        Object destinationAttribute = request.getAttribute("atlassian.plugin.auth.destination");
        this.pageBuilderService.assembler().data().requireData("com.atlassian.plugins.authentication.atlassian-authentication-plugin:login-gateway-data", this.loginGatewayDataProvider.get(refererUrl, destinationAttribute));
        this.renderer.render((Appendable)response.getWriter(), "com.atlassian.plugins.authentication.atlassian-authentication-plugin:templates", TEMPLATE_NAME, Collections.emptyMap());
    }
}

