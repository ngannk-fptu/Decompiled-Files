/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 */
package com.atlassian.confluence.content.ui;

import com.atlassian.confluence.content.ui.ContentUiSupport;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;

public class PageUiSupport
implements ContentUiSupport<Page> {
    private static final String ICON_CLASS = "aui-icon content-type-page";
    private static final String ICON_PATH = "/images/icons/contenttypes/page_16.png";
    private static final String I18N_KEY = "page.word";
    private static final String HOME_PAGE_ICON_CLASS = "aui-icon content-type-home";
    private static final String HOME_PAGE_ICON_PATH = "/images/icons/contenttypes/home_page_16.png";
    private static final String HOME_PAGE_I18N_KEY = "home.page";
    private final WebResourceUrlProvider webResourceUrlProvider;

    public PageUiSupport(WebResourceUrlProvider webResourceUrlProvider) {
        this.webResourceUrlProvider = webResourceUrlProvider;
    }

    @Override
    public String getIconFilePath(Page content, int size) {
        return ICON_PATH;
    }

    @Override
    public String getIconPath(Page content, int size) {
        if (content.isHomePage()) {
            return this.getHomePageIconPath();
        }
        return this.getLegacyIconPath(content.getType(), size);
    }

    @Override
    public String getLegacyIconPath(String contentType, int size) {
        return this.prependResourcePath(ICON_PATH);
    }

    @Override
    public String getIconCssClass(SearchResult result) {
        if (result.isHomePage()) {
            return HOME_PAGE_ICON_CLASS;
        }
        return ICON_CLASS;
    }

    @Override
    public String getContentTypeI18NKey(SearchResult result) {
        return I18N_KEY;
    }

    @Override
    public String getIconCssClass(Page content) {
        if (content.isHomePage()) {
            return HOME_PAGE_ICON_CLASS;
        }
        return ICON_CLASS;
    }

    @Override
    public String getContentCssClass(String contentType, String contentPluginKey) {
        return "content-type-" + contentType;
    }

    @Override
    public String getContentCssClass(Page content) {
        return this.getContentCssClass(content.getType(), null);
    }

    @Override
    public String getContentTypeI18NKey(Page content) {
        if (content.isHomePage()) {
            return HOME_PAGE_I18N_KEY;
        }
        return I18N_KEY;
    }

    private String getHomePageIconPath() {
        return this.prependResourcePath(HOME_PAGE_ICON_PATH);
    }

    private String prependResourcePath(String homePageIconPath) {
        return this.webResourceUrlProvider.getStaticResourcePrefix(UrlMode.RELATIVE) + homePageIconPath;
    }
}

