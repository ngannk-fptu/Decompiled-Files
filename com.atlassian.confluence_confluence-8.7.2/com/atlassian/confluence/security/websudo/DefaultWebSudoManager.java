/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoNotRequired
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.google.common.base.Preconditions
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 */
package com.atlassian.confluence.security.websudo;

import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.security.websudo.AuthenticateAction;
import com.atlassian.confluence.security.websudo.WebSudoManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatorOverwrite;
import com.atlassian.sal.api.websudo.WebSudoNotRequired;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.google.common.base.Preconditions;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class DefaultWebSudoManager
implements WebSudoManager {
    private final SettingsManager settingsManager;
    private static final String URL_AUTHENTICATE = "/authenticate.action";
    private static final String URL_ADMIN = "/admin/";
    private static final String URL_SETUP = "/setup/";
    private static final String SESSION_TIMESTAMP = "confluence.websudo.timestamp";
    private static final String REQUEST_ATTRIBUTE = "confluence.websudo.request";
    private static final String REQUIRE_AUTHENTICATION = "Require-Authentication";
    private static final String HAS_AUTHENTICATION = "Has-Authentication";

    public DefaultWebSudoManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @Override
    public boolean isEnabled() {
        return !ConfluenceSystemProperties.isDevMode() && this.settingsManager.getGlobalSettings().getWebSudoEnabled() && !AuthenticatorOverwrite.isPasswordConfirmationDisabled();
    }

    public static boolean isElevatedDefaultPath(String servletPath) {
        return servletPath.startsWith(URL_ADMIN) || servletPath.startsWith(URL_SETUP);
    }

    @Override
    public boolean matches(String requestServletPath, Class<?> actionClass, Method method) {
        if (requestServletPath.startsWith(URL_AUTHENTICATE) && actionClass.isAssignableFrom(AuthenticateAction.class)) {
            return false;
        }
        if (DefaultWebSudoManager.isElevatedDefaultPath(requestServletPath)) {
            return method.getAnnotation(WebSudoNotRequired.class) == null && actionClass.getAnnotation(WebSudoNotRequired.class) == null && actionClass.getPackage().getAnnotation(WebSudoNotRequired.class) == null;
        }
        return method.getAnnotation(WebSudoRequired.class) != null || actionClass.getAnnotation(WebSudoRequired.class) != null || actionClass.getPackage().getAnnotation(WebSudoRequired.class) != null;
    }

    @Override
    public boolean hasValidSession(HttpSession session) {
        if (null == session) {
            return false;
        }
        long timeout = this.settingsManager.getGlobalSettings().getWebSudoTimeout();
        Long timestamp = (Long)session.getAttribute(SESSION_TIMESTAMP);
        long timeoutMillis = timeout * 60L * 1000L;
        return timestamp != null && timestamp >= this.currentTimeMillis() - timeoutMillis;
    }

    @Override
    public void startSession(HttpServletRequest request, HttpServletResponse response) {
        Preconditions.checkNotNull((Object)request);
        Preconditions.checkNotNull((Object)response);
        request.getSession(true).setAttribute(SESSION_TIMESTAMP, (Object)this.currentTimeMillis());
        this.markWebSudoRequest(request);
        response.setHeader("X-Atlassian-WebSudo", HAS_AUTHENTICATION);
    }

    @Override
    public void markWebSudoRequest(HttpServletRequest request) {
        if (null == request) {
            return;
        }
        request.setAttribute(REQUEST_ATTRIBUTE, (Object)Boolean.TRUE);
    }

    @Override
    public boolean isWebSudoRequest(HttpServletRequest request) {
        return null != request && Boolean.TRUE.equals(request.getAttribute(REQUEST_ATTRIBUTE));
    }

    @Override
    public void invalidateSession(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session;
        HttpSession httpSession = session = null != request ? request.getSession(false) : null;
        if (null != session) {
            session.removeAttribute(SESSION_TIMESTAMP);
        }
        response.setHeader("X-Atlassian-WebSudo", REQUIRE_AUTHENTICATION);
    }

    long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    @Override
    public URI buildAuthenticationRedirectUri(HttpServletRequest request) {
        String encoding = this.settingsManager.getGlobalSettings().getDefaultEncoding();
        String queryString = request.getQueryString();
        String pathInfo = request.getPathInfo();
        String destination = request.getServletPath() + (null != pathInfo ? pathInfo : "") + (String)(null != queryString ? "?" + queryString : "");
        try {
            return URI.create(request.getContextPath() + "/authenticate.action?destination=" + URLEncoder.encode(destination, encoding));
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}

