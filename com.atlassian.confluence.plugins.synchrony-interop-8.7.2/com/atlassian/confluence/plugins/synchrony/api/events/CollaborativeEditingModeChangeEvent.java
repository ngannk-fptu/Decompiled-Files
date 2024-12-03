/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 */
package com.atlassian.confluence.plugins.synchrony.api.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.plugins.synchrony.api.CollaborativeEditingMode;
import java.util.Objects;

@EventName(value="confluence.collaborative.editing.mode.change")
public abstract class CollaborativeEditingModeChangeEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = -9058590833663330007L;
    private final CollaborativeEditingMode previousMode;
    private final boolean synchronyUp;
    private final long modeDurationInSeconds;

    CollaborativeEditingModeChangeEvent(CollaborativeEditingMode previousMode, boolean synchronyUp, long modeDurationInSeconds) {
        super(new Object());
        this.previousMode = Objects.requireNonNull(previousMode);
        this.synchronyUp = synchronyUp;
        this.modeDurationInSeconds = modeDurationInSeconds;
    }

    public CollaborativeEditingMode getPreviousMode() {
        return this.previousMode;
    }

    public abstract CollaborativeEditingMode getNewMode();

    public boolean isSynchronyUp() {
        return this.synchronyUp;
    }

    public long getModeDurationInSeconds() {
        return this.modeDurationInSeconds;
    }
}

