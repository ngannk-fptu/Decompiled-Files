/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.UUID;

public final class UUIDSerializationUtil {
    private UUIDSerializationUtil() {
    }

    public static void writeUUID(DataOutput out, UUID uid) throws IOException {
        out.writeLong(uid.getLeastSignificantBits());
        out.writeLong(uid.getMostSignificantBits());
    }

    public static UUID readUUID(DataInput in) throws IOException {
        long least = in.readLong();
        long most = in.readLong();
        return new UUID(most, least);
    }
}

