/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.label;

import com.atlassian.confluence.event.events.label.LabelEvent;
import com.atlassian.confluence.labels.Label;

public class LabelDeleteEvent
extends LabelEvent {
    private static final long serialVersionUID = -3276469005123845055L;

    public LabelDeleteEvent(Label source) {
        super(source);
    }
}

