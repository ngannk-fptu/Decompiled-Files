/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.html;

import com.opensymphony.module.sitemesh.html.BasicRule;
import com.opensymphony.module.sitemesh.html.State;
import com.opensymphony.module.sitemesh.html.Tag;

public class StateTransitionRule
extends BasicRule {
    private final State newState;
    private final boolean writeEnclosingTag;
    private State lastState;

    public StateTransitionRule(String tagName, State newState) {
        this(tagName, newState, true);
    }

    public StateTransitionRule(String tagName, State newState, boolean writeEnclosingTag) {
        super(tagName);
        this.newState = newState;
        this.writeEnclosingTag = writeEnclosingTag;
    }

    public void process(Tag tag) {
        if (tag.getType() == 1) {
            this.lastState = this.context.currentState();
            this.context.changeState(this.newState);
            this.newState.addRule(this);
        } else if (tag.getType() == 2 && this.lastState != null) {
            this.context.changeState(this.lastState);
            this.lastState = null;
        }
    }
}

