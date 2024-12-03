/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.html.rules;

import com.opensymphony.module.sitemesh.html.BlockExtractingRule;
import com.opensymphony.module.sitemesh.html.Tag;
import com.opensymphony.module.sitemesh.html.rules.PageBuilder;

public class TitleExtractingRule
extends BlockExtractingRule {
    private final PageBuilder page;
    private boolean seenTitle;

    public TitleExtractingRule(PageBuilder page) {
        super(false, "title");
        this.page = page;
    }

    protected void end(Tag tag) {
        if (!this.seenTitle) {
            this.page.addProperty("title", this.getCurrentBufferContent());
            this.seenTitle = true;
        }
    }
}

