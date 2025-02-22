/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationservice.impl;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Bits;
import com.hazelcast.nio.EndpointManager;
import com.hazelcast.nio.Packet;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationResponseHandler;
import com.hazelcast.spi.impl.SpiDataSerializerHook;
import com.hazelcast.spi.impl.operationservice.impl.responses.CallTimeoutResponse;
import com.hazelcast.spi.impl.operationservice.impl.responses.ErrorResponse;
import com.hazelcast.spi.impl.operationservice.impl.responses.NormalResponse;
import com.hazelcast.spi.impl.operationservice.impl.responses.Response;
import com.hazelcast.util.Preconditions;
import java.nio.ByteOrder;

public final class OutboundResponseHandler
implements OperationResponseHandler {
    private final Address thisAddress;
    private final InternalSerializationService serializationService;
    private final boolean useBigEndian;
    private final ILogger logger;

    OutboundResponseHandler(Address thisAddress, InternalSerializationService serializationService, ILogger logger) {
        this.thisAddress = thisAddress;
        this.serializationService = serializationService;
        this.useBigEndian = serializationService.getByteOrder() == ByteOrder.BIG_ENDIAN;
        this.logger = logger;
    }

    public void sendResponse(Operation operation, Object obj) {
        boolean send;
        Address target = operation.getCallerAddress();
        EndpointManager endpointManager = operation.getConnection().getEndpointManager();
        if (obj == null) {
            send = this.sendNormalResponse(endpointManager, target, operation.getCallId(), 0, operation.isUrgent(), null);
        } else if (obj.getClass() == NormalResponse.class) {
            NormalResponse response = (NormalResponse)obj;
            send = this.sendNormalResponse(endpointManager, target, response.getCallId(), response.getBackupAcks(), response.isUrgent(), response.getValue());
        } else {
            send = obj.getClass() == ErrorResponse.class || obj.getClass() == CallTimeoutResponse.class ? this.send(endpointManager, target, (Response)obj) : (obj instanceof Throwable ? this.send(endpointManager, target, new ErrorResponse((Throwable)obj, operation.getCallId(), operation.isUrgent())) : this.sendNormalResponse(endpointManager, target, operation.getCallId(), 0, operation.isUrgent(), obj));
        }
        if (!send) {
            this.logger.warning("Cannot send response: " + obj + " to " + target + ". " + operation);
        }
    }

    public boolean send(EndpointManager endpointManager, Address target, Response response) {
        Preconditions.checkNotNull(target, "Target is required!");
        if (this.thisAddress.equals(target)) {
            throw new IllegalArgumentException("Target is this node! -> " + target + ", response: " + response);
        }
        byte[] bytes = this.serializationService.toBytes(response);
        Packet packet = this.newResponsePacket(bytes, response.isUrgent());
        return this.transmit(target, packet, endpointManager);
    }

    private boolean sendNormalResponse(EndpointManager endpointManager, Address target, long callId, int backupAcks, boolean urgent, Object value) {
        this.checkTarget(target);
        Packet packet = this.toNormalResponsePacket(callId, (byte)backupAcks, urgent, value);
        return this.transmit(target, packet, endpointManager);
    }

    Packet toNormalResponsePacket(long callId, int backupAcks, boolean urgent, Object value) {
        byte[] bytes;
        boolean isData = value instanceof Data;
        if (isData) {
            Data data = (Data)value;
            int dataLengthInBytes = data.totalSize();
            bytes = new byte[32 + dataLengthInBytes];
            Bits.writeInt(bytes, 28, dataLengthInBytes, this.useBigEndian);
            data.copyTo(bytes, 32);
        } else if (value == null) {
            bytes = new byte[32];
            Bits.writeInt(bytes, 28, 0, this.useBigEndian);
        } else {
            bytes = this.serializationService.toBytes(value, 28, false);
        }
        this.writeResponsePrologueBytes(bytes, 0, callId, urgent);
        bytes[26] = (byte)backupAcks;
        bytes[27] = (byte)(isData ? 1 : 0);
        return this.newResponsePacket(bytes, urgent);
    }

    public void sendBackupAck(EndpointManager endpointManager, Address target, long callId, boolean urgent) {
        this.checkTarget(target);
        Packet packet = this.toBackupAckPacket(callId, urgent);
        this.transmit(target, packet, endpointManager);
    }

    Packet toBackupAckPacket(long callId, boolean urgent) {
        byte[] bytes = new byte[26];
        this.writeResponsePrologueBytes(bytes, 2, callId, urgent);
        return this.newResponsePacket(bytes, urgent);
    }

    private void writeResponsePrologueBytes(byte[] bytes, int typeId, long callId, boolean urgent) {
        Bits.writeIntB(bytes, 0, 0);
        Bits.writeIntB(bytes, 4, -2);
        bytes[8] = 1;
        Bits.writeInt(bytes, 9, SpiDataSerializerHook.F_ID, this.useBigEndian);
        Bits.writeInt(bytes, 13, typeId, this.useBigEndian);
        Bits.writeLong(bytes, 17, callId, this.useBigEndian);
        bytes[25] = (byte)(urgent ? 1 : 0);
    }

    private Packet newResponsePacket(byte[] bytes, boolean urgent) {
        Packet packet = new Packet(bytes, -1).setPacketType(Packet.Type.OPERATION).raiseFlags(2);
        if (urgent) {
            packet.raiseFlags(16);
        }
        return packet;
    }

    private boolean transmit(Address target, Packet packet, EndpointManager endpointManager) {
        return endpointManager.transmit(packet, target);
    }

    private void checkTarget(Address target) {
        Preconditions.checkNotNull(target, "Target is required!");
        if (this.thisAddress.equals(target)) {
            throw new IllegalArgumentException("Target is this node! -> " + target);
        }
    }
}

