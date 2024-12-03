/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.google.common.collect.ImmutableMap
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.oauth2.provider.core.web.servlet;

import com.atlassian.oauth2.common.validator.HttpsValidator;
import com.atlassian.oauth2.common.web.loopsprevention.RedirectsLoopPreventer;
import com.atlassian.oauth2.provider.core.web.servlet.AuthorizationErrorServletConfiguration;
import com.atlassian.oauth2.provider.core.web.servlet.AuthorizationServlet;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthorizationErrorServlet
extends AuthorizationServlet {
    private final AuthorizationErrorServletConfiguration servletConfiguration;

    public AuthorizationErrorServlet(LoginUriProvider loginUriProvider, UserManager userManager, SoyTemplateRenderer templateRenderer, RedirectsLoopPreventer loopPreventer, I18nResolver i18nResolver, HttpsValidator httpsValidator, AuthorizationErrorServletConfiguration servletConfiguration) {
        super(loginUriProvider, userManager, templateRenderer, loopPreventer, i18nResolver, httpsValidator);
        this.servletConfiguration = servletConfiguration;
    }

    @Override
    void render(HttpServletRequest request, HttpServletResponse response, SoyTemplateRenderer soyTemplateRenderer) throws IOException {
        ImmutableMap parameters = ImmutableMap.of((Object)AuthorizationErrorServletConfiguration.QueryParameter.ERROR_NAME.name, (Object)request.getParameter("errorName"), (Object)AuthorizationErrorServletConfiguration.QueryParameter.ERROR_DESCRIPTION.name, (Object)request.getParameter("errorDescription"));
        soyTemplateRenderer.render((Appendable)response.getWriter(), this.servletConfiguration.moduleKey(), this.servletConfiguration.templateName(), (Map)parameters);
    }
}

