/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.html.rules;

import com.opensymphony.module.sitemesh.html.BasicRule;
import com.opensymphony.module.sitemesh.html.CustomTag;
import com.opensymphony.module.sitemesh.html.Tag;

public class TagReplaceRule
extends BasicRule {
    private final String newTagName;

    public TagReplaceRule(String originalTagName, String newTagName) {
        super(originalTagName);
        this.newTagName = newTagName;
    }

    public void process(Tag tag) {
        this.currentBuffer().delete(tag.getPosition(), tag.getLength());
        CustomTag customTag = new CustomTag(tag);
        customTag.setName(this.newTagName);
        customTag.writeTo(this.currentBuffer(), tag.getPosition());
    }
}

