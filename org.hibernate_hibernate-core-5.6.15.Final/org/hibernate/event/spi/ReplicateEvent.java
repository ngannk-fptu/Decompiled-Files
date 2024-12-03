/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import org.hibernate.ReplicationMode;
import org.hibernate.event.spi.AbstractEvent;
import org.hibernate.event.spi.EventSource;

public class ReplicateEvent
extends AbstractEvent {
    private Object object;
    private ReplicationMode replicationMode;
    private String entityName;

    public ReplicateEvent(Object object, ReplicationMode replicationMode, EventSource source) {
        this(null, object, replicationMode, source);
    }

    public ReplicateEvent(String entityName, Object object, ReplicationMode replicationMode, EventSource source) {
        super(source);
        this.entityName = entityName;
        if (object == null) {
            throw new IllegalArgumentException("attempt to create replication strategy with null entity");
        }
        if (replicationMode == null) {
            throw new IllegalArgumentException("attempt to create replication strategy with null replication mode");
        }
        this.object = object;
        this.replicationMode = replicationMode;
    }

    public Object getObject() {
        return this.object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public ReplicationMode getReplicationMode() {
        return this.replicationMode;
    }

    public void setReplicationMode(ReplicationMode replicationMode) {
        this.replicationMode = replicationMode;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
}

