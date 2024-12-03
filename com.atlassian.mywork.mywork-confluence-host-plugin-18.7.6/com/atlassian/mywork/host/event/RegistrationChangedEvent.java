/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 *  com.atlassian.confluence.event.events.cluster.ClusterEvent
 *  com.atlassian.mywork.model.Registration
 */
package com.atlassian.mywork.host.event;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;
import com.atlassian.mywork.model.Registration;

public class RegistrationChangedEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = 2610707515852920996L;
    private final Registration registration;

    public RegistrationChangedEvent(Object src, Registration registration) {
        super(src);
        this.registration = registration;
    }

    public Registration getRegistration() {
        return this.registration;
    }

    public String toString() {
        return "RegistrationChangedEvent{registration=" + this.registration + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || ((Object)((Object)this)).getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        RegistrationChangedEvent that = (RegistrationChangedEvent)((Object)o);
        return this.registration != null ? this.registration.equals((Object)that.registration) : that.registration == null;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.registration != null ? this.registration.hashCode() : 0);
        return result;
    }
}

