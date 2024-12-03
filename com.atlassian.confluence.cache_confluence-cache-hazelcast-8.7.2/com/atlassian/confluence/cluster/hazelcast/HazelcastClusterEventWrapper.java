/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.cluster.ClusterEventWrapper
 *  com.atlassian.event.Event
 *  com.atlassian.hazelcast.serialization.OsgiSafe
 *  com.hazelcast.nio.ObjectDataInput
 *  com.hazelcast.nio.ObjectDataOutput
 *  com.hazelcast.nio.serialization.DataSerializable
 */
package com.atlassian.confluence.cluster.hazelcast;

import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.event.Event;
import com.atlassian.hazelcast.serialization.OsgiSafe;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import java.io.IOException;
import java.io.Serializable;

public class HazelcastClusterEventWrapper
extends ClusterEventWrapper
implements Serializable,
DataSerializable {
    private static final long serialVersionUID = 8996628876503234689L;
    private OsgiSafe<Event> wrappedEvent;

    public HazelcastClusterEventWrapper(Object src, Event event) {
        super(src, event);
        this.wrappedEvent = new OsgiSafe((Object)event);
    }

    public HazelcastClusterEventWrapper() {
        super(new Object(), null);
    }

    public Event getEvent() {
        return (Event)this.wrappedEvent.getValue();
    }

    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.wrappedEvent);
    }

    public void readData(ObjectDataInput in) throws IOException {
        this.wrappedEvent = (OsgiSafe)in.readObject();
        this.source = null;
        this.event = (Event)this.wrappedEvent.getValue();
    }
}

