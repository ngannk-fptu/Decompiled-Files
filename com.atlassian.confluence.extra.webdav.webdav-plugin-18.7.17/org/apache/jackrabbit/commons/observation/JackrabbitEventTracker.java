/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.observation;

import javax.jcr.observation.Event;
import org.apache.jackrabbit.api.observation.JackrabbitEvent;
import org.apache.jackrabbit.commons.observation.EventTracker;
import org.apache.jackrabbit.commons.observation.ListenerTracker;

class JackrabbitEventTracker
extends EventTracker
implements JackrabbitEvent {
    public JackrabbitEventTracker(ListenerTracker listener, Event event) {
        super(listener, event);
    }

    @Override
    protected boolean eventIsExternal() {
        return ((JackrabbitEvent)this.event).isExternal();
    }

    @Override
    public boolean isExternal() {
        this.externalAccessed.set(true);
        return this.eventIsExternal();
    }
}

