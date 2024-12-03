/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service.page;

import com.atlassian.confluence.content.service.page.AbstractPageLocator;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;

public class IdPageLocator
extends AbstractPageLocator {
    private final PageManager pageManager;
    private final long pageId;

    public IdPageLocator(PageManager pageManager, long pageId) {
        this.pageManager = pageManager;
        this.pageId = pageId;
    }

    @Override
    public Page getPage() {
        return this.pageManager.getPage(this.pageId);
    }
}

