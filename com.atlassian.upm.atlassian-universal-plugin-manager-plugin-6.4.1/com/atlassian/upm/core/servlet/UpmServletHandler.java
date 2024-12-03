/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.user.UserRole
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  javax.ws.rs.core.UriBuilder
 */
package com.atlassian.upm.core.servlet;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserRole;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.UriBuilder;

public abstract class UpmServletHandler {
    public static final String FRAGMENT_NAME = "fragment";
    public static final String JIRA_SERAPH_SECURITY_ORIGINAL_URL = "os_security_originalurl";
    public static final String CONF_SERAPH_SECURITY_ORIGINAL_URL = "seraph_originalurl";
    private final TemplateRenderer renderer;
    private final PermissionEnforcer permissionEnforcer;
    private final LoginUriProvider loginUriProvider;
    private final WebSudoManager webSudoManager;

    protected UpmServletHandler(TemplateRenderer renderer, PermissionEnforcer permissionEnforcer, LoginUriProvider loginUriProvider, WebSudoManager webSudoManager) {
        this.renderer = Objects.requireNonNull(renderer, "renderer");
        this.webSudoManager = Objects.requireNonNull(webSudoManager, "webSudoManager");
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.loginUriProvider = Objects.requireNonNull(loginUriProvider, "loginUriProvider");
    }

    public abstract Map<String, Object> getContext(HttpServletRequest var1);

    public void handle(HttpServletRequest request, HttpServletResponse response, String template, boolean requiresWebsudo) throws IOException, ServletException {
        this.handle(request, response, template, requiresWebsudo, (Map<String, Object>)ImmutableMap.of());
    }

    public void handle(HttpServletRequest request, HttpServletResponse response, String template, boolean requiresWebsudo, Map<String, Object> baseParams) throws IOException, ServletException {
        try {
            if (this.authenticate(request, response, requiresWebsudo)) {
                return;
            }
            if (request.getParameter(FRAGMENT_NAME) != null) {
                this.redirectToFragment(request, response);
                return;
            }
            this.removeSessionAttributes(request.getSession());
            response.setContentType("text/html;charset=utf-8");
            ImmutableMap.Builder contextBuilder = ImmutableMap.builder();
            contextBuilder.putAll(baseParams);
            contextBuilder.putAll(this.getContext(request));
            this.renderer.render(template, (Map)contextBuilder.build(), (Writer)response.getWriter());
        }
        catch (WebSudoSessionException wse) {
            this.webSudoManager.enforceWebSudoProtection(request, response);
        }
    }

    public boolean authenticate(HttpServletRequest request, HttpServletResponse response, boolean requiresWebsudo) throws IOException {
        if (requiresWebsudo) {
            if (!this.webSudoManager.canExecuteRequest(request)) {
                this.webSudoManager.enforceWebSudoProtection(request, response);
                return true;
            }
            this.webSudoManager.willExecuteWebSudoRequest(request);
        }
        if (!this.permissionEnforcer.isLoggedIn() || requiresWebsudo && !this.permissionEnforcer.isAdmin()) {
            this.redirectToLogin(request, response, requiresWebsudo ? PermissionLevel.ADMIN : PermissionLevel.ANY);
            return true;
        }
        return false;
    }

    public void redirectToLogin(HttpServletRequest request, HttpServletResponse response, PermissionLevel level) throws IOException {
        URI uri = this.getUri(request);
        this.addSessionAttributes(request, uri.toASCIIString());
        response.sendRedirect(this.getLoginUri(uri, level).toASCIIString());
    }

    private URI getLoginUri(URI uri, PermissionLevel level) {
        try {
            UserRole role;
            this.getClass().getClassLoader().loadClass("com.atlassian.sal.api.user.UserRole");
            switch (level) {
                case ADMIN: {
                    role = UserRole.ADMIN;
                    break;
                }
                default: {
                    role = UserRole.USER;
                }
            }
            return this.loginUriProvider.getLoginUriForRole(uri, role);
        }
        catch (ClassNotFoundException e) {
            return this.loginUriProvider.getLoginUri(uri);
        }
    }

    private void redirectToFragment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UriBuilder builder = UriBuilder.fromUri((String)request.getRequestURL().toString());
        builder.fragment(request.getParameter(FRAGMENT_NAME));
        for (Object k : request.getParameterMap().keySet()) {
            String key = k.toString();
            if (FRAGMENT_NAME.equals(key)) continue;
            builder.queryParam(key, new Object[]{request.getParameter(key)});
        }
        response.sendRedirect(builder.build(new Object[0]).toASCIIString());
    }

    public URI getUri(HttpServletRequest request) {
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

    private void removeSessionAttributes(HttpSession session) {
        session.removeAttribute(JIRA_SERAPH_SECURITY_ORIGINAL_URL);
        session.removeAttribute(CONF_SERAPH_SECURITY_ORIGINAL_URL);
    }

    public static enum PermissionLevel {
        ANY,
        ADMIN;

    }
}

