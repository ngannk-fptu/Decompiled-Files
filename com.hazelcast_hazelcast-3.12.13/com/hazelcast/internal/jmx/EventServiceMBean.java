/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.internal.jmx.HazelcastMBean;
import com.hazelcast.internal.jmx.ManagedAnnotation;
import com.hazelcast.internal.jmx.ManagedDescription;
import com.hazelcast.internal.jmx.ManagementService;
import com.hazelcast.spi.EventService;
import java.util.Hashtable;

@ManagedDescription(value="HazelcastInstance.EventService")
public class EventServiceMBean
extends HazelcastMBean<EventService> {
    private static final int INITIAL_CAPACITY = 5;

    public EventServiceMBean(HazelcastInstance hazelcastInstance, EventService eventService, ManagementService service) {
        super(eventService, service);
        Hashtable<String, String> properties = new Hashtable<String, String>(5);
        properties.put("type", ManagementService.quote("HazelcastInstance.EventService"));
        properties.put("name", ManagementService.quote(hazelcastInstance.getName()));
        properties.put("instance", ManagementService.quote(hazelcastInstance.getName()));
        this.setObjectName(properties);
    }

    @ManagedAnnotation(value="eventThreadCount")
    @ManagedDescription(value="The event thread count")
    public int getEventThreadCount() {
        return ((EventService)this.managedObject).getEventThreadCount();
    }

    @ManagedAnnotation(value="eventQueueCapacity")
    @ManagedDescription(value="The event queue capacity")
    public int getEventQueueCapacity() {
        return ((EventService)this.managedObject).getEventQueueCapacity();
    }

    @ManagedAnnotation(value="eventQueueSize")
    @ManagedDescription(value="The size of the event queue")
    public int getEventQueueSize() {
        return ((EventService)this.managedObject).getEventQueueSize();
    }
}

