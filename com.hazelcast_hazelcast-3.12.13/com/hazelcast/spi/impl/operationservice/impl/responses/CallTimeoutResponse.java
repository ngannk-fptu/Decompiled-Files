/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationservice.impl.responses;

import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.impl.SpiDataSerializerHook;
import com.hazelcast.spi.impl.operationservice.impl.responses.Response;

public class CallTimeoutResponse
extends Response
implements IdentifiedDataSerializable {
    public CallTimeoutResponse() {
    }

    public CallTimeoutResponse(long callId, boolean urgent) {
        super(callId, urgent);
    }

    @Override
    public int getFactoryId() {
        return SpiDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 8;
    }

    public String toString() {
        return "CallTimeoutResponse{callId=" + this.callId + ", urgent=" + this.urgent + '}';
    }
}

