/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  com.atlassian.sal.api.xsrf.XsrfTokenAccessor
 *  com.atlassian.sal.api.xsrf.XsrfTokenValidator
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.applinks.internal.web;

import com.atlassian.applinks.analytics.ApplinksAdminViewEvent;
import com.atlassian.applinks.core.util.MessageFactory;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.docs.DocumentationLinker;
import com.atlassian.applinks.internal.common.net.ResponseHeaderUtil;
import com.atlassian.applinks.internal.feature.ApplinksFeatureService;
import com.atlassian.applinks.internal.feature.ApplinksFeatures;
import com.atlassian.applinks.ui.AbstractAppLinksAdminOnlyServlet;
import com.atlassian.applinks.ui.auth.AdminUIAuthenticator;
import com.atlassian.applinks.ui.velocity.VelocityContextFactory;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import com.atlassian.sal.api.xsrf.XsrfTokenValidator;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ListApplicationLinksServlet
extends AbstractAppLinksAdminOnlyServlet {
    @VisibleForTesting
    static final String V2_TEMPLATE_PATH = "com/atlassian/applinks/ui/admin/list_application_links.vm";
    @VisibleForTesting
    static final String V3_TEMPLATE_PATH = "com/atlassian/applinks/ui/admin/list_application_links_agent_v3.vm";
    @VisibleForTesting
    static final String V4_TEMPLATE_PATH = "com/atlassian/applinks/ui/admin/list_application_links_agent_v4.vm";
    private static final String V2_WEB_RESOURCE_CONTEXT = "applinks.list.application.links";
    private static final String V3_WEB_RESOURCE_CONTEXT = "applinks.list.application.links.agent";
    private final VelocityContextFactory velocityContextFactory;
    private final WebSudoManager webSudoManager;
    private final ApplinksFeatureService applinksFeatureService;
    private final EventPublisher eventPublisher;

    public ListApplicationLinksServlet(I18nResolver i18nResolver, MessageFactory messageFactory, TemplateRenderer templateRenderer, WebResourceManager webResourceManager, AdminUIAuthenticator adminUIAuthenticator, InternalHostApplication internalHostApplication, DocumentationLinker documentationLinker, LoginUriProvider loginUriProvider, VelocityContextFactory velocityContextFactory, WebSudoManager webSudoManager, XsrfTokenAccessor xsrfTokenAccessor, XsrfTokenValidator xsrfTokenValidator, ApplinksFeatureService applinksFeatureService, EventPublisher eventPublisher) {
        super(i18nResolver, messageFactory, templateRenderer, webResourceManager, adminUIAuthenticator, documentationLinker, loginUriProvider, internalHostApplication, xsrfTokenAccessor, xsrfTokenValidator);
        this.velocityContextFactory = velocityContextFactory;
        this.webSudoManager = webSudoManager;
        this.applinksFeatureService = applinksFeatureService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected List<String> getRequiredWebResourceContexts() {
        String webResourceContext = this.applinksFeatureService.isEnabled(ApplinksFeatures.V3_UI) || this.applinksFeatureService.isEnabled(ApplinksFeatures.V4_UI) ? V3_WEB_RESOURCE_CONTEXT : V2_WEB_RESOURCE_CONTEXT;
        return Collections.singletonList(webResourceContext);
    }

    @Override
    protected void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doService(request, response);
        ResponseHeaderUtil.preventCrossFrameClickJacking(response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            this.webSudoManager.willExecuteWebSudoRequest(request);
            this.publishAnalytics();
            this.render(this.getTemplatePath(), (Map<String, Object>)ImmutableMap.of((Object)"context", (Object)this.velocityContextFactory.buildListApplicationLinksContext(request)), request, response);
        }
        catch (WebSudoSessionException wse) {
            this.webSudoManager.enforceWebSudoProtection(request, response);
        }
    }

    private void publishAnalytics() {
        this.eventPublisher.publish((Object)new ApplinksAdminViewEvent());
    }

    private String getTemplatePath() {
        if (this.applinksFeatureService.isEnabled(ApplinksFeatures.V4_UI)) {
            return V4_TEMPLATE_PATH;
        }
        if (this.applinksFeatureService.isEnabled(ApplinksFeatures.V3_UI)) {
            return V3_TEMPLATE_PATH;
        }
        return V2_TEMPLATE_PATH;
    }
}

