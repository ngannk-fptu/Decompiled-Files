/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Renderer
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceManager
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.web.UrlBuilder
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.user.User
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.remotepageview.service;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceManager;
import com.atlassian.confluence.plugins.remotepageview.api.service.RemotePageViewService;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.renderer.RenderContext;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.user.User;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RemotePageViewServiceImpl
implements RemotePageViewService {
    private static final String REMOTE_PAGE_VIEW_RESOURCES_MODULE_COMPLETE_KEY = "com.atlassian.confluence.plugins.confluence-remote-page-view-plugin:remote-view-page-web-resource";
    private static final String REMOTE_PAGE_VIEW_SOY_TEMPLATE = "Confluence.RemotePageView.renderPage.soy";
    private static final String REMOTE_LOGIN_REDIRECT_PAGE_SOY_TEMPLATE = "Confluence.RemotePageView.renderLoginRedirectPage.soy";
    private static final String REMOTE_ERROR_MESSAGE_PAGE_SOY_TEMPLATE = "Confluence.RemotePageView.errorPage.soy";
    public static final String VIEWPAGE_ACTION_PATH = "/pages/viewpage.action";
    public static final String PAGEID_PARAM = "pageId";
    private final Renderer viewRenderer;
    private final ConfluenceWebResourceManager webResourceManager;
    private final TemplateRenderer templateRenderer;
    private final PageManager pageManager;
    private final SettingsManager settingsManager;
    private final I18nResolver i18nResolver;
    private final PermissionManager permissionManager;

    @Autowired
    public RemotePageViewServiceImpl(Renderer viewRenderer, ConfluenceWebResourceManager webResourceManager, TemplateRenderer templateRenderer, PageManager pageManager, PermissionManager permissionManager, SettingsManager settingsManager, I18nResolver i18nResolver) {
        this.viewRenderer = viewRenderer;
        this.webResourceManager = webResourceManager;
        this.templateRenderer = templateRenderer;
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.settingsManager = settingsManager;
        this.i18nResolver = i18nResolver;
    }

    @Override
    public String renderPage(long pageId, ConfluenceUser user) {
        Page page = this.pageManager.getPage(pageId);
        if (page == null || page.isDeleted()) {
            return this.renderUserMessagePageFromTemplate(REMOTE_ERROR_MESSAGE_PAGE_SOY_TEMPLATE, this.i18nResolver.getText("remote-page-view.page-not-found.header"), this.i18nResolver.getText("remote-page-view.page-not-found.message"));
        }
        if (user == null) {
            boolean anonymousUseEnabled = this.isAnonymousUseAllowed(user);
            boolean anonymousCanViewPage = this.canViewPage(user, (AbstractPage)page);
            if (!anonymousUseEnabled || !anonymousCanViewPage) {
                return this.renderLoginRedirectPageFromTemplate((AbstractPage)page, REMOTE_LOGIN_REDIRECT_PAGE_SOY_TEMPLATE);
            }
        } else if (!this.canViewPage(user, (AbstractPage)page)) {
            return this.renderUserMessagePageFromTemplate(REMOTE_ERROR_MESSAGE_PAGE_SOY_TEMPLATE, this.i18nResolver.getText("remote-page-view.no-permission.header"), this.i18nResolver.getText("remote-page-view.no-permission.message"));
        }
        return this.renderRemotePageFromTemplate((AbstractPage)page);
    }

    private String renderRemotePageFromTemplate(AbstractPage page) {
        this.requirePresentationResources();
        String body = this.viewRenderer.render((ContentEntityObject)page, (ConversionContext)new DefaultConversionContext((RenderContext)page.toPageContext()));
        HashMap<String, String> soyContext = new HashMap<String, String>();
        soyContext.put("bodyHTML", body);
        StringBuilder htmlPageSource = new StringBuilder();
        this.templateRenderer.renderTo((Appendable)htmlPageSource, REMOTE_PAGE_VIEW_RESOURCES_MODULE_COMPLETE_KEY, REMOTE_PAGE_VIEW_SOY_TEMPLATE, soyContext);
        return htmlPageSource.toString();
    }

    private String renderLoginRedirectPageFromTemplate(AbstractPage page, String templateName) {
        this.requirePresentationResources();
        UrlBuilder uriBuilder = new UrlBuilder(this.settingsManager.getGlobalSettings().getBaseUrl() + VIEWPAGE_ACTION_PATH).add(PAGEID_PARAM, page.getIdAsString());
        HashMap<String, String> soyContext = new HashMap<String, String>();
        soyContext.put("loginPathUrl", uriBuilder.toUrl());
        StringBuilder htmlPageSource = new StringBuilder();
        this.templateRenderer.renderTo((Appendable)htmlPageSource, REMOTE_PAGE_VIEW_RESOURCES_MODULE_COMPLETE_KEY, templateName, soyContext);
        return htmlPageSource.toString();
    }

    private String renderUserMessagePageFromTemplate(String templateName, String headerText, String messageText) {
        this.requirePresentationResources();
        HashMap<String, String> soyContext = new HashMap<String, String>();
        soyContext.put("headerText", headerText);
        soyContext.put("messageText", messageText);
        StringBuilder htmlPageSource = new StringBuilder();
        this.templateRenderer.renderTo((Appendable)htmlPageSource, REMOTE_PAGE_VIEW_RESOURCES_MODULE_COMPLETE_KEY, templateName, soyContext);
        return htmlPageSource.toString();
    }

    private void requirePresentationResources() {
        this.webResourceManager.requireResourcesForContext("preview");
        this.webResourceManager.requireResourcesForContext("remotepageview");
    }

    private boolean canViewPage(ConfluenceUser user, AbstractPage page) {
        return this.permissionManager.hasPermission((User)user, Permission.VIEW, (Object)page);
    }

    private boolean isAnonymousUseAllowed(ConfluenceUser user) {
        return this.permissionManager.hasPermission((User)user, Permission.VIEW, PermissionManager.TARGET_APPLICATION);
    }
}

