/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.label;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.Labelable;

public abstract class LabelEvent
extends ConfluenceEvent {
    protected Labelable labelled;

    public LabelEvent(Label source) {
        super(source);
    }

    public LabelEvent(Label source, Labelable labelled) {
        this(source);
        this.labelled = labelled;
    }

    public Label getLabel() {
        return (Label)this.getSource();
    }

    public Labelable getLabelled() {
        return this.labelled;
    }
}

