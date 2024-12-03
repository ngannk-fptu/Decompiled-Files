/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.oauth.serviceprovider.internal.servlet.user;

import com.atlassian.oauth.serviceprovider.internal.servlet.authorize.LoginRedirector;
import com.atlassian.oauth.serviceprovider.internal.servlet.user.AccessTokensRevoke;
import com.atlassian.oauth.serviceprovider.internal.servlet.user.AccessTokensServletContext;
import com.atlassian.oauth.serviceprovider.internal.servlet.user.AccessTokensServletValidation;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AccessTokensServlet
extends HttpServlet {
    public static final String PATH = "/plugins/servlet/oauth/users/access-tokens";
    private static final String TEMPLATE = "templates/user/access-tokens.vm";
    protected final LoginRedirector loginRedirector;
    protected final AccessTokensServletValidation accessTokensServletValidation;
    private final AccessTokensServletContext accessTokensServletContext;
    private final AccessTokensRevoke accessTokensRevoke;
    private final TemplateRenderer templateRenderer;

    public AccessTokensServlet(AccessTokensServletContext accessTokensServletContext, AccessTokensRevoke accessTokensRevoke, AccessTokensServletValidation accessTokensServletValidation, TemplateRenderer templateRenderer, LoginRedirector loginRedirector) {
        this.accessTokensServletContext = Objects.requireNonNull(accessTokensServletContext, "accessTokenServletContext");
        this.accessTokensRevoke = Objects.requireNonNull(accessTokensRevoke, "accessTokenRevoke");
        this.accessTokensServletValidation = Objects.requireNonNull(accessTokensServletValidation, "accessTokenServletValidation");
        this.templateRenderer = Objects.requireNonNull(templateRenderer, "templateRenderer");
        this.loginRedirector = Objects.requireNonNull(loginRedirector, "loginRedirector");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Optional<String> username = this.accessTokensServletValidation.validate(request);
        if (username.isPresent()) {
            this.render(response, this.getContext(username.get()));
        } else {
            this.loginRedirector.redirectToLogin(request, response);
        }
    }

    void render(HttpServletResponse response, Map<String, Object> context) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        this.templateRenderer.render(this.getTemplate(), context, (Writer)response.getWriter());
    }

    protected Map<String, Object> getContext(String username) {
        return this.accessTokensServletContext.getContext(username);
    }

    @Deprecated
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Optional<Integer> errorCode = this.accessTokensRevoke.revoke(request);
        if (errorCode.isPresent()) {
            response.sendError(errorCode.get().intValue());
        }
    }

    protected String getTemplate() {
        return TEMPLATE;
    }
}

