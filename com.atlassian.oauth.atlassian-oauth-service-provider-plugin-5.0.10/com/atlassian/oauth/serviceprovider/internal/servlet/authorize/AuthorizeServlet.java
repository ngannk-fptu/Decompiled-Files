/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken
 *  com.atlassian.oauth.shared.servlet.ResponseHeaderUtil
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.xsrf.XsrfTokenAccessor
 *  com.atlassian.sal.api.xsrf.XsrfTokenValidator
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  net.oauth.OAuthException
 *  net.oauth.OAuthProblemException
 *  net.oauth.server.OAuthServlet
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.oauth.serviceprovider.internal.servlet.authorize;

import com.atlassian.oauth.serviceprovider.ServiceProviderToken;
import com.atlassian.oauth.serviceprovider.internal.servlet.OAuthProblemUtils;
import com.atlassian.oauth.serviceprovider.internal.servlet.TokenLoader;
import com.atlassian.oauth.serviceprovider.internal.servlet.TransactionalServlet;
import com.atlassian.oauth.serviceprovider.internal.servlet.authorize.AuthorizationRequestProcessor;
import com.atlassian.oauth.serviceprovider.internal.servlet.authorize.LoginRedirector;
import com.atlassian.oauth.shared.servlet.ResponseHeaderUtil;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import com.atlassian.sal.api.xsrf.XsrfTokenValidator;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.oauth.OAuthException;
import net.oauth.OAuthProblemException;
import net.oauth.server.OAuthServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

public final class AuthorizeServlet
extends TransactionalServlet {
    static final int VERIFIER_LENGTH = 6;
    private static final String AUTH_ERROR_TEMPLATE = "templates/auth/authorize-error.vm";
    private final Logger log = LoggerFactory.getLogger(((Object)((Object)this)).getClass());
    private final AuthorizationRequestProcessor get;
    private final AuthorizationRequestProcessor post;
    private final TokenLoader loader;
    private final LoginRedirector loginRedirector;
    private final ApplicationProperties applicationProperties;
    private final TemplateRenderer templateRenderer;
    private final I18nResolver i18nResolver;
    private final XsrfTokenAccessor xsrfTokenAccessor;
    private final XsrfTokenValidator xsrfTokenValidator;

    public AuthorizeServlet(@Qualifier(value="getAuthorizationProcessor") AuthorizationRequestProcessor get, @Qualifier(value="postAuthorizationProcessor") AuthorizationRequestProcessor post, TokenLoader loader, LoginRedirector loginRedirector, ApplicationProperties applicationProperties, TransactionTemplate transactionTemplate, TemplateRenderer templateRenderer, I18nResolver i18nResolver, XsrfTokenAccessor xsrfTokenAccessor, XsrfTokenValidator xsrfTokenValidator) {
        super(transactionTemplate);
        this.get = Objects.requireNonNull(get, "get");
        this.post = Objects.requireNonNull(post, "post");
        this.loader = Objects.requireNonNull(loader, "loader");
        this.loginRedirector = Objects.requireNonNull(loginRedirector, "loginRedirector");
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        this.templateRenderer = Objects.requireNonNull(templateRenderer, "templateRenderer");
        this.i18nResolver = Objects.requireNonNull(i18nResolver, "i18nResolver");
        this.xsrfTokenAccessor = Objects.requireNonNull(xsrfTokenAccessor, "xsrfTokenAccessor");
        this.xsrfTokenValidator = Objects.requireNonNull(xsrfTokenValidator, "xsrfTokenValidator");
    }

    @Override
    public void doGetInTransaction(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        this.process(this.get, request, response);
        ResponseHeaderUtil.preventCrossFrameClickJacking((HttpServletResponse)response);
    }

    @Override
    public void doPostInTransaction(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (this.xsrfTokenValidator.validateFormEncodedToken(request)) {
            this.process(this.post, request, response);
        } else {
            response.setStatus(403);
            this.renderError(response, "Xsrf token validation failed");
        }
        ResponseHeaderUtil.preventCrossFrameClickJacking((HttpServletResponse)response);
    }

    private void process(AuthorizationRequestProcessor processor, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ServiceProviderToken token;
        try {
            token = this.loader.getTokenForAuthorization(request);
        }
        catch (OAuthException e) {
            if (e instanceof OAuthProblemException) {
                OAuthProblemUtils.logOAuthProblem(OAuthServlet.getMessage((HttpServletRequest)request, null), (OAuthProblemException)((Object)e), this.log);
            }
            OAuthServlet.handleException((HttpServletResponse)response, (Exception)((Object)e), (String)this.applicationProperties.getBaseUrl(), (boolean)false);
            this.renderError(response, this.getMessage(e));
            return;
        }
        if (!this.loginRedirector.isLoggedIn(request)) {
            this.loginRedirector.redirectToLogin(request, response);
        } else {
            processor.process(request, response, token);
        }
    }

    private void renderError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        HashMap<String, String> context = new HashMap<String, String>();
        context.put("message", message);
        context.put("applicationProperties", (String)this.applicationProperties);
        this.templateRenderer.render(AUTH_ERROR_TEMPLATE, Collections.unmodifiableMap(context), (Writer)response.getWriter());
    }

    private String getMessage(OAuthException e) {
        if (e instanceof OAuthProblemException) {
            OAuthProblemException problem = (OAuthProblemException)((Object)e);
            if ("token_rejected".equals(problem.getProblem())) {
                return this.i18nResolver.getText("com.atlassian.oauth.serviceprovider.authorize.error.token.rejected");
            }
            if ("token_used".equals(problem.getProblem())) {
                return this.i18nResolver.getText("com.atlassian.oauth.serviceprovider.authorize.error.token.used");
            }
            if ("token_expired".equals(problem.getProblem())) {
                return this.i18nResolver.getText("com.atlassian.oauth.serviceprovider.authorize.error.token.expired");
            }
        }
        return this.i18nResolver.getText("com.atlassian.oauth.serviceprovider.authorize.error.generic");
    }
}

