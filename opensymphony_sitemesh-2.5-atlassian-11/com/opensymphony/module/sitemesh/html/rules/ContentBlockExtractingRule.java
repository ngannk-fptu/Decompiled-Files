/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.html.rules;

import com.opensymphony.module.sitemesh.html.BlockExtractingRule;
import com.opensymphony.module.sitemesh.html.Tag;
import com.opensymphony.module.sitemesh.html.rules.PageBuilder;

public class ContentBlockExtractingRule
extends BlockExtractingRule {
    private final PageBuilder page;
    private String contentBlockId;

    public ContentBlockExtractingRule(PageBuilder page) {
        super(false, "content");
        this.page = page;
    }

    protected void start(Tag tag) {
        this.contentBlockId = tag.getAttributeValue("tag", false);
    }

    protected void end(Tag tag) {
        this.page.addProperty("page." + this.contentBlockId, this.getCurrentBufferContent());
    }
}

