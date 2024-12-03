/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.label;

import com.atlassian.confluence.event.events.label.LabelEvent;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.Labelable;

public class LabelRemoveEvent
extends LabelEvent {
    private static final long serialVersionUID = 6745847369510660608L;

    public LabelRemoveEvent(Label source, Labelable labelled) {
        super(source, labelled);
    }
}

