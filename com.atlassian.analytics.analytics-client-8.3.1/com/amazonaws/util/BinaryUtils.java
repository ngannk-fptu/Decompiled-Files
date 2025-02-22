/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

import com.amazonaws.util.Base16Lower;
import com.amazonaws.util.Base64;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class BinaryUtils {
    public static String toHex(byte[] data) {
        return Base16Lower.encodeAsString(data);
    }

    public static byte[] fromHex(String hexData) {
        return Base16Lower.decode(hexData);
    }

    public static String toBase64(byte[] data) {
        return Base64.encodeAsString(data);
    }

    public static byte[] fromBase64(String b64Data) {
        return b64Data == null ? null : Base64.decode(b64Data);
    }

    public static ByteArrayInputStream toStream(ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            return new ByteArrayInputStream(new byte[0]);
        }
        return new ByteArrayInputStream(BinaryUtils.copyBytesFrom(byteBuffer));
    }

    public static byte[] copyAllBytesFrom(ByteBuffer bb) {
        if (bb == null) {
            return null;
        }
        if (bb.hasArray()) {
            return Arrays.copyOfRange(bb.array(), bb.arrayOffset(), bb.arrayOffset() + bb.limit());
        }
        ByteBuffer copy = bb.asReadOnlyBuffer();
        copy.rewind();
        byte[] dst = new byte[copy.remaining()];
        copy.get(dst);
        return dst;
    }

    public static byte[] copyBytesFrom(ByteBuffer bb) {
        if (bb == null) {
            return null;
        }
        if (bb.hasArray()) {
            return Arrays.copyOfRange(bb.array(), bb.arrayOffset() + bb.position(), bb.arrayOffset() + bb.limit());
        }
        byte[] dst = new byte[bb.remaining()];
        bb.asReadOnlyBuffer().get(dst);
        return dst;
    }
}

