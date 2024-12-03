/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.html.rules;

import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.html.BasicRule;
import com.opensymphony.module.sitemesh.html.Tag;
import com.opensymphony.module.sitemesh.html.rules.PageBuilder;

public class BodyTagRule
extends BasicRule {
    private final PageBuilder page;
    private final SitemeshBufferFragment.Builder body;

    public BodyTagRule(PageBuilder page, SitemeshBufferFragment.Builder body) {
        super("body");
        this.page = page;
        this.body = body;
    }

    public void process(Tag tag) {
        if (tag.getType() == 1 || tag.getType() == 3) {
            this.context.currentBuffer().setStart(tag.getPosition() + tag.getLength());
            for (int i = 0; i < tag.getAttributeCount(); ++i) {
                this.page.addProperty("body." + tag.getAttributeName(i), tag.getAttributeValue(i));
            }
            this.body.markStart(tag.getPosition() + tag.getLength());
        } else {
            this.body.end(tag.getPosition());
            this.context.pushBuffer(SitemeshBufferFragment.builder());
        }
    }
}

