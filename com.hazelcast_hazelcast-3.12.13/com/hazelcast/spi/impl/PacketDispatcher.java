/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl;

import com.hazelcast.instance.OutOfMemoryErrorDispatcher;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.EndpointManager;
import com.hazelcast.nio.Packet;
import com.hazelcast.util.function.Consumer;

public final class PacketDispatcher
implements Consumer<Packet> {
    private final ILogger logger;
    private final Consumer<Packet> eventService;
    private final Consumer<Packet> operationExecutor;
    private final Consumer<Packet> jetPacketConsumer;
    private final Consumer<Packet> responseHandler;
    private final Consumer<Packet> invocationMonitor;

    public PacketDispatcher(ILogger logger, Consumer<Packet> operationExecutor, Consumer<Packet> responseHandler, Consumer<Packet> invocationMonitor, Consumer<Packet> eventService, Consumer<Packet> jetPacketConsumer) {
        this.logger = logger;
        this.responseHandler = responseHandler;
        this.eventService = eventService;
        this.invocationMonitor = invocationMonitor;
        this.operationExecutor = operationExecutor;
        this.jetPacketConsumer = jetPacketConsumer;
    }

    @Override
    public void accept(Packet packet) {
        try {
            switch (packet.getPacketType()) {
                case OPERATION: {
                    if (packet.isFlagRaised(2)) {
                        this.responseHandler.accept(packet);
                        break;
                    }
                    if (packet.isFlagRaised(64)) {
                        this.invocationMonitor.accept(packet);
                        break;
                    }
                    this.operationExecutor.accept(packet);
                    break;
                }
                case EVENT: {
                    this.eventService.accept(packet);
                    break;
                }
                case BIND: 
                case EXTENDED_BIND: {
                    Connection connection = packet.getConn();
                    EndpointManager endpointManager = connection.getEndpointManager();
                    endpointManager.accept(packet);
                    break;
                }
                case JET: {
                    this.jetPacketConsumer.accept(packet);
                    break;
                }
                default: {
                    this.logger.severe("Header flags [" + Integer.toBinaryString(packet.getFlags()) + "] specify an undefined packet type " + packet.getPacketType().name());
                    break;
                }
            }
        }
        catch (Throwable t) {
            OutOfMemoryErrorDispatcher.inspectOutOfMemoryError(t);
            this.logger.severe("Failed to process: " + packet, t);
        }
    }
}

