/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.partition;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public enum MigrationEndpoint {
    SOURCE(0),
    DESTINATION(1);

    private final byte code;

    private MigrationEndpoint(int code) {
        this.code = (byte)code;
    }

    public static void writeTo(MigrationEndpoint endpoint, DataOutput out) throws IOException {
        out.writeByte(endpoint.code);
    }

    public static MigrationEndpoint readFrom(DataInput in) throws IOException {
        byte code = in.readByte();
        return code == 0 ? SOURCE : DESTINATION;
    }
}

