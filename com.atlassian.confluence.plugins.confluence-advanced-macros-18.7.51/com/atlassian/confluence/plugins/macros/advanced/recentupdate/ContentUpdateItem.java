/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.DateFormatter
 *  com.atlassian.confluence.importexport.impl.ExportUtils
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.util.RequestCacheThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.macros.advanced.recentupdate;

import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.importexport.impl.ExportUtils;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.AbstractUpdateItem;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.UpdateItemUtils;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.util.RequestCacheThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.velocity.htmlsafe.HtmlSafe;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentUpdateItem
extends AbstractUpdateItem {
    private static final Logger log = LoggerFactory.getLogger(ContentUpdateItem.class);

    public ContentUpdateItem(SearchResult searchResult, DateFormatter dateFormatter, I18NBean i18n, String iconClass) {
        super(searchResult, dateFormatter, i18n, iconClass);
    }

    @Override
    @HtmlSafe
    public String getBody() {
        return null;
    }

    @Override
    protected String getUpdateTargetToolTip() {
        return this.getSpaceName();
    }

    @HtmlSafe
    public String getChangesLink() {
        int version;
        String latestVersionId = (String)this.searchResult.getExtraFields().get(SearchFieldNames.LATEST_VERSION_ID);
        String versionString = (String)this.searchResult.getExtraFields().get(SearchFieldNames.CONTENT_VERSION);
        if (StringUtils.isBlank((CharSequence)latestVersionId) || StringUtils.isBlank((CharSequence)versionString)) {
            return null;
        }
        try {
            version = Integer.parseInt(versionString);
        }
        catch (NumberFormatException e) {
            log.debug(versionString + " could not be parsed into an integer.");
            return null;
        }
        if (version <= 1) {
            return null;
        }
        if (this.isPageContentType(this.searchResult)) {
            return String.format("<a class=\"changes-link\" href=\"%s/pages/diffpagesbyversion.action?pageId=%s&selectedPageVersions=%s&selectedPageVersions=%s\">%s</a>", RequestCacheThreadLocal.getContextPath(), latestVersionId, version, version - 1, this.i18n.getText("update.item.changes"));
        }
        return null;
    }

    private boolean isPageContentType(SearchResult searchResult) {
        return "page".equals(searchResult.getType()) || "blogpost".equals(searchResult.getType());
    }

    @Override
    public String getDescriptionAndDateKey() {
        int version = UpdateItemUtils.getContentVersion(this.searchResult);
        String i18nKey = version > 0 ? (version == 1 ? "update.item.desc.created" : "update.item.desc.updated") : "update.item.desc.generic";
        return i18nKey;
    }

    @Override
    protected String getDescriptionAndAuthorKey() {
        int version = UpdateItemUtils.getContentVersion(this.searchResult);
        String i18nKey = version > 0 ? (version == 1 ? "update.item.desc.author.created" : "update.item.desc.author.updated") : "update.item.desc.author.generic";
        return i18nKey;
    }

    @Override
    @HtmlSafe
    public String getLinkedUpdateTargetForHtmlExport() {
        if (this.searchResult.getSpaceName().equals(this.getUpdateTargetTitle())) {
            return String.format("<a href=\"index.html\" title=\"%s\">%s</a>", this.getUpdateTargetToolTip(), this.getUpdateTargetTitle());
        }
        return String.format("<a href=\"%s\" title=\"%s\">%s</a>", ExportUtils.getTitleAsHref((SearchResult)this.searchResult), this.getUpdateTargetToolTip(), this.getUpdateTargetTitle());
    }
}

