/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.DateFormatter
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.renderer.util.RendererUtil
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.macros.advanced.recentupdate;

import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.AbstractUpdateItem;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.renderer.util.RendererUtil;
import org.apache.commons.lang3.StringUtils;

public class CommentUpdateItem
extends AbstractUpdateItem {
    public CommentUpdateItem(SearchResult searchResult, DateFormatter dateFormatter, I18NBean i18n, String iconClass) {
        super(searchResult, dateFormatter, i18n, iconClass);
    }

    @Override
    public String getUpdateTargetTitle() {
        if (this.searchResult.getDisplayTitle() == null) {
            return "";
        }
        return HtmlUtil.htmlEncode((String)this.searchResult.getDisplayTitle().substring("Re: ".length()));
    }

    @Override
    public String getBody() {
        String comment = this.searchResult.getContent();
        if (StringUtils.isNotBlank((CharSequence)comment)) {
            return GeneralUtil.shortenString((String)RendererUtil.summarise((String)comment), (int)120);
        }
        return "";
    }

    @Override
    public String getDescriptionAndDateKey() {
        return "update.item.desc.comment";
    }

    @Override
    public String getDescriptionAndAuthorKey() {
        return "update.item.desc.author.comment";
    }
}

