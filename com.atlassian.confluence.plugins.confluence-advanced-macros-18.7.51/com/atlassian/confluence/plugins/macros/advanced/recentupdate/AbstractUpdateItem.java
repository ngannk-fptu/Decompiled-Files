/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.DateFormatter
 *  com.atlassian.confluence.core.datetime.FriendlyDateFormatter
 *  com.atlassian.confluence.core.datetime.RequestTimeThreadLocal
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.util.RequestCacheThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.Message
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.macros.advanced.recentupdate;

import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.datetime.FriendlyDateFormatter;
import com.atlassian.confluence.core.datetime.RequestTimeThreadLocal;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.DefaultUpdater;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.UpdateItem;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.Updater;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.RequestCacheThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.Message;
import com.atlassian.velocity.htmlsafe.HtmlSafe;
import java.util.Collections;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractUpdateItem
implements UpdateItem {
    final SearchResult searchResult;
    final FriendlyDateFormatter dateFormatter;
    private final String iconClass;
    private DefaultUpdater updater;
    final I18NBean i18n;

    public AbstractUpdateItem(SearchResult searchResult, DateFormatter dateFormatter, I18NBean i18n, String iconClass) {
        this.searchResult = searchResult;
        this.dateFormatter = new FriendlyDateFormatter(RequestTimeThreadLocal.getTimeOrNow(), dateFormatter);
        this.iconClass = iconClass;
        this.i18n = i18n;
    }

    @Override
    @HtmlSafe
    public String getDescriptionAndDate() {
        return this.i18n.getText(this.getDescriptionAndDateKey(), Collections.singletonList(this.getFormattedDate()));
    }

    protected abstract String getDescriptionAndDateKey();

    @Override
    @HtmlSafe
    public String getDescriptionAndAuthor() {
        return this.i18n.getText(this.getDescriptionAndAuthorKey(), Collections.singletonList(this.getUpdater().getLinkedFullName()));
    }

    protected abstract String getDescriptionAndAuthorKey();

    @Override
    public Updater getUpdater() {
        if (this.updater == null) {
            this.updater = new DefaultUpdater(this.searchResult.getLastModifierUser(), this.i18n);
        }
        return this.updater;
    }

    @Override
    @HtmlSafe
    public String getUpdateTargetTitle() {
        return StringEscapeUtils.escapeHtml4((String)this.searchResult.getDisplayTitle());
    }

    @HtmlSafe
    public String getLinkedUpdateTargetForHtmlExport() {
        return this.getLinkedUpdateTarget();
    }

    @HtmlSafe
    public String getLinkedUpdateTarget() {
        return String.format("<a href=\"%s%s\" title=\"%s\">%s</a>", this.getRequestCacheThreadLocalContextPath(), this.getUpdateTargetUrl(), this.getUpdateTargetToolTip(), this.getUpdateTargetTitle());
    }

    protected String getRequestCacheThreadLocalContextPath() {
        return RequestCacheThreadLocal.getContextPath();
    }

    @HtmlSafe
    protected String getUpdateTargetToolTip() {
        return "";
    }

    @HtmlSafe
    public String getLinkedSpace() {
        if (StringUtils.isBlank((CharSequence)this.searchResult.getSpaceKey())) {
            return null;
        }
        return String.format("<a href=\"%s%s\">%s</a>", this.getRequestCacheThreadLocalContextPath(), "/display/" + HtmlUtil.urlEncode((String)this.searchResult.getSpaceKey()), HtmlUtil.htmlEncode((String)this.searchResult.getSpaceName()));
    }

    @HtmlSafe
    public String getSpaceName() {
        if (StringUtils.isBlank((CharSequence)this.searchResult.getSpaceName())) {
            return null;
        }
        return HtmlUtil.htmlEncode((String)this.searchResult.getSpaceName());
    }

    protected String getUpdateTargetUrl() {
        return StringEscapeUtils.escapeHtml4((String)this.searchResult.getUrlPath());
    }

    @Override
    public String getIconClass() {
        return this.iconClass;
    }

    @Override
    public String getFormattedDate() {
        Message message = this.dateFormatter.getFormatMessage(this.searchResult.getLastModificationDate());
        return this.i18n.getText(message.getKey(), message.getArguments());
    }

    @Override
    public String getBody() {
        return null;
    }
}

