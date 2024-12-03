/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.DateFormatter
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.core.TimeZone
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.web.UrlBuilder
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.user.User
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.core.TimeZone;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.createcontent.api.services.CreateButtonService;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.plugins.createcontent.services.BlueprintResolver;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.user.User;
import com.google.common.collect.Maps;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={CreateButtonService.class})
public class DefaultCreateButtonService
implements CreateButtonService {
    private static final String SPACE_KEY = "spaceKey";
    private static final String TEMPLATE_ID = "templateId";
    private static final String BUTTON_LABEL = "buttonLabel";
    private static final String TITLE = "title";
    private static final String HAS_CREATE_PERMISSION = "hasCreatePermission";
    private static final String CREATE_CONTENT_URL = "createContentUrl";
    private static final String CONTENT_BLUEPRINT_ID = "contentBlueprintId";
    private static final String BUTTON_LABEL_DEFAULT_KEY = "com.atlassian.confluence.plugins.confluence-create-content-plugin.create-from-template.param.buttonLabel.default-value";
    private final UserAccessor userAccessor;
    private final FormatSettingsManager formatSettingsManager;
    private final LocaleManager localeManager;
    private final I18nResolver i18nResolver;
    private final TemplateRenderer templateRenderer;
    private final PermissionManager permissionManager;
    private final SettingsManager settingsManager;
    private final BlueprintResolver blueprintResolver;

    @Autowired
    public DefaultCreateButtonService(@ComponentImport UserAccessor userAccessor, @ComponentImport FormatSettingsManager formatSettingsManager, @ComponentImport LocaleManager localeManager, @ComponentImport I18nResolver i18nResolver, @ComponentImport TemplateRenderer templateRenderer, @ComponentImport PermissionManager permissionManager, @ComponentImport SettingsManager settingsManager, BlueprintResolver blueprintResolver) {
        this.userAccessor = userAccessor;
        this.formatSettingsManager = formatSettingsManager;
        this.localeManager = localeManager;
        this.i18nResolver = i18nResolver;
        this.templateRenderer = templateRenderer;
        this.permissionManager = permissionManager;
        this.settingsManager = settingsManager;
        this.blueprintResolver = blueprintResolver;
    }

    @Override
    public String renderBlueprintButton(Space space, String contentBlueprintId, String blueprintModuleCompleteKey, String buttonLabelKey, String newPageTitle) {
        return this.renderButton(space, contentBlueprintId, blueprintModuleCompleteKey, 0L, buttonLabelKey, newPageTitle);
    }

    @Override
    public String renderTemplateButton(Space space, long templateId, String buttonLabelKey, String newPageTitle) {
        return this.renderButton(space, null, null, templateId, buttonLabelKey, newPageTitle);
    }

    private String renderButton(Space space, String contentBlueprintId, String blueprintModuleCompleteKey, long templateId, String buttonLabelKey, String pageTitle) {
        ContentBlueprint contentBlueprint = null;
        HashMap context = Maps.newHashMap();
        if (templateId != 0L) {
            context.put(TEMPLATE_ID, templateId);
        } else {
            contentBlueprint = this.blueprintResolver.getContentBlueprint(contentBlueprintId, blueprintModuleCompleteKey, space.getKey());
            context.put(CONTENT_BLUEPRINT_ID, contentBlueprint.getId().toString());
        }
        if (StringUtils.isBlank((CharSequence)buttonLabelKey)) {
            buttonLabelKey = BUTTON_LABEL_DEFAULT_KEY;
        }
        String buttonLabel = this.i18nResolver.getText(buttonLabelKey);
        context.put(BUTTON_LABEL, buttonLabel);
        String title = this.getTitle(pageTitle, space);
        if (title != null) {
            context.put(TITLE, title);
        }
        String spaceKey = space.getKey();
        context.put(SPACE_KEY, spaceKey);
        context.put(HAS_CREATE_PERMISSION, this.permissionManager.hasCreatePermission((User)this.getUser(), (Object)space, Page.class));
        context.put(CREATE_CONTENT_URL, this.getCreateContentUrl(contentBlueprint, templateId, space, title));
        StringBuilder output = new StringBuilder();
        this.templateRenderer.renderTo((Appendable)output, "com.atlassian.confluence.plugins.confluence-create-content-plugin:create-from-template-resources", "Confluence.Templates.Blueprints.CreateFromTemplate.macroTemplate.soy", (Map)context);
        return output.toString();
    }

    private ConfluenceUser getUser() {
        return AuthenticatedUserThreadLocal.get();
    }

    private String getTitle(String title, Space space) {
        if (StringUtils.isBlank((CharSequence)title)) {
            return title;
        }
        title = title.replaceAll("(?<!\\\\)@spaceName", space.getName());
        title = title.replaceAll("(?<!\\\\)@spaceKey", space.getKey());
        TimeZone timeZone = this.userAccessor.getConfluenceUserPreferences((User)AuthenticatedUserThreadLocal.get()).getTimeZone();
        title = title.replaceAll("(?<!\\\\)@currentDate", new DateFormatter(timeZone, this.formatSettingsManager, this.localeManager).getDateForBlogPost(new Date()));
        return title;
    }

    private String getCreateContentUrl(ContentBlueprint blueprint, long templateId, Space space, String title) {
        String baseUrl = this.settingsManager.getGlobalSettings().getBaseUrl();
        UrlBuilder createContentUrl = new UrlBuilder(baseUrl);
        String spaceKey = space.getKey();
        if (templateId != 0L) {
            createContentUrl.add(TEMPLATE_ID, templateId);
            createContentUrl.add(SPACE_KEY, spaceKey);
            createContentUrl.add("newSpaceKey", spaceKey);
        } else {
            createContentUrl.add("createDialogSpaceKey", spaceKey);
            createContentUrl.add("createDialogBlueprintId", blueprint.getId().toString());
        }
        if (title != null) {
            createContentUrl.add(TITLE, title);
        }
        return createContentUrl.toString();
    }
}

