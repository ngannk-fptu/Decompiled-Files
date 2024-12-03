/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationservice.impl;

import com.hazelcast.internal.partition.ReplicaErrorLogger;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.util.counters.MwCounter;
import com.hazelcast.internal.util.counters.SwCounter;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Bits;
import com.hazelcast.nio.Packet;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.operationservice.impl.Invocation;
import com.hazelcast.spi.impl.operationservice.impl.InvocationRegistry;
import com.hazelcast.spi.impl.operationservice.impl.responses.ErrorResponse;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.function.Consumer;
import java.nio.ByteOrder;

public final class InboundResponseHandler
implements Consumer<Packet> {
    final SwCounter responsesNormal = SwCounter.newSwCounter();
    final SwCounter responsesTimeout = SwCounter.newSwCounter();
    final MwCounter responsesBackup = MwCounter.newMwCounter();
    final SwCounter responsesError = SwCounter.newSwCounter();
    final MwCounter responsesMissing = MwCounter.newMwCounter();
    private final ILogger logger;
    private final InternalSerializationService serializationService;
    private final InvocationRegistry invocationRegistry;
    private final NodeEngine nodeEngine;
    private final boolean useBigEndian;

    InboundResponseHandler(InvocationRegistry invocationRegistry, NodeEngine nodeEngine) {
        this.logger = nodeEngine.getLogger(InboundResponseHandler.class);
        this.serializationService = (InternalSerializationService)nodeEngine.getSerializationService();
        this.useBigEndian = this.serializationService.getByteOrder() == ByteOrder.BIG_ENDIAN;
        this.invocationRegistry = invocationRegistry;
        this.nodeEngine = nodeEngine;
    }

    @Override
    public void accept(Packet packet) {
        Preconditions.checkNotNull(packet, "packet can't be null");
        Preconditions.checkTrue(packet.getPacketType() == Packet.Type.OPERATION, "Packet type is not OPERATION");
        Preconditions.checkTrue(packet.isFlagRaised(2), "FLAG_OP_RESPONSE is not set");
        byte[] bytes = packet.toByteArray();
        int typeId = Bits.readInt(bytes, 13, this.useBigEndian);
        long callId = Bits.readLong(bytes, 17, this.useBigEndian);
        Address sender = packet.getConn().getEndPoint();
        try {
            switch (typeId) {
                case 0: {
                    byte backupAcks = bytes[26];
                    this.notifyNormalResponse(callId, packet, backupAcks, sender);
                    break;
                }
                case 2: {
                    this.notifyBackupComplete(callId);
                    break;
                }
                case 8: {
                    this.notifyCallTimeout(callId, sender);
                    break;
                }
                case 9: {
                    ErrorResponse errorResponse = (ErrorResponse)this.serializationService.toObject(packet);
                    this.notifyErrorResponse(callId, errorResponse.getCause(), sender);
                    break;
                }
                default: {
                    this.logger.severe("Unrecognized type: " + typeId + " packet:" + packet);
                    break;
                }
            }
        }
        catch (Throwable e) {
            this.logger.severe("While processing response...", e);
        }
    }

    public void notifyBackupComplete(long callId) {
        this.responsesBackup.inc();
        try {
            Invocation invocation = this.invocationRegistry.get(callId);
            if (invocation == null) {
                if (this.logger.isFinestEnabled()) {
                    this.logger.finest("No Invocation found for backup response with callId=" + callId);
                }
                return;
            }
            invocation.notifyBackupComplete();
        }
        catch (Exception e) {
            ReplicaErrorLogger.log(e, this.logger);
        }
    }

    void notifyErrorResponse(long callId, Object cause, Address sender) {
        this.responsesError.inc();
        Invocation invocation = this.invocationRegistry.get(callId);
        if (invocation == null) {
            this.responsesMissing.inc();
            if (this.nodeEngine.isRunning() && callId != 0L) {
                this.logger.warning("No Invocation found for error response with callId=" + callId + " sent from " + sender);
            }
            return;
        }
        invocation.notifyError(cause);
    }

    void notifyNormalResponse(long callId, Object value, int backupCount, Address sender) {
        this.responsesNormal.inc();
        Invocation invocation = this.invocationRegistry.get(callId);
        if (invocation == null) {
            this.responsesMissing.inc();
            if (this.nodeEngine.isRunning()) {
                this.logger.warning("No Invocation found for normal response with callId=" + callId + " sent from " + sender);
            }
            return;
        }
        invocation.notifyNormalResponse(value, backupCount);
    }

    void notifyCallTimeout(long callId, Address sender) {
        this.responsesTimeout.inc();
        Invocation invocation = this.invocationRegistry.get(callId);
        if (invocation == null) {
            this.responsesMissing.inc();
            if (this.nodeEngine.isRunning()) {
                this.logger.warning("No Invocation found for call timeout response with callId=" + callId + " sent from " + sender);
            }
            return;
        }
        invocation.notifyCallTimeout();
    }
}

