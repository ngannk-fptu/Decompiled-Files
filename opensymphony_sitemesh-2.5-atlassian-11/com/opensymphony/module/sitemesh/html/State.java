/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.html;

import com.opensymphony.module.sitemesh.html.HTMLProcessorContext;
import com.opensymphony.module.sitemesh.html.StateChangeListener;
import com.opensymphony.module.sitemesh.html.TagRule;
import com.opensymphony.module.sitemesh.html.Text;
import com.opensymphony.module.sitemesh.html.TextFilter;
import com.opensymphony.module.sitemesh.html.util.StringSitemeshBuffer;
import java.util.ArrayList;
import java.util.List;

public final class State {
    private TagRule[] rules = new TagRule[16];
    private int ruleCount = 0;
    private List listeners = null;
    private List textFilters = null;

    public void addRule(TagRule rule) {
        if (this.ruleCount == this.rules.length) {
            TagRule[] longerArray = new TagRule[this.rules.length * 2];
            System.arraycopy(this.rules, 0, longerArray, 0, this.ruleCount);
            this.rules = longerArray;
        }
        this.rules[this.ruleCount++] = rule;
    }

    public void addTextFilter(TextFilter textFilter) {
        if (this.textFilters == null) {
            this.textFilters = new ArrayList();
        }
        this.textFilters.add(textFilter);
    }

    public boolean shouldProcessTag(String tagName) {
        for (int i = this.ruleCount - 1; i >= 0; --i) {
            if (!this.rules[i].shouldProcess(tagName)) continue;
            return true;
        }
        return false;
    }

    public TagRule getRule(String tagName) {
        for (int i = this.ruleCount - 1; i >= 0; --i) {
            if (!this.rules[i].shouldProcess(tagName)) continue;
            return this.rules[i];
        }
        return null;
    }

    public void addListener(StateChangeListener listener) {
        if (this.listeners == null) {
            this.listeners = new ArrayList();
        }
        this.listeners.add(listener);
    }

    public void endOfState() {
        if (this.listeners == null) {
            return;
        }
        for (StateChangeListener listener : this.listeners) {
            listener.stateFinished();
        }
    }

    public void handleText(Text text, HTMLProcessorContext context) {
        if (this.textFilters != null && !this.textFilters.isEmpty()) {
            String original;
            String asString = original = text.getContents();
            for (TextFilter textFilter : this.textFilters) {
                asString = textFilter.filter(asString);
            }
            if (!original.equals(asString)) {
                context.currentBuffer().delete(text.getPosition(), text.getLength());
                context.currentBuffer().insert(text.getPosition(), StringSitemeshBuffer.createBufferFragment(asString));
            }
        }
    }
}

