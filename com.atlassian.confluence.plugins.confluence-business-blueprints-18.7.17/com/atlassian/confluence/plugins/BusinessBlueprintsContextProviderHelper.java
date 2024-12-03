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
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins;

import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.user.User;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class BusinessBlueprintsContextProviderHelper {
    public static final String STORAGE_DATE_FORMAT = "yyyy-MM-dd";
    private I18NBeanFactory i18NBeanFactory;
    private LocaleManager localeManager;
    private TemplateRenderer templateRenderer;
    private final UserAccessor userAccessor;
    private final FormatSettingsManager formatSettingsManager;

    public BusinessBlueprintsContextProviderHelper(I18NBeanFactory i18NBeanFactory, LocaleManager localeManager, TemplateRenderer templateRenderer, UserAccessor userAccessor, FormatSettingsManager formatSettingsManager) {
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
        this.templateRenderer = templateRenderer;
        this.userAccessor = userAccessor;
        this.formatSettingsManager = formatSettingsManager;
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

    public String getFormattedLocalDate(String dateFormat) {
        Date today = new Date();
        ConfluenceUserPreferences preferences = this.userAccessor.getConfluenceUserPreferences(this.getUser());
        DateFormatter dateFormatter = preferences.getDateFormatter(this.formatSettingsManager, this.localeManager);
        if (null == dateFormat) {
            return dateFormatter.format(today);
        }
        return dateFormatter.formatGivenString(dateFormat, today);
    }

    public String createStorageFormatForToday() {
        return this.createStorageFormatForDate(this.getFormattedLocalDate(STORAGE_DATE_FORMAT));
    }

    public String createStorageFormatForDate(String date) {
        if (StringUtils.isBlank((CharSequence)date)) {
            return "";
        }
        return String.format("<time datetime=\"%s\"></time>", date);
    }
}

