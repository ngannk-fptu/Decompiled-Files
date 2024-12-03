/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.pages.CommentManager
 *  com.atlassian.confluence.plugins.createcontent.ContentBlueprintManager
 *  com.atlassian.confluence.plugins.createcontent.TemplateRendererHelper
 *  com.atlassian.confluence.plugins.sharepage.api.SharePageService
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.plugins.whitelist.OutboundWhitelist
 *  com.atlassian.sal.api.net.RequestFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins;

import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.plugins.createcontent.ContentBlueprintManager;
import com.atlassian.confluence.plugins.createcontent.TemplateRendererHelper;
import com.atlassian.confluence.plugins.sharepage.api.SharePageService;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.plugins.whitelist.OutboundWhitelist;
import com.atlassian.sal.api.net.RequestFactory;
import org.springframework.stereotype.Component;

@Component
public class ComponentImporter {
    @ComponentImport
    private LocaleManager localeManager;
    @ComponentImport
    private I18NBeanFactory i18NBeanFactory;
    @ComponentImport
    private SharePageService sharePageService;
    @ComponentImport
    private ContentBlueprintManager contentBlueprintManager;
    @ComponentImport
    private TemplateRendererHelper templateRendererHelper;
    @ComponentImport
    private OutboundWhitelist outboundWhitelist;
    @ComponentImport
    private RequestFactory requestFactory;
    @ComponentImport
    private TemplateRenderer templateRenderer;
    @ComponentImport
    private UserAccessor userAccessor;
    @ComponentImport
    private FormatSettingsManager formatSettingsManager;
    @ComponentImport
    private SettingsManager settingsManager;
    @ComponentImport
    private PermissionManager permissionManager;
    @ComponentImport
    private SpaceManager spaceManager;
    @ComponentImport
    private WebResourceUrlProvider webResourceUrlProvider;
    @ComponentImport
    private EventPublisher eventPublisher;
    @ComponentImport
    private CommentManager commentManager;
    @ComponentImport
    private LabelManager labelManager;
}

