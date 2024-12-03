/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.util;

import com.hazelcast.nio.serialization.Data;
import java.util.Map;

public final class ParameterUtil {
    static final int UTF8_MAX_BYTES_PER_CHAR = 3;

    private ParameterUtil() {
    }

    public static int calculateDataSize(String string) {
        return 4 + string.length() * 3;
    }

    public static int calculateDataSize(Data data) {
        return ParameterUtil.addByteArrayLengthHeader(data.totalSize());
    }

    public static int calculateDataSize(Map.Entry<Data, Data> entry) {
        return ParameterUtil.addByteArrayLengthHeader(entry.getKey().totalSize()) + ParameterUtil.addByteArrayLengthHeader(entry.getValue().totalSize());
    }

    public static int calculateDataSize(byte[] bytes) {
        return ParameterUtil.addByteArrayLengthHeader(bytes.length);
    }

    private static int addByteArrayLengthHeader(int length) {
        return 4 + length;
    }

    public static int calculateDataSize(Integer data) {
        return 4;
    }

    public static int calculateDataSize(Boolean data) {
        return 1;
    }

    public static int calculateDataSize(Long data) {
        return 8;
    }
}

