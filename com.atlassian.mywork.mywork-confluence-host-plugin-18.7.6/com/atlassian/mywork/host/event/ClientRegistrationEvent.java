/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 *  com.atlassian.confluence.event.events.cluster.ClusterEvent
 *  com.atlassian.mywork.model.Registration
 *  com.google.common.collect.Lists
 */
package com.atlassian.mywork.host.event;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;
import com.atlassian.mywork.model.Registration;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;

public class ClientRegistrationEvent
extends ConfluenceEvent
implements ClusterEvent {
    private final Collection<Registration> registrations;

    public ClientRegistrationEvent(Object src, Iterable<Registration> registrations) {
        super(src);
        this.registrations = Lists.newArrayList(registrations);
    }

    public Collection<Registration> getRegistrations() {
        return Collections.unmodifiableCollection(this.registrations);
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
        ClientRegistrationEvent that = (ClientRegistrationEvent)((Object)o);
        return this.registrations.equals(that.registrations);
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.registrations.hashCode();
        return result;
    }
}

