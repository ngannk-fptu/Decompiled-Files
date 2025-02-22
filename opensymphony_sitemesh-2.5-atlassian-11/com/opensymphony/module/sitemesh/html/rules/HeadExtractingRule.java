/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.html.rules;

import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.html.BlockExtractingRule;

public class HeadExtractingRule
extends BlockExtractingRule {
    private final SitemeshBufferFragment.Builder head;

    public HeadExtractingRule(SitemeshBufferFragment.Builder head) {
        super(false, "head");
        this.head = head;
    }

    protected SitemeshBufferFragment.Builder createBuffer(SitemeshBuffer sitemeshBuffer) {
        return this.head;
    }
}

