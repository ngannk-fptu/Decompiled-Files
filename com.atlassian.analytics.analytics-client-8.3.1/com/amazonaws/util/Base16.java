/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

import com.amazonaws.util.Base16Codec;
import com.amazonaws.util.CodecUtils;

public enum Base16 {

    private static final Base16Codec codec = new Base16Codec();

    public static String encodeAsString(byte ... bytes) {
        if (bytes == null) {
            return null;
        }
        return bytes.length == 0 ? "" : CodecUtils.toStringDirect(codec.encode(bytes));
    }

    public static byte[] encode(byte[] bytes) {
        return bytes == null || bytes.length == 0 ? bytes : codec.encode(bytes);
    }

    public static byte[] decode(String b16) {
        if (b16 == null) {
            return null;
        }
        if (b16.length() == 0) {
            return new byte[0];
        }
        byte[] buf = new byte[b16.length()];
        int len = CodecUtils.sanitize(b16, buf);
        return codec.decode(buf, len);
    }

    public static byte[] decode(byte[] b16) {
        return b16 == null || b16.length == 0 ? b16 : codec.decode(b16, b16.length);
    }
}

