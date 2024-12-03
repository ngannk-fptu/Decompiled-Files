/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.DateFormatter
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUserPreferences
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.time.DateUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins;

import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.analytics.AnalyticsPublisher;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AtlassianPlaybookBlueprintsContextProvider {
    public static final String STORAGE_DATE_FORMAT = "yyyy-MM-dd";
    private I18NBeanFactory i18NBeanFactory;
    private LocaleManager localeManager;
    private TemplateRenderer templateRenderer;
    private final UserAccessor userAccessor;
    private final FormatSettingsManager formatSettingsManager;
    private final AnalyticsPublisher analyticsPublisher;

    @Autowired
    public AtlassianPlaybookBlueprintsContextProvider(@ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport LocaleManager localeManager, @ComponentImport UserAccessor userAccessor, @ComponentImport TemplateRenderer templateRenderer, @ComponentImport FormatSettingsManager formatSettingsManager, AnalyticsPublisher analyticsPublisher) {
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
        this.userAccessor = userAccessor;
        this.templateRenderer = templateRenderer;
        this.formatSettingsManager = formatSettingsManager;
        this.analyticsPublisher = analyticsPublisher;
    }

    public String renderFromSoy(String pluginKey, String soyTemplate, Map<String, Object> soyContext) {
        StringBuilder output = new StringBuilder();
        this.templateRenderer.renderTo((Appendable)output, pluginKey, soyTemplate, soyContext);
        return output.toString();
    }

    private Locale getLocale() {
        return this.localeManager.getSiteDefaultLocale();
    }

    public I18NBean getI18nBean() {
        return this.i18NBeanFactory.getI18NBean(this.getLocale());
    }

    public Locale getAuthenticatedUserLocale() {
        return this.localeManager.getLocale(this.getUser());
    }

    public User getUser() {
        return AuthenticatedUserThreadLocal.get();
    }

    public String getFormattedLocalDate(Date date, String dateFormat) {
        ConfluenceUserPreferences preferences = this.userAccessor.getConfluenceUserPreferences(this.getUser());
        DateFormatter dateFormatter = preferences.getDateFormatter(this.formatSettingsManager, this.localeManager);
        if (null == dateFormat) {
            return dateFormatter.format(date);
        }
        return dateFormatter.formatGivenString(dateFormat, date);
    }

    public String createStorageFormatForToday() {
        return this.createStorageFormatForDate(this.getFormattedLocalDate(new Date(), STORAGE_DATE_FORMAT));
    }

    public String createStorageFormatAroundToday() {
        Random rand = new Random();
        int within = rand.nextInt(30);
        Date today = new Date();
        Date aroundToday = DateUtils.addDays((Date)today, (int)within);
        return this.createStorageFormatForDate(this.getFormattedLocalDate(aroundToday, STORAGE_DATE_FORMAT));
    }

    public String createStorageFormatForDate(String date) {
        if (StringUtils.isBlank((CharSequence)date)) {
            return "";
        }
        return String.format("<time datetime=\"%s\"></time>", date);
    }

    public void onBlueprintCreated(String blueprintKey) {
        this.analyticsPublisher.publishCreatedEvent(blueprintKey);
    }
}

