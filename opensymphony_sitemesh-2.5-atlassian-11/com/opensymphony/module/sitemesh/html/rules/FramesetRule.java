/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.html.rules;

import com.opensymphony.module.sitemesh.html.BasicRule;
import com.opensymphony.module.sitemesh.html.Tag;
import com.opensymphony.module.sitemesh.html.rules.PageBuilder;

public class FramesetRule
extends BasicRule {
    private final PageBuilder page;

    public FramesetRule(PageBuilder page) {
        super(new String[]{"frame", "frameset"});
        this.page = page;
    }

    public void process(Tag tag) {
        this.context.currentBuffer().delete(tag.getPosition(), tag.getLength());
        this.page.addProperty("frameset", "true");
    }
}

