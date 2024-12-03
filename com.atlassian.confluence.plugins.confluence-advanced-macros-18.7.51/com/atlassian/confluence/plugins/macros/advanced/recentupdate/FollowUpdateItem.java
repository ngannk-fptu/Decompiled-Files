/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.DateFormatter
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.user.PersonalInformation
 *  com.atlassian.confluence.util.RequestCacheThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBean
 */
package com.atlassian.confluence.plugins.macros.advanced.recentupdate;

import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.AbstractUpdateItem;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.DefaultUpdater;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.Updater;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.util.RequestCacheThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;

public class FollowUpdateItem
extends AbstractUpdateItem {
    private final PersonalInformation followeePersonalInfo;
    private final String follower;

    public FollowUpdateItem(SearchResult searchResult, DateFormatter dateFormatter, PersonalInformation followeePersonalInfo, String follower, I18NBean i18n) {
        super(searchResult, dateFormatter, i18n, "content-type-follow");
        this.followeePersonalInfo = followeePersonalInfo;
        this.follower = follower;
    }

    @Override
    public Updater getUpdater() {
        return new DefaultUpdater(this.follower, this.i18n);
    }

    @Override
    public String getLinkedUpdateTarget() {
        return String.format("<a href=\"%s%s\">%s</a>", RequestCacheThreadLocal.getContextPath(), this.followeePersonalInfo.getUrlPath(), this.followeePersonalInfo.getDisplayTitle());
    }

    @Override
    public String getDescriptionAndDateKey() {
        return "update.item.desc.follow";
    }

    @Override
    public String getDescriptionAndAuthorKey() {
        return "update.item.desc.author.follow";
    }
}

