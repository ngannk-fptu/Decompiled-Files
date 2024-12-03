/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.google.common.base.Strings
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.core.web.servlet;

import com.atlassian.oauth2.common.validator.HttpsValidator;
import com.atlassian.oauth2.common.web.loopsprevention.RedirectsLoopPreventer;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.google.common.base.Strings;
import java.io.IOException;
import java.net.URI;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AuthorizationServlet
extends HttpServlet {
    protected final UserManager userManager;
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationServlet.class);
    private final LoginUriProvider loginUriProvider;
    private final SoyTemplateRenderer templateRenderer;
    private final RedirectsLoopPreventer loopPreventer;
    private final HttpsValidator httpsValidator;
    protected final I18nResolver i18nResolver;

    AuthorizationServlet(LoginUriProvider loginUriProvider, UserManager userManager, SoyTemplateRenderer templateRenderer, RedirectsLoopPreventer loopPreventer, I18nResolver i18nResolver, HttpsValidator httpsValidator) {
        this.loginUriProvider = loginUriProvider;
        this.userManager = userManager;
        this.templateRenderer = templateRenderer;
        this.loopPreventer = loopPreventer;
        this.i18nResolver = i18nResolver;
        this.httpsValidator = httpsValidator;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!this.isSecureRequest(request)) {
            logger.debug("Https required but request was not https [" + request.getRequestURI() + "]");
            response.sendError(400, this.i18nResolver.getText("oauth2.rest.error.no.https.warning.message"));
        } else if (this.userManager.getRemoteUserKey() != null) {
            response.setContentType("text/html;charset=UTF-8");
            this.render(request, response, this.templateRenderer);
        } else {
            logger.debug("User not logged in. Redirecting to login page");
            this.sendRedirectToLogin(request, response);
        }
    }

    private boolean isSecureRequest(HttpServletRequest request) {
        return !this.httpsValidator.isBaseUrlHttpsRequired() || this.httpsValidator.isBaseUrlHttps() && this.isSecure(request);
    }

    private boolean isSecure(HttpServletRequest request) {
        return request.isSecure() || this.isForwardedProtocolHttps(request);
    }

    private boolean isForwardedProtocolHttps(HttpServletRequest request) {
        return this.isHttps(request.getHeader("X-Forwarded-Proto")) || this.isHttps(this.extractProtocolFromForwardedHeader(request.getHeader("Forwarded")));
    }

    private boolean isHttps(String protocol) {
        return StringUtils.isNotEmpty((CharSequence)protocol) && StringUtils.startsWithIgnoreCase((CharSequence)protocol, (CharSequence)"https");
    }

    private String extractProtocolFromForwardedHeader(String forwardedHeader) {
        int indexOfProto;
        if (StringUtils.isNotEmpty((CharSequence)forwardedHeader) && (indexOfProto = forwardedHeader.indexOf("proto=")) > 0) {
            int startOfProtoValue = indexOfProto + 6;
            int endOfProtoValue = startOfProtoValue + 5;
            return forwardedHeader.substring(startOfProtoValue, Math.min(endOfProtoValue, forwardedHeader.length()));
        }
        return "";
    }

    private void sendRedirectToLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String requestUri = req.getRequestURI();
        String contextPath = req.getContextPath();
        if (!Strings.isNullOrEmpty((String)contextPath)) {
            requestUri = requestUri.substring(contextPath.length());
        }
        String target = this.loginUriProvider.getLoginUri(URI.create(requestUri + "?" + req.getQueryString())).toString();
        this.loopPreventer.preventRedirectsLoop(req, target);
        resp.sendRedirect(target);
    }

    abstract void render(HttpServletRequest var1, HttpServletResponse var2, SoyTemplateRenderer var3) throws IOException;
}

