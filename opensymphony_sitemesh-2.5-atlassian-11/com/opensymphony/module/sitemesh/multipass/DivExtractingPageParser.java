/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.multipass;

import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.html.BasicRule;
import com.opensymphony.module.sitemesh.html.State;
import com.opensymphony.module.sitemesh.html.Tag;
import com.opensymphony.module.sitemesh.html.rules.PageBuilder;
import com.opensymphony.module.sitemesh.parser.HTMLPageParser;

public class DivExtractingPageParser
extends HTMLPageParser {
    protected void addUserDefinedRules(State html, PageBuilder page) {
        super.addUserDefinedRules(html, page);
        html.addRule(new TopLevelDivExtractingRule(page));
    }

    private static class TopLevelDivExtractingRule
    extends BasicRule {
        private String blockId;
        private int depth;
        private final PageBuilder page;

        public TopLevelDivExtractingRule(PageBuilder page) {
            super("div");
            this.page = page;
        }

        public void process(Tag tag) {
            if (tag.getType() == 1) {
                String id = tag.getAttributeValue("id", false);
                if (this.depth == 0 && id != null) {
                    this.currentBuffer().insert(tag.getPosition(), "<sitemesh:multipass id=\"div." + id + "\"/>");
                    this.blockId = id;
                    this.currentBuffer().markStartDelete(tag.getPosition());
                    this.context.pushBuffer(SitemeshBufferFragment.builder().setBuffer(this.context.getSitemeshBuffer()));
                    this.currentBuffer().markStart(tag.getPosition());
                }
                ++this.depth;
            } else if (tag.getType() == 2) {
                --this.depth;
                if (this.depth == 0 && this.blockId != null) {
                    this.currentBuffer().end(tag.getPosition() + tag.getLength());
                    this.page.addProperty("div." + this.blockId, this.getCurrentBufferContent());
                    this.blockId = null;
                    this.context.popBuffer();
                    this.currentBuffer().endDelete(tag.getPosition() + tag.getLength());
                }
            }
        }
    }
}

