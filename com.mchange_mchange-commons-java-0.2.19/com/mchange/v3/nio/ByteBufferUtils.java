/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v3.nio;

import java.nio.ByteBuffer;

public final class ByteBufferUtils {
    public static byte[] newArray(ByteBuffer byteBuffer) {
        if (byteBuffer.hasArray()) {
            return (byte[])byteBuffer.array().clone();
        }
        byte[] byArray = new byte[byteBuffer.remaining()];
        byteBuffer.get(byArray);
        return byArray;
    }

    private ByteBufferUtils() {
    }
}

