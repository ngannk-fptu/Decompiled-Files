/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.html;

import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.html.HTMLProcessorContext;
import com.opensymphony.module.sitemesh.html.Tag;
import com.opensymphony.module.sitemesh.html.TagRule;

public abstract class BasicRule
implements TagRule {
    private final String[] acceptableTagNames;
    protected HTMLProcessorContext context;

    protected BasicRule(String[] acceptableTagNames) {
        this.acceptableTagNames = acceptableTagNames;
    }

    protected BasicRule(String acceptableTagName) {
        this.acceptableTagNames = new String[]{acceptableTagName};
    }

    protected BasicRule() {
        this.acceptableTagNames = null;
    }

    public void setContext(HTMLProcessorContext context) {
        this.context = context;
    }

    public boolean shouldProcess(String name) {
        if (this.acceptableTagNames == null || this.acceptableTagNames.length < 1) {
            throw new UnsupportedOperationException(this.getClass().getName() + " should be constructed with acceptableTagNames OR should implement shouldProcess()");
        }
        for (int i = 0; i < this.acceptableTagNames.length; ++i) {
            if (!name.equals(this.acceptableTagNames[i])) continue;
            return true;
        }
        return false;
    }

    public abstract void process(Tag var1);

    protected SitemeshBufferFragment.Builder currentBuffer() {
        return this.context.currentBuffer();
    }

    protected String getCurrentBufferContent() {
        return this.context.currentBuffer().build().getStringContent();
    }
}

