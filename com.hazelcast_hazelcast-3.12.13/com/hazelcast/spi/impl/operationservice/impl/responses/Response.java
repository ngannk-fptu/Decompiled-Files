/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationservice.impl.responses;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.impl.SpiDataSerializerHook;
import java.io.IOException;

public abstract class Response
implements IdentifiedDataSerializable {
    public static final int OFFSET_SERIALIZER_TYPE_ID = 4;
    public static final int OFFSET_IDENTIFIED = 8;
    public static final int OFFSET_TYPE_FACTORY_ID = 9;
    public static final int OFFSET_TYPE_ID = 13;
    public static final int OFFSET_CALL_ID = 17;
    public static final int OFFSET_URGENT = 25;
    public static final int RESPONSE_SIZE_IN_BYTES = 26;
    protected long callId;
    protected boolean urgent;

    public Response() {
    }

    public Response(long callId, boolean urgent) {
        this.callId = callId;
        this.urgent = urgent;
    }

    public boolean isUrgent() {
        return this.urgent;
    }

    public long getCallId() {
        return this.callId;
    }

    @Override
    public int getFactoryId() {
        return SpiDataSerializerHook.F_ID;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeLong(this.callId);
        out.writeBoolean(this.urgent);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.callId = in.readLong();
        this.urgent = in.readBoolean();
    }
}

