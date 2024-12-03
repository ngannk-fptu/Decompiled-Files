/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.impl;

import com.hazelcast.core.MigrationEvent;
import com.hazelcast.core.MigrationListener;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.partition.MigrationInfo;
import com.hazelcast.internal.partition.impl.MigrationListenerAdapter;
import com.hazelcast.internal.partition.impl.PartitionLostListenerAdapter;
import com.hazelcast.logging.ILogger;
import com.hazelcast.partition.PartitionLostEvent;
import com.hazelcast.partition.PartitionLostListener;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.PartitionAwareService;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.eventservice.InternalEventService;
import com.hazelcast.spi.partition.IPartitionLostEvent;
import java.util.Collection;

public class PartitionEventManager {
    private final Node node;
    private final NodeEngineImpl nodeEngine;

    public PartitionEventManager(Node node) {
        this.node = node;
        this.nodeEngine = node.nodeEngine;
    }

    void sendMigrationEvent(MigrationInfo migrationInfo, MigrationEvent.MigrationStatus status) {
        if (migrationInfo.getSourceCurrentReplicaIndex() != 0 && migrationInfo.getDestinationNewReplicaIndex() != 0) {
            return;
        }
        ClusterServiceImpl clusterService = this.node.getClusterService();
        MemberImpl current = clusterService.getMember(migrationInfo.getSourceAddress());
        MemberImpl newOwner = clusterService.getMember(migrationInfo.getDestinationAddress());
        MigrationEvent event = new MigrationEvent(migrationInfo.getPartitionId(), current, newOwner, status);
        InternalEventService eventService = this.nodeEngine.getEventService();
        Collection<EventRegistration> registrations = eventService.getRegistrations("hz:core:partitionService", ".migration");
        eventService.publishEvent("hz:core:partitionService", registrations, (Object)event, event.getPartitionId());
    }

    public String addMigrationListener(MigrationListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener can't be null");
        }
        MigrationListenerAdapter adapter = new MigrationListenerAdapter(listener);
        InternalEventService eventService = this.nodeEngine.getEventService();
        EventRegistration registration = eventService.registerListener("hz:core:partitionService", ".migration", adapter);
        return registration.getId();
    }

    public boolean removeMigrationListener(String registrationId) {
        if (registrationId == null) {
            throw new NullPointerException("registrationId can't be null");
        }
        InternalEventService eventService = this.nodeEngine.getEventService();
        return eventService.deregisterListener("hz:core:partitionService", ".migration", registrationId);
    }

    public String addPartitionLostListener(PartitionLostListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener can't be null");
        }
        PartitionLostListenerAdapter adapter = new PartitionLostListenerAdapter(listener);
        InternalEventService eventService = this.nodeEngine.getEventService();
        EventRegistration registration = eventService.registerListener("hz:core:partitionService", ".partitionLost", adapter);
        return registration.getId();
    }

    public String addLocalPartitionLostListener(PartitionLostListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener can't be null");
        }
        PartitionLostListenerAdapter adapter = new PartitionLostListenerAdapter(listener);
        InternalEventService eventService = this.nodeEngine.getEventService();
        EventRegistration registration = eventService.registerLocalListener("hz:core:partitionService", ".partitionLost", adapter);
        return registration.getId();
    }

    public boolean removePartitionLostListener(String registrationId) {
        if (registrationId == null) {
            throw new NullPointerException("registrationId can't be null");
        }
        InternalEventService eventService = this.nodeEngine.getEventService();
        return eventService.deregisterListener("hz:core:partitionService", ".partitionLost", registrationId);
    }

    public void onPartitionLost(IPartitionLostEvent event) {
        PartitionLostEvent partitionLostEvent = new PartitionLostEvent(event.getPartitionId(), event.getLostReplicaIndex(), event.getEventSource());
        InternalEventService eventService = this.nodeEngine.getEventService();
        Collection<EventRegistration> registrations = eventService.getRegistrations("hz:core:partitionService", ".partitionLost");
        eventService.publishEvent("hz:core:partitionService", registrations, (Object)partitionLostEvent, event.getPartitionId());
    }

    public void sendPartitionLostEvent(int partitionId, int lostReplicaIndex) {
        IPartitionLostEvent event = new IPartitionLostEvent(partitionId, lostReplicaIndex, this.nodeEngine.getThisAddress());
        InternalPartitionLostEventPublisher publisher = new InternalPartitionLostEventPublisher(this.nodeEngine, event);
        this.nodeEngine.getExecutionService().execute("hz:system", publisher);
    }

    private static class InternalPartitionLostEventPublisher
    implements Runnable {
        private final NodeEngineImpl nodeEngine;
        private final IPartitionLostEvent event;

        InternalPartitionLostEventPublisher(NodeEngineImpl nodeEngine, IPartitionLostEvent event) {
            this.nodeEngine = nodeEngine;
            this.event = event;
        }

        @Override
        public void run() {
            for (PartitionAwareService service : this.nodeEngine.getServices(PartitionAwareService.class)) {
                try {
                    service.onPartitionLost(this.event);
                }
                catch (Exception e) {
                    ILogger logger = this.nodeEngine.getLogger(InternalPartitionLostEventPublisher.class);
                    logger.warning("Handling partitionLostEvent failed. Service: " + service.getClass() + " Event: " + this.event, e);
                }
            }
        }

        public IPartitionLostEvent getEvent() {
            return this.event;
        }
    }
}

