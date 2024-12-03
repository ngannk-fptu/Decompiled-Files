/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 */
package com.atlassian.confluence.content.ui;

import com.atlassian.confluence.content.ui.ContentUiSupport;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;

public class SimpleUiSupport<T extends ContentEntityObject>
implements ContentUiSupport<T> {
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final String iconPath;
    private final String iconCssClass;
    private final String contentCssClass;
    private final String i18NKey;

    public SimpleUiSupport(WebResourceUrlProvider webResourceUrlProvider, String iconPath, String iconCssClass, String contentCssClass, String i18NKey) {
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.iconPath = iconPath;
        this.iconCssClass = iconCssClass;
        this.contentCssClass = contentCssClass;
        this.i18NKey = i18NKey;
    }

    @Override
    public String getIconFilePath(T content, int size) {
        return this.iconPath;
    }

    @Override
    public String getIconPath(T content, int size) {
        return this.getLegacyIconPath(((ContentEntityObject)content).getType(), size);
    }

    @Override
    public String getLegacyIconPath(String contentType, int size) {
        return this.webResourceUrlProvider.getStaticResourcePrefix(UrlMode.RELATIVE) + this.iconPath;
    }

    @Override
    public String getIconCssClass(SearchResult result) {
        return this.getIconCssClass();
    }

    @Override
    public String getContentTypeI18NKey(SearchResult result) {
        return this.getContentTypeI18NKey();
    }

    @Override
    public String getIconCssClass(T content) {
        return this.getIconCssClass();
    }

    @Override
    public String getContentCssClass(String contentType, String contentPluginKey) {
        return this.contentCssClass;
    }

    @Override
    public String getContentCssClass(T content) {
        return this.contentCssClass;
    }

    @Override
    public String getContentTypeI18NKey(T content) {
        return this.getContentTypeI18NKey();
    }

    private String getIconCssClass() {
        return this.iconCssClass;
    }

    private String getContentTypeI18NKey() {
        return this.i18NKey;
    }

    public static ContentUiSupport getUnknown(WebResourceUrlProvider webResourceUrlProvider) {
        return new SimpleUiSupport(webResourceUrlProvider, "/images/icons/attachments/generic_16.png", "", "custom", "");
    }
}

