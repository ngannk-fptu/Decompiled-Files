/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import org.apache.commons.lang3.StringUtils;

public class QuickPageRenderBean {
    private PageManager pageManager;
    private Renderer viewRenderer;
    private static final String PAGE_NOT_FOUND = "<span class=\"error\"><span class=\"errorMessage\">Page not found</span></span>";

    public boolean canRender(String spaceKey, String pageTitle) {
        return StringUtils.isNotEmpty((CharSequence)spaceKey) && this.pageManager.getPage(spaceKey, pageTitle) != null;
    }

    public boolean pageExists(String spaceKey, String title) {
        return this.pageManager.getPage(spaceKey, title) != null;
    }

    public boolean pageExists(long pageId) {
        return this.pageManager.getPage(pageId) != null;
    }

    public String render(String spaceKey, String pageTitle) {
        if (!StringUtils.isNotEmpty((CharSequence)spaceKey)) {
            return "";
        }
        Page p = this.pageManager.getPage(spaceKey, pageTitle);
        if (p == null) {
            return PAGE_NOT_FOUND;
        }
        return this.viewRenderer.render(p);
    }

    public String render(long pageId) {
        Page p = this.pageManager.getPage(pageId);
        if (p == null) {
            return PAGE_NOT_FOUND;
        }
        return this.viewRenderer.render(p);
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public void setViewRenderer(Renderer viewRenderer) {
        this.viewRenderer = viewRenderer;
    }
}

