/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import com.hazelcast.internal.util.ThreadLocalRandomProvider;
import com.hazelcast.nio.Address;
import java.util.Random;
import java.util.UUID;

public final class UuidUtil {
    private UuidUtil() {
    }

    public static String createClusterUuid() {
        return UuidUtil.newUnsecureUuidString();
    }

    public static String createMemberUuid(Address endpoint) {
        return UuidUtil.newUnsecureUuidString();
    }

    public static String createClientUuid(Address endpoint) {
        return UuidUtil.newUnsecureUuidString();
    }

    public static String newUnsecureUuidString() {
        return UuidUtil.newUnsecureUUID().toString();
    }

    public static String newSecureUuidString() {
        return UuidUtil.newSecureUUID().toString();
    }

    public static UUID newUnsecureUUID() {
        return UuidUtil.getUUID(ThreadLocalRandomProvider.get());
    }

    public static UUID newSecureUUID() {
        return UuidUtil.getUUID(ThreadLocalRandomProvider.getSecure());
    }

    private static UUID getUUID(Random random) {
        int i;
        byte[] data = new byte[16];
        random.nextBytes(data);
        data[6] = (byte)(data[6] & 0xF);
        data[6] = (byte)(data[6] | 0x40);
        data[8] = (byte)(data[8] & 0x3F);
        data[8] = (byte)(data[8] | 0x80);
        long mostSigBits = 0L;
        long leastSigBits = 0L;
        assert (data.length == 16) : "data must be 16 bytes in length";
        for (i = 0; i < 8; ++i) {
            mostSigBits = mostSigBits << 8 | (long)(data[i] & 0xFF);
        }
        for (i = 8; i < 16; ++i) {
            leastSigBits = leastSigBits << 8 | (long)(data[i] & 0xFF);
        }
        return new UUID(mostSigBits, leastSigBits);
    }
}

