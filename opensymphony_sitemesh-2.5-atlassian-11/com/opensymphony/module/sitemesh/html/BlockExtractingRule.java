/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.html;

import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.html.BasicRule;
import com.opensymphony.module.sitemesh.html.Tag;

public abstract class BlockExtractingRule
extends BasicRule {
    private boolean keepInBuffer;
    private boolean seenOpeningTag;

    protected BlockExtractingRule(boolean keepInBuffer, String acceptableTagName) {
        super(acceptableTagName);
        this.keepInBuffer = keepInBuffer;
    }

    protected BlockExtractingRule(boolean keepInBuffer) {
        this.keepInBuffer = keepInBuffer;
    }

    public void process(Tag tag) {
        if (tag.getType() == 1) {
            if (!this.keepInBuffer) {
                this.context.currentBuffer().markStartDelete(tag.getPosition());
            }
            this.context.pushBuffer(this.createBuffer(this.context.getSitemeshBuffer()).markStart(tag.getPosition() + tag.getLength()));
            this.start(tag);
            this.seenOpeningTag = true;
        } else if (tag.getType() == 2 && this.seenOpeningTag) {
            this.context.currentBuffer().end(tag.getPosition());
            this.end(tag);
            this.context.popBuffer();
            if (!this.keepInBuffer) {
                this.context.currentBuffer().endDelete(tag.getPosition() + tag.getLength());
            }
            this.seenOpeningTag = false;
        } else if (!this.keepInBuffer) {
            this.context.currentBuffer().delete(tag.getPosition(), tag.getLength());
        }
    }

    protected void start(Tag tag) {
    }

    protected void end(Tag tag) {
    }

    protected SitemeshBufferFragment.Builder createBuffer(SitemeshBuffer sitemeshBuffer) {
        return SitemeshBufferFragment.builder().setBuffer(sitemeshBuffer);
    }
}

