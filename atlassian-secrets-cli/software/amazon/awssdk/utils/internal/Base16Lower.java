/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.utils.internal.Base16Codec;
import software.amazon.awssdk.utils.internal.CodecUtils;

@SdkInternalApi
public final class Base16Lower {
    private static final Base16Codec CODEC = new Base16Codec(false);

    private Base16Lower() {
    }

    public static String encodeAsString(byte ... bytes) {
        if (bytes == null) {
            return null;
        }
        return bytes.length == 0 ? "" : CodecUtils.toStringDirect(CODEC.encode(bytes));
    }

    public static byte[] encode(byte[] bytes) {
        return bytes == null || bytes.length == 0 ? bytes : CODEC.encode(bytes);
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
        return CODEC.decode(buf, len);
    }

    public static byte[] decode(byte[] b16) {
        return b16 == null || b16.length == 0 ? b16 : CODEC.decode(b16, b16.length);
    }
}

