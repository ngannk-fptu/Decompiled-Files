/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.DateFormatter
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.util.i18n.I18NBean
 */
package com.atlassian.confluence.plugins.macros.advanced.recentupdate;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.AbstractUpdateItem;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.DefaultUpdater;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.Updater;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.util.i18n.I18NBean;

public class AddToFavouritesUpdateItem
extends AbstractUpdateItem {
    private final ContentEntityObject content;
    private final String username;

    public AddToFavouritesUpdateItem(SearchResult searchResult, DateFormatter dateFormatter, ContentEntityObject content, String username, I18NBean i18n) {
        super(searchResult, dateFormatter, i18n, "content-type-favourite");
        this.content = content;
        this.username = username;
    }

    @Override
    public Updater getUpdater() {
        return new DefaultUpdater(this.username, this.i18n);
    }

    @Override
    protected String getUpdateTargetUrl() {
        return this.content.getUrlPath();
    }

    @Override
    public String getUpdateTargetTitle() {
        return this.content.getDisplayTitle();
    }

    @Override
    public String getDescriptionAndDateKey() {
        return "update.item.desc.add.to.favourites";
    }

    @Override
    public String getDescriptionAndAuthorKey() {
        return "update.item.desc.author.add.to.favourites";
    }
}

