/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.DateFormatter
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.util.i18n.I18NBean
 */
package com.atlassian.confluence.plugins.macros.advanced.recentupdate;

import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.AbstractUpdateItem;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.util.i18n.I18NBean;

public class AttachmentUpdateItem
extends AbstractUpdateItem {
    public AttachmentUpdateItem(SearchResult searchResult, DateFormatter dateFormatter, I18NBean i18n, String iconClass) {
        super(searchResult, dateFormatter, i18n, iconClass);
    }

    @Override
    public String getBody() {
        return super.getBody();
    }

    @Override
    public String getDescriptionAndDateKey() {
        return "update.item.desc.attachment";
    }

    @Override
    public String getDescriptionAndAuthorKey() {
        return "update.item.desc.author.attachment";
    }
}

