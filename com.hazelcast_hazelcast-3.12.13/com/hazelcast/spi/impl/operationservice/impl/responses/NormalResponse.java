/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationservice.impl.responses;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.impl.operationservice.impl.responses.Response;
import java.io.IOException;

public class NormalResponse
extends Response {
    public static final int OFFSET_BACKUP_ACKS = 26;
    public static final int OFFSET_IS_DATA = 27;
    public static final int OFFSET_NOT_DATA = 28;
    public static final int OFFSET_DATA_LENGTH = 28;
    public static final int OFFSET_DATA_PAYLOAD = 32;
    private Object value;
    private int backupAcks;

    public NormalResponse() {
    }

    public NormalResponse(Object value, long callId, int backupAcks, boolean urgent) {
        super(callId, urgent);
        this.value = value;
        this.backupAcks = backupAcks;
    }

    public Object getValue() {
        return this.value;
    }

    public int getBackupAcks() {
        return this.backupAcks;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeByte(this.backupAcks);
        boolean isData = this.value instanceof Data;
        out.writeBoolean(isData);
        if (isData) {
            out.writeData((Data)this.value);
        } else {
            out.writeObject(this.value);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.backupAcks = in.readByte();
        boolean isData = in.readBoolean();
        this.value = isData ? in.readData() : in.readObject();
    }

    public String toString() {
        return "NormalResponse{callId=" + this.callId + ", urgent=" + this.urgent + ", value=" + this.value + ", backupAcks=" + this.backupAcks + '}';
    }
}

