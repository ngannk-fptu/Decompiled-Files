/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.sal.api.auth.AuthenticationController
 *  com.atlassian.sal.api.auth.AuthenticationListener
 *  com.atlassian.sal.api.auth.Authenticator$Result
 *  com.atlassian.sal.api.auth.Authenticator$Result$Failure
 *  com.atlassian.sal.api.auth.Authenticator$Result$Success
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.seraph.ioc.ApplicationServicesRegistry
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.collect.ImmutableMap
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.mywork.host.servlet;

import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.mywork.host.util.HostUtils;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.sal.api.auth.AuthenticationController;
import com.atlassian.sal.api.auth.AuthenticationListener;
import com.atlassian.sal.api.auth.Authenticator;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.seraph.ioc.ApplicationServicesRegistry;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.security.Principal;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginMiniviewServlet
extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(LoginMiniviewServlet.class);
    private static final String LOGIN_SERVLET = "/plugins/servlet/login-miniview";
    private static final String MINIVIEW_SERVLET = "/plugins/servlet/notifications-miniview";
    private final TemplateRenderer templateRenderer;
    private final UserManager userManager;
    private final AuthenticationController authenticationController;
    private final AuthenticationListener authenticationListener;
    private final InternalHostApplication internalHostApplication;
    private final I18nResolver i18nResolver;
    private static final Message AUTH_SUCCESS_MESSAGE = new Message(){

        public Serializable[] getArguments() {
            return null;
        }

        public String getKey() {
            return "com.atlassian.mywork.login.success";
        }
    };
    private static final Message AUTH_FAILURE_MESSAGE = new Message(){

        public Serializable[] getArguments() {
            return null;
        }

        public String getKey() {
            return "com.atlassian.mywork.login.failed";
        }
    };

    public LoginMiniviewServlet(TemplateRenderer templateRenderer, UserManager userManager, AuthenticationController authenticationController, AuthenticationListener authenticationListener, InternalHostApplication internalHostApplication, I18nResolver i18nResolver) {
        this.templateRenderer = templateRenderer;
        this.userManager = userManager;
        this.authenticationController = authenticationController;
        this.authenticationListener = authenticationListener;
        this.internalHostApplication = internalHostApplication;
        this.i18nResolver = i18nResolver;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = this.userManager.getRemoteUsername(req);
        if (username == null) {
            this.render(req, resp, (Map<String, Object>)ImmutableMap.of());
        } else {
            resp.sendRedirect(req.getContextPath() + this.getSuccessPath(req));
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean authSuccess = this.authenticate(req, resp);
        if (authSuccess) {
            resp.sendRedirect(req.getContextPath() + this.getSuccessPath(req));
        } else {
            this.render(req, resp, (Map<String, Object>)ImmutableMap.of((Object)"error", (Object)this.i18nResolver.getText("com.atlassian.mywork.login.failed.long")));
        }
    }

    public static String getLoginPath(String requestPath) {
        if (requestPath == null) {
            return LOGIN_SERVLET;
        }
        return "/plugins/servlet/login-miniview?os_destination=" + HostUtils.urlEncode(requestPath);
    }

    private String getSuccessPath(HttpServletRequest req) throws ServletException {
        String destination = req.getParameter("os_destination");
        if (destination == null) {
            return MINIVIEW_SERVLET;
        }
        if (destination.startsWith("/")) {
            return destination;
        }
        throw new ServletException("Invalid os_destination: " + destination);
    }

    private void render(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> extra) throws ServletException, IOException {
        ImmutableMap context = ImmutableMap.builder().putAll(extra).put((Object)"resp", (Object)resp).put((Object)"urlMode", (Object)UrlMode.RELATIVE).put((Object)"instanceName", (Object)this.internalHostApplication.getName()).put((Object)"baseHref", (Object)this.internalHostApplication.getBaseUrl().toString()).put((Object)"os_destination", (Object)this.getSuccessPath(req)).put((Object)"i18n", (Object)this.i18nResolver).build();
        resp.setContentType("text/html; charset=UTF-8");
        this.templateRenderer.render("templates/login.vm", (Map)context, (Writer)resp.getWriter());
    }

    private boolean authenticate(HttpServletRequest req, HttpServletResponse resp) {
        String username = req.getParameter("os_username");
        String password = req.getParameter("os_password");
        boolean authSuccess = false;
        if (StringUtils.isBlank((CharSequence)username) && StringUtils.isBlank((CharSequence)password)) {
            log.debug("Username or password is blank");
        } else {
            Principal principal = this.userManager.resolve(username);
            if (principal == null) {
                log.debug("User does not exist: {}", (Object)username);
            } else if (!this.authenticationController.canLogin(principal, req)) {
                log.debug("User is not allowed to log in: {}", (Object)username);
            } else if (this.userManager.authenticate(username, password)) {
                log.debug("User authenticated successfully: {}", (Object)username);
                this.authenticationListener.authenticationSuccess((Authenticator.Result)new Authenticator.Result.Success(AUTH_SUCCESS_MESSAGE, principal), req, resp);
                ApplicationServicesRegistry.getRememberMeService().addRememberMeCookie(req, resp, username);
                authSuccess = true;
            } else {
                log.debug("User attempted to authenticate with incorrect credentials: {}", (Object)username);
                this.authenticationListener.authenticationFailure((Authenticator.Result)new Authenticator.Result.Failure(AUTH_FAILURE_MESSAGE), req, resp);
            }
        }
        return authSuccess;
    }
}

