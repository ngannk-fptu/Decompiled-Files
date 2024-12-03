/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.json.jsonorg.JSONArray
 *  com.atlassian.json.jsonorg.JSONException
 *  com.atlassian.json.jsonorg.JSONObject
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.json.marshal.Jsonable$JsonMappingException
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.user.User
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  com.google.common.annotations.VisibleForTesting
 *  javax.inject.Inject
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.plugins.pulp.wrm;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.json.jsonorg.JSONArray;
import com.atlassian.json.jsonorg.JSONException;
import com.atlassian.json.jsonorg.JSONObject;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.user.User;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import com.google.common.annotations.VisibleForTesting;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.checkerframework.checker.nullness.qual.NonNull;

public class NewsWrmDataProvider
implements WebResourceDataProvider {
    @VisibleForTesting
    static final String I18N_NEWS_HEADLINE_KEY = "pulp.rows.news.headline";
    @VisibleForTesting
    static final String I18N_NEWS_DESCRIPTION_KEY = "pulp.rows.news.description";
    @VisibleForTesting
    static final String I18N_NEWS_ITEM_KEY_PREFIX_TEMPLATE = "pulp.rows.news.%s.%s.item.";
    @VisibleForTesting
    static final String I18N_NEWS_ITEM_HEADLINE_SUFFIX = ".headline";
    @VisibleForTesting
    static final String I18N_NEWS_ITEM_TEXT_SUFFIX = ".text";
    @VisibleForTesting
    static final String I18N_NEWS_ITEM_ARTICLE_LINK_SUFFIX = ".article.link";
    @VisibleForTesting
    static final String I18N_NEWS_ITEM_ARTICLE_LABEL_SUFFIX = ".article.label";
    static final int NUMBER_OF_PIECES_OF_INFO_FOR_ARTICLE = 2;
    private final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;
    private final ApplicationProperties applicationProperties;

    @Inject
    public NewsWrmDataProvider(@ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport LocaleManager localeManager, @ComponentImport ApplicationProperties applicationProperties) {
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
        this.applicationProperties = applicationProperties;
    }

    public @NonNull Jsonable get() {
        return writer -> {
            try {
                this.getNewsJson().write(writer);
            }
            catch (JSONException e) {
                throw new Jsonable.JsonMappingException((Throwable)e);
            }
        };
    }

    private JSONArray getNewsJson() {
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get()));
        Map items = i18NBean.getTranslationsForPrefix(this.getI18nNewsItemKeyPrefix());
        if (this.hasNews(i18NBean) && items != null && !items.isEmpty()) {
            return this.filterNewsItems(i18NBean, items);
        }
        return new JSONArray();
    }

    private JSONArray filterNewsItems(I18NBean i18NBean, Map<String, String> items) {
        int indexOfItemNumber = this.getI18nNewsItemKeyPrefix().length();
        return new JSONArray((Collection)items.keySet().stream().filter(key -> key.length() > indexOfItemNumber).collect(Collectors.groupingBy(key -> Character.valueOf(key.charAt(indexOfItemNumber)), Collectors.toSet())).entrySet().stream().map(e -> this.createNewsItem(i18NBean, ((Character)e.getKey()).charValue(), (Set)e.getValue())).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
    }

    private Optional<JSONObject> createNewsItem(I18NBean i18NBean, char newsItemNumber, Set<String> newsItemKeys) {
        if (!this.areNewsItemKeysSufficient(newsItemKeys)) {
            return Optional.empty();
        }
        try {
            JSONObject newsItem = new JSONObject();
            newsItem.put("headline", (Object)this.getI18n(i18NBean, newsItemNumber, I18N_NEWS_ITEM_HEADLINE_SUFFIX));
            newsItem.put("text", (Object)this.getI18n(i18NBean, newsItemNumber, I18N_NEWS_ITEM_TEXT_SUFFIX));
            newsItem.putOpt("article", (Object)this.createArticle(i18NBean, newsItemNumber));
            return Optional.of(newsItem);
        }
        catch (JSONException e) {
            return Optional.empty();
        }
    }

    private JSONObject createArticle(I18NBean i18NBean, char newsItemNumber) throws JSONException {
        JSONObject article = new JSONObject();
        article.putOpt("link", (Object)this.getI18n(i18NBean, newsItemNumber, I18N_NEWS_ITEM_ARTICLE_LINK_SUFFIX));
        article.putOpt("label", (Object)this.getI18n(i18NBean, newsItemNumber, I18N_NEWS_ITEM_ARTICLE_LABEL_SUFFIX));
        return article.length() == 2 ? article : null;
    }

    private boolean areNewsItemKeysSufficient(Collection<String> keys) {
        return keys.stream().anyMatch(s -> s.endsWith(I18N_NEWS_ITEM_HEADLINE_SUFFIX)) && keys.stream().anyMatch(s -> s.endsWith(I18N_NEWS_ITEM_TEXT_SUFFIX));
    }

    private String getI18n(I18NBean i18NBean, char newsItemNumber, String suffix) {
        String key = this.getI18NKeyForNewsItem(newsItemNumber, suffix);
        if (this.i18nValueExists(i18NBean, key)) {
            return i18NBean.getText(key);
        }
        return null;
    }

    private String getI18NKeyForNewsItem(char newsItemNumber, String suffix) {
        return this.getI18nNewsItemKeyPrefix() + newsItemNumber + suffix;
    }

    private String getI18nNewsItemKeyPrefix() {
        String version = this.applicationProperties.getVersion();
        String[] components = version.split("\\.");
        if (components.length >= 2) {
            return String.format(I18N_NEWS_ITEM_KEY_PREFIX_TEMPLATE, components[0], components[1]);
        }
        throw new RuntimeException("Bad version number format " + version);
    }

    private boolean hasNews(I18NBean i18NBean) {
        return this.i18nValueExists(i18NBean, I18N_NEWS_HEADLINE_KEY) && this.i18nValueExists(i18NBean, I18N_NEWS_DESCRIPTION_KEY);
    }

    private boolean i18nValueExists(I18NBean i18NBean, String key) {
        return !i18NBean.getText(key).equals(key);
    }
}

