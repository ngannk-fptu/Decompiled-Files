/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.html.rules;

import com.opensymphony.module.sitemesh.html.BasicRule;
import com.opensymphony.module.sitemesh.html.Tag;
import com.opensymphony.module.sitemesh.html.rules.PageBuilder;

public class HtmlAttributesRule
extends BasicRule {
    private final PageBuilder page;

    public HtmlAttributesRule(PageBuilder page) {
        super("html");
        this.page = page;
    }

    public void process(Tag tag) {
        if (tag.getType() == 1) {
            this.context.currentBuffer().markStart(tag.getPosition() + tag.getLength());
            for (int i = 0; i < tag.getAttributeCount(); ++i) {
                this.page.addProperty(tag.getAttributeName(i), tag.getAttributeValue(i));
            }
        } else {
            this.context.currentBuffer().end(tag.getPosition());
        }
    }
}

