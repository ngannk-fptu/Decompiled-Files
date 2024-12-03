/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.utils;

import org.apache.hc.core5.annotation.Internal;

@Internal
public class Base64 {
    private static final Base64 CODEC = new Base64();
    private static final byte[] EMPTY_BYTES = new byte[0];

    public Base64() {
    }

    public Base64(int lineLength) {
        if (lineLength != 0) {
            throw new UnsupportedOperationException("Line breaks not supported");
        }
    }

    public static byte[] decodeBase64(byte[] base64) {
        return CODEC.decode(base64);
    }

    public static byte[] decodeBase64(String base64) {
        return CODEC.decode(base64);
    }

    public static byte[] encodeBase64(byte[] base64) {
        return CODEC.encode(base64);
    }

    public static String encodeBase64String(byte[] bytes) {
        if (null == bytes) {
            return null;
        }
        return java.util.Base64.getEncoder().encodeToString(bytes);
    }

    public byte[] decode(byte[] base64) {
        if (null == base64) {
            return null;
        }
        try {
            return java.util.Base64.getMimeDecoder().decode(base64);
        }
        catch (IllegalArgumentException e) {
            return EMPTY_BYTES;
        }
    }

    public byte[] decode(String base64) {
        if (null == base64) {
            return null;
        }
        try {
            return java.util.Base64.getMimeDecoder().decode(base64);
        }
        catch (IllegalArgumentException e) {
            return EMPTY_BYTES;
        }
    }

    public byte[] encode(byte[] value) {
        if (null == value) {
            return null;
        }
        return java.util.Base64.getEncoder().encode(value);
    }
}

