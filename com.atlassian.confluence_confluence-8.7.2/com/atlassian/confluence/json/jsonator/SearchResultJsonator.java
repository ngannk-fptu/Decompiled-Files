/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.json.jsonator;

import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.core.datetime.FriendlyDateFormatter;
import com.atlassian.confluence.core.datetime.RequestTimeThreadLocal;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.json.json.Json;
import com.atlassian.confluence.json.json.JsonObject;
import com.atlassian.confluence.json.jsonator.Jsonator;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserPreferencesAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.i18n.Message;
import java.util.Date;

public class SearchResultJsonator
implements Jsonator<SearchResult> {
    private final ContextPathHolder contextPathHolder;
    private final I18NBeanFactory i18NBeanFactory;
    private final UserPreferencesAccessor userPreferencesAccessor;
    private final FormatSettingsManager formatSettingsManager;
    private final LocaleManager localeManager;

    public SearchResultJsonator(ContextPathHolder context, I18NBeanFactory i18NBeanFactory, UserPreferencesAccessor userPreferencesAccessor, FormatSettingsManager formatSettingsManager, LocaleManager localeManager) {
        this.contextPathHolder = context;
        this.userPreferencesAccessor = userPreferencesAccessor;
        this.formatSettingsManager = formatSettingsManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
    }

    @Override
    public Json convert(SearchResult result) {
        JsonObject json = new JsonObject();
        HibernateHandle handle = (HibernateHandle)result.getHandle();
        json.setProperty("id", handle.getId());
        json.setProperty("type", result.getType());
        json.setProperty("title", result.getDisplayTitle());
        json.setProperty("spaceKey", result.getSpaceKey());
        json.setProperty("spaceName", result.getSpaceName());
        json.setProperty("creator", result.getCreator());
        json.setProperty("lastModifier", result.getLastModifier());
        json.setProperty("date", this.getDateFormatter().formatDateTime(result.getLastModificationDate()));
        json.setProperty("friendlyDate", this.formatFriendlyDate(result.getLastModificationDate()));
        json.setProperty("url", this.contextPathHolder.getContextPath() + result.getUrlPath());
        return json;
    }

    private DateFormatter getDateFormatter() {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        return this.userPreferencesAccessor.getConfluenceUserPreferences(user).getDateFormatter(this.formatSettingsManager, this.localeManager);
    }

    private String formatFriendlyDate(Date date) {
        Message message = this.getFriendlyDateFormatter().getFormatMessage(date);
        return this.getText(message.getKey(), message.getArguments());
    }

    private FriendlyDateFormatter getFriendlyDateFormatter() {
        return new FriendlyDateFormatter(RequestTimeThreadLocal.getTimeOrNow(), this.getDateFormatter());
    }

    private String getText(String key, Object ... args) {
        return this.i18NBeanFactory.getI18NBean().getText(key, args);
    }
}

