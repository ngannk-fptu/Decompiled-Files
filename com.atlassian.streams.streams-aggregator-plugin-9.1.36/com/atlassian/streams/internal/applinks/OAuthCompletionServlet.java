/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.streams.internal.applinks;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.streams.internal.applinks.ApplicationLinkServiceExtensionsImpl;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuthCompletionServlet
extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(OAuthCompletionServlet.class);
    public static final String APPLINK_ID_PARAM = "applinkId";
    private static final Pattern APPLINK_ID_REGEX = Pattern.compile("[0-9a-z-]+");
    private static final String TEMPLATE = "template/applinks/auth-completion.vm";
    private final ApplicationLinkService appLinkService;
    private final ApplicationLinkServiceExtensionsImpl appLinkServiceExtensionsImpl;
    private final TemplateRenderer templateRenderer;
    private final WebResourceManager webResourceManager;

    public OAuthCompletionServlet(ApplicationLinkService appLinkService, ApplicationLinkServiceExtensionsImpl appLinkServiceExtensionsImpl, TemplateRenderer templateRenderer, WebResourceManager webResourceManager) {
        this.appLinkService = (ApplicationLinkService)Preconditions.checkNotNull((Object)appLinkService, (Object)"appLinkService");
        this.appLinkServiceExtensionsImpl = (ApplicationLinkServiceExtensionsImpl)Preconditions.checkNotNull((Object)appLinkServiceExtensionsImpl, (Object)"appLinkServiceExtensionsImpl");
        this.templateRenderer = (TemplateRenderer)Preconditions.checkNotNull((Object)templateRenderer, (Object)"templateRenderer");
        this.webResourceManager = (WebResourceManager)Preconditions.checkNotNull((Object)webResourceManager, (Object)"webResourceManager");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ApplicationLink appLink;
        String applinkId = request.getParameter(APPLINK_ID_PARAM);
        if (applinkId == null || !APPLINK_ID_REGEX.matcher(applinkId).matches()) {
            response.setStatus(400);
            return;
        }
        boolean success = false;
        try {
            appLink = this.appLinkService.getApplicationLink(new ApplicationId(applinkId));
            if (appLink != null) {
                success = this.appLinkServiceExtensionsImpl.isAuthorised(appLink);
            }
        }
        catch (TypeNotInstalledException e) {
            log.error("Unknown applink type for applink ID '" + applinkId + "'");
            appLink = null;
        }
        ImmutableMap context = ImmutableMap.of((Object)APPLINK_ID_PARAM, (Object)applinkId, (Object)"authAdminUri", (Object)(appLink == null ? "" : this.appLinkServiceExtensionsImpl.getUserAdminUri(appLink)), (Object)"success", (Object)success);
        response.setContentType("text/html");
        this.webResourceManager.requireResource("com.atlassian.auiplugin:ajs");
        this.templateRenderer.render(TEMPLATE, (Map)context, (Writer)response.getWriter());
    }
}

