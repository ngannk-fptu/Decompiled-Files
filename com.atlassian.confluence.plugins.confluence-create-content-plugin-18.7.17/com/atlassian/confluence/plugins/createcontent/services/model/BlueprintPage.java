/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Page
 */
package com.atlassian.confluence.plugins.createcontent.services.model;

import com.atlassian.confluence.pages.Page;

public class BlueprintPage {
    private final Page page;
    private final Page indexPage;

    public BlueprintPage(Page page, Page indexPage) {
        this.page = page;
        this.indexPage = indexPage;
    }

    public Page getPage() {
        return this.page;
    }

    public Page getIndexPage() {
        return this.indexPage;
    }
}

