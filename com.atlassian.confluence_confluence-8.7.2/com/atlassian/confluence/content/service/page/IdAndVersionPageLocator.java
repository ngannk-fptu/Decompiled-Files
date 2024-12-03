/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service.page;

import com.atlassian.confluence.content.service.page.AbstractPageLocator;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;

public class IdAndVersionPageLocator
extends AbstractPageLocator {
    private final PageManager pageManager;
    private final long pageId;
    private final int version;

    public IdAndVersionPageLocator(PageManager pageManager, long pageId, int version) {
        this.pageManager = pageManager;
        this.pageId = pageId;
        this.version = version;
    }

    @Override
    public Page getPage() {
        Page page = this.pageManager.getPage(this.pageId);
        if (this.version < 0 || this.version > page.getVersion()) {
            return null;
        }
        AbstractPage historicalVersion = this.pageManager.getPageByVersion(page, this.version);
        if (!(historicalVersion instanceof Page)) {
            return null;
        }
        return (Page)historicalVersion;
    }
}

