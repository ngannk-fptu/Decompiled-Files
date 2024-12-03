/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.label;

import com.atlassian.confluence.event.events.label.LabelEvent;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.Labelable;

public class LabelAddEvent
extends LabelEvent {
    private static final long serialVersionUID = 5613807165419133501L;

    public LabelAddEvent(Label source, Labelable labelled) {
        super(source, labelled);
    }
}

