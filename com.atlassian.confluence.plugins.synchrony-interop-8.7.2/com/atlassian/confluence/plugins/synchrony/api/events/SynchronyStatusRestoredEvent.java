/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.plugins.synchrony.api.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.plugins.synchrony.api.events.AbstractSynchronyWasDownEvent;

@EventName(value="confluence.synchrony.status.restored")
public class SynchronyStatusRestoredEvent
extends AbstractSynchronyWasDownEvent {
    public SynchronyStatusRestoredEvent(long approximateDurationInSeconds) {
        super(approximateDurationInSeconds);
    }
}

