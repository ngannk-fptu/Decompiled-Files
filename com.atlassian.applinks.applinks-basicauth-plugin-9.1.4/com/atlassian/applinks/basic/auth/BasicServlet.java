/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.auth.types.BasicAuthenticationProvider
 *  com.atlassian.applinks.core.auth.AbstractSysadminOnlyAuthServlet
 *  com.atlassian.applinks.core.util.MessageFactory
 *  com.atlassian.applinks.core.util.RendererContextBuilder
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.internal.common.docs.DocumentationLinker
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.applinks.ui.AbstractApplinksServlet$BadRequestException
 *  com.atlassian.applinks.ui.auth.AdminUIAuthenticator
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  com.atlassian.sal.api.xsrf.XsrfTokenAccessor
 *  com.atlassian.sal.api.xsrf.XsrfTokenValidator
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.collect.ImmutableMap
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.applinks.basic.auth;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.auth.types.BasicAuthenticationProvider;
import com.atlassian.applinks.core.auth.AbstractSysadminOnlyAuthServlet;
import com.atlassian.applinks.core.util.MessageFactory;
import com.atlassian.applinks.core.util.RendererContextBuilder;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.docs.DocumentationLinker;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.applinks.ui.AbstractApplinksServlet;
import com.atlassian.applinks.ui.auth.AdminUIAuthenticator;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import com.atlassian.sal.api.xsrf.XsrfTokenValidator;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

public class BasicServlet
extends AbstractSysadminOnlyAuthServlet {
    private static final String TEMPLATE = "com/atlassian/applinks/basic/auth/config.vm";
    private final AuthenticationConfigurationManager authenticationConfigurationManager;
    private final WebSudoManager webSudoManager;
    public static final String WEB_RESOURCE_KEY = "com.atlassian.applinks.applinks-basicauth-plugin:";

    public BasicServlet(AdminUIAuthenticator adminUIAuthenticator, ApplicationLinkService applicationLinkService, AuthenticationConfigurationManager authenticationConfigurationManager, I18nResolver i18nResolver, TemplateRenderer templateRenderer, WebResourceManager webResourceManager, MessageFactory messageFactory, DocumentationLinker documentationLinker, LoginUriProvider loginUriProvider, InternalHostApplication internalHostApplication, WebSudoManager webSudoManager, XsrfTokenAccessor xsrfTokenAccessor, XsrfTokenValidator xsrfTokenValidator, UserManager userManager) {
        super(i18nResolver, messageFactory, templateRenderer, webResourceManager, applicationLinkService, adminUIAuthenticator, documentationLinker, loginUriProvider, internalHostApplication, xsrfTokenAccessor, xsrfTokenValidator, userManager);
        this.authenticationConfigurationManager = authenticationConfigurationManager;
        this.webSudoManager = webSudoManager;
    }

    protected List<String> getRequiredWebResources() {
        return Collections.singletonList("com.atlassian.applinks.applinks-basicauth-plugin:basic-auth");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            this.webSudoManager.willExecuteWebSudoRequest(request);
            ApplicationLink applicationLink = this.getRequiredApplicationLink(request);
            String username = this.getConfiguredUsername(applicationLink);
            RendererContextBuilder builder = this.createContextBuilder(applicationLink);
            builder.put("configured", (Object)this.authenticationConfigurationManager.isConfigured(applicationLink.getId(), BasicAuthenticationProvider.class));
            if (StringUtils.isEmpty((CharSequence)username)) {
                builder.put("view", (Object)"disabled");
            } else {
                builder.put("username", (Object)username).put("view", (Object)"enabled");
            }
            this.render(TEMPLATE, builder.build(), request, response);
        }
        catch (WebSudoSessionException wse) {
            this.webSudoManager.enforceWebSudoProtection(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            this.webSudoManager.willExecuteWebSudoRequest(request);
            if (Method.PUT == this.getRequiredMethod(request)) {
                this.doPut(request, response);
            } else {
                this.doDelete(request, response);
            }
        }
        catch (WebSudoSessionException wse) {
            this.webSudoManager.enforceWebSudoProtection(request, response);
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            this.webSudoManager.willExecuteWebSudoRequest(request);
            ApplicationLink applicationLink = this.getRequiredApplicationLink(request);
            String usernameInput = request.getParameter("username");
            String passwordInput1 = request.getParameter("password1");
            String passwordInput2 = request.getParameter("password2");
            RendererContextBuilder contextBuilder = this.createContextBuilder(applicationLink).put("view", (Object)"edit").put("usernameInput", (Object)usernameInput).put("username", (Object)this.getConfiguredUsername(applicationLink)).put("configured", (Object)this.authenticationConfigurationManager.isConfigured(applicationLink.getId(), BasicAuthenticationProvider.class));
            if (StringUtils.isBlank((CharSequence)usernameInput)) {
                contextBuilder.put("error", (Object)this.messageFactory.newI18nMessage("auth.basic.config.error.nousername", new Serializable[0]));
            } else if (StringUtils.isBlank((CharSequence)passwordInput1) && StringUtils.isBlank((CharSequence)passwordInput2)) {
                contextBuilder.put("error", (Object)this.messageFactory.newI18nMessage("auth.basic.config.error.nopassword", new Serializable[0]));
            } else if (!StringUtils.equals((CharSequence)passwordInput1, (CharSequence)passwordInput2)) {
                contextBuilder.put("error", (Object)this.messageFactory.newI18nMessage("auth.basic.config.error.mismatch", new Serializable[0]));
            } else {
                ImmutableMap config = ImmutableMap.of((Object)"username", (Object)usernameInput, (Object)"password", (Object)passwordInput1);
                this.authenticationConfigurationManager.registerProvider(applicationLink.getId(), BasicAuthenticationProvider.class, (Map)config);
                response.sendRedirect("./" + applicationLink.getId());
                return;
            }
            this.render(TEMPLATE, contextBuilder.build(), request, response);
        }
        catch (WebSudoSessionException wse) {
            this.webSudoManager.enforceWebSudoProtection(request, response);
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            this.webSudoManager.willExecuteWebSudoRequest(request);
            ApplicationLink applicationLink = this.getRequiredApplicationLink(request);
            this.authenticationConfigurationManager.unregisterProvider(applicationLink.getId(), BasicAuthenticationProvider.class);
            response.sendRedirect("./" + applicationLink.getId());
        }
        catch (WebSudoSessionException wse) {
            this.webSudoManager.enforceWebSudoProtection(request, response);
        }
    }

    private Method getRequiredMethod(HttpServletRequest request) {
        String value = this.getRequiredParameter(request, "method");
        try {
            return Method.valueOf(value);
        }
        catch (IllegalArgumentException e) {
            throw new AbstractApplinksServlet.BadRequestException(this.messageFactory.newLocalizedMessage("Invalid method: " + value));
        }
    }

    private String getConfiguredUsername(ApplicationLink applicationLink) {
        Map config = this.authenticationConfigurationManager.getConfiguration(applicationLink.getId(), BasicAuthenticationProvider.class);
        return config == null ? null : (String)config.get("username");
    }

    private static enum Method {
        PUT,
        DELETE;

    }
}

