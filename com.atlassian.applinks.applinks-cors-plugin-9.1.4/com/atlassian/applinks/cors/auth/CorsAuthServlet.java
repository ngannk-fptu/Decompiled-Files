/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.core.auth.AbstractSysadminOnlyAuthServlet
 *  com.atlassian.applinks.core.util.MessageFactory
 *  com.atlassian.applinks.core.util.RendererContextBuilder
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.internal.common.docs.DocumentationLinker
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
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.applinks.cors.auth;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.core.auth.AbstractSysadminOnlyAuthServlet;
import com.atlassian.applinks.core.util.MessageFactory;
import com.atlassian.applinks.core.util.RendererContextBuilder;
import com.atlassian.applinks.cors.auth.CorsService;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.docs.DocumentationLinker;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CorsAuthServlet
extends AbstractSysadminOnlyAuthServlet {
    private static final String TEMPLATE = "com/atlassian/applinks/cors/auth/config.vm";
    public static final String WEB_RESOURCE_KEY = "com.atlassian.applinks.applinks-cors-plugin:";
    private final CorsService corsService;
    private final WebSudoManager webSudoManager;

    public CorsAuthServlet(I18nResolver i18nResolver, MessageFactory messageFactory, TemplateRenderer templateRenderer, WebResourceManager webResourceManager, ApplicationLinkService applicationLinkService, AdminUIAuthenticator adminUIAuthenticator, DocumentationLinker documentationLinker, LoginUriProvider loginUriProvider, InternalHostApplication internalHostApplication, XsrfTokenAccessor xsrfTokenAccessor, XsrfTokenValidator xsrfTokenValidator, CorsService corsService, WebSudoManager webSudoManager, UserManager userManager) {
        super(i18nResolver, messageFactory, templateRenderer, webResourceManager, applicationLinkService, adminUIAuthenticator, documentationLinker, loginUriProvider, internalHostApplication, xsrfTokenAccessor, xsrfTokenValidator, userManager);
        this.corsService = corsService;
        this.webSudoManager = webSudoManager;
    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            this.webSudoManager.willExecuteWebSudoRequest(req);
            ApplicationLink link = this.getRequiredApplicationLink(req);
            this.corsService.disableCredentials(link);
            this.render(link, false, req, resp);
        }
        catch (WebSudoSessionException e) {
            this.webSudoManager.enforceWebSudoProtection(req, resp);
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            this.webSudoManager.willExecuteWebSudoRequest(req);
            ApplicationLink link = this.getRequiredApplicationLink(req);
            boolean configured = this.corsService.allowsCredentials(link);
            this.render(link, configured, req, resp);
        }
        catch (WebSudoSessionException e) {
            this.webSudoManager.enforceWebSudoProtection(req, resp);
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String method = this.getRequiredParameter(req, "method");
        if ("PUT".equals(method)) {
            this.doPut(req, resp);
        } else if ("DELETE".equals(method)) {
            this.doDelete(req, resp);
        } else {
            throw new AbstractApplinksServlet.BadRequestException(this.messageFactory.newLocalizedMessage("Invalid method: " + method));
        }
    }

    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            this.webSudoManager.willExecuteWebSudoRequest(req);
            ApplicationLink link = this.getRequiredApplicationLink(req);
            this.corsService.enableCredentials(link);
            this.render(link, true, req, resp);
        }
        catch (WebSudoSessionException e) {
            this.webSudoManager.enforceWebSudoProtection(req, resp);
        }
    }

    protected List<String> getRequiredWebResources() {
        return Collections.singletonList("com.atlassian.applinks.applinks-cors-plugin:cors-auth");
    }

    private void render(ApplicationLink link, boolean allowsCredentials, HttpServletRequest request, HttpServletResponse response) throws IOException {
        RendererContextBuilder builder = this.createContextBuilder(link);
        builder.put("configured", (Object)allowsCredentials);
        Collection<ApplicationLink> matches = this.corsService.getApplicationLinksByUri(link.getRpcUrl());
        boolean conflicted = false;
        if (matches.size() > 1) {
            ArrayList<ApplicationLink> conflicts = new ArrayList<ApplicationLink>(matches.size());
            for (ApplicationLink match : matches) {
                if (link.getId().equals((Object)match.getId()) || allowsCredentials == this.corsService.allowsCredentials(match)) continue;
                conflicts.add(match);
            }
            if (!conflicts.isEmpty()) {
                conflicted = true;
                builder.put("conflicts", conflicts);
            }
        }
        builder.put("conflicted", (Object)conflicted);
        this.render(TEMPLATE, builder.build(), request, response);
    }
}

