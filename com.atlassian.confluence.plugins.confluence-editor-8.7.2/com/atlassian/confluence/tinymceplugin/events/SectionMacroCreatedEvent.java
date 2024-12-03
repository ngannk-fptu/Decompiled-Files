/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 */
package com.atlassian.confluence.tinymceplugin.events;

import com.atlassian.confluence.pages.AbstractPage;

public class SectionMacroCreatedEvent {
    private AbstractPage page;

    public SectionMacroCreatedEvent(AbstractPage page) {
        this.page = page;
    }

    public AbstractPage getPage() {
        return this.page;
    }
}

