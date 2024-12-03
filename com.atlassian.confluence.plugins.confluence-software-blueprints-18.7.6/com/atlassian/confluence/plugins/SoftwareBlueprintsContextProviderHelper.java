/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.DateFormatter
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUserPreferences
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.core.util.XMLUtils
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.user.User
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.plugins;

import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.common.event.SoftwareBPAnalyticEvent;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.core.util.XMLUtils;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.user.User;
import com.google.common.collect.Maps;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SoftwareBlueprintsContextProviderHelper {
    public static final String STORAGE_DATE_FORMAT = "yyyy-MM-dd";
    public static final String PLUGIN_KEY = "com.atlassian.confluence.plugins.confluence-software-blueprints:jirareports-resources";
    public static final String SOY_ERROR_TIMEOUT_TEMPLATE = "Confluence.Blueprints.JiraReports.Template.errortimeout.soy";
    public static final String RELEASE_TITLE_TEMPLATE = "Confluence.Blueprints.JiraReports.Template.releaseTitle.soy";
    private static final String CREATE_FROM_TEMPLATE_MACRO = "Confluence.Blueprints.Common.createFromTemplateMacro.soy";
    private TemplateRenderer templateRenderer;
    private LocaleManager localeManager;
    private FormatSettingsManager formatSettingsManager;
    private I18NBeanFactory i18NBeanFactory;
    private SettingsManager settingsManager;
    private EventPublisher eventPublisher;
    private final UserAccessor userAccessor;

    public SoftwareBlueprintsContextProviderHelper(TemplateRenderer templateRenderer, LocaleManager localeManager, FormatSettingsManager formatSettingsManager, I18NBeanFactory i18NBeanFactory, SettingsManager settingsManager, EventPublisher eventPublisher, UserAccessor userAccessor) {
        this.templateRenderer = templateRenderer;
        this.localeManager = localeManager;
        this.formatSettingsManager = formatSettingsManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.settingsManager = settingsManager;
        this.eventPublisher = eventPublisher;
        this.userAccessor = userAccessor;
    }

    public String renderFromSoy(String pluginKey, String soyTemplate, Map<String, Object> soyContext) {
        StringBuilder output = new StringBuilder();
        this.templateRenderer.renderTo((Appendable)output, pluginKey, soyTemplate, soyContext);
        return output.toString();
    }

    public Locale getAuthenticatedUserLocale() {
        return this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get());
    }

    public String getDateFormat() {
        return this.formatSettingsManager.getDateFormat();
    }

    public I18NBean getI18NBeanForCurrentUser() {
        return this.i18NBeanFactory.getI18NBean(this.getAuthenticatedUserLocale());
    }

    private I18NBean getI18NBean() {
        return this.i18NBeanFactory.getI18NBean();
    }

    public String getText(String i18n) {
        return this.getI18NBean().getText(i18n);
    }

    public String renderTimeout() {
        return this.renderFromSoy(PLUGIN_KEY, SOY_ERROR_TIMEOUT_TEMPLATE, Collections.emptyMap());
    }

    public String renderReleaseTitle() {
        return this.renderFromSoy(PLUGIN_KEY, RELEASE_TITLE_TEMPLATE, Collections.emptyMap());
    }

    public String formatDate(String format, Date date) {
        SimpleDateFormat titleDateFormat = new SimpleDateFormat(format, this.getAuthenticatedUserLocale());
        return titleDateFormat.format(date);
    }

    public String serverFormatDate(Date date) {
        SimpleDateFormat titleDateFormat = new SimpleDateFormat(this.getDateFormat(), this.getAuthenticatedUserLocale());
        return titleDateFormat.format(date);
    }

    public String getCreateFromTemplateMacro(BlueprintContext context, String buttonLabel, String pluginKey) {
        String spaceKey = XMLUtils.escape((String)context.getSpaceKey());
        String blueprintKey = XMLUtils.escape((String)context.getBlueprintModuleCompleteKey().getCompleteKey());
        HashMap templateContext = Maps.newHashMap();
        templateContext.put("blueprintKey", blueprintKey);
        templateContext.put("spaceKey", spaceKey);
        templateContext.put("buttonLabel", buttonLabel);
        return this.renderFromSoy(pluginKey, CREATE_FROM_TEMPLATE_MACRO, templateContext);
    }

    public void publishAnalyticEvent(String eventName) {
        this.eventPublisher.publish((Object)new SoftwareBPAnalyticEvent(eventName));
    }

    public String createStorageFormatForToday() {
        ConfluenceUserPreferences preferences = this.userAccessor.getConfluenceUserPreferences((User)AuthenticatedUserThreadLocal.get());
        DateFormatter dateFormatter = preferences.getDateFormatter(this.formatSettingsManager, this.localeManager);
        return String.format("<time datetime=\"%s\"></time>", dateFormatter.formatGivenString(STORAGE_DATE_FORMAT, new Date()));
    }
}

