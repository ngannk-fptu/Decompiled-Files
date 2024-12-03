/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.content.ui.ContentUiSupport
 *  com.atlassian.confluence.core.ConfluenceEntityObject
 *  com.atlassian.confluence.search.v2.SearchResult
 */
package com.atlassian.confluence.extra.calendar3.contenttype;

import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.content.ui.ContentUiSupport;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.search.v2.SearchResult;

public class CalendarContentUiSupport
implements ContentUiSupport {
    public String getIconFilePath(ConfluenceEntityObject content, int size) {
        return "";
    }

    public String getIconPath(ConfluenceEntityObject content, int size) {
        return "";
    }

    public String getLegacyIconPath(String contentType, int size) {
        return "";
    }

    public String getIconCssClass(ConfluenceEntityObject content) {
        return "";
    }

    public String getContentCssClass(ConfluenceEntityObject confluenceEntityObject) {
        return "content-type-calendar";
    }

    public String getContentCssClass(String contentType, String contentPluginKey) {
        return "content-type-calendar";
    }

    public String getIconCssClass(SearchResult result) {
        return "";
    }

    public String getContentTypeI18NKey(ConfluenceEntityObject content) {
        if ("com.atlassian.confluence.extra.team-calendars:calendar-content-type".equals(((CustomContentEntityObject)content).getPluginModuleKey())) {
            return "calendar3.legend.heading";
        }
        return "calendar3.space.calendars.view.heading";
    }

    public String getContentTypeI18NKey(SearchResult result) {
        return "";
    }

    public String getContentTypeCssClass(ConfluenceEntityObject confluenceEntityObject) {
        return this.getContentCssClass(confluenceEntityObject);
    }

    public String getContentTypeCssClass(String s1, String s2) {
        return "";
    }
}

