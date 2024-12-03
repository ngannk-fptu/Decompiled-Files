/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service.page;

import com.atlassian.confluence.content.service.page.AbstractPageLocator;
import com.atlassian.confluence.pages.Page;

public final class SinglePageLocator
extends AbstractPageLocator {
    private Page page;

    public SinglePageLocator(Page page) {
        this.page = page;
    }

    @Override
    public Page getPage() {
        return this.page;
    }
}

