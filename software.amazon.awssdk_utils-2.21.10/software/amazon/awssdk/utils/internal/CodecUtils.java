/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.utils.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public final class CodecUtils {
    private CodecUtils() {
    }

    static int sanitize(String singleOctets, byte[] dest) {
        int capacity = dest.length;
        char[] src = singleOctets.toCharArray();
        int limit = 0;
        for (int i = 0; i < capacity; ++i) {
            char c = src[i];
            if (c == '\r' || c == '\n' || c == ' ') continue;
            if (c > '\u007f') {
                throw new IllegalArgumentException("Invalid character found at position " + i + " for " + singleOctets);
            }
            dest[limit++] = (byte)c;
        }
        return limit;
    }

    public static byte[] toBytesDirect(String singleOctets) {
        char[] src = singleOctets.toCharArray();
        byte[] dest = new byte[src.length];
        for (int i = 0; i < dest.length; ++i) {
            char c = src[i];
            if (c > '\u007f') {
                throw new IllegalArgumentException("Invalid character found at position " + i + " for " + singleOctets);
            }
            dest[i] = (byte)c;
        }
        return dest;
    }

    public static String toStringDirect(byte[] bytes) {
        char[] dest = new char[bytes.length];
        int i = 0;
        for (byte b : bytes) {
            dest[i++] = (char)b;
        }
        return new String(dest);
    }
}

