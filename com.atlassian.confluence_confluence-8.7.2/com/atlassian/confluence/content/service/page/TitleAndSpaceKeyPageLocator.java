/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service.page;

import com.atlassian.confluence.content.service.page.AbstractPageLocator;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;

public class TitleAndSpaceKeyPageLocator
extends AbstractPageLocator {
    private final PageManager pageManager;
    private String spaceKey;
    private String title;

    public TitleAndSpaceKeyPageLocator(PageManager pageManager, String spaceKey, String title) {
        this.pageManager = pageManager;
        this.spaceKey = spaceKey;
        this.title = title;
    }

    @Override
    public Page getPage() {
        return this.pageManager.getPage(this.spaceKey, this.title);
    }
}

