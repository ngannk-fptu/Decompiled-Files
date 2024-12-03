/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.observation;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.jcr.RepositoryException;
import javax.jcr.observation.Event;
import org.apache.jackrabbit.commons.observation.ListenerTracker;

class EventTracker
implements Event {
    private final ListenerTracker listener;
    protected final Event event;
    protected final AtomicBoolean externalAccessed = new AtomicBoolean();

    public EventTracker(ListenerTracker listenerTracker, Event event) {
        this.listener = listenerTracker;
        this.event = event;
    }

    private void userInfoAccessed() {
        if (!this.externalAccessed.get() && !this.listener.userInfoAccessedWithoutExternalsCheck.getAndSet(true)) {
            this.listener.warn("Event listener " + this.listener + " is trying to access user information of event " + this.event + " without checking whether the event is external.");
        }
        if (this.eventIsExternal() && !this.listener.userInfoAccessedFromExternalEvent.getAndSet(true)) {
            this.listener.warn("Event listener " + this.listener + " is trying to access user information of external event " + this.event + ".");
        }
    }

    private void dateInfoAccessed() {
        if (!this.externalAccessed.get() && !this.listener.dateAccessedWithoutExternalsCheck.getAndSet(true)) {
            this.listener.warn("Event listener " + this.listener + " is trying to access date information of event " + this.event + " without checking whether the event is external.");
        }
        if (this.eventIsExternal() && !this.listener.dateAccessedFromExternalEvent.getAndSet(true)) {
            this.listener.warn("Event listener " + this.listener + " is trying to access date information of external event " + this.event + ".");
        }
    }

    protected boolean eventIsExternal() {
        return false;
    }

    public String toString() {
        return this.event.toString();
    }

    public int hashCode() {
        return this.event.hashCode();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof EventTracker) {
            return this.event.equals(other);
        }
        return false;
    }

    @Override
    public int getType() {
        return this.event.getType();
    }

    @Override
    public String getPath() throws RepositoryException {
        return this.event.getPath();
    }

    @Override
    public String getUserID() {
        this.userInfoAccessed();
        return this.event.getUserID();
    }

    @Override
    public String getIdentifier() throws RepositoryException {
        return this.event.getIdentifier();
    }

    @Override
    public Map<?, ?> getInfo() throws RepositoryException {
        return this.event.getInfo();
    }

    @Override
    public String getUserData() throws RepositoryException {
        this.userInfoAccessed();
        return this.event.getUserData();
    }

    @Override
    public long getDate() throws RepositoryException {
        this.dateInfoAccessed();
        return this.event.getDate();
    }
}

