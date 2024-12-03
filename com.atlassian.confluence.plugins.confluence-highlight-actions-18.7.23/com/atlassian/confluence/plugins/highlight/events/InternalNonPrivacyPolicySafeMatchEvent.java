/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.highlight.events;

import com.atlassian.confluence.plugins.highlight.events.MatchEvent;

public class InternalNonPrivacyPolicySafeMatchEvent
extends MatchEvent {
    private final String text;

    public InternalNonPrivacyPolicySafeMatchEvent(Object src, String modifier, boolean found, String text, long pageId) {
        super(src, modifier, found, pageId);
        this.text = found ? null : text;
    }

    public String getText() {
        return this.text;
    }
}

