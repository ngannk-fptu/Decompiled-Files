/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.eventservice.impl;

import com.hazelcast.nio.Packet;
import com.hazelcast.replicatedmap.ReplicatedMapCantBeCreatedOnLiteMemberException;
import com.hazelcast.spi.impl.eventservice.impl.EventEnvelope;
import com.hazelcast.spi.impl.eventservice.impl.EventProcessor;
import com.hazelcast.spi.impl.eventservice.impl.EventServiceImpl;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.executor.StripedRunnable;

public class RemoteEventProcessor
extends EventProcessor
implements StripedRunnable {
    private final EventServiceImpl eventService;
    private final Packet packet;

    public RemoteEventProcessor(EventServiceImpl eventService, Packet packet) {
        super(eventService, null, packet.getPartitionId());
        this.eventService = eventService;
        this.packet = packet;
    }

    @Override
    public void run() {
        try {
            EventEnvelope eventEnvelope = (EventEnvelope)this.eventService.nodeEngine.toObject(this.packet);
            this.process(eventEnvelope);
        }
        catch (ReplicatedMapCantBeCreatedOnLiteMemberException e) {
            EmptyStatement.ignore(e);
        }
        catch (Exception e) {
            this.eventService.logger.warning("Error while logging processing event", e);
        }
    }
}

