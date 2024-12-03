/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.core.web.servlet;

import com.atlassian.oauth2.common.web.loopsprevention.RedirectsLoopPreventer;
import com.atlassian.oauth2.provider.core.authentication.LogoutException;
import com.atlassian.oauth2.provider.core.authentication.LogoutHandler;
import com.atlassian.oauth2.provider.core.xsrf.XsrfTokenValidationException;
import com.atlassian.oauth2.provider.core.xsrf.XsrfValidator;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import java.io.IOException;
import java.net.URI;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorizationSwitchAccountServlet
extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationSwitchAccountServlet.class);
    private final LoginUriProvider loginUriProvider;
    private final RedirectsLoopPreventer loopPreventer;
    private final I18nResolver i18nResolver;
    private final XsrfValidator xsrfValidator;
    private final LogoutHandler logoutHandler;

    public AuthorizationSwitchAccountServlet(LoginUriProvider loginUriProvider, RedirectsLoopPreventer loopPreventer, I18nResolver i18nResolver, XsrfValidator xsrfValidator, LogoutHandler logoutHandler) {
        this.loginUriProvider = loginUriProvider;
        this.loopPreventer = loopPreventer;
        this.i18nResolver = i18nResolver;
        this.xsrfValidator = xsrfValidator;
        this.logoutHandler = logoutHandler;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            this.xsrfValidator.validateXsrf(request);
            String redirectPath = request.getParameter("redirectPath");
            if (redirectPath == null) {
                logger.debug("Required parameter 'redirectPath' was missing");
                response.sendError(400, this.i18nResolver.getText("oauth2.servlet.error.missing.redirect.parameter"));
            } else if (!this.isAuthorizationPath(redirectPath)) {
                logger.debug("Required parameter 'redirectPath' was incorrect");
                response.sendError(400, this.i18nResolver.getText("oauth2.servlet.error.invalid.redirect.parameter"));
            } else {
                this.logoutAndRedirectToLogin(redirectPath, request, response);
            }
        }
        catch (LogoutException e) {
            logger.warn("Failure during logout attempt", (Throwable)e);
            throw new ServletException((Throwable)e);
        }
        catch (XsrfTokenValidationException xsrfTokenValidationException) {
            logger.debug("Attempting to access 'not you' servlet outside of consent screen", (Throwable)xsrfTokenValidationException);
            throw new ServletException((Throwable)xsrfTokenValidationException);
        }
    }

    private boolean isAuthorizationPath(String redirectPath) {
        return redirectPath.startsWith("/rest/oauth2/latest/authorize") && this.containsRequiredParameters(redirectPath);
    }

    private boolean containsRequiredParameters(String redirectPath) {
        return redirectPath.contains("client_id=") && redirectPath.contains("response_type=code") && redirectPath.contains("redirect_uri=");
    }

    private void logoutAndRedirectToLogin(String redirectPath, HttpServletRequest request, HttpServletResponse response) throws IOException, LogoutException {
        this.logoutHandler.logout(request, response);
        this.sendRedirectToLogin(redirectPath, request, response);
    }

    private void sendRedirectToLogin(String redirectPath, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String loginUriWithRedirect = this.loginUriProvider.getLoginUri(URI.create(redirectPath)).toString();
        this.loopPreventer.preventRedirectsLoop(request, loginUriWithRedirect);
        response.sendRedirect(loginUriWithRedirect);
    }
}

