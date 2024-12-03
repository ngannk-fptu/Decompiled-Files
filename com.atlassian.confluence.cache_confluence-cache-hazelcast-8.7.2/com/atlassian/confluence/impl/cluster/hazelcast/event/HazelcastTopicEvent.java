/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.cluster.ClusterEventWrapper
 *  com.atlassian.confluence.impl.cluster.event.TopicEvent
 *  com.hazelcast.nio.ObjectDataInput
 *  com.hazelcast.nio.ObjectDataOutput
 *  com.hazelcast.nio.serialization.DataSerializable
 */
package com.atlassian.confluence.impl.cluster.hazelcast.event;

import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.impl.cluster.event.TopicEvent;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

final class HazelcastTopicEvent
implements DataSerializable,
TopicEvent {
    private UUID id;
    private Object payload;

    public HazelcastTopicEvent() {
    }

    public HazelcastTopicEvent(Object payload) {
        this.id = UUID.randomUUID();
        this.payload = Objects.requireNonNull(payload);
    }

    public String toString() {
        if (this.payload instanceof ClusterEventWrapper) {
            return String.format("%s %s wrapping %s", this.payload.getClass().getSimpleName(), this.id, ((ClusterEventWrapper)this.payload).getEvent().getClass().getSimpleName());
        }
        return String.format("%s %s ", this.payload.getClass().getSimpleName(), this.id);
    }

    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject((Object)this.id);
        out.writeObject(this.payload);
    }

    public void readData(ObjectDataInput in) throws IOException {
        this.id = (UUID)in.readObject();
        this.payload = in.readObject();
    }

    public UUID getId() {
        return this.id;
    }

    public Object getPayload() {
        return this.payload;
    }
}

