/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationservice.impl.responses;

import com.hazelcast.spi.impl.operationservice.impl.responses.Response;

public final class BackupAckResponse
extends Response {
    public static final int BACKUP_RESPONSE_SIZE_IN_BYTES = 26;

    public BackupAckResponse() {
    }

    public BackupAckResponse(long callId, boolean urgent) {
        super(callId, urgent);
    }

    @Override
    public int getId() {
        return 2;
    }

    public String toString() {
        return "BackupAckResponse{callId=" + this.callId + ", urgent=" + this.urgent + '}';
    }
}

