/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.label;

import com.atlassian.confluence.event.events.label.LabelEvent;
import com.atlassian.confluence.labels.Label;

public class LabelCreateEvent
extends LabelEvent {
    private static final long serialVersionUID = -3086468275286564463L;

    public LabelCreateEvent(Label source) {
        super(source);
    }
}

