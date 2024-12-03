/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.codec.binary;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class StringUtils {
    private static byte[] getBytes(String string, Charset charset) {
        return string == null ? null : string.getBytes(charset);
    }

    public static byte[] getBytesUtf8(String string) {
        return StringUtils.getBytes(string, StandardCharsets.UTF_8);
    }

    private static String newString(byte[] bytes, Charset charset) {
        return bytes == null ? null : new String(bytes, charset);
    }

    public static String newStringUsAscii(byte[] bytes) {
        return StringUtils.newString(bytes, StandardCharsets.US_ASCII);
    }

    public static String newStringUtf8(byte[] bytes) {
        return StringUtils.newString(bytes, StandardCharsets.UTF_8);
    }
}

