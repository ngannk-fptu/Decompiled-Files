/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.spi.exception.DistributedObjectDestroyedException;

public class DistributedObjectEvent {
    protected DistributedObject distributedObject;
    private EventType eventType;
    private String serviceName;
    private String objectName;

    public DistributedObjectEvent(EventType eventType, String serviceName, String objectName, DistributedObject distributedObject) {
        this.eventType = eventType;
        this.serviceName = serviceName;
        this.objectName = objectName;
        this.distributedObject = distributedObject;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public EventType getEventType() {
        return this.eventType;
    }

    public Object getObjectId() {
        return this.getObjectName();
    }

    public Object getObjectName() {
        return this.objectName;
    }

    public DistributedObject getDistributedObject() {
        if (EventType.DESTROYED.equals((Object)this.eventType)) {
            throw new DistributedObjectDestroyedException(this.objectName + " destroyed!");
        }
        return this.distributedObject;
    }

    public String toString() {
        return "DistributedObjectEvent{eventType=" + (Object)((Object)this.eventType) + ", serviceName='" + this.serviceName + '\'' + ", objectName='" + this.objectName + '\'' + ", distributedObject=" + this.distributedObject + '}';
    }

    public static enum EventType {
        CREATED,
        DESTROYED;

    }
}

