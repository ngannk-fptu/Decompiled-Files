/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.proxyservice.impl;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.DistributedObjectEvent;
import com.hazelcast.core.DistributedObjectListener;
import com.hazelcast.core.DistributedObjectUtil;
import com.hazelcast.util.executor.StripedRunnable;
import java.util.Collection;

final class ProxyEventProcessor
implements StripedRunnable {
    private final Collection<DistributedObjectListener> listeners;
    private final DistributedObjectEvent.EventType type;
    private final String serviceName;
    private final String objectName;
    private final DistributedObject object;

    ProxyEventProcessor(Collection<DistributedObjectListener> listeners, DistributedObjectEvent.EventType eventType, String serviceName, String objectName, DistributedObject object) {
        this.listeners = listeners;
        this.type = eventType;
        this.serviceName = serviceName;
        this.objectName = objectName;
        this.object = object;
    }

    @Override
    public void run() {
        DistributedObjectEvent event = new DistributedObjectEvent(this.type, this.serviceName, this.objectName, this.object);
        block4: for (DistributedObjectListener listener : this.listeners) {
            switch (this.type) {
                case CREATED: {
                    listener.distributedObjectCreated(event);
                    continue block4;
                }
                case DESTROYED: {
                    listener.distributedObjectDestroyed(event);
                    continue block4;
                }
            }
            throw new IllegalStateException("Unrecognized EventType:" + (Object)((Object)this.type));
        }
    }

    @Override
    public int getKey() {
        return DistributedObjectUtil.getName(this.object).hashCode();
    }
}

