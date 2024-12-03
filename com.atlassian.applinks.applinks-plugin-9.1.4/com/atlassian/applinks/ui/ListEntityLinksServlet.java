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
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.http.entity.ContentType
 */
package com.atlassian.applinks.ui;

import com.atlassian.applinks.analytics.EntityLinksAdminViewEvent;
import com.atlassian.applinks.core.AppLinkPluginUtil;
import com.atlassian.applinks.core.util.MessageFactory;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.docs.DocumentationLinker;
import com.atlassian.applinks.ui.AbstractApplinksServlet;
import com.atlassian.applinks.ui.EntityLinksContextFactory;
import com.atlassian.applinks.ui.auth.AdminUIAuthenticator;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import com.atlassian.sal.api.xsrf.XsrfTokenValidator;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;

public class ListEntityLinksServlet
extends AbstractApplinksServlet {
    private static final String DEFAULT_CONTENT_TYPE = ContentType.create((String)"text/html", (Charset)StandardCharsets.UTF_8).toString();
    private static final String MODULE_KEY = "entitylinks-react-ui";
    private static final String TEMPLATE_DESCRIPTOR = "applinks.internal.entitylinks.entitylinkPage";
    private final AppLinkPluginUtil appLinkPluginUtil;
    private final SoyTemplateRenderer soyTemplateRenderer;
    private final WebSudoManager webSudoManager;
    private final EntityLinksContextFactory entityLinksContextFactory;
    private final EventPublisher eventPublisher;

    public ListEntityLinksServlet(I18nResolver i18nResolver, MessageFactory messageFactory, TemplateRenderer templateRenderer, WebResourceManager webResourceManager, DocumentationLinker documentationLinker, LoginUriProvider loginUriProvider, InternalHostApplication internalHostApplication, XsrfTokenAccessor xsrfTokenAccessor, XsrfTokenValidator xsrfTokenValidator, AppLinkPluginUtil appLinkPluginUtil, AdminUIAuthenticator adminUIAuthenticator, SoyTemplateRenderer soyTemplateRenderer, WebSudoManager webSudoManager, EntityLinksContextFactory entityLinksContextFactory, EventPublisher eventPublisher) {
        super(i18nResolver, messageFactory, templateRenderer, webResourceManager, documentationLinker, loginUriProvider, internalHostApplication, adminUIAuthenticator, xsrfTokenAccessor, xsrfTokenValidator);
        this.appLinkPluginUtil = appLinkPluginUtil;
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.webSudoManager = webSudoManager;
        this.entityLinksContextFactory = entityLinksContextFactory;
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected List<String> getRequiredWebResources() {
        return Collections.singletonList("com.atlassian.applinks.applinks-plugin:list-entity-links");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            this.webSudoManager.willExecuteWebSudoRequest(request);
            String[] pathParams = this.extractParams(request);
            String typeId = pathParams[pathParams.length - 2];
            String projectKey = pathParams[pathParams.length - 1];
            this.publishAnalytics(typeId, projectKey);
            response.setContentType(DEFAULT_CONTENT_TYPE);
            this.soyTemplateRenderer.render((Appendable)response.getWriter(), this.appLinkPluginUtil.completeModuleKey(MODULE_KEY), TEMPLATE_DESCRIPTOR, this.entityLinksContextFactory.createContext(typeId, projectKey));
        }
        catch (WebSudoSessionException wse) {
            this.webSudoManager.enforceWebSudoProtection(request, response);
        }
    }

    private String[] extractParams(HttpServletRequest request) {
        String[] pathParams = StringUtils.split((String)request.getPathInfo(), (char)'/');
        if (pathParams.length < 2) {
            throw new AbstractApplinksServlet.BadRequestException(this.messageFactory.newLocalizedMessage("Servlet URL should be of form /listEntityLinks/{entity-type}/{entity-key}"));
        }
        return pathParams;
    }

    private void publishAnalytics(String typeId, String key) {
        this.eventPublisher.publish((Object)new EntityLinksAdminViewEvent(typeId, key));
    }
}

