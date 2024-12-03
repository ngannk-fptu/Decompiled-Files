/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

import com.amazonaws.util.Base32Codec;
import com.amazonaws.util.CodecUtils;

public enum Base32 {

    private static final Base32Codec codec = new Base32Codec();

    public static String encodeAsString(byte ... bytes) {
        if (bytes == null) {
            return null;
        }
        return bytes.length == 0 ? "" : CodecUtils.toStringDirect(codec.encode(bytes));
    }

    public static byte[] encode(byte[] bytes) {
        return bytes == null || bytes.length == 0 ? bytes : codec.encode(bytes);
    }

    public static byte[] decode(String b32) {
        if (b32 == null) {
            return null;
        }
        if (b32.length() == 0) {
            return new byte[0];
        }
        byte[] buf = new byte[b32.length()];
        int len = CodecUtils.sanitize(b32, buf);
        return codec.decode(buf, len);
    }

    public static byte[] decode(byte[] b32) {
        return b32 == null || b32.length == 0 ? b32 : codec.decode(b32, b32.length);
    }
}

