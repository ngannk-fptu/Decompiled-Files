/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationservice.impl;

import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.Packet;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.Preconditions;

public class OutboundOperationHandler {
    private final Address thisAddress;
    private final InternalSerializationService serializationService;
    private final Node node;

    public OutboundOperationHandler(Node node, InternalSerializationService serializationService) {
        this.node = node;
        this.thisAddress = node.getThisAddress();
        this.serializationService = serializationService;
    }

    public boolean send(Operation op, Address target) {
        Preconditions.checkNotNull(target, "Target is required!");
        if (this.thisAddress.equals(target)) {
            throw new IllegalArgumentException("Target is this node! -> " + target + ", op: " + op);
        }
        Object connection = this.node.getNetworkingService().getEndpointManager(EndpointQualifier.MEMBER).getOrConnect(target);
        return this.send(op, (Connection)connection);
    }

    public boolean send(Operation op, Connection connection) {
        byte[] bytes = this.serializationService.toBytes(op);
        int partitionId = op.getPartitionId();
        Packet packet = new Packet(bytes, partitionId).setPacketType(Packet.Type.OPERATION);
        if (op.isUrgent()) {
            packet.raiseFlags(16);
        }
        return this.node.getEndpointManager(EndpointQualifier.MEMBER).transmit(packet, connection);
    }
}

